package com.epfl.beatlink.model.library

import com.epfl.beatlink.model.spotify.objects.SpotifyTrack

/**
 * Represents a playlist that already exists on Spotify and belongs to the user.
 *
 *
 * @property playlistID Unique identifier for the playlist on Spotify.
 * @property ownerID ID of the user who owns the playlist on Spotify.
 * @property playlistCover URL or reference to the playlist's cover image.
 * @property playlistName Name of the playlist.
 * @property playlistPublic Indicates whether the playlist is publicly visible or private.
 * @property playlistTracks List of tracks ([SpotifyTrack]) included in the playlist.
 * @property nbTracks Number of tracks in the playlist.
 */
data class UserPlaylist(
    val playlistID: String,
    val ownerID: String,
    val playlistCover: String,
    val playlistName: String,
    val playlistPublic: Boolean = false,
    val playlistTracks: List<SpotifyTrack>,
    val nbTracks: Int
)
