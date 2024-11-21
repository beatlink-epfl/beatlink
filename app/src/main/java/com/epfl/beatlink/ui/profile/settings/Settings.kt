package com.epfl.beatlink.ui.profile.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.ui.components.PrincipalButton
import com.epfl.beatlink.ui.components.ScreenTopAppBar
import com.epfl.beatlink.ui.navigation.BottomNavigationMenu
import com.epfl.beatlink.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen

@Composable
fun SettingsScreen(navigationActions: NavigationActions) {
  Scaffold(
      modifier = Modifier.testTag("settingScreen"),
      topBar = { ScreenTopAppBar("Settings", "settingScreenTitle", navigationActions) },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = navigationActions.currentRoute())
      },
      content = { paddingValue ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValue).testTag("settingScreenContent"),
            horizontalAlignment = Alignment.CenterHorizontally) {
              Spacer(modifier = Modifier.height(48.dp))
              PrincipalButton("Account Settings", "accountSettingButton") {
                navigationActions.navigateTo(Screen.ACCOUNT)
              }
              Spacer(modifier = Modifier.height(24.dp))
              PrincipalButton("Notification Settings", "notificationSettingsButton") {
                navigationActions.navigateTo(Screen.NOTIFICATIONS)
              }
              Spacer(modifier = Modifier.height(24.dp))
              PrincipalButton("Invite Friends", "inviteFriendsButton") {
                navigationActions.navigateTo("TODO")
              }
              Spacer(modifier = Modifier.height(24.dp))
              PrincipalButton("Rate BeatLink", "rateBeatLinkButton") {
                navigationActions.navigateTo("TODO")
              }
              Spacer(modifier = Modifier.height(228.dp))
              PrincipalButton("Disconnect", "disconnectButton", isRed = true) {
                navigationActions.navigateTo("TODO")
              }
            }
      })
}
