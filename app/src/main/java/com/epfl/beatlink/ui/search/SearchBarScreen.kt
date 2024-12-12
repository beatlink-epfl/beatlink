package com.epfl.beatlink.ui.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.model.profile.ProfileData
import com.epfl.beatlink.model.spotify.objects.SpotifyArtist
import com.epfl.beatlink.model.spotify.objects.SpotifyTrack
import com.epfl.beatlink.ui.components.search.DatabaseSearchQuery
import com.epfl.beatlink.ui.components.search.DisplayResults
import com.epfl.beatlink.ui.components.search.HandleSearchQuery
import com.epfl.beatlink.ui.components.search.SearchScaffold
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.theme.PrimaryOrange
import com.epfl.beatlink.ui.theme.PrimaryPurple
import com.epfl.beatlink.ui.theme.PrimaryRed
import com.epfl.beatlink.ui.theme.primaryWhite
import com.epfl.beatlink.viewmodel.map.user.MapUsersViewModel
import com.epfl.beatlink.viewmodel.profile.FriendRequestViewModel
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel
import com.epfl.beatlink.viewmodel.spotify.api.SpotifyApiViewModel

@Composable
fun SearchBarScreen(
    navigationActions: NavigationActions,
    spotifyApiViewModel: SpotifyApiViewModel,
    mapUsersViewModel: MapUsersViewModel,
    profileViewModel: ProfileViewModel,
    friendRequestViewModel: FriendRequestViewModel
) {
  val selectedCategory = remember { mutableStateOf("Songs") }
  val searchQuery = remember { mutableStateOf(TextFieldValue("")) }
  val results = remember {
    mutableStateOf(Pair(emptyList<SpotifyTrack>(), emptyList<SpotifyArtist>()))
  }
  val peopleResult = remember { mutableStateOf(emptyList<ProfileData?>()) }
  LaunchedEffect(Unit) {
    // Resets selected users in people searching
    profileViewModel.unselectSelectedUser()
    profileViewModel.unreadyProfile()
  }

  if (selectedCategory.value == "People") {
    DatabaseSearchQuery(
        query = searchQuery.value.text,
        onResults = { peopleResult.value = it },
        profileViewModel = profileViewModel)
  } else {
    HandleSearchQuery(
        query = searchQuery.value.text,
        onResults = { tracks, artists -> results.value = Pair(tracks, artists) },
        onFailure = { results.value = Pair(emptyList(), emptyList()) },
        spotifyApiViewModel = spotifyApiViewModel)
  }

  SearchScaffold(
      navigationActions = navigationActions,
      spotifyApiViewModel = spotifyApiViewModel,
      mapUsersViewModel = mapUsersViewModel,
      backArrowButton = false,
      searchQuery = searchQuery) { paddingValues ->
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)) {
              // Add category buttons
              CategoryButtons(selectedCategory = selectedCategory)

              // Display results based on the selected category
              when (selectedCategory.value) {
                "Songs" -> {
                  DisplayResults(tracks = results.value.first)
                }
                "Artists" -> {
                  DisplayResults(artists = results.value.second)
                }
                "People" -> {
                  DisplayResults(
                      people = peopleResult.value,
                      profileViewModel = profileViewModel,
                      navigationActions = navigationActions,
                      friendRequestViewModel = friendRequestViewModel)
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
          Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 8.dp).testTag("categoryButtons")) {
        CategoryButton("Songs", selectedCategory.value, PrimaryRed) {
          selectedCategory.value = "Songs"
        }
        CategoryButton("Artists", selectedCategory.value, PrimaryOrange) {
          selectedCategory.value = "Artists"
        }
        CategoryButton("People", selectedCategory.value, PrimaryPurple) {
          selectedCategory.value = "People"
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
      modifier = Modifier.testTag("$category categoryButton").height(36.dp)) {
        Text(
            text = category,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.testTag("$category categoryText"))
      }
}
