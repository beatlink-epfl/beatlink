package com.android.sample.model.spotify

import SpotifyApiRepository
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.json.JSONObject

class SpotifyApiViewModel(
	application: Application,
	private val apiRepository: SpotifyApiRepository
) : AndroidViewModel(application) {

	fun fetchUserProfile(onResult: (Result<JSONObject>) -> Unit) {
		viewModelScope.launch {
			val result = apiRepository.get("me/top/artists")
			val value = parseItemNames(result.getOrNull().toString())
			Log.d("SpotifyApiViewModel", "Result: $result")
			Log.d("SpotifyApiViewModel", "Result: $value")
			onResult(result)
		}
	}

	fun parseItemNames(response: String): List<String> {
		val jsonResponse = JSONObject(response)
		val itemsArray = jsonResponse.getJSONArray("items")

		val namesList = mutableListOf<String>()
		for (i in 0 until itemsArray.length()) {
			val item = itemsArray.getJSONObject(i)
			val name = item.getString("name")
			namesList.add(name)
		}
		return namesList
	}
}