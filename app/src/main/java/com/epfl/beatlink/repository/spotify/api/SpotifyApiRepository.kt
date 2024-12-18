package com.epfl.beatlink.repository.spotify.api

import android.content.SharedPreferences
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

open class SpotifyApiRepository(
    private val client: OkHttpClient,
    private val sharedPreferences: SharedPreferences
) : ApiRepository {
  // Function to get the token from SharedPreferences
  override fun getToken(): String? = sharedPreferences.getString("access_token", null)

  // Main generic function to configure and execute the request
  private suspend fun makeRequest(
      endpoint: String,
      requestConfig: (Request.Builder) -> Unit
  ): Result<JSONObject> {
    val token = getToken() ?: return Result.failure(Exception("Token not found"))
    val requestBuilder =
        Request.Builder()
            .url("https://api.spotify.com/v1/$endpoint")
            .addHeader("Authorization", "Bearer $token")

    if (endpoint.contains("playlists") && endpoint.contains("images")) {
      requestBuilder.addHeader("Content-Type", "image/jpeg")
    }

    // Apply the specific HTTP method configuration (GET, POST, etc.)
    requestConfig(requestBuilder)

    val request = requestBuilder.build()

    return try {
      val response = withContext(Dispatchers.IO) { client.newCall(request).execute() }
      Log.d("SpotifyApiRepository", "API call to $endpoint returned code ${response.code}")
      if (response.isSuccessful) {
        if (response.body == null) {
          Log.d("SpotifyApiRepository", "Empty response body")
          return Result.success(JSONObject())
        } else {
          val contentLength = response.body!!.contentLength()
          if (contentLength > 0 || contentLength == -1L) {
            Result.success(JSONObject(response.body!!.string()))
          } else {
            Log.d("SpotifyApiRepository", "Empty response body")
            return Result.success(JSONObject())
          }
        }
      } else {
        Log.e("SpotifyApiRepository", "API call failed with code ${response.code}")
        Log.e("SpotifyApiRepository", "Response: ${response.body?.string()}")
        Result.failure(Exception("API call failed with code ${response.code}"))
      }
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  // GET request function
  override suspend fun get(endpoint: String): Result<JSONObject> {
    return makeRequest(endpoint) { builder -> builder.get() }
  }

  // POST request function
  override suspend fun post(endpoint: String, body: RequestBody): Result<JSONObject> {
    return makeRequest(endpoint) { builder -> builder.post(body) }
  }

  // PUT request function
  override suspend fun put(endpoint: String, body: RequestBody?): Result<JSONObject> {
    return makeRequest(endpoint) { builder ->
      if (body != null) builder.put(body) else builder.put("".toRequestBody())
    }
  }

  // DELETE request function
  override suspend fun delete(endpoint: String, body: RequestBody?): Result<JSONObject> {
    return makeRequest(endpoint) { builder ->
      if (body != null) builder.delete(body) else builder.delete()
    }
  }
}
