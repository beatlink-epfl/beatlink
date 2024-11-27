package com.epfl.beatlink.ui.library

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Card
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.R
import com.epfl.beatlink.model.spotify.objects.SpotifyTrack
import com.epfl.beatlink.model.spotify.objects.State
import com.epfl.beatlink.ui.components.EditButton
import com.epfl.beatlink.ui.components.FilledButton
import com.epfl.beatlink.ui.components.IconWithText
import com.epfl.beatlink.ui.components.PrincipalButton
import com.epfl.beatlink.ui.components.ScreenTopAppBar
import com.epfl.beatlink.ui.components.ViewDescriptionButton
import com.epfl.beatlink.ui.components.library.TrackVoteCard
import com.epfl.beatlink.ui.navigation.AppIcons.collab
import com.epfl.beatlink.ui.navigation.BottomNavigationMenu
import com.epfl.beatlink.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen.EDIT_PLAYLIST
import com.epfl.beatlink.ui.theme.TypographyPlaylist
import com.epfl.beatlink.viewmodel.library.PlaylistViewModel
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel

@Composable
fun PlaylistOverviewScreen(
    navigationActions: NavigationActions,
    profileViewModel: ProfileViewModel,
    playlistViewModel: PlaylistViewModel
) {
  val selectedPlaylistState =
      playlistViewModel.selectedPlaylist.collectAsState().value
          ?: return Text("No Playlist selected.")

  val isOwner = selectedPlaylistState.userId == playlistViewModel.getUserId()
  val isCollab = selectedPlaylistState.playlistCollaborators.contains(playlistViewModel.getUserId())

  val fetchedUsernames = mutableListOf<String>()
  var collabUsernames by remember { mutableStateOf<List<String>>(emptyList()) }

  selectedPlaylistState.playlistCollaborators.forEach { userId ->
    profileViewModel.getUsername(userId) { username ->
      if (username != null) {
        fetchedUsernames.add(username)
      }
      collabUsernames = fetchedUsernames.toList()
    }
  }

  val sample =
      SpotifyTrack(
          name = "This is a song",
          artist = "john",
          trackId = "1",
          cover = "",
          duration = 1,
          popularity = 50,
          state = State.PAUSE)

  Scaffold(
      modifier = Modifier.testTag("playlistOverviewScreen"),
      topBar = {
        ScreenTopAppBar(
            selectedPlaylistState.playlistName,
            "playlistName",
            navigationActions,
            listOf { if (isOwner) EditButton { navigationActions.navigateTo(EDIT_PLAYLIST) } })
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
                Modifier.padding(innerPadding)
                    .padding(vertical = 16.dp)
                    .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally) {
              Row(
                  horizontalArrangement = Arrangement.spacedBy(30.dp),
                  modifier =
                      Modifier.padding(top = 14.dp, bottom = 14.dp, start = 30.dp).height(150.dp)) {
                    // Cover image
                    Card(
                        modifier = Modifier.testTag("playlistCoverCard"),
                        shape = RoundedCornerShape(10.dp)) {
                          Image(
                              painter = painterResource(id = R.drawable.cover_test1), // TODO
                              contentDescription = "Playlist cover",
                              modifier = Modifier.size(150.dp))
                        }

                    // Playlist details
                    Column(
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceBetween) {
                          Text(
                              text = selectedPlaylistState.playlistName,
                              style = TypographyPlaylist.headlineLarge,
                              color = MaterialTheme.colorScheme.primary,
                              modifier = Modifier.testTag("playlistTitle"))
                          Spacer(modifier = Modifier.height(4.dp))
                          IconWithText(
                              "@" + selectedPlaylistState.playlistOwner,
                              "ownerText",
                              Icons.Outlined.AccountCircle,
                              TypographyPlaylist.headlineMedium)
                          IconWithText(
                              collabUsernames.joinToString(", "),
                              "collaboratorsText",
                              collab,
                              TypographyPlaylist.headlineSmall)
                          IconWithText(
                              if (selectedPlaylistState.playlistPublic) "Public" else "Private",
                              "publicText",
                              Icons.Outlined.Lock,
                              TypographyPlaylist.headlineSmall)
                          Spacer(modifier = Modifier.height(10.dp))
                          ViewDescriptionButton {}
                        }
                  }
              Spacer(modifier = Modifier.height(16.dp))
              if (isOwner || isCollab) {
                FilledButton(
                    "Add to this playlist",
                    "addToThisPlaylistButton") { /* Opens a page to add songs */}
                Spacer(modifier = Modifier.height(16.dp))
              }

              if (isOwner) {
                PrincipalButton(
                    "Export this playlist", "exportButton") { /* Exports the playlist to Spotify */}
                Spacer(modifier = Modifier.height(16.dp))
              }

              if (selectedPlaylistState.nbTracks == 0) {
                Text(
                    text = "NO SONGS ADDED",
                    style = TypographyPlaylist.displayMedium,
                    modifier = Modifier.padding(top = 165.dp).testTag("emptyPlaylistPrompt"))
              } else {
                Box(modifier = Modifier.fillMaxSize().heightIn(min = 0.dp, max = 400.dp)) {
                  LazyColumn(
                      verticalArrangement = Arrangement.Top,
                      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                      modifier = Modifier.fillMaxSize()) {
                        // List of tracks
                        items(1) { trackId ->
                          // val track = playlistViewModel.getTrackById(trackId)
                          TrackVoteCard(sample)
                        }
                      }
                }
              }
            }
      })
}
