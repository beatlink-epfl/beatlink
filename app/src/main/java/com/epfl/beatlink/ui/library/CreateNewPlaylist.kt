package com.epfl.beatlink.ui.library

import android.Manifest.permission.READ_MEDIA_IMAGES
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.epfl.beatlink.ui.components.library.PlaylistModifierColumn
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen.CREATE_NEW_PLAYLIST
import com.epfl.beatlink.ui.navigation.Screen.INVITE_COLLABORATORS
import com.epfl.beatlink.ui.navigation.Screen.PLAYLIST_OVERVIEW
import com.epfl.beatlink.utils.ImageUtils.base64ToBitmap
import com.epfl.beatlink.utils.ImageUtils.permissionLauncher
import com.epfl.beatlink.utils.ImageUtils.resizeAndCompressImageFromUri
import com.epfl.beatlink.viewmodel.library.PlaylistViewModel
import com.epfl.beatlink.viewmodel.profile.FriendRequestViewModel
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun CreateNewPlaylistScreen(
    navigationActions: NavigationActions,
    profileViewModel: ProfileViewModel,
    friendRequestViewModel: FriendRequestViewModel,
    playlistViewModel: PlaylistViewModel,
) {
  LaunchedEffect(Unit) { profileViewModel.fetchProfile() }
  val profileData by profileViewModel.profile.collectAsState()
  val playlistTitle by playlistViewModel.tempPlaylistTitle.collectAsState()
  val playlistDescription by playlistViewModel.tempPlaylistDescription.collectAsState()
  val playlistIsPublic by playlistViewModel.tempPlaylistIsPublic.collectAsState()
  val playlistCollab by playlistViewModel.tempPlaylistCollaborators.collectAsState() // user IDs
  var imageUri by remember { mutableStateOf(Uri.EMPTY) }

  val context = LocalContext.current
  val titleError = playlistTitle.length !in 1..MAX_PLAYLIST_TITLE_LENGTH
  val descriptionError = playlistDescription.length > MAX_PLAYLIST_DESCRIPTION_LENGTH

  var showDialog by remember { mutableStateOf(false) }

  // Permission launcher for reading images
  val permissionLauncher =
      permissionLauncher(context) { uri: Uri? ->
        imageUri = uri ?: Uri.EMPTY
        playlistViewModel.coverImage.value =
            base64ToBitmap(resizeAndCompressImageFromUri(imageUri, context) ?: "")
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
      content = { innerPadding ->
        PlaylistModifierColumn(innerPadding) {
          // Playlist Cover
          PlaylistCover(
              playlistViewModel.coverImage,
              100.dp,
              isClickable = true,
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
    InviteCollaboratorsOverlay(
        navigationActions,
        profileViewModel,
        friendRequestViewModel,
        onDismissRequest = { showDialog = false })
  }
}
