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

  private val sharedPlaylistList_ = MutableStateFlow<List<Playlist>>(emptyList())
  val sharedPlaylistList: StateFlow<List<Playlist>>
    get() = sharedPlaylistList_

  private val publicPlaylistList_ = MutableStateFlow<List<Playlist>>(emptyList())
  val publicPlaylistList: StateFlow<List<Playlist>>
    get() = publicPlaylistList_

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
    repository.init(onSuccess = { fetchData() })
  }

  fun getUserId(): String? {
    return repository.getUserId()
  }

  fun fetchData() {
    getPlaylists()
    getSharedPlaylists()
    getPublicPlaylists()
  }

  fun getPlaylists() {
    Log.d("PlaylistViewModel", "Fetching user playlists...")
    repository.getPlaylists(
        onSuccess = { playlistList_.value = it },
        onFailure = { Log.e("PlaylistViewModel", "Failed to fetch user playlists", it) })
  }

  fun getSharedPlaylists() {
    Log.d("PlaylistViewModel", "Fetching shared playlists...")
    repository.getSharedPlaylists(
        onSuccess = { sharedPlaylistList_.value = it },
        onFailure = { Log.e("PlaylistViewModel", "Failed to fetch shared playlists", it) })
  }

  fun getPublicPlaylists() {
    Log.d("PlaylistViewModel", "Fetching public playlists...")
    repository.getPublicPlaylists(
        onSuccess = { publicPlaylistList_.value = it },
        onFailure = { Log.e("PlaylistViewModel", "Failed to fetch public playlists", it) })
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
        onSuccess = { selectedPlaylist_.value = playlist },
        onFailure = { e -> Log.e("PlaylistViewModel", "Failed to update track count", e) })
    getPlaylists()
  }

  fun updateCollaborators(playlist: Playlist, newCollabList: List<String>) {
    repository.updatePlaylistCollaborators(
        playlist,
        newCollabList,
        onSuccess = { selectedPlaylist_.value = playlist },
        onFailure = { e -> Log.e("PlaylistViewModel", "Failed to update collab list", e) })
    getPlaylists()
  }

  fun deletePlaylist(playlistUID: String) {
    repository.deletePlaylistById(
        playlistUID,
        onSuccess = {},
        onFailure = { e -> Log.e("PlaylistViewModel", "Failed to delete playlist", e) })
    getPlaylists()
  }
}
