package com.epfl.beatlink.model.map.user

data class CurrentPlayingTrack(
    val songName: String,
    val artistName: String,
    val albumName: String,
    val albumCover: String
)
