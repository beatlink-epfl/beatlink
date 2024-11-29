package com.epfl.beatlink.model.library

import com.epfl.beatlink.model.spotify.objects.SpotifyTrack

data class PlaylistTrack(
    val track: SpotifyTrack,
    var likes: Int = 0,
    val likedBy: MutableList<String> = mutableListOf()
)
