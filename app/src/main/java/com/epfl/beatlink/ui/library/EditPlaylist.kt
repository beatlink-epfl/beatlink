package com.epfl.beatlink.ui.library

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.model.library.Playlist
import com.epfl.beatlink.model.library.Playlist.Companion.MAX_PLAYLIST_DESCRIPTION_LENGTH
import com.epfl.beatlink.model.library.Playlist.Companion.MAX_PLAYLIST_TITLE_LENGTH
import com.epfl.beatlink.ui.components.CollabButton
import com.epfl.beatlink.ui.components.CollabList
import com.epfl.beatlink.ui.components.CustomInputField
import com.epfl.beatlink.ui.components.DeleteButton
import com.epfl.beatlink.ui.components.PrincipalButton
import com.epfl.beatlink.ui.components.ScreenTopAppBar
import com.epfl.beatlink.ui.components.SettingsSwitch
import com.epfl.beatlink.ui.navigation.BottomNavigationMenu
import com.epfl.beatlink.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen.MY_PLAYLISTS
import com.epfl.beatlink.ui.navigation.Screen.PLAYLIST_OVERVIEW
import com.epfl.beatlink.ui.theme.PrimaryGray
import com.epfl.beatlink.ui.theme.SecondaryGray
import com.epfl.beatlink.viewmodel.library.PlaylistViewModel
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel

@Composable
fun EditPlaylistScreen(
    navigationActions: NavigationActions,
    profileViewModel: ProfileViewModel,
    playlistViewModel: PlaylistViewModel
) {
  val context = LocalContext.current
  val selectedPlaylistState =
      playlistViewModel.selectedPlaylist.collectAsState().value
          ?: return Text("No Playlist selected.")
  var playlistTitle by remember { mutableStateOf(selectedPlaylistState.playlistName) }
  var playlistDescription by remember { mutableStateOf(selectedPlaylistState.playlistDescription) }
  var playlistIsPublic by remember { mutableStateOf(selectedPlaylistState.playlistPublic) }
  val playlistCollab by remember {
    mutableStateOf(selectedPlaylistState.playlistCollaborators)
  } // user IDs
  val coverImage by remember { mutableStateOf(selectedPlaylistState.playlistCover) }

  var titleError by remember { mutableStateOf(false) }
  var descriptionError by remember { mutableStateOf(false) }

  Scaffold(
      modifier = Modifier.testTag("editPlaylistScreen"),
      topBar = {
        ScreenTopAppBar(
            "Edit " + selectedPlaylistState.playlistName,
            "editPlaylistTitle",
            navigationActions,
            listOf {
              DeleteButton {
                selectedPlaylistState.playlistID.let { playlistViewModel.deletePlaylist(it) }
                navigationActions.navigateTo(MY_PLAYLISTS)
                Toast.makeText(context, "Playlist deleted successfully!", Toast.LENGTH_LONG).show()
              }
            })
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
              Box(
                  modifier =
                      Modifier.background(
                              color = SecondaryGray, shape = RoundedCornerShape(size = 10.dp))
                          .clickable(onClick = { /* Opens option gallery or camera */})
                          .width(100.dp)
                          .height(100.dp)
                          .align(Alignment.CenterHorizontally)
                          .testTag("playlistCover"),
                  contentAlignment = Alignment.Center) {
                    if (coverImage == "something") {
                      // Show the selected cover image
                      // TODO

                    } else {
                      // Placeholder content if no image is selected
                      Text(
                          text = "Add \n Playlist Cover",
                          style = MaterialTheme.typography.bodyLarge,
                          color = PrimaryGray,
                          textAlign = TextAlign.Center,
                          modifier = Modifier.testTag("emptyCoverText"))
                    }
                  }

              // TITLE
              CustomInputField(
                  value = playlistTitle,
                  onValueChange = { newTitle ->
                    playlistTitle = newTitle
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
                    playlistDescription = newDescription
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
                playlistIsPublic = newOption
              }

              Spacer(Modifier.height(5.dp))

              Row(
                  verticalAlignment = Alignment.CenterVertically, // Center items vertically
                  horizontalArrangement = Arrangement.SpaceBetween, // Space items apart
                  modifier = Modifier.width(320.dp)) {
                    Text(
                        text = "Collaborators",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.testTag("collaboratorsTitle"))
                    CollabButton {}
                  }
              CollabList(playlistCollab)

              Spacer(modifier = Modifier.height(10.dp))

              PrincipalButton("Save", "saveEditPlaylist") {
                if (titleError || descriptionError) {
                  Toast.makeText(context, "Fields not correctly filled", Toast.LENGTH_SHORT).show()
                } else {
                  val updatedPlaylist =
                      Playlist(
                          playlistID = selectedPlaylistState.playlistID,
                          playlistCover = coverImage,
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
                  navigationActions.navigateTo(PLAYLIST_OVERVIEW)
                }
              }
            }
      })
}
