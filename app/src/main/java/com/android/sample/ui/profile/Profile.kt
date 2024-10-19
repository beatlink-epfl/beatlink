package com.android.sample.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.model.profile.ProfileData
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.theme.BorderColor
import com.android.sample.ui.theme.IconsGradientBrush
import com.android.sample.ui.theme.PrimaryGradientBrush
import com.android.sample.ui.theme.PrimaryPurple
import com.android.sample.ui.theme.PrimaryWhite
import com.android.sample.ui.theme.ShadowColor
import com.android.sample.ui.theme.TypographyBeatLink

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(user: ProfileData, navigationAction: NavigationActions) {
  Scaffold(
      modifier = Modifier.testTag("profileScreen"),
      topBar = {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(titleContentColor = PrimaryPurple),
            // Username displayed
            title = {
              Text(
                  text = user.username,
                  style = TypographyBeatLink.headlineLarge,
                  modifier = Modifier.padding(top = 14.dp).testTag("titleUsername"))
            },
            actions = {
              // Notification icon
              IconButton(
                  modifier = Modifier.testTag("profileScreenNotificationsButton"),
                  onClick = {
                    // Handle navigation icon click
                  }) {
                    Icon(
                        imageVector = Icons.Filled.Notifications,
                        contentDescription = "Notifications",
                        modifier =
                            Modifier.size(28.dp).graphicsLayer(alpha = 0.99f).drawWithCache {
                              onDrawWithContent {
                                drawContent()
                                drawRect(IconsGradientBrush, blendMode = BlendMode.SrcAtop)
                              }
                            })
                  }
              // Settings icon
              IconButton(
                  modifier = Modifier.testTag("profileScreenSettingsButton"),
                  onClick = {
                    // Handle navigation icon click
                  }) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Settings",
                        modifier =
                            Modifier.size(28.dp).graphicsLayer(alpha = 0.99f).drawWithCache {
                              onDrawWithContent {
                                drawContent()
                                drawRect(IconsGradientBrush, blendMode = BlendMode.SrcAtop)
                              }
                            })
                  }
            },
            modifier =
                Modifier.shadow(
                        elevation = 2.dp, spotColor = ShadowColor, ambientColor = ShadowColor)
                    .border(width = 1.dp, color = BorderColor)
                    .width(412.dp)
                    .height(48.dp)
                    .background(color = PrimaryWhite))
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
                  style = TypographyBeatLink.bodyLarge,
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
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryWhite),
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
              text = user.name ?: "",
              style = TypographyBeatLink.bodyLarge,
              modifier = Modifier.padding(horizontal = 28.dp).testTag("name"))
          Text(
              text = user.bio ?: "No description provided",
              style = TypographyBeatLink.bodyMedium,
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
