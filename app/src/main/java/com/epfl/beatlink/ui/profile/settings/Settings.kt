package com.epfl.beatlink.ui.profile.settings

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.ui.components.PrincipalButton
import com.epfl.beatlink.ui.components.ScreenTopAppBar
import com.epfl.beatlink.ui.navigation.BottomNavigationMenu
import com.epfl.beatlink.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.viewmodel.auth.FirebaseAuthViewModel
import com.epfl.beatlink.viewmodel.map.user.MapUsersViewModel

@Composable
fun SettingsScreen(
    navigationActions: NavigationActions,
    firebaseAuthViewModel: FirebaseAuthViewModel,
    mapUsersViewModel: MapUsersViewModel
) {
  val context = LocalContext.current
  var showDialog by remember { mutableStateOf(false) }
  val scrollState = rememberScrollState()

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
            modifier =
                Modifier.fillMaxSize()
                    .padding(paddingValue)
                    .verticalScroll(scrollState)
                    .testTag("settingScreenContent"),
            horizontalAlignment = Alignment.CenterHorizontally) {
              Spacer(modifier = Modifier.height(48.dp))
              PrincipalButton("Account Settings", "accountSettingButton") {
                navigationActions.navigateTo(Screen.ACCOUNT)
              }
              Spacer(modifier = Modifier.height(24.dp))
              PrincipalButton("Notification Settings", "notificationSettingsButton") {
                navigationActions.navigateTo(Screen.NOTIFICATION_SETTINGS)
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
              PrincipalButton("Sign out", "signOutButton", isRed = true) { showDialog = true }
            }
      })

  if (showDialog) {
    AlertDialog(
        onDismissRequest = { showDialog = false },
        title = { Text(text = "Sign out", style = MaterialTheme.typography.titleLarge) },
        text = {
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Are you sure you want to sign out?",
                style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
          }
        },
        confirmButton = {
          TextButton(
              modifier = Modifier.testTag("confirmButton"),
              onClick = {
                mapUsersViewModel.deleteMapUser()
                firebaseAuthViewModel.signOut(
                    onSuccess = {
                      navigationActions.navigateToAndClearAllBackStack(Screen.WELCOME)
                      Toast.makeText(context, "Sign out successfully", Toast.LENGTH_SHORT).show()
                      showDialog = false
                    },
                    onFailure = {
                      Toast.makeText(context, "Sign out failed: ${it.message}", Toast.LENGTH_SHORT)
                          .show()
                      showDialog = false // Close the dialog on failure
                    })
              }) {
                Text("Confirm")
              }
        },
        dismissButton = {
          TextButton(
              modifier = Modifier.testTag("cancelButton"), onClick = { showDialog = false }) {
                Text("Cancel")
              }
        })
  }
}
