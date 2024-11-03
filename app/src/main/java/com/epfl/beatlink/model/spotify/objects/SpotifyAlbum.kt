package com.epfl.beatlink.model.spotify.objects

data class SpotifyAlbum(
    val spotifyId: String,
    val name: String,
    val cover: String,
    val artist: String,
    val tracks: List<String>, // Track IDs
    val size: Int,
    val genres: List<String>,
    val popularity: Int
)
