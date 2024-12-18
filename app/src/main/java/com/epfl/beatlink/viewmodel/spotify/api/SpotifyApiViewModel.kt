package com.epfl.beatlink.viewmodel.spotify.api

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.epfl.beatlink.model.library.UserPlaylist
import com.epfl.beatlink.model.spotify.objects.SpotifyAlbum
import com.epfl.beatlink.model.spotify.objects.SpotifyArtist
import com.epfl.beatlink.model.spotify.objects.SpotifyTrack
import com.epfl.beatlink.model.spotify.objects.State
import com.epfl.beatlink.repository.spotify.api.SpotifyApiRepository
import kotlinx.coroutines.launch
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

open class SpotifyApiViewModel(
    application: Application,
    private val apiRepository: SpotifyApiRepository
) : AndroidViewModel(application) {

  companion object {
    const val PLAY_ENDPOINT = "me/player/play"
    const val PAUSE_ENDPOINT = "me/player/pause"
    const val NEXT_ENDPOINT = "me/player/next"
    const val PREVIOUS_ENDPOINT = "me/player/previous"
    const val PLAYER_ENDPOINT = "me/player"
  }

  var playbackActive by mutableStateOf(false)
  var isPlaying by mutableStateOf(false)
  var currentTrack by mutableStateOf(SpotifyTrack("", "", "", "", 0, 0, State.PAUSE))
  var currentAlbum by mutableStateOf(SpotifyAlbum("", "", "", "", 0, listOf(), 0, listOf(), 0))
  var currentArtist by mutableStateOf(SpotifyArtist("", "", listOf(), 0))
  var queue = mutableStateListOf<SpotifyTrack>()

  /** Plays a playlist. */
  fun playPlaylist(playlist: UserPlaylist) {
    viewModelScope.launch {
      val body =
          JSONObject().apply { put("context_uri", "spotify:playlist:${playlist.playlistID}") }
      val result = apiRepository.put(PLAY_ENDPOINT, body.toString().toRequestBody())
      if (result.isSuccess) {
        Log.d("SpotifyApiViewModel", "Playlist played successfully")
        updatePlayer()
      } else {
        Log.e("SpotifyApiViewModel", "Failed to play playlist")
      }
    }
  }

  /** Plays a track alone, that is without context */
  fun playTrackAlone(track: SpotifyTrack) {
    viewModelScope.launch {
      val body =
          JSONObject().apply { put("uris", JSONArray(listOf("spotify:track:${track.trackId}"))) }
      val result = apiRepository.put(PLAY_ENDPOINT, body.toString().toRequestBody())
      if (result.isSuccess) {
        Log.d("SpotifyApiViewModel", "Track played successfully")
        updatePlayer()
      } else {
        Log.e("SpotifyApiViewModel", "Failed to play track")
      }
    }
  }

  /** Add custom playlist cover image which is a Base64-encoded JPEG string */
  fun addCustomPlaylistCoverImage(playlistID: String, image: String) {
    viewModelScope.launch {
      val result = apiRepository.put("playlists/$playlistID/images", image.toRequestBody())
      if (result.isSuccess) {
        Log.d("SpotifyApiViewModel", "Custom playlist cover image added successfully")
      } else {
        Log.e("SpotifyApiViewModel", "Failed to add custom playlist cover image")
      }
    }
  }

  /** Creates a playlist with the given name and description and adds the given tracks to it. */
  open fun createBeatLinkPlaylist(
      playlistName: String,
      playlistDescription: String = "",
      tracks: List<SpotifyTrack>,
      onResult: (String?) -> Unit
  ) {
    viewModelScope.launch {
      var playlistId: String?
      createEmptySpotifyPlaylist(playlistName, playlistDescription) { id ->
        playlistId = id
        addTracksToPlaylist(id, tracks)
        onResult(playlistId)
      }
    }
  }

  /** Adds tracks to a Spotify playlist. */
  private fun addTracksToPlaylist(playlistID: String, tracks: List<SpotifyTrack>) {
    val uris = tracks.map { "spotify:track:${it.trackId}" }
    val body = JSONObject().apply { put("uris", JSONArray(uris)) }

    viewModelScope.launch {
      val result =
          apiRepository.post("playlists/$playlistID/tracks", body.toString().toRequestBody())
      if (result.isSuccess) {
        Log.d("SpotifyApiViewModel", "Track added to playlist successfully")
      } else {
        Log.e("SpotifyApiViewModel", "Failed to add track to playlist")
      }
    }
  }

  /** Creates an empty Spotify playlist. */
  private fun createEmptySpotifyPlaylist(
      playlistName: String,
      playlistDescription: String = "",
      onSuccess: (String) -> Unit
  ) {
    getCurrentUserId(
        onSuccess = { userId ->
          val body = JSONObject()
          body.put("name", playlistName)
          body.put("description", playlistDescription)

          viewModelScope.launch {
            val result =
                apiRepository.post("users/$userId/playlists", body.toString().toRequestBody())
            if (result.isSuccess) {
              Log.d("SpotifyApiViewModel", "Empty playlist created successfully")
              val playlistId = result.getOrNull()!!.getString("id")
              onSuccess(playlistId)
            } else {
              Log.e("SpotifyApiViewModel", "Failed to create empty playlist")
            }
          }
        },
        onFailure = {
          Log.e("SpotifyApiViewModel", "Failed to create empty playlist: could not fetch user ID")
        })
  }

  /** Gets the current user's ID. */
  fun getCurrentUserId(onSuccess: (String) -> Unit, onFailure: () -> Unit) {
    viewModelScope.launch {
      val result = apiRepository.get("me")
      if (result.isSuccess) {
        Log.d("SpotifyApiViewModel", "User ID fetched successfully")
        val id = result.getOrNull()!!.getString("id")
        onSuccess(id)
      } else {
        Log.e("SpotifyApiViewModel", "Failed to fetch user ID")
        onFailure()
      }
    }
  }

  /** Fetches the tracks of a Spotify playlist. */
  fun getPlaylistTracks(
      playlistID: String,
      onSuccess: (List<SpotifyTrack>) -> Unit,
      onFailure: (List<SpotifyTrack>) -> Unit
  ) {
    viewModelScope.launch {
      val result = apiRepository.get("playlists/$playlistID/tracks?limit=50")
      if (result.isSuccess) {
        Log.d("SpotifyApiViewModel", "Playlist tracks fetched successfully")
        val items = result.getOrNull()!!.getJSONArray("items")
        val tracks = mutableListOf<SpotifyTrack>()
        for (i in 0 until items.length()) {
          val track = items.getJSONObject(i).getJSONObject("track")
          val spotifyTrack = createSpotifyTrack(track)
          tracks.add(spotifyTrack)
        }
        onSuccess(tracks)
      } else {
        Log.e("SpotifyApiViewModel", "Failed to fetch playlist tracks")
        onFailure(emptyList())
      }
    }
  }

  /** Searches for artists and tracks based on a query. */
  open fun searchArtistsAndTracks(
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
          val spotifyArtist = createSpotifyArtist(artist)
          artists.add(spotifyArtist)
        }

        // Add tracks to the list
        for (i in 0 until tracksResponse.length()) {
          val track = tracksResponse.getJSONObject(i)
          val spotifyTrack = createSpotifyTrack(track)
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

  /** Fetches the current user's top 20 artists from the last 4 weeks. */
  open fun getCurrentUserTopArtists(
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
          val spotifyArtist = createSpotifyArtist(artist)
          artists.add(spotifyArtist)
        }
        onSuccess(artists)
      } else {
        Log.e("SpotifyApiViewModel", "Failed to fetch top artists")
        onFailure(emptyList())
      }
    }
  }

  /** Fetches the current user's top 20 tracks from the last 4 weeks. */
  open fun getCurrentUserTopTracks(
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
          val spotifyTrack = createSpotifyTrack(track)
          tracks.add(spotifyTrack)
        }
        onSuccess(tracks)
      } else {
        Log.e("SpotifyApiViewModel", "Failed to fetch top tracks")
        onFailure(emptyList())
      }
    }
  }

  /** Pauses the current playback. */
  fun pausePlayback() {
    viewModelScope.launch {
      if (isPlaying) {
        apiRepository.put(PAUSE_ENDPOINT)
        Log.d("SpotifyApiViewModel", "Playback paused")
        isPlaying = false
      } else {
        Log.e("SpotifyApiViewModel", "Playback not active, pause failed")
      }
    }
  }

  /** Resumes the current playback. */
  fun playPlayback() {
    viewModelScope.launch {
      if (!isPlaying) {
        apiRepository.put(PLAY_ENDPOINT)
        Log.d("SpotifyApiViewModel", "Playback resumed")
        isPlaying = true
      } else {
        Log.e("SpotifyApiViewModel", "Playback not active, play failed")
      }
    }
  }

  /** Fetches the current playback state. */
  private fun getPlaybackState(onSuccess: (JSONObject) -> Unit, onFailure: () -> Unit) {
    viewModelScope.launch {
      val result = apiRepository.get(PLAYER_ENDPOINT)
      if (result.isSuccess) {
        if (result.getOrNull()!!.has("is_playing")) {
          onSuccess(result.getOrNull()!!)
        } else {
          onFailure()
        }
      } else {
        onFailure()
      }
      Log.d("SpotifyApiViewModel", "Playback state fetched")
    }
  }

  /** Skips to the next song. */
  fun skipSong() {
    viewModelScope.launch {
      if (playbackActive) {
        apiRepository.post(NEXT_ENDPOINT, "".toRequestBody())
        Log.d("SpotifyApiViewModel", "Song skipped")
        updatePlayer()
      } else {
        Log.e("SpotifyApiViewModel", "Playback not active")
      }
    }
  }

  /** Plays the previous song. */
  fun previousSong() {
    viewModelScope.launch {
      if (playbackActive) {
        apiRepository.post(PREVIOUS_ENDPOINT, "".toRequestBody())
        Log.d("SpotifyApiViewModel", "Previous song played")
        updatePlayer()
      } else {
        Log.e("SpotifyApiViewModel", "Playback not active")
      }
    }
  }

  /** Updates the player state. */
  open fun updatePlayer() {
    getPlaybackState(
        onSuccess = {
          playbackActive = true
          viewModelScope.launch {
            val result = apiRepository.get("me/player/currently-playing")
            if (result.isSuccess) {
              val json = result.getOrNull() ?: return@launch
              currentTrack = buildTrack(json)
              currentAlbum = buildAlbum(json)
              currentArtist = buildArtist(json)
              isPlaying = currentTrack.state == State.PLAY
            }
          }
        },
        onFailure = {
          Log.d("SpotifyApiViewModel", "There's no playback state")
          playbackActive = false
        })
  }

  /**
   * Builds a SpotifyAlbum object from a JSON object.
   *
   * @return The constructed SpotifyAlbum object.
   */
  fun buildAlbum(json: JSONObject): SpotifyAlbum {
    val retAlbum: SpotifyAlbum
    val item = json.getJSONObject("item")
    val album = item.getJSONObject("album")
    retAlbum =
        SpotifyAlbum(
            album.getString("id"),
            album.getString("name"),
            album.getJSONArray("images").getJSONObject(0).getString("url"),
            album.getJSONArray("artists").getJSONObject(0).getString("name"),
            album.getString("release_date").substring(0, 4).toInt(),
            listOf(),
            album.getInt("total_tracks"),
            listOf(),
            0)

    return retAlbum
  }

  /**
   * Builds a SpotifyTrack object from a JSON object.
   *
   * @return The constructed SpotifyTrack object.
   */
  fun buildTrack(json: JSONObject): SpotifyTrack {
    val retTrack: SpotifyTrack
    val isPlaying = json.getBoolean("is_playing")
    val item = json.getJSONObject("item")
    val artist = item.getJSONArray("artists").getJSONObject(0).getString("name")
    val image = item.getJSONObject("album").getJSONArray("images").getJSONObject(0)
    retTrack =
        SpotifyTrack(
            item.getString("name"),
            artist,
            item.getString("id"),
            image.getString("url"),
            item.getInt("duration_ms"),
            item.getInt("popularity"),
            if (isPlaying) State.PLAY else State.PAUSE)
    return retTrack
  }

  /**
   * Builds a SpotifyArtist object from a JSON object.
   *
   * @return The constructed SpotifyArtist object.
   */
  fun buildArtist(json: JSONObject): SpotifyArtist {
    val retArtist: SpotifyArtist
    val item = json.getJSONObject("item")
    val artists = item.getJSONArray("artists")
    val artist = artists.getJSONObject(0)
    retArtist = SpotifyArtist("", artist.getString("name"), listOf(), 0)
    return retArtist
  }

  /** Fetches the current user's playback queue and builds a sub-list of 5 elements from it */
  fun buildQueue() {
    viewModelScope.launch {
      val queueResponse = apiRepository.get("me/player/queue")
      if (queueResponse.isSuccess) {
        val queueOrNull = queueResponse.getOrNull() ?: return@launch
        val queueJson = queueOrNull.getJSONArray("queue")
        val top = minOf(5, queueJson.length())

        // Map JSON objects to SpotifyTrack and update the mutable state list in-place
        val newQueue = List(top) { i -> createSpotifyTrack(queueJson.getJSONObject(i)) }

        // Instead of replacing the entire list, update the state list in-place:
        queue.clear() // Clear the existing list
        queue.addAll(newQueue) // Add new tracks to the list
      }
    }
  }

  /** Creates a SpotifyTrack object from a JSON object. */
  private fun createSpotifyTrack(track: JSONObject): SpotifyTrack {
    val artist = track.getJSONArray("artists").getJSONObject(0)
    val album = track.getJSONObject("album")

    // Get cover URL from album images
    val coverUrl =
        if (album.getJSONArray("images").length() == 0) ""
        else album.getJSONArray("images").getJSONObject(0).getString("url")

    return SpotifyTrack(
        name = track.getString("name"),
        artist = artist.getString("name"),
        trackId = track.getString("id"),
        cover = coverUrl,
        duration = track.getInt("duration_ms"),
        popularity = track.getInt("popularity"),
        state = State.PAUSE)
  }

  /** Creates a SpotifyArtist object from a JSON object. */
  private fun createSpotifyArtist(artist: JSONObject): SpotifyArtist {
    val coverUrl =
        if (artist.getJSONArray("images").length() == 0) ""
        else artist.getJSONArray("images").getJSONObject(0).getString("url")
    val genres = mutableListOf<String>()
    val genresArray = artist.getJSONArray("genres")
    for (j in 0 until genresArray.length()) {
      genres.add(genresArray.getString(j))
    }
    return SpotifyArtist(
        image = coverUrl,
        name = artist.getString("name"),
        genres = genres,
        popularity = artist.getInt("popularity"))
  }

  /**
   * Creates a UserPlaylist object from a JSON object.
   *
   * @param playlist The JSON object representing the playlist.
   * @return The constructed UserPlaylist object.
   */
  private fun createUserPlaylist(playlist: JSONObject): UserPlaylist {
    val name = playlist.getString("name")
    val id = playlist.getString("id")
    val owner = playlist.getJSONObject("owner").getString("id")
    val public = playlist.getBoolean("public")
    val imagesArray = playlist.optJSONArray("images")
    val coverUrl =
        if (imagesArray != null && imagesArray.length() > 0)
            imagesArray.getJSONObject(0).getString("url")
        else ""
    val nbTracks = playlist.getJSONObject("tracks").getInt("total")
    val tracks = listOf<SpotifyTrack>()

    return UserPlaylist(
        playlistID = id,
        ownerID = owner,
        playlistCover = coverUrl,
        playlistName = name,
        playlistPublic = public,
        playlistTracks = tracks,
        nbTracks = nbTracks)
  }

  /**
   * Fetches the current user's Spotify playlists.
   *
   * @param onSuccess Callback function to be invoked with the list of user playlists if the fetch
   *   is successful.
   * @param onFailure Callback function to be invoked with an empty list if the fetch fails.
   */
  open fun getCurrentUserPlaylists(
      onSuccess: (List<UserPlaylist>) -> Unit,
      onFailure: (List<UserPlaylist>) -> Unit
  ) {
    viewModelScope.launch {
      val result = apiRepository.get("me/playlists?limit=50")
      if (result.isSuccess) {
        Log.d("SpotifyApiViewModel", "Playlists fetched successfully")
        val items = result.getOrNull()?.getJSONArray("items") ?: return@launch
        val playlists = handleUserPlaylists(items)
        onSuccess(playlists)
      } else {
        Log.e("SpotifyApiViewModel", "Failed to fetch playlists")
        onFailure(emptyList())
      }
    }
  }

  /**
   * Fetches the user's Spotify playlists upon a giving the user's spotifyId
   *
   * @param userId the user's spotifyId
   * @param onSuccess Callback function to be invoked with the list of user playlists if the fetch
   *   is successful.
   * @param onFailure Callback function to be invoked with an empty list if the fetch fails.
   */
  open fun getUserPlaylists(
      userId: String,
      onSuccess: (List<UserPlaylist>) -> Unit,
      onFailure: (List<UserPlaylist>) -> Unit
  ) {
    viewModelScope.launch {
      val result = apiRepository.get("users/${userId}/playlists?limit=50")
      if (result.isSuccess) {
        Log.d("SpotifyApiViewModel", "User's playlists fetched successfully")
        val items = result.getOrNull()?.getJSONArray("items") ?: return@launch
        val playlists = handleUserPlaylists(items)
        onSuccess(playlists)
      } else {
        Log.e("SpotifyApiViewModel", "Failed to fetch user's playlists")
        onFailure(emptyList())
      }
    }
  }

  /** Handles a user playlist by adding it to the list of playlists if it's public. */
  private fun handleUserPlaylists(items: JSONArray): MutableList<UserPlaylist> {
    val playlists = mutableListOf<UserPlaylist>()
    for (i in 0 until items.length()) {
      val playlist = items.optJSONObject(i)
      if (playlist != null) {
        val userPlaylist = createUserPlaylist(playlist)
        // Only add the playlist if it's public
        if (userPlaylist.playlistPublic) {
          playlists.add(userPlaylist)
        }
      } else {
        Log.w("SpotifyApiViewModel", "Skipping null playlist at index $i")
      }
    }
    return playlists
  }
}
