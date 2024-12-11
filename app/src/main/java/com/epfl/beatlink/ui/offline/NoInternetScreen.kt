package com.epfl.beatlink.ui.offline

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.ui.navigation.BottomNavigationMenu
import com.epfl.beatlink.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.theme.LightGray
import com.epfl.beatlink.ui.theme.OfflineBackground
import com.epfl.beatlink.ui.theme.PrimaryPurple

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NoInternetScreen(navigationAction: NavigationActions) {
  Scaffold(
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationAction.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = navigationAction.currentRoute())
      },
      content = {
        Box(modifier = Modifier.fillMaxSize().background(color = OfflineBackground)) {
          Column(
              modifier =
                  Modifier.align(Alignment.Center)
                      .padding(16.dp)
                      .background(color = LightGray, shape = RoundedCornerShape(16.dp))
                      .padding(24.dp)
                      .testTag("offline_screen"),
              horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "No Internet Connection",
                    style = MaterialTheme.typography.labelLarge,
                    color = PrimaryPurple)
              }
        }
      })
}
