package com.epfl.beatlink.model.spotify.objects

data class SpotifyTrack(
    val name: String = "",
    val artist: String = "",
    val trackId: String = "",
    val cover: String = "",
    val duration: Int = 0,
    val popularity: Int = 0,
    var state: State = State.PAUSE
)

enum class State {
  PLAY,
  PAUSE
}
