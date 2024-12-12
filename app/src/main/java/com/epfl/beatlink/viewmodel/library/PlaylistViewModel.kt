package com.epfl.beatlink.viewmodel.library

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.epfl.beatlink.model.library.DEFAULT_TRACK_LIMIT
import com.epfl.beatlink.model.library.Playlist
import com.epfl.beatlink.model.library.PlaylistRepository
import com.epfl.beatlink.model.library.PlaylistTrack
import com.epfl.beatlink.model.spotify.objects.SpotifyTrack
import com.epfl.beatlink.repository.library.PlaylistRepositoryFirestore
import com.epfl.beatlink.utils.ImageUtils.base64ToBitmap
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.io.ByteArrayOutputStream
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val repository: PlaylistRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {
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

  val coverImage = mutableStateOf<Bitmap?>(null)

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

  init {
    repository.init(onSuccess = { fetchData() })
  }

  fun resetCoverImage() {
    coverImage.value = null
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

  fun addTrack(track: PlaylistTrack, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    selectedPlaylist_.value?.let { playlist ->
      try {
        val newTrackList = playlist.playlistTracks.toMutableList()
        newTrackList.add(track)
        val updatedPlaylist =
            playlist.copy(
                playlistTracks = newTrackList,
                nbTracks = newTrackList.size,
                playlistCover = playlist.playlistCover // Explicitly preserve cover
                )

        updatePlaylist(updatedPlaylist)
        onSuccess()
      } catch (e: Exception) {
        onFailure(e)
      }
    } ?: run { onFailure(IllegalStateException("No playlist selected")) }
  }

  fun updateTrackLikes(trackId: String, userId: String) {
    selectedPlaylist_.value?.let { playlist ->
      // Update the tracks in the playlist
      val updatedTracks =
          playlist.playlistTracks.map { track ->
            if (track.track.trackId == trackId) {
              if (track.likedBy.contains(userId)) {
                // User has already liked the track, so remove the like
                track.copy(
                    likes = track.likes - 1,
                    likedBy = track.likedBy.toMutableList().apply { remove(userId) })
              } else {
                // User has not liked the track, so add a like
                track.copy(
                    likes = track.likes + 1,
                    likedBy = track.likedBy.toMutableList().apply { add(userId) })
              }
            } else {
              track // Keep other tracks unchanged
            }
          }

      // Create a new playlist with the updated tracks
      val updatedPlaylist = playlist.copy(playlistTracks = updatedTracks)

      // Update the playlist in the repository
      updatePlaylist(updatedPlaylist)
    } ?: run { Log.e("PlaylistViewModel", "No playlist selected to update track likes") }
  }

  /**
   * Create the final list of track to export the playlist that contain the 50 most liked songs in
   * descending order
   */
  fun getFinalListTracks(): List<SpotifyTrack> {
    return selectedPlaylist_.value
        ?.playlistTracks
        ?.sortedByDescending { it.likes } // Sort by likes in descending order
        ?.take(DEFAULT_TRACK_LIMIT) // Take at most 50 tracks
        ?.map { it.track } // Map to SpotifyTrack
    ?: emptyList() // Return an empty list if no playlist is selected
  }

  fun deleteOwnedPlaylists() {
    repository.deleteOwnedPlaylists(
        onSuccess = { Log.d("PlaylistViewModel", "All playlists deleted successfully") },
        onFailure = { e -> Log.e("PlaylistViewModel", "Failed to delete playlists", e) })
  }

  fun deletePlaylistById(playlistUID: String) {
    repository.deletePlaylistById(
        playlistUID,
        onSuccess = {
          resetCoverImage() // Reset the cover image after deletion
        },
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
    resetCoverImage()
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

  // Playlist cover image
  fun uploadPlaylistCover(imageUri: Uri, context: Context, playlist: Playlist) {
    if (playlist.playlistID.isEmpty()) {
      Log.e("PlaylistViewModel", "Playlist ID is empty, upload failed")
      return
    }
    viewModelScope.launch(dispatcher) {
      repository.uploadPlaylistCover(imageUri, context, playlist)
    }
  }

  // Playlist cover image
  fun loadPlaylistCover(playlist: Playlist, onBitmapLoaded: (Bitmap?) -> Unit) {
    if (playlist.playlistID.isEmpty()) {
      Log.e("PlaylistViewModel", "Playlist ID is empty, load failed")
      return
    }
    repository.loadPlaylistCover(playlist, onBitmapLoaded)
  }

  fun preparePlaylistCoverForSpotify(): String? {
    return try {
      val cover = selectedPlaylist.value?.playlistCover
      if (cover == null) {
        Log.e("PlaylistViewModel", "Playlist cover is null")
        return null
      }

      // Decode Base64 to Bitmap and Re-encode the Bitmap to Base64 JPEG
      val byteArray =
          ByteArrayOutputStream().use {
            base64ToBitmap(cover)?.compress(Bitmap.CompressFormat.JPEG, 100, it)
            it.toByteArray()
          }
      Base64.encodeToString(byteArray, Base64.NO_WRAP) // NO_WRAP removes padding
    } catch (e: Exception) {
      Log.e("PlaylistViewModel", "Error preparing playlist cover: ${e.message}", e)
      null
    }
  }
}
