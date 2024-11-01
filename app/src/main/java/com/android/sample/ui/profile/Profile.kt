package com.android.sample.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.sample.model.profile.ProfileData
import com.android.sample.ui.components.PageTopBarApp
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.theme.PrimaryGradientBrush

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(user: ProfileData, navigationAction: NavigationActions) {
  Scaffold(
      modifier = Modifier.testTag("profileScreen"),
      topBar = {
        PageTopBarApp(
            user.username,
            "titleUsername",
            {},
            Icons.Filled.Notifications,
            "Notifications",
            "profileScreenNotificationsButton",
            {},
            Icons.Filled.Settings,
            "Settings",
            "profileScreenSettingsButton")
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
          Row(modifier = Modifier.padding(16.dp)) {
            ProfilePicture(user.profilePicture ?: R.drawable.default_profile_picture)
            Spacer(modifier = Modifier.width(24.dp))
            Column {
              Text(
                  text = "${user.links} Links",
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
                        onClick = { /* Handle button click */},
                        modifier = Modifier.fillMaxWidth().testTag("editProfileButton"),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.onSurfaceVariant),
                    ) {
                      Text(
                          text = "Edit Profile",
                          style = MaterialTheme.typography.labelSmall,
                          color = MaterialTheme.colorScheme.primary)
                    }
                  }
            }
          }
          Text(
              text = user.name ?: "",
              color = MaterialTheme.colorScheme.onPrimary,
              style = MaterialTheme.typography.bodyLarge,
              modifier = Modifier.padding(horizontal = 28.dp).testTag("name"))
          Text(
              text = user.bio ?: "No description provided",
              color = MaterialTheme.colorScheme.primary,
              style = MaterialTheme.typography.bodyMedium,
              modifier = Modifier.padding(horizontal = 28.dp).testTag("bio"))
        }
      })
}

@Composable
fun ProfilePicture(id: Int) {
  // Profile picture
  Image(
      painter = painterResource(id = id),
      contentDescription = "Profile Picture",
      modifier = Modifier.size(100.dp).testTag("profilePicture"))
}
