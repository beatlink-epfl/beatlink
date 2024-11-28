package com.epfl.beatlink.ui.search

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.model.spotify.objects.SpotifyArtist
import com.epfl.beatlink.model.spotify.objects.SpotifyTrack
import com.epfl.beatlink.ui.components.search.ArtistItem
import com.epfl.beatlink.ui.components.search.TrackItem
import com.epfl.beatlink.ui.navigation.BottomNavigationMenu
import com.epfl.beatlink.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.search.components.GradientTitle
import com.epfl.beatlink.ui.search.components.ShortSearchBarLayout
import com.epfl.beatlink.ui.theme.PrimaryOrange
import com.epfl.beatlink.ui.theme.PrimaryPurple
import com.epfl.beatlink.ui.theme.PrimaryRed
import com.epfl.beatlink.ui.theme.primaryWhite
import com.epfl.beatlink.viewmodel.spotify.api.SpotifyApiViewModel

@Composable
fun SearchBarScreen(
    navigationActions: NavigationActions,
    spotifyApiViewModel: SpotifyApiViewModel
) {
  val selectedCategory = remember { mutableStateOf("Songs") }
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
      modifier = Modifier.testTag("searchBarScreen")) { paddingValues ->
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(paddingValues)
                    .background(color = MaterialTheme.colorScheme.background)) {
              // Category buttons (Songs, Artists, Events)
              CategoryButtons(selectedCategory = selectedCategory)

              // Display the search results dynamically based on selected category
              when (selectedCategory.value) {
                "Songs" -> {
                  SearchResultsLazyColumn(tracks = results.value.first)
                }
                "Artists" -> {
                  SearchResultsLazyColumn(artists = results.value.second)
                }
                "Events" -> {
                  SearchResultsLazyColumn()
                }
              }
            }
      }
}

@Composable
fun CategoryButtons(selectedCategory: MutableState<String>) {
  Row(
      horizontalArrangement = Arrangement.SpaceEvenly,
      verticalAlignment = Alignment.CenterVertically,
      modifier =
          Modifier.fillMaxWidth()
              .padding(horizontal = 12.dp, vertical = 8.dp)
              .testTag("categoryButtons")) {
        CategoryButton("Songs", selectedCategory.value, PrimaryRed) {
          selectedCategory.value = "Songs"
        }
        CategoryButton("Artists", selectedCategory.value, PrimaryOrange) {
          selectedCategory.value = "Artists"
        }
        CategoryButton("Events", selectedCategory.value, PrimaryPurple) {
          selectedCategory.value = "Events"
        }
      }
}

@Composable
fun CategoryButton(
    category: String,
    selectedCategory: String,
    categoryColor: Color,
    onClick: () -> Unit
) {
  val isSelected = selectedCategory == category
  val backgroundColor = if (isSelected) categoryColor else Color.Transparent
  val contentColor = if (isSelected) MaterialTheme.colorScheme.primaryWhite else categoryColor

  Button(
      onClick = onClick,
      colors =
          ButtonDefaults.buttonColors(
              containerColor = backgroundColor, contentColor = contentColor),
      shape = RoundedCornerShape(20.dp),
      border = BorderStroke(2.dp, categoryColor),
      modifier =
          Modifier.testTag("$category categoryButton").height(36.dp).padding(horizontal = 4.dp)) {
        Text(
            text = category,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.testTag("$category categoryText"))
      }
}

@Composable
fun SearchResultsLazyColumn(
    tracks: List<SpotifyTrack>? = null,
    artists: List<SpotifyArtist>? = null
) {
  Column(modifier = Modifier.fillMaxSize().padding(8.dp).testTag("searchResultsColumn")) {
    // Gradient title for the search results
    GradientTitle("RECENT SEARCHES", false) {}

    Spacer(modifier = Modifier.height(8.dp))

    // Display either tracks or artists
    LazyColumn {
      when {
        tracks != null -> {
          items(tracks) { track -> TrackItem(track = track) }
        }
        artists != null -> {
          items(artists) { artist ->
            ArtistItem(artist = artist)
            Spacer(modifier = Modifier.height(16.dp))
          }
        }
      }
    }
  }
}
