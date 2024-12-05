package com.epfl.beatlink.ui.player

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.epfl.beatlink.viewmodel.map.user.MapUsersViewModel
import com.epfl.beatlink.viewmodel.spotify.api.SpotifyApiViewModel
import kotlinx.coroutines.delay

@Composable
fun SharedPlayerEffect(
    spotifyApiViewModel: SpotifyApiViewModel,
    mapUsersViewModel: MapUsersViewModel
) {

  LaunchedEffect(spotifyApiViewModel.isPlaying) {
    while (true) {
      spotifyApiViewModel.updatePlayer()
      delay(5000L)
    }
  }

  LaunchedEffect(
      spotifyApiViewModel.currentAlbum,
      spotifyApiViewModel.currentArtist,
      spotifyApiViewModel.currentTrack) {
        mapUsersViewModel.updatePlayback(
            spotifyApiViewModel.currentAlbum,
            spotifyApiViewModel.currentTrack,
            spotifyApiViewModel.currentArtist)
        spotifyApiViewModel.buildQueue()
      }
}
