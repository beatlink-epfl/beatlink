package com.epfl.beatlink.model.spotify.api

import okhttp3.RequestBody
import org.json.JSONObject

interface ApiRepository {
  fun getToken(): String?

  suspend fun get(endpoint: String): Result<JSONObject>

  suspend fun post(endpoint: String, body: RequestBody): Result<JSONObject>

  suspend fun put(endpoint: String, body: RequestBody? = null): Result<JSONObject>

  suspend fun delete(endpoint: String, body: RequestBody? = null): Result<JSONObject>
}
