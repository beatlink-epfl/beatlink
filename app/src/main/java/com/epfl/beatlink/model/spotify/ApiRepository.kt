package com.epfl.beatlink.model.spotify

import org.json.JSONObject

interface ApiRepository {
	fun getToken(): String?
	suspend fun getCall(endpoint: String): Result<JSONObject>
}