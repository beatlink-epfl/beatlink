package com.android.sample.model.spotify.objects

import android.media.Image

data class SpotifyAlbum(
    val spotifyId: String,
    val name: String,
    val cover: Image,
    val artist: String,
    val tracks: List<String>, // Track IDs
    val size: Int,
    val genres: List<String>,
    val popularity: Int
)
