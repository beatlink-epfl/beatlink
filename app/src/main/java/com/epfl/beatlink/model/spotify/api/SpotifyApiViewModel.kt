package com.epfl.beatlink.model.spotify.api

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.epfl.beatlink.model.spotify.objects.SpotifyAlbum
import com.epfl.beatlink.model.spotify.objects.SpotifyArtist
import com.epfl.beatlink.model.spotify.objects.SpotifyTrack
import com.epfl.beatlink.model.spotify.objects.State
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class SpotifyApiViewModel(
    application: Application,
    private val apiRepository: SpotifyApiRepository
) : AndroidViewModel(application) {

  var deviceId: String? = null

  /** Fetches the device ID of the current active device. */
  fun getDeviceId() {
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
          delay(1000)

          if (deviceId != null && !isActive) transferPlayback()
          else if (deviceId == null) {
            Log.e("SpotifyApiViewModel", "No device found")
          }
        }
      } else {
        Log.e("SpotifyApiViewModel", "Failed to fetch devices")
        deviceId = null
        delay(1000)
      }
    }
  }

  /** Transfers playback to the current device. */
  fun transferPlayback() {
    viewModelScope.launch {
      val requestBody =
          "{\"device_ids\":[\"${deviceId.toString()}\"]}"
              .toRequestBody() // the json format spotify is using isn't a valid json format
      apiRepository.put("me/player", requestBody)
      Log.d("SpotifyApiViewModel", "Playback transferred successfully")
    }
  }

  /**
   * Fetches the current user's profile.
   *
   * @param onResult Callback to handle the result.
   */
  fun fetchCurrentUserProfile(onResult: (Result<JSONObject>) -> Unit) {
    viewModelScope.launch {
      val result = apiRepository.get("me")
      if (result.isSuccess) Log.d("SpotifyApiViewModel", "User profile fetched successfully")
      else Log.e("SpotifyApiViewModel", "Failed to fetch user profile")
      onResult(result)
    }
  }

  /**
   * Pauses the current playback.
   *
   * @param onResult Callback to handle the result.
   */
  fun pausePlayback(onResult: (Result<JSONObject>) -> Unit) {
    viewModelScope.launch {
      val result = apiRepository.put("me/player/pause")
      if (result.isSuccess) Log.d("SpotifyApiViewModel", "Playback paused")
      else Log.e("SpotifyApiViewModel", "Failed to pause playback")
      onResult(result)
    }
  }

  /**
   * Resumes the current playback.
   *
   * @param onResult Callback to handle the result.
   */
  fun playPlayback(onResult: (Result<JSONObject>) -> Unit) {
    viewModelScope.launch {
      val result = apiRepository.put("me/player/play")
      Log.d("SpotifyApiViewModel", "Playback resumed")
      onResult(result)
    }
  }

  /**
   * Fetches the current playback state.
   *
   * @param onResult Callback to handle the result.
   */
  fun getPlaybackState(onResult: (Result<JSONObject>) -> Unit) {
    if (deviceId == null) {
      getDeviceId()
    }
    viewModelScope.launch {
      val result = apiRepository.get("me/player")
      Log.d("SpotifyApiViewModel", "Playback state fetched")
      onResult(result)
    }
  }

  /**
   * Skips to the next song.
   *
   * @param onResult Callback to handle the result.
   */
  fun skipSong(onResult: (Result<JSONObject>) -> Unit) {
    viewModelScope.launch {
      val result = apiRepository.post("me/player/next", "".toRequestBody())
      Log.d("SpotifyApiViewModel", "Song skipped")
      onResult(result)
    }
  }

  /**
   * Plays the previous song.
   *
   * @param onResult Callback to handle the result.
   */
  fun previousSong(onResult: (Result<JSONObject>) -> Unit) {
    viewModelScope.launch {
      val result = apiRepository.post("me/player/previous", "".toRequestBody())
      if (result.isSuccess) Log.d("SpotifyApiViewModel", "Previous song played")
      else Log.e("SpotifyApiViewModel", "Failed to play previous song")
      onResult(result)
    }
  }

  /**
   * Builds a SpotifyAlbum object from a JSON object.
   *
   * @param json The JSON object containing the item.
   * @return The constructed SpotifyAlbum object.
   */
  fun buildAlbum(json: JSONObject): SpotifyAlbum {
    val album = json.get("album") as JSONObject
    val retAlbum =
        SpotifyAlbum(
            album.getString("id"),
            album.getString("name"),
            "",
            album.getJSONArray("artists").getJSONObject(0).getString("name"),
            2023, // album.getInt("release_date").toString().substring(0, 4)
            listOf(),
            album.getInt("total_tracks"),
            listOf(),
            json.getInt("popularity"))
    return retAlbum
  }

  /**
   * Builds a SpotifyTrack object from a JSON object.
   *
   * @param json The JSON object containing the item.
   * @return The constructed SpotifyTrack object.
   */
  fun buildTrack(json: JSONObject): SpotifyTrack {
    val track =
        SpotifyTrack(
            json.getString("name"),
            json.getString("id"),
            "",
            json.getInt("duration_ms"),
            json.getInt("popularity"),
            State.PAUSE)
    return track
  }

  /**
   * Builds a SpotifyArtist object from a JSON object.
   *
   * @param json The JSON object containing the item.
   * @return The constructed SpotifyArtist object.
   */
  fun buildArtist(json: JSONObject): SpotifyArtist {
    val artist = json.getJSONArray("artists").getJSONObject(0)
    val retArtist =
        SpotifyArtist(
            artist.getString("href"), artist.getString("name"), listOf(), json.getInt("popularity"))
    return retArtist
  }
}
