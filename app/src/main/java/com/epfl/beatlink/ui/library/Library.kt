package com.epfl.beatlink.ui.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.ui.components.AddButton
import com.epfl.beatlink.ui.components.PageTopAppBar
import com.epfl.beatlink.ui.components.SearchButton
import com.epfl.beatlink.ui.components.TitleWithArrow
import com.epfl.beatlink.ui.navigation.BottomNavigationMenu
import com.epfl.beatlink.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.viewmodel.library.PlaylistViewModel

@Composable
fun LibraryScreen(navigationActions: NavigationActions, playlistViewModel: PlaylistViewModel) {

  val playlistListFlow by playlistViewModel.playlistList.collectAsState()

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
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = navigationActions.currentRoute())
      },
      content = { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
          // FAVORITES
          TitleWithArrow("FAVORITES") {}

          LazyRow(
              horizontalArrangement = Arrangement.spacedBy(16.dp),
              modifier = Modifier.fillMaxWidth().height(115.dp)) {
                items(1) { SongCard() }
              }

          // PLAYLISTS
          TitleWithArrow("PLAYLISTS") { navigationActions.navigateTo(Screen.MY_PLAYLISTS) }

          LazyColumn(
              verticalArrangement = Arrangement.spacedBy(16.dp),
              modifier = Modifier.fillMaxWidth()) {
                items(playlistListFlow.size) { i -> PlaylistCard(playlistListFlow[i]) }
              }
        }
      })
}
