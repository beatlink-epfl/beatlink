package com.epfl.beatlink.ui.search

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.epfl.beatlink.ui.navigation.BottomNavigationMenu
import com.epfl.beatlink.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.search.components.ShortSearchBarLayout
import com.epfl.beatlink.ui.search.components.StandardFillerColumn

@Composable
fun MostMatchedSongsScreen(navigationActions: NavigationActions) {
  Scaffold(
      topBar = { ShortSearchBarLayout(navigationActions) },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = navigationActions.currentRoute())
      },
      modifier = Modifier.testTag("mostMatchedSongsScreen")) { paddingValues ->
        StandardFillerColumn(tag = "mostMatchedSearchColumn", paddingValues = paddingValues)
      }
}
