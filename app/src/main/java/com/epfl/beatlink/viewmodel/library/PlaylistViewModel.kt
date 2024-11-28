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
  private val ownedPlaylistList_ = MutableStateFlow<List<Playlist>>(emptyList())
  val ownedPlaylistList: StateFlow<List<Playlist>>
    get() = ownedPlaylistList_

  private val sharedPlaylistList_ = MutableStateFlow<List<Playlist>>(emptyList())
  val sharedPlaylistList: StateFlow<List<Playlist>>
    get() = sharedPlaylistList_

  private val publicPlaylistList_ = MutableStateFlow<List<Playlist>>(emptyList())
  val publicPlaylistList: StateFlow<List<Playlist>>
    get() = publicPlaylistList_

  private val selectedPlaylist_ = MutableStateFlow<Playlist?>(null)
  val selectedPlaylist: StateFlow<Playlist?>
    get() = selectedPlaylist_


    // When creating or modifying a playlist
    private var isTempStateInitialized_ = MutableStateFlow(false)
    val isTempStateInitialized: StateFlow<Boolean>
        get() = isTempStateInitialized_

    private val tempPlaylistTitle_ = MutableStateFlow("")
    val tempPlaylistTitle: StateFlow<String>
        get() = tempPlaylistTitle_

    private val tempPlaylistDescription_ = MutableStateFlow("")
    val tempPlaylistDescription: StateFlow<String>
        get() = tempPlaylistDescription_

    private val tempPlaylistIsPublic_ = MutableStateFlow(false)
    val tempPlaylistIsPublic: StateFlow<Boolean>
        get() = tempPlaylistIsPublic_

    private val tempPlaylistCollaborators_ = MutableStateFlow<List<String>>(emptyList())
    val tempPlaylistCollaborators: StateFlow<List<String>>
        get() = tempPlaylistCollaborators_

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
    getOwnedPlaylists()
    getSharedPlaylists()
    getPublicPlaylists()
  }

  fun getOwnedPlaylists() {
    Log.d("PlaylistViewModel", "Fetching user playlists...")
    repository.getOwnedPlaylists(
        onSuccess = { ownedPlaylistList_.value = it },
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
        onSuccess = { getOwnedPlaylists() },
        onFailure = { e -> Log.e("PlaylistViewModel", "Failed to add playlist", e) })
  }

  fun updatePlaylist(playlist: Playlist) {
    repository.updatePlaylist(
        playlist,
        onSuccess = { selectedPlaylist_.value = playlist },
        onFailure = { e -> Log.e("PlaylistViewModel", "Failed to update playlist", e) })
    getOwnedPlaylists()
  }

  fun updateTrackCount(playlist: Playlist, newTrackCount: Int) {
    repository.updatePlaylistTrackCount(
        playlist = playlist,
        newTrackCount = newTrackCount,
        onSuccess = { selectedPlaylist_.value = playlist },
        onFailure = { e -> Log.e("PlaylistViewModel", "Failed to update track count", e) })
    getOwnedPlaylists()
  }

  fun updateCollaborators(playlist: Playlist, newCollabList: List<String>) {
    repository.updatePlaylistCollaborators(
        playlist,
        newCollabList,
        onSuccess = { selectedPlaylist_.value = playlist },
        onFailure = { e -> Log.e("PlaylistViewModel", "Failed to update collab list", e) })
    getOwnedPlaylists()
  }

  fun deletePlaylist(playlistUID: String) {
    repository.deletePlaylistById(
        playlistUID,
        onSuccess = {},
        onFailure = { e -> Log.e("PlaylistViewModel", "Failed to delete playlist", e) })
    getOwnedPlaylists()
  }

    fun updateTemporallyTitle(title: String) {
        tempPlaylistTitle_.value = title
    }
    fun updateTemporallyDescription(description: String) {
        tempPlaylistDescription_.value = description
    }
    fun updateTemporallyIsPublic(isPublic: Boolean) {
        tempPlaylistIsPublic_.value = isPublic
    }
    fun updateTemporallyCollaborators(collaborators: List<String>) {
        tempPlaylistCollaborators_.value = collaborators
    }
    fun resetTemporaryState() {
        isTempStateInitialized_.value = false
        tempPlaylistTitle_.value = ""
        tempPlaylistDescription_.value = ""
        tempPlaylistIsPublic_.value = false
        tempPlaylistCollaborators_.value = emptyList()
    }
    fun preloadTemporaryState(selectedPlaylist: Playlist) {
        if (!isTempStateInitialized_.value) {
            tempPlaylistTitle_.value = selectedPlaylist.playlistName
            tempPlaylistDescription_.value = selectedPlaylist.playlistDescription
            tempPlaylistIsPublic_.value = selectedPlaylist.playlistPublic
            tempPlaylistCollaborators_.value = selectedPlaylist.playlistCollaborators
            isTempStateInitialized_.value = true
        }
    }
}
