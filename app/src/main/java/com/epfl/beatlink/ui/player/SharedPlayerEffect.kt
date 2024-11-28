package com.epfl.beatlink.ui.player

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.epfl.beatlink.viewmodel.map.user.MapUsersViewModel
import com.epfl.beatlink.viewmodel.spotify.api.SpotifyApiViewModel
import kotlinx.coroutines.delay

@Composable
fun SharedPlayerEffect(
    api: SpotifyApiViewModel,
    mapUsersViewModel: MapUsersViewModel
){

    LaunchedEffect(api.isPlaying) {
        api.updatePlayer()
    }

    LaunchedEffect(api.currentAlbum, api.currentArtist, api.currentTrack) {
        mapUsersViewModel.updatePlayback(api.currentAlbum, api.currentTrack, api.currentArtist)
    }

    LaunchedEffect(api.triggerChange) {
        delay(5000L)
        api.updatePlayer()
    }
}