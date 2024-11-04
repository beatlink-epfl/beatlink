package com.epfl.beatlink.ui.library

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.model.profile.ProfileData
import com.epfl.beatlink.ui.authentication.CustomInputField
import com.epfl.beatlink.ui.components.CollabButton
import com.epfl.beatlink.ui.components.CollabList
import com.epfl.beatlink.ui.components.PrincipalButton
import com.epfl.beatlink.ui.components.ScreenTopAppBar
import com.epfl.beatlink.ui.components.SettingsSwitch
import com.epfl.beatlink.ui.navigation.BottomNavigationMenu
import com.epfl.beatlink.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.theme.PrimaryGray
import com.epfl.beatlink.ui.theme.SecondaryGray

@Composable
fun CreateNewPlaylistScreen(
    navigationActions: NavigationActions,
    currentMusicPlayed: String? = null,
) {
  var playlistTitle by remember { mutableStateOf("") }
  var playlistDescription by remember { mutableStateOf("") }
  var playlistIsPublic by remember { mutableStateOf(false) }
  val playlistCollab by remember { mutableStateOf<List<ProfileData>>(emptyList()) } // TODO
  val coverImage by remember { mutableStateOf<Painter?>(null) }

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
                if (coverImage != null) {
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
              onValueChange = { playlistTitle = it },
              label = "Playlist Title",
              placeholder = "Enter Playlist Title",
              supportingText = "Max 30 characters",
              modifier = Modifier.testTag("inputPlaylistTitle"))

          // DESCRIPTION
          CustomInputField(
              value = playlistDescription,
              onValueChange = { playlistDescription = it },
              label = "Playlist Description",
              placeholder = "Enter Playlist Description",
              singleLine = false,
              supportingText = "Max 200 characters",
              modifier = Modifier.testTag("inputPlaylistDescription"))

          Spacer(Modifier.height(0.dp))

          SettingsSwitch("Make Playlist Public", "makePlaylistPublicText", playlistIsPublic) {
            playlistIsPublic = it
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

          PrincipalButton("Create", "createPlaylist") {}
        }
  }
}