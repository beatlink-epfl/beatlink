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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.sample.R
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.theme.PrimaryGradientBrush
import com.android.sample.ui.theme.PrimaryPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userId: String,
    profileViewModel: ProfileViewModel = viewModel(factory = ProfileViewModel.Factory),
    navigationAction: NavigationActions
) {
  val profileData by profileViewModel.profile.collectAsState()

  LaunchedEffect(Unit) { profileViewModel.fetchProfile(userId) }

  Scaffold(
      modifier = Modifier.testTag("profileScreen"),
      topBar = {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(titleContentColor = PrimaryPurple),
            // Username displayed
            title = {
              Text(
                  text = profileData?.username ?: "",
                  fontWeight = FontWeight.Bold,
                  fontSize = 20.sp,
                  modifier = Modifier.testTag("titleUsername"))
            },
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
      bottomBar = {
        // Bottom navigation bar
        BottomNavigationMenu(
            onTabSelect = { route -> navigationAction.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = navigationAction.currentRoute())
      },
      content = { paddingValue ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValue)) {
          HorizontalDivider(
              color = Color.LightGray, thickness = 1.dp, modifier = Modifier.testTag("divider"))
          Row(modifier = Modifier.padding(16.dp)) {
            ProfilePicture(profileData?.profilePicture ?: R.drawable.default_profile_picture)
            Spacer(modifier = Modifier.width(24.dp))
            Column {
              Text(
                  text = "${profileData?.links ?: 0} Links",
                  fontWeight = FontWeight.Bold,
                  color = PrimaryPurple,
                  fontSize = 18.sp,
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
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    ) {
                      Text(
                          text = "Edit Profile",
                          fontSize = 12.sp,
                          fontWeight = FontWeight.Bold,
                          color = PrimaryPurple)
                    }
                  }
            }
          }
          Text(
              text = profileData?.name ?: "",
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp,
              color = PrimaryPurple,
              modifier = Modifier.padding(horizontal = 28.dp).testTag("name"))
          Text(
              text = profileData?.bio ?: "No description provided",
              fontSize = 14.sp,
              color = Color.Black,
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
