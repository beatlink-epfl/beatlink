package com.android.sample.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.model.profile.ProfileData
import com.android.sample.ui.theme.PrimaryGradientBrush
import com.android.sample.ui.theme.PrimaryPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    user: ProfileData,
    // navigationAction: NavigationActions
) {
  Scaffold(
      modifier = Modifier.testTag("profileScreen"),
      topBar = {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(titleContentColor = PrimaryPurple),
            // Username displayed
            title = { Text(text = user.username, fontWeight = FontWeight.Bold) },
            actions = {
              // Notification icon
              IconButton(
                  modifier = Modifier.testTag("profileScreenNotificationsButton"),
                  onClick = {
                    // Handle navigation icon click
                  }) {
                    Icon(
                        painter = painterResource(id = R.drawable.bell_pin_fill),
                        modifier = Modifier.size(28.dp),
                        tint = Color.Unspecified,
                        contentDescription = "Notifications")
                  }
              // Settings icon
              IconButton(
                  modifier = Modifier.testTag("profileScreenSettingsButton"),
                  onClick = {
                    // Handle navigation icon click
                  }) {
                    Icon(
                        painter = painterResource(id = R.drawable.settings),
                        modifier = Modifier.size(28.dp),
                        tint = Color.Unspecified,
                        contentDescription = "Settings")
                  }
            },
        )
      },
      /*bottomBar = {
          // Bottom navigation bar
          BottomNavigationMenu(
              onTabSelect = { route -> navigationAction.navigateTo(route) },
              tabList = LIST_TOP_LEVEL_DESTINATION,
              selectedItem = navigationAction.currentRoute()
          )
      },*/
      content = { paddingValue ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValue)) {
          Row(modifier = Modifier.padding(16.dp)) {
            ProfilePicture(user.profilePicture ?: R.drawable.default_profile_picture)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
              Text(
                  text = "${user.links} Links",
                  fontWeight = FontWeight.Bold,
                  color = PrimaryPurple,
                  fontSize = 27.sp,
                  modifier =
                      Modifier.align(Alignment.CenterHorizontally)
                          .padding(18.dp)
                          .testTag("linksCount"))
              Box(
                  modifier =
                      Modifier.border(2.dp, PrimaryGradientBrush, RoundedCornerShape(30.dp))) {
                    Button(
                        onClick = { /* Handle button click */},
                        modifier = Modifier.fillMaxWidth().testTag("editProfileButton"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    ) {
                      Text(
                          text = "Edit Profile",
                          fontWeight = FontWeight.Bold,
                          color = PrimaryPurple)
                    }
                  }
            }
          }
          Text(
              text = user.name ?: "",
              fontWeight = FontWeight.Bold,
              color = PrimaryPurple,
              modifier = Modifier.padding(horizontal = 16.dp).testTag("name"))
          Text(
              text = user.bio ?: "No description provided",
              color = Color.Black,
              modifier = Modifier.padding(horizontal = 16.dp).testTag("bio"))
        }
      })
}

@Composable
fun ProfilePicture(id: Int) {
  // Profile picture
  Image(
      painter = painterResource(id = id),
      contentDescription = "Profile Picture",
      modifier = Modifier.size(150.dp).testTag("profilePicture"))
}

@Preview
@Composable
fun ProfileScreenPreview() {
  ProfileScreen(ProfileData("John Doe", "james", "my name is jeff", 20, null))
}
