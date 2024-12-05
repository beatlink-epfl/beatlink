package com.epfl.beatlink.model.map.user

/**
 * Data class representing the current playing track.
 *
 * This class is used to store information about the track currently being listened to by a MapUser
 *
 * @property trackId The unique identifier of the track.
 * @property songName The name of the song.
 * @property artistName The name of the artist.
 * @property albumName The name of the album.
 * @property albumCover The URL or path to the album cover image.
 */
data class CurrentPlayingTrack(
    val trackId: String,
    val songName: String,
    val artistName: String,
    val albumName: String,
    val albumCover: String
)
