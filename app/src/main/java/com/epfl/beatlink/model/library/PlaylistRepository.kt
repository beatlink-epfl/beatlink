package com.epfl.beatlink.model.library

interface PlaylistRepository {
  /** Generates and returns a new unique ID for a playlist item */
  fun getNewUid(): String

  fun init(onSuccess: () -> Unit)

  /** Retrieves a list of playlists from Firestore */
  fun getPlaylists(onSuccess: (List<Playlist>) -> Unit, onFailure: (Exception) -> Unit)

  /** Add a new playlist to Firestore */
  fun addPlaylist(playlist: Playlist, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  /** Updates an existing playlist in Firestore */
  fun updatePlaylist(playlist: Playlist, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  /** Updates only the track count of a specific playlist in Firestore */
  fun updatePlaylistTrackCount(
      playlist: Playlist,
      newTrackCount: Int,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  )

  /** Updates only the list of songs contained in the playlist in Firestore */
  fun updatePlaylistSongs(
      playlist: Playlist,
      newListSongs: List<String>,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  )

  /** Deletes a playlist by its ID from Firestore */
  fun deletePlaylistById(id: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
}
