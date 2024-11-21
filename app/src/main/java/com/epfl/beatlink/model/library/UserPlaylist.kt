package com.epfl.beatlink.model.library

import com.epfl.beatlink.model.spotify.objects.SpotifyTrack

data class UserPlaylist(
    val playlistID: String,
    val ownerID: String,
    val playlistCover: String,
    val playlistName: String, // mandatory
    val playlistPublic: Boolean = false,
    val playlistSongs: List<SpotifyTrack>, // list of ids of SpotifyTrack
    val nbTracks: Int
)
