package com.epfl.beatlink.model.spotify

import android.content.SharedPreferences
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import org.json.JSONObject

class SpotifyApiRepository(
	private val client: OkHttpClient,
	private val sharedPreferences: SharedPreferences
): ApiRepository {
	// Function to get the token from SharedPreferences
	override fun getToken(): String? = sharedPreferences.getString("access_token", null)

	// Main generic function to configure and execute the request
	private suspend fun makeRequest(
		endpoint: String,
		requestConfig: (Request.Builder) -> Unit
	): Result<JSONObject> {
		val token = getToken() ?: return Result.failure(Exception("Token not found"))
		Log.d("SpotifyApiRepository", "Token: $token")

		val requestBuilder = Request.Builder()
			.url("https://api.spotify.com/v1/$endpoint")
			.addHeader("Authorization", "Bearer $token")

		// Apply the specific HTTP method configuration (GET, POST, etc.)
		requestConfig(requestBuilder)

		val request = requestBuilder.build()

		return try {
			val response = withContext(Dispatchers.IO) { client.newCall(request).execute() }
			if (response.isSuccessful) {
				response.body?.let { Result.success(JSONObject(it.string())) }
					?: Result.failure(Exception("Empty response body"))
			} else {
				Log.e("com.epfl.beatlink.model.spotify.SpotifyApiRepository", "API call failed with code ${response.code}")
				Log.e("com.epfl.beatlink.model.spotify.SpotifyApiRepository", "Response: ${response.body?.string()}")
				Result.failure(Exception("API call failed with code ${response.code}"))
			}
		} catch (e: Exception) {
			Result.failure(e)
		}
	}

	// GET request function
	override suspend fun get(endpoint: String): Result<JSONObject> {
		return makeRequest(endpoint) { builder ->
			builder.get()
		}
	}

	// POST request function
	override suspend fun post(endpoint: String, body: RequestBody): Result<JSONObject> {
		return makeRequest(endpoint) { builder ->
			builder.post(body)
		}
	}

	// PUT request function
	override suspend fun put(endpoint: String, body: RequestBody?): Result<JSONObject> {
		return makeRequest(endpoint) { builder ->
			if (body != null) builder.put(body)
			else builder.put(RequestBody.create(null, ""))
		}
	}

	// DELETE request function
	override suspend fun delete(endpoint: String, body: RequestBody?): Result<JSONObject> {
		return makeRequest(endpoint) { builder ->
			if (body != null) builder.delete(body)
			else builder.delete()
		}
	}
}