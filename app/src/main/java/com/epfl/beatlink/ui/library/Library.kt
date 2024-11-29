package com.epfl.beatlink.ui.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.ui.components.AddButton
import com.epfl.beatlink.ui.components.MusicPlayerUI
import com.epfl.beatlink.ui.components.PageTopAppBar
import com.epfl.beatlink.ui.components.SearchButton
import com.epfl.beatlink.ui.components.TitleWithArrow
import com.epfl.beatlink.ui.components.library.PlaylistCard
import com.epfl.beatlink.ui.navigation.BottomNavigationMenu
import com.epfl.beatlink.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.viewmodel.library.PlaylistViewModel
import com.epfl.beatlink.viewmodel.map.user.MapUsersViewModel
import com.epfl.beatlink.viewmodel.spotify.api.SpotifyApiViewModel

@Composable
fun LibraryScreen(
    navigationActions: NavigationActions,
    playlistViewModel: PlaylistViewModel,
    spotifyApiViewModel: SpotifyApiViewModel,
    mapUsersViewModel: MapUsersViewModel
) {

  LaunchedEffect(Unit) { playlistViewModel.fetchData() }

  val playlistListFlow by playlistViewModel.ownedPlaylistList.collectAsState()
  val sharedPlaylistListFlow by playlistViewModel.sharedPlaylistList.collectAsState()
  val publicPlaylistListFlow by playlistViewModel.publicPlaylistList.collectAsState()

  Scaffold(
      modifier = Modifier.testTag("libraryScreen"),
      topBar = {
        PageTopAppBar(
            "My Library",
            "libraryTitle",
            listOf {
              SearchButton {}
              AddButton { navigationActions.navigateTo(Screen.CREATE_NEW_PLAYLIST) }
            })
      },
      bottomBar = {
        Column {
          MusicPlayerUI(navigationActions, spotifyApiViewModel, mapUsersViewModel)

          BottomNavigationMenu(
              onTabSelect = { route -> navigationActions.navigateTo(route) },
              tabList = LIST_TOP_LEVEL_DESTINATION,
              selectedItem = navigationActions.currentRoute())
        }
      },
      content = { innerPadding ->
        Column(
            modifier =
                Modifier.padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
          // MY PLAYLISTS
          TitleWithArrow("MY PLAYLISTS") { navigationActions.navigateTo(Screen.MY_PLAYLISTS) }
          LazyColumn(
              verticalArrangement = Arrangement.spacedBy(16.dp),
              modifier = Modifier.fillMaxWidth().heightIn(max = 200.dp)) {
                items(playlistListFlow.size) { i ->
                  PlaylistCard(playlistListFlow[i], navigationActions, playlistViewModel)
                }
              }

          // SHARED PLAYLISTS
          TitleWithArrow("SHARED WITH ME") {
            navigationActions.navigateTo(Screen.SHARED_WITH_ME_PLAYLISTS)
          }
          LazyColumn(
              verticalArrangement = Arrangement.spacedBy(16.dp),
              modifier = Modifier.fillMaxWidth().heightIn(max = 200.dp)) {
                items(sharedPlaylistListFlow.size) { i ->
                  PlaylistCard(sharedPlaylistListFlow[i], navigationActions, playlistViewModel)
                }
              }

          // PUBLIC PLAYLISTS
          TitleWithArrow("PUBLIC") { navigationActions.navigateTo(Screen.PUBLIC_PLAYLISTS) }
          LazyColumn(
              verticalArrangement = Arrangement.spacedBy(16.dp),
              modifier = Modifier.fillMaxWidth().heightIn(max = 200.dp)) {
                items(publicPlaylistListFlow.size) { i ->
                  PlaylistCard(publicPlaylistListFlow[i], navigationActions, playlistViewModel)
                }
              }
        }
      })
}
