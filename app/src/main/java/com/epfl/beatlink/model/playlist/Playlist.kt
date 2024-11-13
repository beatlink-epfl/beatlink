package com.epfl.beatlink.model.playlist

data class Playlist(
    val playlistID: String,
    val playlistCover: String, // TODO
    val playlistName: String, // mandatory
    val playlistDescription: String? = null,
    val playlistPublic: Boolean = false,
    val userId: String, // user ID
    val playlistOwner: String, // username
    val playlistCollaborators: List<String>, // list of user IDs
    val playlistSongs: List<String>, // TODO change to SpotifyTrack
    val nbTracks: Int = 0
)
