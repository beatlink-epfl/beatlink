package com.android.sample.ui.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.ui.components.CornerIcons
import com.epfl.beatlink.ui.components.PageTopAppBar
import com.epfl.beatlink.ui.components.TitleWithArrow
import com.epfl.beatlink.ui.navigation.BottomNavigationMenu
import com.epfl.beatlink.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen

@Composable
fun LibraryScreen(navigationActions: NavigationActions) {

  Scaffold(
      modifier = Modifier.testTag("libraryScreen"),
      topBar = {
        PageTopAppBar(
            "My Library",
            "libraryTitle",
            listOf {
              CornerIcons(
                  onClick = {},
                  icon = Icons.Outlined.Search,
                  contentDescription = "Search",
                  modifier = Modifier.testTag("searchButton"))
              CornerIcons(
                  onClick = { navigationActions.navigateTo(Screen.CREATE_NEW_PLAYLIST) },
                  icon = Icons.Outlined.Add,
                  contentDescription = "Add playlist",
                  modifier = Modifier.testTag("addPlaylistButton"))
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
                items(1) { FavoriteItem() }
              }

          // PLAYLISTS
          TitleWithArrow("PLAYLISTS") {}

          LazyColumn(
              verticalArrangement = Arrangement.spacedBy(16.dp),
              modifier = Modifier.fillMaxWidth()) {
                items(1) { PlaylistItem() }
              }
        }
      })
}

@Composable
fun FavoriteItem() {
  Card(modifier = Modifier.testTag("favoriteItem")) { Text("one fav song") }
}

@Composable
fun PlaylistItem() {
  Card(modifier = Modifier.testTag("playlistItem")) { Text("playlist 1") }
}
