package com.epfl.beatlink.ui.profile

import android.Manifest.permission.READ_MEDIA_IMAGES
import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.model.profile.MusicGenre
import com.epfl.beatlink.model.profile.ProfileData
import com.epfl.beatlink.model.profile.ProfileData.Companion.MAX_DESCRIPTION_LENGTH
import com.epfl.beatlink.model.profile.ProfileData.Companion.MAX_USERNAME_LENGTH
import com.epfl.beatlink.ui.authentication.MusicGenreSelectionDialog
import com.epfl.beatlink.ui.authentication.SelectFavoriteMusicGenres
import com.epfl.beatlink.ui.components.CircleWithIcon
import com.epfl.beatlink.ui.components.CustomInputField
import com.epfl.beatlink.ui.components.PrincipalButton
import com.epfl.beatlink.ui.components.ProfilePicture
import com.epfl.beatlink.ui.components.ScreenTopAppBar
import com.epfl.beatlink.ui.navigation.BottomNavigationMenu
import com.epfl.beatlink.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.utils.ImageUtils.base64ToBitmap
import com.epfl.beatlink.utils.ImageUtils.permissionLauncher
import com.epfl.beatlink.utils.ImageUtils.resizeAndCompressImageFromUri
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EditProfileScreen(profileViewModel: ProfileViewModel, navigationActions: NavigationActions) {
  LaunchedEffect(Unit) { profileViewModel.fetchProfile() }
  val profileData by profileViewModel.profile.collectAsState()
  val context = LocalContext.current
  val scrollState = rememberScrollState()

  var name by remember {
    mutableStateOf(
        profileData?.name
            ?: "This is your name. It can be up to $MAX_USERNAME_LENGTH characters long")
  }
  var description by remember {
    mutableStateOf(
        profileData?.bio
            ?: "This is a description. It can be up to $MAX_DESCRIPTION_LENGTH characters long.")
  }
  var imageUri by remember { mutableStateOf(Uri.EMPTY) }
  var isGenreSelectionVisible by remember { mutableStateOf(false) }
  // Load profile picture
  LaunchedEffect(Unit) {
    profileViewModel.loadProfilePicture { profileViewModel.profilePicture.value = it }
  }

  val permissionLauncher =
      permissionLauncher(context) { uri: Uri? ->
        imageUri = uri
        if (imageUri == null) {
          // Do nothing
        } else {
          profileViewModel.profilePicture.value =
              base64ToBitmap(resizeAndCompressImageFromUri(imageUri, context) ?: "")
        }
      }
  Scaffold(
      modifier = Modifier.testTag("editProfileScreen"),
      topBar = { ScreenTopAppBar("Edit profile", "editProfileTitle", navigationActions) },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = navigationActions.currentRoute())
      },
      content = { paddingValues ->
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .testTag("editProfileContent"),
            horizontalAlignment = Alignment.CenterHorizontally) {
              HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
              Spacer(modifier = Modifier.height(50.dp))
              Box(
                  modifier =
                      Modifier.clickable(
                          onClick = { permissionLauncher.launch(READ_MEDIA_IMAGES) })) {
                    ProfilePicture(profileViewModel.profilePicture)
                    Box(modifier = Modifier.align(Alignment.BottomEnd)) {
                      CircleWithIcon(Icons.Filled.Edit, MaterialTheme.colorScheme.primary)
                    }
                  }
              Spacer(modifier = Modifier.height(66.dp))
              CustomInputField(
                  value = name,
                  onValueChange = { newName ->
                    if (newName.length <= MAX_USERNAME_LENGTH) {
                      name = newName
                    }
                  },
                  label = "Name",
                  placeholder = "Current name",
                  supportingText = "Max $MAX_USERNAME_LENGTH characters",
                  trailingIcon = Icons.Filled.Clear,
                  modifier = Modifier.testTag("editProfileNameInput"))
              Spacer(modifier = Modifier.height(16.dp))
              Row {
                CustomInputField(
                    value = description,
                    onValueChange = { newDescription ->
                      if (newDescription.length <= MAX_DESCRIPTION_LENGTH) {
                        description = newDescription
                      }
                    },
                    label = "Description",
                    placeholder = "Current description",
                    supportingText = "Max $MAX_DESCRIPTION_LENGTH characters",
                    singleLine = false,
                    trailingIcon = Icons.Filled.Clear,
                    modifier = Modifier.testTag("editProfileDescriptionInput"))
              }
              Spacer(modifier = Modifier.height(10.dp))
              SelectFavoriteMusicGenres(
                  onGenreSelectionVisibilityChanged = { isGenreSelectionVisible = it })
              Spacer(modifier = Modifier.height(100.dp))
              PrincipalButton(
                  "Save",
                  "saveProfileButton",
                  onClick = {
                    try {
                      val newData =
                          ProfileData(
                              bio = description,
                              links = profileData?.links ?: 0,
                              name = name,
                              profilePicture = profileData?.profilePicture,
                              username = profileData?.username ?: "",
                              email = profileData?.email ?: "",
                              favoriteMusicGenres = profileData?.favoriteMusicGenres ?: emptyList(),
                              topSongs = profileData?.topSongs ?: emptyList(),
                              topArtists = profileData?.topArtists ?: emptyList())
                      profileViewModel.updateProfile(newData)
                      if (imageUri != null) {
                        profileViewModel.uploadProfilePicture(context, imageUri)
                      }
                      Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
                      navigationActions.goBack()
                    } catch (e: Exception) {
                      e.printStackTrace()
                    }
                  })

              if (isGenreSelectionVisible) {
                MusicGenreSelectionDialog(
                    musicGenres = MusicGenre.getAllGenres(),
                    selectedGenres = profileData?.favoriteMusicGenres!!.toMutableList(),
                    onDismissRequest = { isGenreSelectionVisible = false },
                    onGenresSelected = { selectedGenres ->
                      profileData?.favoriteMusicGenres = selectedGenres
                      isGenreSelectionVisible = false
                    })
              }
            }
      })
}
