package com.epfl.beatlink.viewmodel.library

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.epfl.beatlink.model.library.Playlist
import com.epfl.beatlink.model.library.PlaylistRepository
import com.epfl.beatlink.repository.library.PlaylistRepositoryFirestore
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PlaylistViewModel(private val repository: PlaylistRepository) : ViewModel() {
  private val playlistList_ = MutableStateFlow<List<Playlist>>(emptyList())
  val playlistList: StateFlow<List<Playlist>>
    get() = playlistList_

  private val selectedPlaylist_ = MutableStateFlow<Playlist?>(null)
  val selectedPlaylist: StateFlow<Playlist?>
    get() = selectedPlaylist_

  // create factory
  companion object {
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PlaylistViewModel(PlaylistRepositoryFirestore(Firebase.firestore)) as T
          }
        }
  }

  // Initialization block that runs when ViewModel is created
  init {
    repository.init(onSuccess = { getPlaylists() })
  }

  fun getPlaylists() {
    repository.getPlaylists(onSuccess = { playlistList_.value = it }, onFailure = {})
  }

  fun getNewUid(): String {
    return repository.getNewUid()
  }

  fun selectPlaylist(playlist: Playlist) {
    selectedPlaylist_.value = playlist
  }

  fun addPlaylist(playlist: Playlist) {
    repository.addPlaylist(
        playlist,
        onSuccess = { getPlaylists() },
        onFailure = { e -> Log.e("PlaylistViewModel", "Failed to add playlist", e) })
  }

  fun updatePlaylist(playlist: Playlist) {
    repository.updatePlaylist(
        playlist,
        onSuccess = { selectedPlaylist_.value = playlist },
        onFailure = { e -> Log.e("PlaylistViewModel", "Failed to update playlist", e) })
    getPlaylists()
  }

  fun updateTrackCount(playlist: Playlist, newTrackCount: Int) {
    repository.updatePlaylistTrackCount(
        playlist = playlist,
        newTrackCount = newTrackCount,
        onSuccess = { getPlaylists() },
        onFailure = { e -> Log.e("PlaylistViewModel", "Failed to update track count", e) })
  }
}
