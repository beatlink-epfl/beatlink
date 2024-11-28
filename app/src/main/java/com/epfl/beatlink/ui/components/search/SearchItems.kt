package com.epfl.beatlink.ui.components.search

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.model.spotify.objects.SpotifyArtist
import com.epfl.beatlink.model.spotify.objects.SpotifyTrack
import com.epfl.beatlink.ui.library.TrackPlaylistItem
import com.epfl.beatlink.ui.navigation.BottomNavigationMenu
import com.epfl.beatlink.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.search.components.ShortSearchBarLayout
import com.epfl.beatlink.viewmodel.library.PlaylistViewModel
import com.epfl.beatlink.viewmodel.spotify.api.SpotifyApiViewModel

@Composable
fun HandleSearchQuery(
    query: String,
    onResults: (List<SpotifyTrack>, List<SpotifyArtist>) -> Unit,
    onFailure: () -> Unit,
    spotifyApiViewModel: SpotifyApiViewModel
) {
  val context = LocalContext.current
  LaunchedEffect(query) {
    if (query.isNotEmpty()) {
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
    } else {
      onResults(emptyList(), emptyList())
    }
  }
}

@Composable
fun SearchScaffold(
    navigationActions: NavigationActions,
    searchQuery: MutableState<TextFieldValue>,
    content: @Composable (PaddingValues) -> Unit
) {
  Scaffold(
      topBar = {
        ShortSearchBarLayout(
            navigationActions = navigationActions,
            searchQuery = searchQuery.value,
            onQueryChange = { newQuery -> searchQuery.value = newQuery })
      },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = navigationActions.currentRoute())
      },
      modifier = Modifier.testTag("searchScaffold"),
      content = content)
}

@Composable
fun DisplayResults(
    tracks: List<SpotifyTrack>? = null,
    artists: List<SpotifyArtist>? = null,
    playlistViewModel: PlaylistViewModel? = null,
    onClearQuery: (() -> Unit)? = null
) {
  if (tracks.isNullOrEmpty() && artists.isNullOrEmpty()) {
    // Empty state
    Column(
        modifier = Modifier.fillMaxSize().testTag("noResultsMessage"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
              text = "No results found. Try searching for something.",
              style = MaterialTheme.typography.bodyLarge)
        }
  } else {
    // Display tracks or artists
    LazyColumn(modifier = Modifier.testTag("searchResultsColumn")) {
      tracks?.let {
        items(it) { track ->
          if (playlistViewModel != null && onClearQuery != null) {
            TrackPlaylistItem(
                track = track, playlistViewModel = playlistViewModel, onClearQuery = onClearQuery)
          } else {
            TrackItem(track = track)
          }
        }
      }
      artists?.let {
        items(it) { artist ->
          ArtistItem(artist = artist)
          Spacer(modifier = Modifier.height(16.dp))
        }
      }
    }
  }
}
