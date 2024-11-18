package com.epfl.beatlink.model.library

data class Playlist(
    val playlistID: String,
    val playlistCover: String, // TODO
    val playlistName: String, // mandatory
    val playlistDescription: String? = null,
    val playlistPublic: Boolean = false,
    val userId: String, // user ID
    val playlistOwner: String, // username
    val playlistCollaborators: List<String>, // list of user IDs
    val playlistTracks: List<String>, // list of ids of SpotifyTrack
    val nbTracks: Int = 0
)
