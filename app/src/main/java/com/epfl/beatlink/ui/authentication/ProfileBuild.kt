package com.epfl.beatlink.ui.authentication

import android.Manifest.permission.READ_MEDIA_IMAGES
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import com.epfl.beatlink.model.profile.MusicGenre
import com.epfl.beatlink.model.profile.ProfileData
import com.epfl.beatlink.ui.components.CustomInputField
import com.epfl.beatlink.ui.components.PrincipalButton
import com.epfl.beatlink.ui.components.ProfilePicture
import com.epfl.beatlink.ui.components.profile.MusicGenreSelectionDialog
import com.epfl.beatlink.ui.components.profile.SelectFavoriteMusicGenres
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.utils.ImageUtils.base64ToBitmap
import com.epfl.beatlink.utils.ImageUtils.permissionLauncher
import com.epfl.beatlink.utils.ImageUtils.resizeAndCompressImageFromUri
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel

@SuppressLint("MutableCollectionMutableState")
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileBuildScreen(navigationActions: NavigationActions, profileViewModel: ProfileViewModel) {
  var name by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  var favoriteMusicGenres by remember { mutableStateOf(mutableListOf<String>()) }
  var isGenreSelectionVisible by remember { mutableStateOf(false) }
  val currentProfile = profileViewModel.profile.collectAsState()
  val context = LocalContext.current
  var imageUri by remember { mutableStateOf(Uri.EMPTY) }
  var imageCover by remember { mutableStateOf("") }

  // Load profile picture
  LaunchedEffect(Unit) {
    profileViewModel.loadProfilePicture { profileViewModel.profilePicture.value = it }
  }
  val permissionLauncher =
      permissionLauncher(context) { uri: Uri? ->
        imageUri = uri
        if (imageUri == null) {
          profileViewModel.profilePicture.value = null
        } else {
          imageCover = resizeAndCompressImageFromUri(imageUri, context) ?: ""
          profileViewModel.profilePicture.value = base64ToBitmap(imageCover)
        }
      }

  // Fetch profile
  LaunchedEffect(Unit) { profileViewModel.fetchProfile() }

  Scaffold(
      modifier = Modifier.testTag("profileBuildScreen"),
      topBar = { TopAppBar(title = { BeatLinkTopLogo() }) }) { paddingValues ->
        Column(
            modifier =
                Modifier.padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
              // Profile Build Title Section
              ProfileBuildTitle()

              // Add Profile Picture
              AddProfilePicture(permissionLauncher, profileViewModel.profilePicture)

              // Name input field
              CustomInputField(
                  value = name,
                  onValueChange = { name = it },
                  label = "Name",
                  placeholder = "Enter name",
                  modifier = Modifier.testTag("inputName"))

              // Description input field
              CustomInputField(
                  value = description,
                  onValueChange = { description = it },
                  label = "Description",
                  placeholder = "Enter description",
                  supportingText = "max. 200 characters",
                  modifier = Modifier.testTag("inputDescription"))

              // Select favorite music genres
              SelectFavoriteMusicGenres(
                  onGenreSelectionVisibilityChanged = { isGenreSelectionVisible = it })

              Spacer(modifier = Modifier.height(6.dp))

              // Save button
              PrincipalButton("Save", "saveButton") {
                val updatedProfile =
                    ProfileData(
                        bio = description,
                        name = name,
                        username = currentProfile.value?.username ?: "",
                        email = currentProfile.value?.email ?: "",
                        favoriteMusicGenres = favoriteMusicGenres,
                        profilePicture = imageCover)
                profileViewModel.updateProfile(updatedProfile)
                navigationActions.navigateToAndClearAllBackStack(Screen.HOME)
              }

              // Show music genre selection dialog if visible
              if (isGenreSelectionVisible) {
                MusicGenreSelectionDialog(
                    musicGenres = MusicGenre.getAllGenres(),
                    selectedGenres = favoriteMusicGenres,
                    onDismissRequest = { isGenreSelectionVisible = false },
                    onGenresSelected = { selectedGenres ->
                      favoriteMusicGenres = selectedGenres
                      isGenreSelectionVisible = false
                    })
              }
            }
      }
}

@Composable
fun ProfileBuildTitle() {
  Text(
      text = "Account created !\nNow build up your profile",
      color = MaterialTheme.colorScheme.primary,
      style = MaterialTheme.typography.displayLarge,
      modifier = Modifier.padding(bottom = 16.dp).fillMaxWidth().testTag("profileBuildTitle"))
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun AddProfilePicture(
    permissionLauncher: ManagedActivityResultLauncher<String, Boolean>,
    profilePicture: MutableState<Bitmap?>
) {
  ProfilePicture(profilePicture)
  Text(
      "Add photo",
      modifier =
          Modifier.testTag("addProfilePicture").clickable {
            permissionLauncher.launch(READ_MEDIA_IMAGES)
          },
      color = MaterialTheme.colorScheme.onPrimary,
      style = MaterialTheme.typography.labelLarge)
}
