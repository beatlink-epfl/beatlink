package com.android.sample.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.sample.model.profile.ProfileData
import com.android.sample.model.spotify.objects.SpotifyTrack
import com.android.sample.ui.components.FullSearchBar
import com.android.sample.ui.components.GradientTitle
import com.android.sample.ui.components.PartyCard
import com.android.sample.ui.components.ProfileCard
import com.android.sample.ui.components.SongCard
import com.android.sample.ui.components.StandardLazyRow
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen

@Composable
fun SearchScreen(navigationActions: NavigationActions) {
  val song1 =
      SpotifyTrack(name = "Song1", trackId = "1", cover = "cover1", duration = 120, popularity = 80)
  val listOfTrendingSongs = listOf(song1)
  val sortedByPopularitySongs = listOfTrendingSongs.sortedByDescending { it.popularity }

  val song2 =
      SpotifyTrack(name = "Song2", trackId = "1", cover = "cover1", duration = 120, popularity = 80)
  val listOfTrendingSongs2 = listOf(song2)
  val sortedByPopularitySongs2 = listOfTrendingSongs2.sortedByDescending { it.popularity }

  val profile1 =
      ProfileData(
          username = "@username1", name = "Name1", bio = "bio1", links = 3, profilePicture = 1)
  val listOfProfiles = listOf(profile1)

  Scaffold(
      topBar = { FullSearchBar(navigationActions) },
      bottomBar = {
        // Bottom navigation bar
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = navigationActions.currentRoute())
      },
      modifier = Modifier.testTag("searchScreen")) { paddingValues ->
        Column(
            modifier =
                Modifier.testTag("searchScreenColumn")
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(color = Color.White)) {
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
