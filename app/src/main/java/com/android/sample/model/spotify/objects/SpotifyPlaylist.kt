package com.android.sample.model.spotify.objects

import android.media.Image

data class SpotifyPlaylist(
    val name: String,
    val cover: Image,
    val tracks: List<String>, // Track IDs
    val size: Int,
    val popularity: Int
)
