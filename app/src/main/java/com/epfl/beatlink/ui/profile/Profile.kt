package com.epfl.beatlink.ui.profile

import android.graphics.Bitmap
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.ui.components.CornerIcons
import com.epfl.beatlink.ui.components.PageTopAppBar
import com.epfl.beatlink.ui.components.ProfilePicture
import com.epfl.beatlink.ui.navigation.BottomNavigationMenu
import com.epfl.beatlink.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.ui.theme.PrimaryGradientBrush
import com.epfl.beatlink.ui.theme.lightThemeBackground
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel

@Composable
fun ProfileScreen(profileViewModel: ProfileViewModel, navigationAction: NavigationActions) {
  LaunchedEffect(Unit) { profileViewModel.fetchProfile() }
  val profileData by profileViewModel.profile.collectAsState()
  val profilePicture = remember { mutableStateOf<Bitmap?>(null) }

  // Load profile picture
  LaunchedEffect(Unit) { profileViewModel.loadProfilePicture { profilePicture.value = it } }

  Scaffold(
      modifier = Modifier.testTag("profileScreen"),
      topBar = {
        PageTopAppBar(
            profileData?.username ?: "",
            "titleUsername",
            listOf {
              CornerIcons(
                  onClick = {},
                  icon = Icons.Filled.Notifications,
                  contentDescription = "Notifications",
                  modifier = Modifier.testTag("profileScreenNotificationsButton"))
              CornerIcons(
                  onClick = {},
                  icon = Icons.Filled.Settings,
                  contentDescription = "Settings",
                  modifier = Modifier.testTag("profileScreenSettingsButton"))
            })
      },
      bottomBar = {
        // Bottom navigation bar
        BottomNavigationMenu(
            onTabSelect = { route -> navigationAction.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = navigationAction.currentRoute())
      },
      content = { paddingValue ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValue)) {
          HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
          Row(modifier = Modifier.padding(16.dp)) {
            ProfilePicture(profilePicture)
            Spacer(modifier = Modifier.width(24.dp))
            Column {
              Text(
                  text = "${profileData?.links ?: 0} Links",
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.primary,
                  style = MaterialTheme.typography.bodyLarge,
                  modifier =
                      Modifier.align(Alignment.CenterHorizontally)
                          .padding(18.dp)
                          .testTag("linksCount"))
              Box(
                  modifier =
                      Modifier.border(1.dp, PrimaryGradientBrush, RoundedCornerShape(30.dp))
                          .testTag("editProfileButtonContainer")
                          .width(233.dp)
                          .height(32.dp)) {
                    Button(
                        onClick = { navigationAction.navigateTo(Screen.EDIT_PROFILE) },
                        modifier = Modifier.fillMaxWidth().testTag("editProfileButton"),
                        colors = ButtonDefaults.buttonColors(containerColor = lightThemeBackground),
                    ) {
                      Text(
                          text = "Edit Profile",
                          fontWeight = FontWeight.Bold,
                          style = MaterialTheme.typography.labelSmall,
                          color = MaterialTheme.colorScheme.primary)
                    }
                  }
            }
          }
          Text(
              text = profileData?.name ?: "",
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary,
              style = MaterialTheme.typography.bodyLarge,
              modifier = Modifier.padding(horizontal = 28.dp).testTag("name"))
          Text(
              text = profileData?.bio ?: "No description provided",
              color = Color.Black,
              style = MaterialTheme.typography.bodyMedium,
              modifier = Modifier.padding(horizontal = 28.dp).testTag("bio"))
        }
      })
}
