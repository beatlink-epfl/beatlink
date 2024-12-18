package com.epfl.beatlink.repository.spotify.auth

import android.content.Context
import androidx.annotation.VisibleForTesting
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

const val REDIRECT_URI = "myapp://callback"
const val CLIENT_ID = "5025edc6cd4b4e508839ae45296d1c82"
const val SPOTIFY_AUTH_PREFS = "spotify_auth"
const val SCOPES =
    "user-read-private user-read-email user-top-read user-modify-playback-state user-read-playback-state playlist-read-private playlist-modify-public ugc-image-upload"

/**
 * Repository to handle the Spotify authorization flow
 *
 * @param client OkHttpClient instance to make the requests
 */
open class SpotifyAuthRepository(private val client: OkHttpClient) : MusicServiceAuthRepository {

  /**
   * Refreshes the access token using the refresh token
   *
   * @param refreshToken The refresh token to use
   * @param context The application context
   * @return A Result object with the success or failure of the request
   */
  suspend fun refreshAccessToken(refreshToken: String, context: Context): Result<Unit> {
    val requestBody =
        FormBody.Builder()
            .add("grant_type", "refresh_token")
            .add("refresh_token", refreshToken)
            .add("client_id", CLIENT_ID)
            .build()

    val request =
        Request.Builder()
            .url("https://accounts.spotify.com/api/token")
            .post(requestBody)
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .build()

    return try {
      val response = withContext(Dispatchers.IO) { client.newCall(request).execute() }
      if (response.isSuccessful) {
        response.body?.let { responseBody ->
          val jsonResponse = JSONObject(responseBody.string())

          // Extract and store the access token and expiry time
          val accessToken = jsonResponse.getString("access_token")
          val expiresIn = jsonResponse.getInt("expires_in")
          val expiryTime = System.currentTimeMillis() + expiresIn * 1000

          setAccessToken(context, accessToken)
          setExpiryTime(context, expiryTime)

          // Optionally store a new refresh token if provided
          if (jsonResponse.has("refresh_token")) {
            val newRefreshToken = jsonResponse.getString("refresh_token")
            setRefreshToken(context, newRefreshToken)
          }

          Result.success(Unit)
        } ?: Result.failure(Exception("Response body is null"))
      } else {
        Result.failure(Exception("Failed to retrieve the Spotify access token"))
      }
    } catch (e: IOException) {
      Result.failure(e)
    }
  }

  /**
   * Requests the access token using the authorization code
   *
   * @param code The authorization code to use
   * @param context The application context
   * @return A Result object with the success or failure of the request
   */
  suspend fun requestAccessToken(
      code: String,
      context: Context,
  ): Result<Unit> {
    val codeVerifier =
        fetch(context, "code_verifier")
            ?: return Result.failure(Exception("Code verifier not found"))

    val requestBody =
        FormBody.Builder()
            .add("grant_type", "authorization_code")
            .add("code", code)
            .add("redirect_uri", REDIRECT_URI)
            .add("client_id", CLIENT_ID)
            .add("code_verifier", codeVerifier)
            .build()

    val request =
        Request.Builder()
            .url("https://accounts.spotify.com/api/token")
            .post(requestBody)
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .build()

    return try {
      val response = withContext(Dispatchers.IO) { client.newCall(request).execute() }
      if (response.isSuccessful) {
        response.body?.let { responseBody ->
          val jsonResponse = JSONObject(responseBody.string())

          // Extract and store token details
          val accessToken = jsonResponse.getString("access_token")
          val refreshToken = jsonResponse.getString("refresh_token")
          val expiresIn = jsonResponse.getInt("expires_in")

          val expiryTime = System.currentTimeMillis() + expiresIn * 1000

          // Store the new data in SharedPreferences
          setAccessToken(context, accessToken)
          setRefreshToken(context, refreshToken)
          setExpiryTime(context, expiryTime)

          Result.success(Unit)
        } ?: Result.failure(Exception("Response body is null"))
      } else {
        Result.failure(Exception("An error occurred retrieving the Spotify access token"))
      }
    } catch (e: IOException) {
      Result.failure(e)
    }
  }

  /**
   * Builds the Spotify authorization URL
   *
   * @param context The application context
   * @return The generated URL
   */
  fun buildSpotifyAuthUrl(context: Context): String {
    val codeVerifier = codeVerifier()
    store(context, "code_verifier", codeVerifier)

    val codeChallenge = createCodeChallenge(codeVerifier)

    return buildAuthUrlString(codeChallenge)
  }

