package com.epfl.beatlink.repository.library

import android.graphics.Bitmap
import com.epfl.beatlink.model.library.Playlist

interface PlaylistRepository {
  /**
   * Generate a new unique ID for a playlist item.
   *
   * @return A String representing the new unique ID.
   */
  fun getNewUid(): String

  /**
   * Initialize the repository.
   *
   * @param onSuccess Callback for successful initialization.
   */
  fun init(onSuccess: () -> Unit)

  /**
   * Retrieve the unique identifier (UID) of the currently authenticated user.
   *
   * @return The UID of the current user, or null if the user is not logged in.
   */
  fun getUserId(): String?

  /**
   * Retrieve all playlists owned by the current user.
   *
   * @param onSuccess Callback that is invoked with the list of owned playlists.
   * @param onFailure Callback that is invoked with an exception upon failure.
   */
  fun getOwnedPlaylists(onSuccess: (List<Playlist>) -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Retrieve all playlists shared with the current user.
   *
   * @param onSuccess Callback that is invoked with the list of shared playlists.
   * @param onFailure Callback that is invoked with an exception upon failure.
   */
  fun getSharedPlaylists(onSuccess: (List<Playlist>) -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Retrieve all public playlists that are not owned by the current user.
   *
   * @param onSuccess Callback that is invoked with the list of public playlists.
   * @param onFailure Callback that is invoked with an exception upon failure.
   */
  fun getPublicPlaylists(onSuccess: (List<Playlist>) -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Add a new playlist.
   *
   * @param playlist The Playlist object to be added.
   * @param onSuccess Callback for successful addition.
   * @param onFailure Callback that is invoked with an exception upon failure.
   */
  fun addPlaylist(playlist: Playlist, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Update an existing playlist.
   *
   * @param playlist The Playlist object to be updated.
   * @param onSuccess Callback for successful update.
   * @param onFailure Callback that is invoked with an exception upon failure.
   */
  fun updatePlaylist(playlist: Playlist, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Delete all playlists owned by the current user.
   *
   * @param onSuccess Callback for successful deletion.
   * @param onFailure Callback that is invoked with an exception upon failure.
   */
  fun deleteOwnedPlaylists(onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Delete a playlist by its ID.
   *
   * @param id The ID of the playlist to be deleted.
   * @param onSuccess Callback for successful deletion.
   * @param onFailure Callback that is invoked with an exception upon failure.
   */
  fun deletePlaylistById(id: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Load a playlist cover image.
   *
   * @param playlist The Playlist object whose cover image is to be loaded.
   * @param onBitmapLoaded Callback invoked with the loaded Bitmap.
   */
  fun loadPlaylistCover(playlist: Playlist, onBitmapLoaded: (Bitmap?) -> Unit)
}
