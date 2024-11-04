package com.epfl.beatlink.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.ui.navigation.BottomNavigationMenu
import com.epfl.beatlink.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.search.components.ShortSearchBarLayout
import com.epfl.beatlink.ui.search.components.StandardLazyColumn

@Composable
fun LiveMusicPartiesScreen(navigationActions: NavigationActions) {
  val liveMusicPartiesList = remember { mutableStateOf(listOf("Party 1")) }

  Scaffold(
      topBar = { ShortSearchBarLayout(navigationActions) },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = navigationActions.currentRoute())
      },
      modifier = Modifier.testTag("liveMusicPartiesScreen")) { paddingValues ->
        Column(
            modifier =
                Modifier.testTag("partiesSearchColumn")
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(color = Color.White)) {
              HorizontalDivider(
                  color = Color.LightGray, thickness = 1.dp, modifier = Modifier.testTag("divider"))

              Spacer(modifier = Modifier.testTag("spacer").height(17.dp))

              StandardLazyColumn(title = "LIVE MUSIC PARTIES", list = liveMusicPartiesList.value)
            }
      }
}
