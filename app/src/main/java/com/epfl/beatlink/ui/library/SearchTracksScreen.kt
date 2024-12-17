package com.epfl.beatlink.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import com.epfl.beatlink.model.spotify.objects.SpotifyArtist
import com.epfl.beatlink.model.spotify.objects.SpotifyTrack
import com.epfl.beatlink.ui.components.search.DisplayResults
import com.epfl.beatlink.ui.components.search.HandleSearchQuery
import com.epfl.beatlink.ui.components.search.SearchScaffold
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.viewmodel.library.PlaylistViewModel
import com.epfl.beatlink.viewmodel.map.user.MapUsersViewModel
import com.epfl.beatlink.viewmodel.spotify.api.SpotifyApiViewModel
import com.epfl.beatlink.viewmodel.spotify.auth.SpotifyAuthViewModel

@Composable
fun SearchTracksScreen(
    navigationActions: NavigationActions,
    spotifyApiViewModel: SpotifyApiViewModel,
    spotifyAuthViewModel: SpotifyAuthViewModel,
    mapUsersViewModel: MapUsersViewModel,
    playlistViewModel: PlaylistViewModel
) {
  val searchQuery = remember { mutableStateOf(TextFieldValue("")) }
  val results = remember {
    mutableStateOf(Pair(emptyList<SpotifyTrack>(), emptyList<SpotifyArtist>()))
  }

  HandleSearchQuery(
      query = searchQuery.value.text,
      onResults = { tracks, artists -> results.value = Pair(tracks, artists) },
      onFailure = { results.value = Pair(emptyList(), emptyList()) },
      spotifyApiViewModel = spotifyApiViewModel,
      spotifyAuthViewModel = spotifyAuthViewModel)

  SearchScaffold(
      navigationActions = navigationActions,
      spotifyApiViewModel = spotifyApiViewModel,
      mapUsersViewModel = mapUsersViewModel,
      backArrowButton = true,
      searchQuery = searchQuery) { paddingValues ->
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)) {
              DisplayResults(
                  tracks = results.value.first,
                  playlistViewModel = playlistViewModel,
                  onClearQuery = {
                    searchQuery.value = TextFieldValue("")
                    results.value = Pair(emptyList(), emptyList())
                  })
            }
      }
}
