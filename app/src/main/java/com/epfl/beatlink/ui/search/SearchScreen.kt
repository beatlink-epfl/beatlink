package com.epfl.beatlink.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.model.profile.ProfileData
import com.epfl.beatlink.model.spotify.objects.SpotifyTrack
import com.epfl.beatlink.model.spotify.objects.State
import com.epfl.beatlink.ui.components.MusicPlayerUI
import com.epfl.beatlink.ui.navigation.BottomNavigationMenu
import com.epfl.beatlink.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.ui.search.components.FullSearchBar
import com.epfl.beatlink.ui.search.components.GradientTitle
import com.epfl.beatlink.ui.search.components.PartyCard
import com.epfl.beatlink.ui.search.components.ProfileCard
import com.epfl.beatlink.ui.search.components.SongCard
import com.epfl.beatlink.ui.search.components.StandardLazyRow
import com.epfl.beatlink.viewmodel.map.user.MapUsersViewModel
import com.epfl.beatlink.viewmodel.spotify.api.SpotifyApiViewModel

@Composable
fun SearchScreen(
    navigationActions: NavigationActions,
    spotifyApiViewModel: SpotifyApiViewModel,
    mapUsersViewModel: MapUsersViewModel
) {
  val song1 =
      SpotifyTrack(
          name = "Song1",
          artist = "Artist1",
          trackId = "1",
          cover = "cover1",
          duration = 120,
          popularity = 80,
          state = State.PAUSE)
  val listOfTrendingSongs = listOf(song1)
  val sortedByPopularitySongs = listOfTrendingSongs.sortedByDescending { it.popularity }

  val song2 =
      SpotifyTrack(
          name = "Song2",
          artist = "Artist2",
          trackId = "1",
          cover = "cover1",
          duration = 120,
          popularity = 80,
          state = State.PAUSE)
  val listOfTrendingSongs2 = listOf(song2)
  val sortedByPopularitySongs2 = listOfTrendingSongs2.sortedByDescending { it.popularity }

  val profile1 =
      ProfileData(
          username = "@username1", name = "Name1", bio = "bio1", links = 3, profilePicture = null)
  val listOfProfiles = listOf(profile1)

  Scaffold(
      topBar = { FullSearchBar(navigationActions) },
      bottomBar = {
        Column {
          MusicPlayerUI(navigationActions, spotifyApiViewModel, mapUsersViewModel)
          // Bottom navigation bar
          BottomNavigationMenu(
              onTabSelect = { route -> navigationActions.navigateTo(route) },
              tabList = LIST_TOP_LEVEL_DESTINATION,
              selectedItem = navigationActions.currentRoute())
        }
      },
      modifier = Modifier.testTag("searchScreen")) { paddingValues ->
        Column(
            modifier =
                Modifier.testTag("searchScreenColumn")
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .background(color = MaterialTheme.colorScheme.background)) {
              HorizontalDivider(
                  color = Color.LightGray,
                  thickness = 1.dp,
                  modifier = Modifier.testTag("searchScreenDivider"))

              Spacer(modifier = Modifier.testTag("searchScreenSpacer").height(16.dp))

              StandardLazyRow(
                  title = "TRENDING SONGS",
                  listOfItems = sortedByPopularitySongs,
                  itemContent = { song -> SongCard(song) },
                  horizontalSpace = 25,
                  navigationActions = navigationActions,
                  screen = Screen.TRENDING_SONGS)

              StandardLazyRow(
                  title = "MOST MATCHED SONGS",
                  listOfItems = sortedByPopularitySongs2,
                  itemContent = { song -> SongCard(song) },
                  horizontalSpace = 25,
                  navigationActions = navigationActions,
                  screen = Screen.MOST_MATCHED_SONGS)

              GradientTitle("LIVE MUSIC PARTIES", true) {
                navigationActions.navigateTo(Screen.LIVE_MUSIC_PARTIES)
              }

              LazyRow(
                  horizontalArrangement = Arrangement.spacedBy(14.dp),
                  modifier =
                      Modifier.testTag("LIVE MUSIC PARTIESLazyColumn")
                          .fillMaxWidth()
                          .height(115.dp)
                          .padding(horizontal = 21.dp)
                          .padding(top = 15.dp, bottom = 15.dp)) {
                    items(1) {
                      PartyCard(
                          title = "PARTY NAME",
                          username = "@username",
                          description = "Description, BLA BLA BLA")
                    }
                  }

              StandardLazyRow(
                  title = "DISCOVER PEOPLE",
                  listOfItems = listOfProfiles,
                  itemContent = { people -> ProfileCard(people) },
                  horizontalSpace = 25,
                  navigationActions = navigationActions,
                  screen = Screen.DISCOVER_PEOPLE)
            }
      }
}
