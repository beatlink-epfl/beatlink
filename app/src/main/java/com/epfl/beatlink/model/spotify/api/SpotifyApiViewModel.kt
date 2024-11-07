package com.epfl.beatlink.model.spotify.api

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class SpotifyApiViewModel(
    application: Application,
    private val apiRepository: SpotifyApiRepository
) : AndroidViewModel(application) {

  private var deviceId: String? = null

  private fun getDeviceId() {
    viewModelScope.launch {
      val result = apiRepository.get("me/player/devices")
      if (result.isSuccess) {
        result.getOrNull()?.let { devices ->
          val devicesArray = devices.getJSONArray("devices")

          // Initialize a variable to store the selected device id
          var selectedDeviceId: String? = null
          var isActive = false

          // Loop through the devices to find a "Smartphone"
          for (i in 0 until devicesArray.length()) {
            val device = devicesArray.getJSONObject(i)
            if (device.getString("type") == "Smartphone") {
              selectedDeviceId = device.getString("id")
              isActive = device.getBoolean("is_active")
              break // Stop loop once a smartphone is found
            }
          }

          if (selectedDeviceId == null && devicesArray.length() > 0) {
            selectedDeviceId = devicesArray.getJSONObject(0).getString("id")
            isActive = devicesArray.getJSONObject(0).getBoolean("is_active")
          }

          deviceId = selectedDeviceId

          if (deviceId != null && !isActive) transferPlayback()
          else if (deviceId == null) {
            Log.e("SpotifyApiViewModel", "No device found")
          }
        }
      } else {
        Log.e("SpotifyApiViewModel", "Failed to fetch devices")
        deviceId = null
      }
    }
  }

  private fun transferPlayback() {
    viewModelScope.launch {
      val requestBody =
          "{\"device_ids\":[\"${deviceId.toString()}\"]}"
              .toRequestBody() // the json format spotify is using isn't a valid json format
      apiRepository.put("me/player", requestBody)
      Log.d("SpotifyApiViewModel", "Playback transferred successfully")
    }
  }

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
      Log.d("SpotifyApiViewModel", "Playback paused")
      onResult(result)
    }
  }

  fun playPlayback(onResult: (Result<JSONObject>) -> Unit) {
    viewModelScope.launch {
      val result = apiRepository.put("me/player/play")
      Log.d("SpotifyApiViewModel", "Playback resumed")
      onResult(result)
    }
  }

  fun getPlaybackState(onResult: (Result<JSONObject>) -> Unit) {
    if (deviceId == null) getDeviceId()
    viewModelScope.launch {
      val result = apiRepository.get("me/player")
      Log.d("SpotifyApiViewModel", "Playback state fetched")
      onResult(result)
    }
  }

  fun skipSong(onResult: (Result<JSONObject>) -> Unit) {
    viewModelScope.launch {
      val result = apiRepository.post("me/player/next", "".toRequestBody())
      Log.d("SpotifyApiViewModel", "Song skipped")
      onResult(result)
    }
  }

  fun previousSong(onResult: (Result<JSONObject>) -> Unit) {
    viewModelScope.launch {
      val result = apiRepository.post("me/player/previous", "".toRequestBody())
      Log.d("SpotifyApiViewModel", "Previous song play")
      onResult(result)
    }
  }
}
