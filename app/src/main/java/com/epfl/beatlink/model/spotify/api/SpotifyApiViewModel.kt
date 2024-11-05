package com.epfl.beatlink.model.spotify.api

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

  fun fetchCurrentUserProfile(onResult: (Result<JSONObject>) -> Unit) {
    viewModelScope.launch {
      val result = apiRepository.get("me")
      if (result.isSuccess) Log.d("SpotifyApiViewModel", "User profile fetched successfully")
      else Log.e("SpotifyApiViewModel", "Failed to fetch user profile")
      onResult(result)
    }
  }

  fun pausePlayback(onResult: (Result<JSONObject>) -> Unit) {
    viewModelScope.launch {
      val result = apiRepository.put("me/player/pause")
      if (result.isSuccess) Log.d("SpotifyApiViewModel", "Playback paused successfully")
      else Log.e("SpotifyApiViewModel", "Failed to pause playback")
      onResult(result)
    }
  }
}
