package com.epfl.beatlink.ui.components.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.TextFieldValue
import com.epfl.beatlink.ui.components.MusicPlayerUI
import com.epfl.beatlink.ui.navigation.BottomNavigationMenu
import com.epfl.beatlink.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.search.components.ShortSearchBarLayout
import com.epfl.beatlink.viewmodel.map.user.MapUsersViewModel
import com.epfl.beatlink.viewmodel.spotify.api.SpotifyApiViewModel

@Composable
fun SearchScaffold(
    navigationActions: NavigationActions,
    spotifyApiViewModel: SpotifyApiViewModel,
    mapUsersViewModel: MapUsersViewModel,
    backArrowButton: Boolean,
    searchQuery: MutableState<TextFieldValue>,
    content: @Composable (PaddingValues) -> Unit
) {
  Scaffold(
      topBar = {
        ShortSearchBarLayout(
            navigationActions = navigationActions,
            backArrowButton = backArrowButton,
            searchQuery = searchQuery.value,
            onQueryChange = { newQuery -> searchQuery.value = newQuery })
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
      modifier = Modifier.testTag("searchScaffold"),
      content = content)
}
