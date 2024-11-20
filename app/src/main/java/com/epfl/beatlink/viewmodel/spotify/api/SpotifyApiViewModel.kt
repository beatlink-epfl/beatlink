package com.epfl.beatlink.viewmodel.spotify.api

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.epfl.beatlink.model.spotify.objects.SpotifyAlbum
import com.epfl.beatlink.model.spotify.objects.SpotifyArtist
import com.epfl.beatlink.model.spotify.objects.SpotifyTrack
import com.epfl.beatlink.model.spotify.objects.State
import com.epfl.beatlink.repository.spotify.api.SpotifyApiRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class SpotifyApiViewModel(
    application: Application,
    private val apiRepository: SpotifyApiRepository
) : AndroidViewModel(application) {

  var deviceId: String? = null
  var playbackActive = false

  fun searchArtistsAndTracks(
      query: String,
      onSuccess: (List<SpotifyArtist>, List<SpotifyTrack>) -> Unit,
      onFailure: (List<SpotifyArtist>, List<SpotifyTrack>) -> Unit
  ) {
    viewModelScope.launch {
      val result = apiRepository.get("search?q=$query&type=artist,track&market=CH&limit=20")
      if (result.isSuccess) {
        Log.d("SpotifyApiViewModel", "Artists and tracks fetched successfully")

        // Initialize lists to store artists and tracks
        val artists = mutableListOf<SpotifyArtist>()
        val tracks = mutableListOf<SpotifyTrack>()

        // Get the artists and tracks from the result
        val artistsResponse = result.getOrNull()!!.getJSONObject("artists").getJSONArray("items")
        val tracksResponse = result.getOrNull()!!.getJSONObject("tracks").getJSONArray("items")

        // Add artists to the list
        for (i in 0 until artistsResponse.length()) {
          val artist = artistsResponse.getJSONObject(i)

          val coverUrl =
              if (artist.getJSONArray("images").length() == 0) ""
              else artist.getJSONArray("images").getJSONObject(0).getString("url")

          val genres = mutableListOf<String>()
          val genresArray = artist.getJSONArray("genres")
          for (j in 0 until genresArray.length()) {
            genres.add(genresArray.getString(j))
          }
          val spotifyArtist =
              SpotifyArtist(
                  image = coverUrl,
                  name = artist.getString("name"),
                  genres = genres,
                  popularity = artist.getInt("popularity"))
          artists.add(spotifyArtist)
        }

        // Add tracks to the list
        for (i in 0 until tracksResponse.length()) {
          val track = tracksResponse.getJSONObject(i)
          val album = track.getJSONObject("album")
          val coverUrl = album.getJSONArray("images").getJSONObject(0).getString("url")
          val artist = track.getJSONArray("artists").getJSONObject(0).getString("name")
          val spotifyTrack =
              SpotifyTrack(
                  name = track.getString("name"),
                  artist = artist,
                  trackId = track.getString("id"),
                  cover = coverUrl,
                  duration = track.getInt("duration_ms"),
                  popularity = track.getInt("popularity"),
                  state = State.PAUSE)
          tracks.add(spotifyTrack)
        }

        // Return the lists
        onSuccess(artists, tracks)
      } else {
        Log.e("SpotifyApiViewModel", "Failed to search for artists and tracks")
        onFailure(emptyList(), emptyList())
      }
    }
  }

  fun getCurrentUserTopArtists(
      onSuccess: (List<SpotifyArtist>) -> Unit,
      onFailure: (List<SpotifyArtist>) -> Unit
  ) {
    viewModelScope.launch {
      val result = apiRepository.get("me/top/artists?time_range=short_term")
      if (result.isSuccess) {
        Log.d("SpotifyApiViewModel", "Top artists fetched successfully")
        val items = result.getOrNull()!!.getJSONArray("items")
        val artists = mutableListOf<SpotifyArtist>()
        for (i in 0 until items.length()) {
          val artist = items.getJSONObject(i)
          val coverUrl = artist.getJSONArray("images").getJSONObject(0).getString("url")
          val genres = mutableListOf<String>()
          val genresArray = artist.getJSONArray("genres")
          for (j in 0 until genresArray.length()) {
            genres.add(genresArray.getString(j))
          }
          val spotifyArtist =
              SpotifyArtist(
                  image = coverUrl,
                  name = artist.getString("name"),
                  genres = genres,
                  popularity = artist.getInt("popularity"))
          artists.add(spotifyArtist)
        }
        onSuccess(artists)
      } else {
        Log.e("SpotifyApiViewModel", "Failed to fetch top artists")
        onFailure(emptyList())
      }
    }
  }

  fun getCurrentUserTopTracks(
      onSuccess: (List<SpotifyTrack>) -> Unit,
      onFailure: (List<SpotifyTrack>) -> Unit
  ) {
    viewModelScope.launch {
      val result = apiRepository.get("me/top/tracks?time_range=short_term")
      if (result.isSuccess) {
        Log.d("SpotifyApiViewModel", "Top tracks fetched successfully")
        val items = result.getOrNull()!!.getJSONArray("items")
        val tracks = mutableListOf<SpotifyTrack>()
        for (i in 0 until items.length()) {
          val track = items.getJSONObject(i)
          val album = track.getJSONObject("album")
          val coverUrl = album.getJSONArray("images").getJSONObject(0).getString("url")
          val artist = track.getJSONArray("artists").getJSONObject(0).getString("name")
          val spotifyTrack =
              SpotifyTrack(
                  name = track.getString("name"),
                  artist = artist,
                  trackId = track.getString("id"),
                  cover = coverUrl,
                  duration = track.getInt("duration_ms"),
                  popularity = track.getInt("popularity"),
                  state = State.PAUSE)
          tracks.add(spotifyTrack)
        }
        onSuccess(tracks)
      } else {
        Log.e("SpotifyApiViewModel", "Failed to fetch top tracks")
        onFailure(emptyList())
      }
    }
  }

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
      if (playbackActive) {
        val result = apiRepository.put("me/player/pause")
        Log.d("SpotifyApiViewModel", "Playback paused")
        onResult(result)
      } else {
        Log.e("SpotifyApiViewModel", "Playback not active")
      }
    }
  }

  /**
   * Resumes the current playback.
   *
   * @param onResult Callback to handle the result.
   */
  fun playPlayback(onResult: (Result<JSONObject>) -> Unit) {
    viewModelScope.launch {
      if (playbackActive) {
        val result = apiRepository.put("me/player/play")
        Log.d("SpotifyApiViewModel", "Playback resumed")
        onResult(result)
      } else {
        Log.e("SpotifyApiViewModel", "Playback not active")
      }
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
      if (result.isSuccess) {
        playbackActive = true
        onResult(result)
      } else {
        playbackActive = false
      }
    }
  }

  /**
   * Skips to the next song.
   *
   * @param onResult Callback to handle the result.
   */
  fun skipSong(onResult: (Result<JSONObject>) -> Unit) {
    viewModelScope.launch {
      if (playbackActive) {
        val result = apiRepository.post("me/player/next", "".toRequestBody())
        Log.d("SpotifyApiViewModel", "Song skipped")
        onResult(result)
      } else {
        Log.e("SpotifyApiViewModel", "Playback not active")
      }
    }
  }

  /**
   * Plays the previous song.
   *
   * @param onResult Callback to handle the result.
   */
  fun previousSong(onResult: (Result<JSONObject>) -> Unit) {
    viewModelScope.launch {
      if (playbackActive) {
        val result = apiRepository.post("me/player/previous", "".toRequestBody())
        Log.d("SpotifyApiViewModel", "Previous song played")
        onResult(result)
      } else {
        Log.e("SpotifyApiViewModel", "Playback not active")
      }
    }
  }

  /**
   * Builds a SpotifyAlbum object from a JSON object.
   *
   * @return The constructed SpotifyAlbum object.
   */
  fun buildAlbum(onResult: (SpotifyAlbum) -> Unit) {
    var retAlbum = SpotifyAlbum("", "", "", "", 0, listOf(), 0, listOf(), 0)
    viewModelScope.launch {
      if (playbackActive) {
        val result = apiRepository.get("me/player/currently-playing")
        if (result.isSuccess) {
          val item = result.getOrNull()?.getJSONObject("item") ?: return@launch
          val album = item.getJSONObject("album")
          retAlbum =
              SpotifyAlbum(
                  album.getString("id"),
                  album.getString("name"),
                  "",
                  album.getJSONArray("artists").getJSONObject(0).getString("name"),
                  album.getString("release_date").substring(0, 4).toInt(),
                  listOf(),
                  album.getInt("total_tracks"),
                  listOf(),
                  0)
        }
      }
      onResult(retAlbum)
    }
  }

  /**
   * Builds a SpotifyTrack object from a JSON object.
   *
   * @return The constructed SpotifyTrack object.
   */
  fun buildTrack(onResult: (SpotifyTrack) -> Unit) {
    var retTrack = SpotifyTrack("", "", "", "", 0, 0, State.PAUSE)
    viewModelScope.launch {
      if (playbackActive) {
        val result = apiRepository.get("me/player/currently-playing")
        if (result.isSuccess) {
          val isPlaying = result.getOrNull()?.getBoolean("is_playing")
          val item = result.getOrNull()?.getJSONObject("item") ?: return@launch
          val artist = item.getJSONArray("artists").getJSONObject(0).getString("name")
          retTrack =
              SpotifyTrack(
                  item.getString("name"),
                  artist,
                  item.getString("id"),
                  "",
                  item.getInt("duration_ms"),
                  item.getInt("popularity"),
                  if (isPlaying == true) State.PLAY else State.PAUSE)
        }
      }
      onResult(retTrack)
    }
  }

  /**
   * Builds a SpotifyArtist object from a JSON object.
   *
   * @return The constructed SpotifyArtist object.
   */
  fun buildArtist(onResult: (SpotifyArtist) -> Unit) {
    var retArtist = SpotifyArtist("", "", listOf(), 0)
    viewModelScope.launch {
      if (playbackActive) {
        val result = apiRepository.get("me/player/currently-playing")
        if (result.isSuccess) {
          val item = result.getOrNull()?.getJSONObject("item") ?: return@launch
          val artists = item.getJSONArray("artists")
          val artist = artists.getJSONObject(0)
          retArtist = SpotifyArtist("", artist.getString("name"), listOf(), 0)
        }
      }
      onResult(retArtist)
    }
  }
}
