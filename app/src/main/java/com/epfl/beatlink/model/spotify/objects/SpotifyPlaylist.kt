package com.epfl.beatlink.model.spotify.objects

data class SpotifyPlaylist(
    val name: String,
    val cover: String,
    val tracks: List<String>, // Track IDs
    val size: Int,
    val popularity: Int
)
