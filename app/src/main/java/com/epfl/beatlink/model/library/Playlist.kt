package com.epfl.beatlink.model.library

/**
 * Represents a beatlink playlist that exists within the application to then export on Spotify.
 *
 * @property playlistID Unique identifier for the playlist.
 * @property playlistCover URL to the playlist's cover image.
 * @property playlistName Name of the playlist (mandatory, with a max length defined by
 *   [MAX_PLAYLIST_TITLE_LENGTH]).
 * @property playlistDescription Optional description of the playlist (max length defined by
 *   [MAX_PLAYLIST_DESCRIPTION_LENGTH]).
 * @property playlistPublic Indicates whether the playlist is publicly visible or private.
 * @property userId ID of the user who owns the playlist.
 * @property playlistOwner Username of the playlist's owner.
 * @property playlistCollaborators List of user IDs who are collaborators on the playlist.
 * @property playlistTracks List of tracks ([PlaylistTrack]) included in the playlist.
 * @property nbTracks Number of tracks in the playlist (default is 0).
 */
data class Playlist(
    val playlistID: String,
    val playlistCover: String,
    val playlistName: String,
    val playlistDescription: String = "",
    val playlistPublic: Boolean = false,
    val userId: String,
    val playlistOwner: String,
    val playlistCollaborators: List<String>,
    val playlistTracks: List<PlaylistTrack>,
    val nbTracks: Int = 0
) {
  companion object {
    const val MAX_PLAYLIST_TITLE_LENGTH = 30
    const val MAX_PLAYLIST_DESCRIPTION_LENGTH = 200
  }
}
