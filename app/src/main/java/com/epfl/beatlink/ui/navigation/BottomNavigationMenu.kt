package com.epfl.beatlink.ui.navigation

//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun BottomNavigationMenu(
    onTabSelect: (TopLevelDestination) -> Unit,
    tabList: List<TopLevelDestination>,
    selectedItem: String
) {
  NavigationBar(
      modifier = Modifier.fillMaxWidth().height(56.dp).testTag("bottomNavigationMenu"),
      containerColor = MaterialTheme.colorScheme.background,
      tonalElevation = 0.dp,
      content = {
        tabList.forEach { tab ->
          val selected = tab.screen == selectedItem
          NavigationBarItem(
              icon = {
                Icon(
                    imageVector = if (selected) tab.selectedIconResId else tab.unselectedIconResId,
                    contentDescription = tab.textId,
                    tint = if (selected) Color.Unspecified else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp))
              },
              label = {
                Text(
                    text = tab.textId,
                    color = MaterialTheme.colorScheme.primary,
                    style =
                        if (selected) {
                          MaterialTheme.typography.headlineMedium
                        } else {
                          MaterialTheme.typography.headlineSmall
                        },
                )
              },
              selected = selectedItem == tab.route,
              onClick = { onTabSelect(tab) },
              modifier = Modifier.testTag(tab.textId))
        }
      },
  )
}
