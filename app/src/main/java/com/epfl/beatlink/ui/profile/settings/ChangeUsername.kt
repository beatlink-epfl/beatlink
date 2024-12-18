package com.epfl.beatlink.ui.profile.settings

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.epfl.beatlink.model.profile.ProfileData
import com.epfl.beatlink.ui.components.CustomInputField
import com.epfl.beatlink.ui.components.PrincipalButton
import com.epfl.beatlink.ui.components.ScreenTopAppBar
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel

@Composable
fun ChangeUsername(navigationActions: NavigationActions, profileViewModel: ProfileViewModel) {
  LaunchedEffect(Unit) { profileViewModel.fetchProfile() }
  val context = LocalContext.current
  val profileData by profileViewModel.profile.collectAsState()
  var username by remember { mutableStateOf(profileData?.username ?: "") }
  Scaffold(
      topBar = {
        ScreenTopAppBar("Change Username", "changeUsernameScreenTitle", navigationActions)
      },
      content = { paddingValue ->
        Column(
            modifier =
                Modifier.testTag("changeUsernameScreenContent").fillMaxSize().padding(paddingValue),
            horizontalAlignment = Alignment.CenterHorizontally) {
              Spacer(modifier = Modifier.height(67.dp))
              Text("Please enter your new username", fontSize = 25.sp)
              Spacer(modifier = Modifier.height(40.dp))
              CustomInputField(
                  value = username,
                  onValueChange = { username = it.lowercase() },
                  label = "My Username",
                  placeholder = "Enter your new username",
                  supportingText = "No special characters, no spaces",
                  modifier = Modifier.testTag("changeUsernameInput"))
              Spacer(modifier = Modifier.height(323.dp))
              PrincipalButton(
                  "Save",
                  "saveButton",
                  onClick = {
                    try {
                      profileViewModel.verifyUsername(username) { usernameValidationResult ->
                        when (usernameValidationResult) {
                          is ProfileViewModel.UsernameValidationResult.Invalid -> {
                            Toast.makeText(
                                    context,
                                    usernameValidationResult.errorMessage,
                                    Toast.LENGTH_SHORT)
                                .show()
                          }
                          ProfileViewModel.UsernameValidationResult.Valid -> {
                            val updatedProfileData =
                                ProfileData(
                                    bio = profileData?.bio ?: "",
                                    links = profileData?.links ?: 0,
                                    name = profileData?.name ?: "",
                                    profilePicture = profileData?.profilePicture,
                                    username = username,
                                    email = profileData?.email ?: "",
                                    favoriteMusicGenres =
                                        profileData?.favoriteMusicGenres ?: emptyList(),
                                    topSongs = profileData?.topSongs ?: emptyList(),
                                    topArtists = profileData?.topArtists ?: emptyList(),
                                    spotifyId = profileData?.spotifyId ?: "")
                            profileViewModel.updateProfile(updatedProfileData)
                            Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
                            navigationActions.navigateTo(Screen.PROFILE)
                          }
                        }
                      }
                    } catch (e: Exception) {
                      Toast.makeText(context, "Error updating username", Toast.LENGTH_SHORT).show()
                    }
                  })
            }
      })
}
