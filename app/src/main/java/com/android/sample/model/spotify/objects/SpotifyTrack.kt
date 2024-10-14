package com.android.sample.model.spotify.objects

import android.media.Image

data class SpotifyTrack (
    val name: String,
    val trackId: String,
    val cover: Image,
    val duration: Int,
    val popularity: Int
)