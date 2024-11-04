package com.epfl.beatlink.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.ui.navigation.BottomNavigationMenu
import com.epfl.beatlink.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.search.components.ShortSearchBarLayout

@Composable
fun DiscoverPeopleScreen(navigationActions: NavigationActions) {
  Scaffold(
      topBar = { ShortSearchBarLayout(navigationActions) },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = navigationActions.currentRoute())
      },
      modifier = Modifier.testTag("discoverPeopleScreen")) { paddingValues ->
        Column(
            modifier =
                Modifier.testTag("profileSearchColumn")
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(color = Color.White)) {
              HorizontalDivider(
                  color = Color.LightGray, thickness = 1.dp, modifier = Modifier.testTag("divider"))

              Spacer(modifier = Modifier.testTag("spacer").height(17.dp))

              Text(text = "Not Drawn In Figma Yet", modifier = Modifier.testTag("placeholderText"))
            }
      }
}
