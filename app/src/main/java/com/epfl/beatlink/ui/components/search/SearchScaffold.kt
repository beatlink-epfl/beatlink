package com.epfl.beatlink.ui.components.search

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.TextFieldValue
import com.epfl.beatlink.ui.navigation.BottomNavigationMenu
import com.epfl.beatlink.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.search.components.ShortSearchBarLayout

@Composable
fun SearchScaffold(
    navigationActions: NavigationActions,
    searchQuery: MutableState<TextFieldValue>,
    content: @Composable (PaddingValues) -> Unit
) {
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
      modifier = Modifier.testTag("searchScaffold"),
      content = content)
}
