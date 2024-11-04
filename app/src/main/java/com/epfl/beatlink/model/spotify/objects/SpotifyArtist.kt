package com.epfl.beatlink.model.spotify.objects

data class SpotifyArtist(
    val image: String,
    val name: String,
    val genres: List<String>,
    val popularity: Int
)
