package com.epfl.beatlink.model.library

import com.epfl.beatlink.model.spotify.objects.SpotifyTrack

interface PlaylistRepository {
  /** Generates and returns a new unique ID for a playlist item */
  fun getNewUid(): String

  fun init(onSuccess: () -> Unit)

  /** Retrieve the unique identifier (UID) of the currently authenticated user. */
  fun getUserId(): String?

  /** Retrieves a list of playlists of the user from Firestore */
  fun getPlaylists(onSuccess: (List<Playlist>) -> Unit, onFailure: (Exception) -> Unit)

  /** Retrieve a list of playlists of playlists that are shared with the user from Firestore */
  fun getSharedPlaylists(onSuccess: (List<Playlist>) -> Unit, onFailure: (Exception) -> Unit)

  /** Retrieves a list of public playlists from Firestore */
  fun getPublicPlaylists(onSuccess: (List<Playlist>) -> Unit, onFailure: (Exception) -> Unit)

  /** Add a new playlist to Firestore */
  fun addPlaylist(playlist: Playlist, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  /** Updates an existing playlist in Firestore */
  fun updatePlaylist(playlist: Playlist, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  /** Updates only the collaborators list in Firestore */
  fun updatePlaylistCollaborators(
      playlist: Playlist,
      newCollabList: List<String>,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit)

  /** Updates only the track count of a specific playlist in Firestore */
  fun updatePlaylistTrackCount(
      playlist: Playlist,
      newTrackCount: Int,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  )

  /** Updates only the list of tracks contained in the playlist in Firestore */
  fun updatePlaylistTracks(
      playlist: Playlist,
      newListTracks: List<SpotifyTrack>,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  )

  /** Deletes a playlist by its ID from Firestore */
  fun deletePlaylistById(id: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
}
