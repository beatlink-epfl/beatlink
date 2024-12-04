package com.epfl.beatlink.ui.library

import android.Manifest.permission.READ_MEDIA_IMAGES
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import com.epfl.beatlink.model.library.Playlist
import com.epfl.beatlink.model.library.Playlist.Companion.MAX_PLAYLIST_DESCRIPTION_LENGTH
import com.epfl.beatlink.model.library.Playlist.Companion.MAX_PLAYLIST_TITLE_LENGTH
import com.epfl.beatlink.ui.components.CustomInputField
import com.epfl.beatlink.ui.components.PrincipalButton
import com.epfl.beatlink.ui.components.ScreenTopAppBar
import com.epfl.beatlink.ui.components.SettingsSwitch
import com.epfl.beatlink.ui.components.library.CollaboratorsSection
import com.epfl.beatlink.ui.components.library.PlaylistCover
import com.epfl.beatlink.ui.navigation.BottomNavigationMenu
import com.epfl.beatlink.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen.CREATE_NEW_PLAYLIST
import com.epfl.beatlink.ui.navigation.Screen.INVITE_COLLABORATORS
import com.epfl.beatlink.ui.navigation.Screen.PLAYLIST_OVERVIEW
import com.epfl.beatlink.utils.ImageUtils.permissionLauncher
import com.epfl.beatlink.viewmodel.library.PlaylistViewModel
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun CreateNewPlaylistScreen(
    navigationActions: NavigationActions,
    profileViewModel: ProfileViewModel,
    playlistViewModel: PlaylistViewModel,
) {
  LaunchedEffect(Unit) { profileViewModel.fetchProfile() }
  val profileData by profileViewModel.profile.collectAsState()
  val playlistTitle by playlistViewModel.tempPlaylistTitle.collectAsState()
  val playlistDescription by playlistViewModel.tempPlaylistDescription.collectAsState()
  val playlistIsPublic by playlistViewModel.tempPlaylistIsPublic.collectAsState()
  val playlistCollab by playlistViewModel.tempPlaylistCollaborators.collectAsState() // user IDs
  var imageUri by remember { mutableStateOf(Uri.EMPTY) }
  val coverImage = remember { mutableStateOf<Bitmap?>(null) }

  val context = LocalContext.current
  val titleError = playlistTitle.length !in 1..MAX_PLAYLIST_TITLE_LENGTH
  val descriptionError = playlistDescription.length > MAX_PLAYLIST_DESCRIPTION_LENGTH

  var showDialog by remember { mutableStateOf(false) }

  // Permission launcher for reading images
  val permissionLauncher =
      permissionLauncher(context) { uri: Uri? ->
        imageUri = uri
        if (imageUri == null) {
          coverImage.value = null
        } else {
          profileViewModel.uploadProfilePicture(context, imageUri)
          profileViewModel.loadProfilePicture { coverImage.value = it }
        }
      }

  val fetchedUsernames = mutableListOf<String>()
  var collabUsernames by remember { mutableStateOf<List<String>>(emptyList()) }
  playlistCollab.forEach { userId ->
    profileViewModel.getUsername(userId) { username ->
      if (username != null) {
        fetchedUsernames.add(username)
      }
      collabUsernames = fetchedUsernames.toList()
    }
  }

  DisposableEffect(Unit) {
    onDispose {
      if (navigationActions.currentRoute() !in listOf(CREATE_NEW_PLAYLIST, INVITE_COLLABORATORS)) {
        playlistViewModel.resetTemporaryState()
      }
    }
  }

  Scaffold(
      modifier = Modifier.testTag("createNewPlaylistScreen"),
      topBar = {
        ScreenTopAppBar("Create a new playlist", "createNewPlaylistTitle", navigationActions)
      },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = navigationActions.currentRoute())
      },
      content = { innerPadding ->
        Column(
            modifier =
                Modifier.fillMaxWidth()
                    .padding(innerPadding)
                    .padding(top = 16.dp)
                    .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
              // Playlist Cover
              PlaylistCover(
                  coverImage,
                  Modifier.size(55.dp),
                  onClick = { permissionLauncher.launch(READ_MEDIA_IMAGES) })

              // TITLE
              CustomInputField(
                  value = playlistTitle,
                  onValueChange = { playlistViewModel.updateTemporallyTitle(it) },
                  label = "Playlist Title *",
                  placeholder = "Enter Playlist Title",
                  supportingText = "Max $MAX_PLAYLIST_TITLE_LENGTH characters",
                  modifier = Modifier.testTag("inputPlaylistTitle"),
                  isError = titleError)

              // DESCRIPTION
              CustomInputField(
                  value = playlistDescription,
                  onValueChange = { playlistViewModel.updateTemporallyDescription(it) },
                  label = "Playlist Description",
                  placeholder = "Enter Playlist Description",
                  singleLine = false,
                  supportingText = "Max $MAX_PLAYLIST_DESCRIPTION_LENGTH characters",
                  modifier = Modifier.testTag("inputPlaylistDescription"),
                  isError = descriptionError)

              Spacer(Modifier.height(0.dp))

              SettingsSwitch("Make Playlist Public", "makePlaylistPublicText", playlistIsPublic) {
                playlistViewModel.updateTemporallyIsPublic(it)
              }

              CollaboratorsSection(
                  collabUsernames,
                  onClick = { showDialog = true },
                  onRemove = { usernameToRemove ->
                    profileViewModel.getUserIdByUsername(
                        username = usernameToRemove,
                        onResult = { userIdToRemove ->
                          if (userIdToRemove != null) {
                            val updatedCollabList = playlistCollab.filter { it != userIdToRemove }
                            playlistViewModel.updateTemporallyCollaborators(updatedCollabList)
                            collabUsernames = collabUsernames.filter { it != usernameToRemove }
                          } else {
                            Log.e("ERROR", "Failed to get userId for username: $usernameToRemove")
                          }
                        })
                  })

              PrincipalButton("Create", "createPlaylist") {
                if (titleError || descriptionError) {
                  Toast.makeText(context, "Fields not correctly filled", Toast.LENGTH_SHORT).show()
                } else {
                  val newPlaylist =
                      Playlist(
                          playlistID = playlistViewModel.getNewUid(),
                          playlistCover = "",
                          playlistName = playlistTitle,
                          playlistDescription = playlistDescription,
                          playlistPublic = playlistIsPublic,
                          userId = playlistViewModel.getUserId() ?: "",
                          playlistOwner = profileData?.username ?: "",
                          playlistCollaborators = playlistCollab,
                          playlistTracks = emptyList(),
                          nbTracks = 0)
                  playlistViewModel.addPlaylist(newPlaylist)
                  if (imageUri != Uri.EMPTY) {
                    playlistViewModel.uploadPlaylistCover(imageUri, context, newPlaylist)
                  }
                  playlistViewModel.resetTemporaryState()
                  playlistViewModel.selectPlaylist(newPlaylist)
                  navigationActions.navigateToAndClearBackStack(PLAYLIST_OVERVIEW, 1)
                }
              }
            }
      })
  if (showDialog) {
    InviteCollaboratorsOverlay(navigationActions, onDismissRequest = { showDialog = false })
  }
}
