package com.epfl.beatlink.model.spotify.objects

data class SpotifyTrack(
    val name: String,
    val artist: String,
    val trackId: String,
    val cover: String,
    val duration: Int,
    val popularity: Int,
    var state: State
)

enum class State {
  PLAY,
  PAUSE
}
