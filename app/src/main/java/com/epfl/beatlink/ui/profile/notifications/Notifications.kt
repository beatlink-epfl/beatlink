package com.epfl.beatlink.ui.profile.notifications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.ui.components.ScreenTopAppBar
import com.epfl.beatlink.ui.components.profile.LinkRequestsButton
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen.LINK_REQUESTS

@Composable
fun NotificationsScreen(navigationActions: NavigationActions) {
  Scaffold(
      topBar = { ScreenTopAppBar("Notifications", "notificationsScreenTitle", navigationActions) },
      content = { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
              LinkRequestsButton { navigationActions.navigateTo(LINK_REQUESTS) }
            }
      })
}
