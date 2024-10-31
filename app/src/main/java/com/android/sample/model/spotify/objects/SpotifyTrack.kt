package com.android.sample.model.spotify.objects

data class SpotifyTrack(
    val name: String,
    val trackId: String,
    val cover: String,
    val duration: Int,
    val popularity: Int,
    val state: State
)

enum class State {
  PLAY,
  PAUSE
}
