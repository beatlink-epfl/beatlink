package com.android.sample.model.spotify.objects

import android.media.Image

data class SpotifyArtist(
    val image: Image,
    val name: String,
    val genres: List<String>,
    val popularity: Int
)