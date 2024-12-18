package com.epfl.beatlink.ui.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.ui.components.AddButton
import com.epfl.beatlink.ui.components.ScreenTopAppBar
import com.epfl.beatlink.ui.components.library.PlaylistCard
import com.epfl.beatlink.ui.navigation.BottomNavigationMenu
import com.epfl.beatlink.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.ui.theme.primaryGray
import com.epfl.beatlink.viewmodel.library.PlaylistViewModel

@Composable
fun MyPlaylistsScreen(navigationActions: NavigationActions, playlistViewModel: PlaylistViewModel) {

  val playlistListFlow by playlistViewModel.ownedPlaylistList.collectAsState()

  Scaffold(
      modifier = Modifier.testTag("myPlaylistsScreen"),
      topBar = {
        ScreenTopAppBar(
            "My Playlists",
            "myPlaylistsTitle",
            navigationActions,
            listOf { AddButton { navigationActions.navigateTo(Screen.CREATE_NEW_PLAYLIST) } })
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
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
              if (playlistListFlow.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().testTag("emptyPlaylistsPrompt"),
                    contentAlignment = Alignment.Center) {
                      Text(
                          text = "No playlists yet",
                          color = MaterialTheme.colorScheme.primaryGray,
                          style = MaterialTheme.typography.bodyLarge,
                          modifier = Modifier.align(Alignment.Center))
                    }
              } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp),
                ) {
                  items(playlistListFlow.size) { i ->
                    PlaylistCard(playlistListFlow[i], navigationActions, playlistViewModel)
                  }
                }
              }
            }
      })
}
