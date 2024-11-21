package com.epfl.beatlink.ui.library

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.epfl.beatlink.ui.navigation.Screen.PLAYLIST_OVERVIEW
import com.epfl.beatlink.viewmodel.library.PlaylistViewModel
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel

@Composable
fun CreateNewPlaylistScreen(
    navigationActions: NavigationActions,
    profileViewModel: ProfileViewModel,
    playlistViewModel: PlaylistViewModel,
) {
  LaunchedEffect(Unit) { profileViewModel.fetchProfile() }
  val profileData by profileViewModel.profile.collectAsState()
  var playlistTitle by remember { mutableStateOf("") }
  var playlistDescription by remember { mutableStateOf("") }
  var playlistIsPublic by remember { mutableStateOf(false) }
  val playlistCollab by remember { mutableStateOf<List<String>>(emptyList()) } // user IDs
  val coverImage by remember { mutableStateOf("") }

  val context = LocalContext.current
  var titleError by remember { mutableStateOf(true) }
  var descriptionError by remember { mutableStateOf(false) }

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
  ) { innerPadding ->
    Column(
        modifier =
            Modifier.fillMaxWidth()
                .padding(innerPadding)
                .padding(top = 16.dp)
                .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
          // Playlist Cover
          PlaylistCover(coverImage)

          // TITLE
          CustomInputField(
              value = playlistTitle,
              onValueChange = {
                playlistTitle = it
                if (it.length in 1..MAX_PLAYLIST_TITLE_LENGTH) {
                  titleError = false
                } else {
                  titleError = true
                }
              },
              label = "Playlist Title *",
              placeholder = "Enter Playlist Title",
              supportingText = "Max $MAX_PLAYLIST_TITLE_LENGTH characters",
              modifier = Modifier.testTag("inputPlaylistTitle"),
              isError = titleError)

          // DESCRIPTION
          CustomInputField(
              value = playlistDescription,
              onValueChange = {
                playlistDescription = it
                if (it.length <= MAX_PLAYLIST_DESCRIPTION_LENGTH) {
                  descriptionError = false
                } else {
                  descriptionError = true
                }
              },
              label = "Playlist Description",
              placeholder = "Enter Playlist Description",
              singleLine = false,
              supportingText = "Max $MAX_PLAYLIST_DESCRIPTION_LENGTH characters",
              modifier = Modifier.testTag("inputPlaylistDescription"),
              isError = descriptionError)

          Spacer(Modifier.height(0.dp))

          SettingsSwitch("Make Playlist Public", "makePlaylistPublicText", playlistIsPublic) {
            playlistIsPublic = it
          }

          CollaboratorsSection(playlistCollab)

          PrincipalButton("Create", "createPlaylist") {
            if (titleError || descriptionError) {
              Toast.makeText(context, "Fields not correctly filled", Toast.LENGTH_SHORT).show()
            } else {
              val newPlaylist =
                  Playlist(
                      playlistID = playlistViewModel.getNewUid(),
                      playlistCover = coverImage,
                      playlistName = playlistTitle,
                      playlistDescription = playlistDescription,
                      playlistPublic = playlistIsPublic,
                      userId = "",
                      playlistOwner = profileData?.username ?: "",
                      playlistCollaborators = playlistCollab,
                      playlistTracks = emptyList(),
                      nbTracks = 0)
              playlistViewModel.addPlaylist(newPlaylist)
              playlistViewModel.selectPlaylist(newPlaylist)
              navigationActions.navigateToAndClearBackStack(PLAYLIST_OVERVIEW, 1)
            }
          }
        }
  }
}
