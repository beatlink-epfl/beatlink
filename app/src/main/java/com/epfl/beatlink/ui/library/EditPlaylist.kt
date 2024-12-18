package com.epfl.beatlink.ui.library

import android.Manifest.permission.READ_MEDIA_IMAGES
import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.epfl.beatlink.model.profile.ProfileData
import com.epfl.beatlink.ui.components.CustomInputField
import com.epfl.beatlink.ui.components.DeleteButton
import com.epfl.beatlink.ui.components.PrincipalButton
import com.epfl.beatlink.ui.components.ScreenTopAppBar
import com.epfl.beatlink.ui.components.SettingsSwitch
import com.epfl.beatlink.ui.components.library.CollaboratorsSection
import com.epfl.beatlink.ui.components.library.PlaylistCover
import com.epfl.beatlink.ui.components.library.PlaylistModifierColumn
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen.EDIT_PLAYLIST
import com.epfl.beatlink.ui.navigation.Screen.INVITE_COLLABORATORS
import com.epfl.beatlink.ui.navigation.Screen.MY_PLAYLISTS
import com.epfl.beatlink.ui.navigation.Screen.PLAYLIST_OVERVIEW
import com.epfl.beatlink.utils.ImageUtils.base64ToBitmap
import com.epfl.beatlink.utils.ImageUtils.permissionLauncher
import com.epfl.beatlink.utils.ImageUtils.resizeAndCompressImageFromUri
import com.epfl.beatlink.viewmodel.library.PlaylistViewModel
import com.epfl.beatlink.viewmodel.profile.FriendRequestViewModel
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuppressLint("SuspiciousIndentation")
@Composable
fun EditPlaylistScreen(
    navigationActions: NavigationActions,
    profileViewModel: ProfileViewModel,
    friendRequestViewModel: FriendRequestViewModel,
    playlistViewModel: PlaylistViewModel
) {
  val context = LocalContext.current
  val selectedPlaylistState =
      playlistViewModel.selectedPlaylist.collectAsState().value
          ?: return Text("No Playlist selected.")
  // Preload Temporary State
  LaunchedEffect(selectedPlaylistState) {
    playlistViewModel.preloadTemporaryState(selectedPlaylistState)
  }
  val playlistTitle by playlistViewModel.tempPlaylistTitle.collectAsState()
  val playlistDescription by playlistViewModel.tempPlaylistDescription.collectAsState()
  val playlistIsPublic by playlistViewModel.tempPlaylistIsPublic.collectAsState()
  val playlistCollab by playlistViewModel.tempPlaylistCollaborators.collectAsState() // user IDs

  // Load Playlist Cover
  var playlistCover by remember { mutableStateOf(selectedPlaylistState.playlistCover ?: "") }
  LaunchedEffect(Unit) {
    playlistViewModel.loadPlaylistCover(selectedPlaylistState) {
      playlistViewModel.coverImage.value = it
    }
  }

  // Permission Launcher
  val permissionLauncher =
      permissionLauncher(context) { uri: Uri? ->
        if (uri == null) {
          // Do nothing
        } else {
          playlistCover = resizeAndCompressImageFromUri(uri, context) ?: ""
          playlistViewModel.coverImage.value = base64ToBitmap(playlistCover)
        }
      }

  var titleError by remember { mutableStateOf(false) }
  var descriptionError by remember { mutableStateOf(false) }

  var showDialog by remember { mutableStateOf(false) }

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

  val fetchedProfileData = mutableListOf<ProfileData>()
  var collabProfileData by remember { mutableStateOf<List<ProfileData>>(emptyList()) }

  fetchedProfileData.clear()
  playlistCollab.forEach { userId ->
    profileViewModel.fetchProfileById(userId) { profile ->
      if (profile != null) {
        fetchedProfileData.add(profile)
        // Update the state after all additions to avoid unnecessary recompositions
        collabProfileData = fetchedProfileData.toList()
      }
    }
  }

  DisposableEffect(Unit) {
    onDispose {
      if (navigationActions.currentRoute() !in listOf(EDIT_PLAYLIST, INVITE_COLLABORATORS)) {
        playlistViewModel.resetTemporaryState()
      }
    }
  }
  Scaffold(
      modifier = Modifier.testTag("editPlaylistScreen"),
      topBar = {
        ScreenTopAppBar(
            "Edit " + selectedPlaylistState.playlistName,
            "editPlaylistTitle",
            navigationActions,
            listOf {
              DeleteButton {
                selectedPlaylistState.playlistID.let { playlistViewModel.deletePlaylistById(it) }
                navigationActions.navigateToAndClearBackStack(MY_PLAYLISTS, 2)
                Toast.makeText(context, "Playlist deleted successfully!", Toast.LENGTH_LONG).show()
              }
            })
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
              onValueChange = { newTitle ->
                playlistViewModel.updateTemporallyTitle(newTitle)
                titleError = newTitle.length !in 1..MAX_PLAYLIST_TITLE_LENGTH
              },
              label = "Playlist Title",
              placeholder = "Enter Playlist Title",
              supportingText = "Max $MAX_PLAYLIST_TITLE_LENGTH characters",
              modifier = Modifier.testTag("inputPlaylistTitle"),
              isError = titleError)

          // DESCRIPTION
          CustomInputField(
              value = playlistDescription,
              onValueChange = { newDescription ->
                playlistViewModel.updateTemporallyDescription(newDescription)
                descriptionError = newDescription.length >= MAX_PLAYLIST_DESCRIPTION_LENGTH
              },
              label = "Playlist Description",
              placeholder = "Enter Playlist Description",
              singleLine = false,
              supportingText = "Max $MAX_PLAYLIST_DESCRIPTION_LENGTH characters",
              modifier = Modifier.testTag("inputPlaylistDescription"),
              isError = descriptionError)

          Spacer(Modifier.height(0.dp))

          SettingsSwitch("Make Playlist Public", "makePlaylistPublicText", playlistIsPublic) {
              newOption ->
            playlistViewModel.updateTemporallyIsPublic(newOption)
          }

          CollaboratorsSection(
              collabUsernames,
              collabProfileData,
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

          PrincipalButton("Save", "saveEditPlaylist") {
            if (titleError || descriptionError) {
              Toast.makeText(context, "Fields not correctly filled", Toast.LENGTH_SHORT).show()
            } else {
              val updatedPlaylist =
                  Playlist(
                      playlistID = selectedPlaylistState.playlistID,
                      playlistCover = playlistCover,
                      playlistName = playlistTitle,
                      playlistDescription = playlistDescription,
                      playlistPublic = playlistIsPublic,
                      userId = selectedPlaylistState.userId,
                      playlistOwner = selectedPlaylistState.playlistOwner,
                      playlistCollaborators = playlistCollab,
                      playlistTracks = selectedPlaylistState.playlistTracks,
                      nbTracks = selectedPlaylistState.nbTracks)
              playlistViewModel.updatePlaylist(updatedPlaylist)
              playlistViewModel.selectPlaylist(updatedPlaylist)
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
        playlistViewModel,
        onDismissRequest = { showDialog = false })
  }
}
