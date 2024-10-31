package com.android.sample.ui.navigation

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BottomNavigationMenu(
    onTabSelect: (TopLevelDestination) -> Unit,
    tabList: List<TopLevelDestination>,
    selectedItem: String
) {
  NavigationBar(
      modifier = Modifier.fillMaxWidth().height(75.dp).testTag("bottomNavigationMenu"),
      containerColor = MaterialTheme.colorScheme.background,
      content = {
        tabList.forEach { tab ->
          val selected = tab.screen == selectedItem
          NavigationBarItem(
              icon = {
                Icon(
                    painter =
                        if (selected) {
                          painterResource(id = tab.selectedIconResId)
                        } else {
                          painterResource(id = tab.unselectedIconResId)
                        },
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(40.dp))
              },
              label = {
                Text(
                    text = tab.textId,
                    style =
                        TextStyle(
                            fontSize = Variables.BodyLargeSize,
                            lineHeight = Variables.BodyLargeLineHeight,
                            fontWeight = FontWeight(400),
                            color =
                                if (selected) {
                                  Color(0xFFEF3535)
                                } else {
                                  Color(0xFF5F2A83)
                                },
                            textAlign = TextAlign.Center,
                            letterSpacing = Variables.BodyLargeTracking,
                        ))
              },
              selected = false,
              onClick = { onTabSelect(tab) },
              modifier = Modifier.testTag(tab.textId))
        }
      },
  )
}

object Variables {
  val BodyLargeSize: TextUnit = 16.sp
  val BodyLargeLineHeight: TextUnit = 24.sp
  val BodyLargeTracking: TextUnit = 0.5.sp
}