  /**
   * Builds the Spotify authorization URL
   *
   * @param codeChallenge The code challenge to use
   * @return The generated URL
   */
  private fun buildAuthUrlString(codeChallenge: String): String {
    return "https://accounts.spotify.com/authorize?" +
        "response_type=code&" +
        "client_id=$CLIENT_ID&" +
        "scope=$SCOPES&" +
        "redirect_uri=$REDIRECT_URI&" +
        "code_challenge_method=S256&" +
        "code_challenge=$codeChallenge"
  }

  /**
   * Generates a random code verifier for the PKCE flow
   *
   * @return The generated code verifier
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
   *
   * @param plain The plain text to hash
   * @return The hashed byte array
   */
  private fun sha256(plain: String): ByteArray {
    val digest = MessageDigest.getInstance("SHA-256")
    return digest.digest(plain.toByteArray(StandardCharsets.UTF_8))
  }

  /**
   * Encodes a byte array to a base64 string
   *
   * @param input The byte array to encode
   * @return The base64 encoded string
   */
  private fun base64encode(input: ByteArray): String {
    return Base64.getUrlEncoder().withoutPadding().encodeToString(input)
  }

  /**
   * Creates a code challenge from a code verifier to be used in the PKCE flow
   *
   * @param codeVerifier The code verifier to use
   * @return The generated code challenge
   */
  private fun createCodeChallenge(codeVerifier: String): String {
    val bytes = sha256(codeVerifier)
    return base64encode(bytes)
  }

  /**
   * Stores the access token in SharedPreferences
   *
   * @param context The application context
   * @param accessToken The access token to store
   */
  private fun setAccessToken(context: Context, accessToken: String) {
    store(context, "access_token", accessToken)
  }

  /**
   * Stores the refresh token in SharedPreferences
   *
   * @param context The application context
   * @param refreshToken The refresh token to store
   */
  private fun setRefreshToken(context: Context, refreshToken: String) {
    store(context, "refresh_token", refreshToken)
  }

  /**
   * Stores the expiry time in SharedPreferences
   *
   * @param context The application context
   * @param expiryTime The expiry time to store
   */
  private fun setExpiryTime(context: Context, expiryTime: Long) {
    store(context, "expiry_time", expiryTime.toString())
  }

  /**
   * Retrieves the access token from SharedPreferences
   *
   * @param context The application context
   * @return The access token if found, null otherwise
   */
  fun getAccessToken(context: Context): String? {
    return fetch(context, "access_token")
  }

  /**
   * Retrieves the refresh token from SharedPreferences
   *
   * @param context The application context
   * @return The refresh token if found, null otherwise
   */
  fun getRefreshToken(context: Context): String? {
    return fetch(context, "refresh_token")
  }

  /**
   * Retrieves the expiry time from SharedPreferences
   *
   * @param context The application context
   * @return The expiry time if found, null otherwise
   */
  fun getExpiryTime(context: Context): Long? {
    val expiryTime = fetch(context, "expiry_time") ?: return null

    return expiryTime.toLong()
  }

  /**
   * Store a key-value pair in SharedPreferences
   *
   * @param context The application context
   * @param id The key to store
   * @param data The value to store
   */
  private fun store(context: Context, id: String, data: String) {
    val sharedPref = context.getSharedPreferences(SPOTIFY_AUTH_PREFS, Context.MODE_PRIVATE)
    with(sharedPref.edit()) {
      putString(id, data)
      apply()
    }
  }

  @VisibleForTesting
  /**
   * Fetch a value from SharedPreferences Returns an empty string if the value is not found
   *
   * @param context The application context
   * @param id The key to fetch
   * @return The value if found, an empty string otherwise
   */
  internal fun fetch(context: Context, id: String): String? {
    val sharedPref = context.getSharedPreferences(SPOTIFY_AUTH_PREFS, Context.MODE_PRIVATE)
    return sharedPref.getString(id, null)
  }

  /**
   * Clear all the data stored in SharedPreferences
   *
   * @param context The application context
   */
  fun clearAuthData(context: Context) {
    val sharedPref = context.getSharedPreferences(SPOTIFY_AUTH_PREFS, Context.MODE_PRIVATE)
    with(sharedPref.edit()) {
      clear()
      apply()
    }
  }
}
