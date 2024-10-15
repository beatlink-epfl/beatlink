package com.android.sample.model.spotify

import android.content.Context
import android.net.Uri
import android.util.Base64
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.SecureRandom

class SpotifyService {
    private val client = OkHttpClient()

    private val clientId = "5025edc6cd4b4e508839ae45296d1c82"
    private val redirectUri = "myapp://callback"

    /**
     * Requests an access token from Spotify using the authorization code
     */
    fun requestAccessToken(
        code: String,
        context: Context,
        callback: (accessToken: String?, error: String?) -> Unit
    ) {
        val codeVerifier = fetch(context, "code_verifier")
        if (codeVerifier == null) {
            callback(null, "Code verifier not found")
        }

        val requestBody = FormBody.Builder()
            .add("grant_type", "authorization_code")
            .add("code", code)
            .add("redirect_uri", redirectUri)
            .add("client_id", clientId)
            .add("code_verifier", codeVerifier!!)
            .build()

        val request = Request.Builder()
            .url("https://accounts.spotify.com/api/token")
            .post(requestBody)
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null, e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body?.let { responseBody ->
                        val jsonResponse = JSONObject(responseBody.string())
                        val accessToken = jsonResponse.getString("access_token")

                        // Store the access_token in SharedPreferences
                        store(context, "access_token", accessToken)
                        callback(accessToken, null)
                    }
                } else {
                    callback(null, "An error occurred retrieving the Spotify access token")
                }
            }
        })
    }

    /**
     * Builds the Spotify authorization URL
     */
    fun buildSpotifyAuthUrl(context: Context): String {
        val codeVerifier = codeVerifier()
        store(context, "code_verifier", codeVerifier)

        val codeChallenge = createCodeChallenge(codeVerifier)
        val scope = "user-read-private user-read-email"

        return Uri.Builder()
            .scheme("https")
            .authority("accounts.spotify.com")
            .appendPath("authorize")
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("client_id", clientId)
            .appendQueryParameter("scope", scope)
            .appendQueryParameter("redirect_uri", redirectUri)
            .appendQueryParameter("code_challenge_method", "S256")
            .appendQueryParameter("code_challenge", codeChallenge)
            .build()
            .toString()
    }

    /**
     * Generates a random code verifier for the PKCE flow
     */
    private fun codeVerifier(): String {
        val possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        val random = SecureRandom()
        val sb = StringBuilder(64)

        for (i in 0 until 64) {
            val randomIndex = random.nextInt(possible.length)
            sb.append(possible[randomIndex])
        }

        return sb.toString()
    }

    /**
     * Hashes a plain text using SHA-256
     */
    private fun sha256(plain: String): ByteArray {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(plain.toByteArray(StandardCharsets.UTF_8))
    }

    /**
     * Encodes a byte array to a base64 string
     */
    private fun base64encode(input: ByteArray): String {
        return Base64.encodeToString(input, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
    }

    /**
     * Creates a code challenge from a code verifier to be used in the PKCE flow
     */
    private fun createCodeChallenge(codeVerifier: String): String {
        val bytes = sha256(codeVerifier)
        return base64encode(bytes)
    }

    /**
     * Store a key-value pair in SharedPreferences
     */
    private fun store(context: Context, id: String, data: String) {
        val sharedPref = context.getSharedPreferences("spotify_auth", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString(id, data)
            apply()
        }
    }

    /**
     * Fetch a value from SharedPreferences
     */
    private fun fetch(context: Context, id: String): String? {
        val sharedPref = context.getSharedPreferences("spotify_auth", Context.MODE_PRIVATE)
        return sharedPref.getString(id, null)
    }
}