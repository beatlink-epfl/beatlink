package com.epfl.beatlink.ui.authentication

import android.Manifest.permission.READ_MEDIA_IMAGES
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.model.profile.MusicGenre
import com.epfl.beatlink.model.profile.MusicGenre.Companion.MAX_SELECTABLE_GENRES
import com.epfl.beatlink.model.profile.ProfileData
import com.epfl.beatlink.ui.components.CustomInputField
import com.epfl.beatlink.ui.components.PrincipalButton
import com.epfl.beatlink.ui.components.ProfilePicture
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.ui.theme.PrimaryGradientBrush
import com.epfl.beatlink.utils.ImageUtils.permissionLauncher
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
  val profilePicture = remember { mutableStateOf<Bitmap?>(null) }

  // Load profile picture
  LaunchedEffect(Unit) { profileViewModel.loadProfilePicture { profilePicture.value = it } }
  val permissionLauncher =
      permissionLauncher(context) { uri: Uri? ->
        imageUri = uri
        if (imageUri == null) {
          profilePicture.value = null
        } else {
          profileViewModel.uploadProfilePicture(context, imageUri)
          profileViewModel.loadProfilePicture { profilePicture.value = it }
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
              AddProfilePicture(permissionLauncher, profilePicture)

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
                        profilePicture = "")
                profileViewModel.updateProfile(updatedProfile)
                if (imageUri != null) {
                  profileViewModel.uploadProfilePicture(context, imageUri)
                }
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

@Composable
fun SelectFavoriteMusicGenres(onGenreSelectionVisibilityChanged: (Boolean) -> Unit) {
  Box(
      modifier =
          Modifier.clickable { onGenreSelectionVisibilityChanged(true) }
              .padding(16.dp)
              .border(
                  width = 1.dp,
                  color = MaterialTheme.colorScheme.primary,
                  shape = RoundedCornerShape(5.dp))
              .height(48.dp)
              .width(320.dp)
              .padding(16.dp),
      contentAlignment = Alignment.Center) {
        Text(
            text = "Select your favorite music genres",
            modifier = Modifier.testTag("selectFavoriteGenresText"),
            style = MaterialTheme.typography.bodyLarge)
      }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MusicGenreSelectionDialog(
    musicGenres: List<String>,
    selectedGenres: MutableList<String>,
    onDismissRequest: () -> Unit,
    onGenresSelected: (MutableList<String>) -> Unit
) {
  val genreCheckedStates = remember { mutableStateMapOf<String, Boolean>() }

  // Initialize genre states
  musicGenres.forEach { genre ->
    genreCheckedStates.putIfAbsent(genre, selectedGenres.contains(genre))
  }

  // Track number of selected genres
  val selectedCount = genreCheckedStates.filter { it.value }.size

  // Dialog for selecting user's favorite music genres
  androidx.compose.ui.window.Dialog(onDismissRequest = onDismissRequest) {
    Scaffold(
        modifier = Modifier.fillMaxHeight(0.8F),
        topBar = {
          Box(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
            MusicGenreSelectionDialogTitle()
          }
        },
        bottomBar = {
          Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
            // Show message when the set limit of music genres are selected
            if (selectedCount >= MAX_SELECTABLE_GENRES) {
              Box(
                  modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                  contentAlignment = Alignment.Center) {
                    Text(
                        text = "You can select up to $MAX_SELECTABLE_GENRES genres only",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium)
                  }
            }
            MusicGenreSelectionDialogButtons(
                onDismissRequest = onDismissRequest,
                onGenresSelected = {
                  onGenresSelected(genreCheckedStates.filter { it.value }.keys.toMutableList())
                },
                modifier = Modifier.testTag("dialogButtonRow"))
          }
        },
        content = { _ ->
          Box(
              modifier =
                  Modifier.fillMaxWidth()
                      .background(
                          color = MaterialTheme.colorScheme.surface,
                          shape = MaterialTheme.shapes.medium)
                      .padding(16.dp)
                      .verticalScroll(rememberScrollState())
                      .testTag("musicGenreSelectionDialog")) {
                Column(modifier = Modifier.fillMaxWidth().padding(bottom = 72.dp)) {

                  // Loop through each genre and create a checkbox with a label
                  musicGenres.forEach { genre ->
                    val isChecked = genreCheckedStates[genre] ?: false
                    val isDisabled = selectedCount >= MAX_SELECTABLE_GENRES && !isChecked

                    MusicGenreCheckbox(
                        musicGenre = genre,
                        checkedState = isChecked,
                        isDisabled = isDisabled,
                        onCheckedChange = { newCheckedState ->
                          if (newCheckedState) {
                            if (selectedCount < MAX_SELECTABLE_GENRES) {
                              genreCheckedStates[genre] = true
                            }
                          } else {
                            genreCheckedStates[genre] = false
                          }
                        })
                  }
                }
              }
        })
  }
}

@Composable
fun MusicGenreSelectionDialogTitle() {
  Column(modifier = Modifier.padding(bottom = 8.dp)) {
    Text(
        text = "MUSIC GENRES",
        style = MaterialTheme.typography.headlineLarge,
        modifier =
            Modifier.graphicsLayer(alpha = 0.99f)
                .drawWithCache {
                  onDrawWithContent {
                    drawContent()
                    drawRect(PrimaryGradientBrush, blendMode = BlendMode.SrcAtop)
                  }
                }
                .testTag("dialogTitle"))
  }
}

// Genre checkbox --> Checkbox + Label
@Composable
fun MusicGenreCheckbox(
    musicGenre: String,
    checkedState: Boolean,
    isDisabled: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
  Row(
      Modifier.fillMaxWidth()
          .height(56.dp)
          .toggleable(value = checkedState, onValueChange = onCheckedChange, role = Role.Checkbox)
          .padding(horizontal = 16.dp),
      verticalAlignment = Alignment.CenterVertically) {

        // Checkbox
        Checkbox(
            checked = checkedState,
            onCheckedChange = { if (!isDisabled) onCheckedChange(!checkedState) },
            colors =
                CheckboxDefaults.colors(
                    uncheckedColor = MaterialTheme.colorScheme.primary,
                    checkmarkColor = MaterialTheme.colorScheme.surface,
                    checkedColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.testTag("genreCheckbox_${musicGenre.replace(" ", "_")}"))

        // Music genre text
        Text(
            text = musicGenre,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyMedium,
            modifier =
                Modifier.padding(start = 16.dp)
                    .testTag("genreCheckboxLabel_${musicGenre.replace(" ", "_")}")
                    .alpha(if (isDisabled) 0.5f else 1f))
      }
}

@Composable
fun MusicGenreSelectionDialogButtons(
    onDismissRequest: () -> Unit,
    onGenresSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
  Row(
      modifier =
          modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp)
              .padding(top = 16.dp) // Add padding if needed
      ) {
        // Spacer to push the buttons to the right
        Spacer(modifier = Modifier.weight(1f))

        // Cancel Button
        Text(
            text = "CANCEL",
            modifier = Modifier.clickable { onDismissRequest() }.testTag("cancelButton"),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary)

        // Ok Button
        Text(
            text = "OK",
            modifier =
                Modifier.clickable { onGenresSelected() }
                    .padding(start = 32.dp, end = 8.dp)
                    .testTag("okButton"),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary)
      }
}
