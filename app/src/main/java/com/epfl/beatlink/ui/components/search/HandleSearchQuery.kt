package com.epfl.beatlink.ui.components.search

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.epfl.beatlink.model.profile.ProfileData
import com.epfl.beatlink.model.spotify.objects.SpotifyArtist
import com.epfl.beatlink.model.spotify.objects.SpotifyTrack
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel
import com.epfl.beatlink.viewmodel.spotify.api.SpotifyApiViewModel
import com.epfl.beatlink.viewmodel.spotify.auth.AuthState
import com.epfl.beatlink.viewmodel.spotify.auth.SpotifyAuthViewModel

@Composable
fun HandleSearchQuery(
    query: String,
    onResults: (List<SpotifyTrack>, List<SpotifyArtist>) -> Unit,
    onFailure: () -> Unit,
    spotifyApiViewModel: SpotifyApiViewModel,
    spotifyAuthViewModel: SpotifyAuthViewModel
) {
  val context = LocalContext.current
  val auth = spotifyAuthViewModel.authState.value
  LaunchedEffect(query) {
    if (query.isNotEmpty()) {
      if (auth is AuthState.Idle) {
        Toast.makeText(
                context,
                "For spotify searches, please connect your Spotify account to the app.",
                Toast.LENGTH_SHORT)
            .show()
        onFailure()
        return@LaunchedEffect
      } else {
        spotifyApiViewModel.searchArtistsAndTracks(
            query = query,
            onSuccess = { artists, tracks -> onResults(tracks, artists) },
            onFailure = { _, _ ->
              Toast.makeText(
                      context,
                      "Sorry, we couldn't find any matches for that search.",
                      Toast.LENGTH_SHORT)
                  .show()
              onFailure()
            })
      }
    } else {
      onResults(emptyList(), emptyList())
    }
  }
}

@Composable
fun DatabaseSearchQuery(
    query: String,
    onResults: (List<ProfileData>) -> Unit,
    profileViewModel: ProfileViewModel
) {
  LaunchedEffect(query) {
    if (query.isNotEmpty()) {
      profileViewModel.searchUsers(query = query, callback = { person -> onResults(person) })
    } else {
      onResults(emptyList())
    }
  }
}
