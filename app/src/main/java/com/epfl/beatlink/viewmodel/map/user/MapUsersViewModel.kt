package com.epfl.beatlink.viewmodel.map.user

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.epfl.beatlink.model.map.user.CurrentPlayingTrack
import com.epfl.beatlink.model.map.user.Location
import com.epfl.beatlink.model.map.user.MapUser
import com.epfl.beatlink.model.map.user.MapUserRepository
import com.epfl.beatlink.model.spotify.objects.SpotifyAlbum
import com.epfl.beatlink.model.spotify.objects.SpotifyArtist
import com.epfl.beatlink.model.spotify.objects.SpotifyTrack
import com.epfl.beatlink.repository.map.user.MapUsersRepositoryFirestore
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

open class MapUsersViewModel(private val repository: MapUserRepository) : ViewModel() {

  // Private mutable LiveData to hold the data
  private val _mapUsers = MutableStateFlow<List<MapUser>>(emptyList())
  private var _mapUser = MutableStateFlow<MapUser?>(null)
  private val _authState = MutableStateFlow<Boolean>(false)
  private val _playbackState = MutableStateFlow<CurrentPlayingTrack?>(null)

  // Public immutable StateFlows for observers
  val mapUsers: StateFlow<List<MapUser>> = _mapUsers.asStateFlow()
  val mapUser: StateFlow<MapUser?> = _mapUser.asStateFlow()
  val authState: StateFlow<Boolean> = _authState.asStateFlow()
  val playbackState: StateFlow<CurrentPlayingTrack?> = _playbackState.asStateFlow()

  init {
    // Initialize repository and listen to authentication state
    repository.init { _authState.value = true }
  }

  /** Fetch users within a certain radius from a given location. */
  fun fetchMapUsers(currentLocation: Location, radiusInMeters: Double) {
    viewModelScope.launch {
      repository.getMapUsers(
          currentLocation,
          radiusInMeters,
          onSuccess = { users -> _mapUsers.value = users },
          onFailure = { /* Handle any failures as needed */})
    }
  }

  /** Add a new user to the database. */
  fun addMapUser(username: String, location: Location) {
    viewModelScope.launch {
      _mapUser.value =
          _playbackState.value?.let {
            MapUser(
                username = username,
                currentPlayingTrack = it,
                location = location,
                lastUpdated = Timestamp.now())
          }
      _mapUser.value?.let { Log.d("info", it.username) }
      _mapUser.value?.let {
        repository.addMapUser(
            it,
            onSuccess = { fetchMapUsers(it.location, 1000.0) }, // Optionally refresh nearby users
            onFailure = {})
      }
    }
  }

  /** Update an existing user in the database. */
  fun updateMapUser(location: Location) {
    viewModelScope.launch {
      _mapUser.value =
          _mapUser.value?.let {
            _playbackState.value?.let { playBackState ->
              MapUser(
                  username = it.username,
                  currentPlayingTrack = playBackState,
                  location = location,
                  lastUpdated = Timestamp.now())
            }
          }
      _mapUser.value?.let {
        repository.updateMapUser(
            it,
            onSuccess = { fetchMapUsers(it.location, 1000.0) }, // Optionally refresh nearby users
            onFailure = { /* Handle any failures as needed */})
      }
    }
  }

  /** Delete a user from the database. */
  fun deleteMapUser() {
    viewModelScope.launch {
      repository.deleteMapUser(
          onSuccess = { /* Optionally refresh users or perform other actions */},
          onFailure = { /* Handle any failures as needed */})
      _mapUser.value = null
      _playbackState.value = null
    }
  }

  fun updatePlayback(album: SpotifyAlbum, track: SpotifyTrack, artist: SpotifyArtist) {
    if (_authState.value) {
      _playbackState.value =
          CurrentPlayingTrack(
              trackId = track.trackId,
              songName = track.name,
              artistName = artist.name,
              albumName = album.name,
              albumCover = album.cover)
      if (track.name.isEmpty() && artist.name.isEmpty() && album.name.isEmpty()) {
        if (_mapUser.value != null) {
          deleteMapUser()
        }
        _playbackState.value = null
      }
    }
  }

  // Factory companion object for creating instances of MapUsersViewModel
  companion object {
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val firestore = FirebaseFirestore.getInstance()
            val firebaseAuth = FirebaseAuth.getInstance()
            val repository = MapUsersRepositoryFirestore(db = firestore, auth = firebaseAuth)
            return MapUsersViewModel(repository) as T
          }
        }
  }
}
