package com.epfl.beatlink.ui.library

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.epfl.beatlink.model.library.PlaylistTrack
import com.epfl.beatlink.model.spotify.objects.SpotifyArtist
import com.epfl.beatlink.model.spotify.objects.SpotifyTrack
import com.epfl.beatlink.ui.navigation.BottomNavigationMenu
import com.epfl.beatlink.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.search.components.ShortSearchBarLayout
import com.epfl.beatlink.ui.theme.TypographySongs
import com.epfl.beatlink.viewmodel.library.PlaylistViewModel
import com.epfl.beatlink.viewmodel.spotify.api.SpotifyApiViewModel

@Composable
fun SearchTracksScreen(
    navigationActions: NavigationActions,
    spotifyApiViewModel: SpotifyApiViewModel,
    playlistViewModel: PlaylistViewModel
) {
  val searchQuery = remember { mutableStateOf(TextFieldValue("")) }
  val results = remember {
    mutableStateOf(Pair(emptyList<SpotifyTrack>(), emptyList<SpotifyArtist>()))
  }

  val context = LocalContext.current

  // Observe search query changes and fetch corresponding results
  LaunchedEffect(searchQuery.value.text) {
    if (searchQuery.value.text.isNotEmpty()) {
      spotifyApiViewModel.searchArtistsAndTracks(
          query = searchQuery.value.text,
          onSuccess = { artists, tracks -> results.value = Pair(tracks, artists) },
          onFailure = { _, _ ->
            Toast.makeText(
                    context,
                    "Sorry, we couldn't find any matches for that search.",
                    Toast.LENGTH_SHORT)
                .show()
          })
    } else {
      results.value = Pair(emptyList(), emptyList())
    }
  }

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
      modifier = Modifier.testTag("searchTracksScreen")) { paddingValues ->
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(paddingValues)
                    .background(color = MaterialTheme.colorScheme.background)) {
              SearchTracksLazyColumn(
                  tracks = results.value.first,
                  playlistViewModel = playlistViewModel,
                  onClearQuery = {
                    searchQuery.value = TextFieldValue("")
                    results.value = Pair(emptyList(), emptyList())
                  })
            }
      }
}

@Composable
fun SearchTracksLazyColumn(
    tracks: List<SpotifyTrack>?,
    playlistViewModel: PlaylistViewModel,
    onClearQuery: () -> Unit
) {
  if (tracks.isNullOrEmpty()) {
    // Display message when no tracks are available
    Column(
        modifier = Modifier.fillMaxSize().testTag("noResultsMessage"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
              text = "Search for a song to add and click on it",
              style = MaterialTheme.typography.bodyLarge)
        }
  } else {
    // Display tracks
    LazyColumn(modifier = Modifier.testTag("searchResultsColumn")) {
      items(tracks) { track ->
        TrackPlaylistItem(
            track = track, playlistViewModel = playlistViewModel, onClearQuery = onClearQuery)
      }
    }
  }
}

@Composable
fun TrackPlaylistItem(
    track: SpotifyTrack,
    playlistViewModel: PlaylistViewModel,
    onClearQuery: () -> Unit
) {
  val context = LocalContext.current
  Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier =
          Modifier.fillMaxWidth()
              .padding(vertical = 8.dp)
              .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
              .padding(horizontal = 8.dp)
              .testTag("trackItem")
              .clickable {
                playlistViewModel.addTrack(
                    PlaylistTrack(track, 0, mutableListOf()),
                    onSuccess = {
                      Toast.makeText(context, "Track added to playlist!", Toast.LENGTH_SHORT).show()
                      onClearQuery()
                    },
                    onFailure = { e ->
                      Toast.makeText(
                              context, "Failed to add track: ${e.message}", Toast.LENGTH_SHORT)
                          .show()
                    })
                onClearQuery()
              }) {
        // Album cover
        Card(
            modifier = Modifier.padding(start = 8.dp).testTag("trackAlbumCover").size(55.dp),
            shape = RoundedCornerShape(5.dp),
        ) {
          AsyncImage(
              model = track.cover,
              contentDescription = "Cover for ${track.name}",
              contentScale = ContentScale.Crop)
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Track name and artist
        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
          Text(
              text = track.name,
              modifier = Modifier.testTag(track.name),
              style = TypographySongs.titleLarge)
          Text(
              text = track.artist,
              modifier = Modifier.testTag(track.artist),
              style = TypographySongs.titleSmall)
        }
      }
}
