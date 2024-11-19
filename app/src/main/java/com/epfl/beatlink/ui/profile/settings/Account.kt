package com.epfl.beatlink.ui.profile.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.ui.components.PrincipalButton
import com.epfl.beatlink.ui.components.ScreenTopAppBar
import com.epfl.beatlink.ui.components.TextInBox
import com.epfl.beatlink.ui.navigation.BottomNavigationMenu
import com.epfl.beatlink.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.ui.spotify.SpotifyAuth
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel
import com.epfl.beatlink.viewmodel.spotify.auth.SpotifyAuthViewModel

@Composable
fun AccountScreen(
    navigationActions: NavigationActions,
    spotifyAuthViewModel: SpotifyAuthViewModel,
    editProfileViewModel: ProfileViewModel
) {
  LaunchedEffect(Unit) { editProfileViewModel.fetchProfile() }
  val profileData by editProfileViewModel.profile.collectAsState()
  val username by remember { mutableStateOf(profileData?.username ?: "") }
  val email by remember { mutableStateOf(profileData?.email ?: "") }
  val scrollState = rememberScrollState()

  Scaffold(
      topBar = { ScreenTopAppBar("Account", "accountScreenTitle", navigationActions) },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = navigationActions.currentRoute())
      },
      content = { paddingValue ->
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(paddingValue)
                    .verticalScroll(scrollState)
                    .testTag("accountScreenContent"),
            horizontalAlignment = Alignment.CenterHorizontally) {
              Spacer(modifier = Modifier.height(53.dp))
              TextInBox("E-mail: $email")
              Spacer(modifier = Modifier.height(35.dp))
              TextInBox(
                  "Username: $username",
                  Modifier.clickable { navigationActions.navigateTo(Screen.CHANGE_USERNAME) },
                  Icons.AutoMirrored.Filled.ArrowForward)
              Spacer(modifier = Modifier.height(35.dp))
              TextInBox(
                  "Change password",
                  Modifier.clickable { navigationActions.navigateTo("TODO") },
                  Icons.AutoMirrored.Filled.ArrowForward)
              Spacer(modifier = Modifier.height(45.dp))
              SpotifyAuth(spotifyAuthViewModel)
              Spacer(modifier = Modifier.height(205.dp))
              PrincipalButton("Delete account", "deleteAccountButton", true) {
                navigationActions.navigateTo("TODO")
              }
            }
      })
}
