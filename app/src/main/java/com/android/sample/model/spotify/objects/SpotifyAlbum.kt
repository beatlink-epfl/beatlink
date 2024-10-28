package com.android.sample.model.spotify.objects

data class SpotifyAlbum(
    val spotifyId: String,
    val name: String,
    val cover: String,
    val artist: String,
    val year: Int,
    val tracks: List<String>, // Track IDs
    val size: Int,
    val genres: List<String>,
    val popularity: Int
)
