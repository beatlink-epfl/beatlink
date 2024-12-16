package com.epfl.beatlink.ui.library

import android.graphics.Bitmap
import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.model.library.Playlist
import com.epfl.beatlink.ui.components.EditButton
import com.epfl.beatlink.ui.components.FilledButton
import com.epfl.beatlink.ui.components.IconWithText
import com.epfl.beatlink.ui.components.PrincipalButton
import com.epfl.beatlink.ui.components.ScreenTopAppBar
import com.epfl.beatlink.ui.components.ViewDescriptionButton
import com.epfl.beatlink.ui.components.library.GrayBox
import com.epfl.beatlink.ui.components.library.PlaylistCover
import com.epfl.beatlink.ui.components.library.TrackVoteCard
import com.epfl.beatlink.ui.navigation.AppIcons.collab
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.ui.navigation.Screen.ADD_TRACK_TO_PLAYLIST
import com.epfl.beatlink.ui.navigation.Screen.EDIT_PLAYLIST
import com.epfl.beatlink.ui.theme.TypographyPlaylist
import com.epfl.beatlink.viewmodel.library.PlaylistViewModel
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel
import com.epfl.beatlink.viewmodel.spotify.api.SpotifyApiViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun PlaylistOverviewScreen(
    navigationActions: NavigationActions,
    profileViewModel: ProfileViewModel,
    playlistViewModel: PlaylistViewModel,
    spotifyViewModel: SpotifyApiViewModel
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  // Observe the currently selected playlist
  val selectedPlaylistState =
      playlistViewModel.selectedPlaylist.collectAsState().value
          ?: return Text("No Playlist selected.")

  // Description Overlay Display
  var showDialogOverlay by remember { mutableStateOf(false) }

  // Export Playlist Dialog
  var showDialogExport by remember { mutableStateOf(false) }

  // Determine if the user is the owner or a collaborator
  val currentUserId = playlistViewModel.getUserId()
  val isOwner = selectedPlaylistState.userId == currentUserId
  val isCollab = selectedPlaylistState.playlistCollaborators.contains(currentUserId)

  // Fetch collaborator usernames
  var collabUsernames by remember { mutableStateOf<List<String>>(emptyList()) }
  val fetchedUsernames = remember { mutableSetOf<String>() }

  // Retrieve collaborator usernames
  selectedPlaylistState.playlistCollaborators.forEach { userId ->
    profileViewModel.getUsername(userId) { username ->
      if (username != null && !fetchedUsernames.contains(username)) {
        fetchedUsernames.add(username)
        collabUsernames = fetchedUsernames.toList()
      }
    }
  }

  // Load the playlist cover image
  val coverImage = remember { mutableStateOf<Bitmap?>(null) }
  LaunchedEffect(Unit) {
    playlistViewModel.loadPlaylistCover(selectedPlaylistState) { coverImage.value = it }
  }

  Scaffold(
      modifier = Modifier.testTag("playlistOverviewScreen"),
      topBar = {
        ScreenTopAppBar(
            selectedPlaylistState.playlistName,
            "playlistName",
            navigationActions,
            listOf { if (isOwner) EditButton { navigationActions.navigateTo(EDIT_PLAYLIST) } })
      },
      content = { innerPadding ->
        Column(
            modifier =
                Modifier.padding(innerPadding)
                    .padding(vertical = 16.dp)
                    .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally) {
              // Playlist Header Section
              Row(
                  modifier =
                      Modifier.padding(horizontal = 30.dp, vertical = 14.dp).height(150.dp)) {
                    // Playlist Cover Image
                    if (coverImage.value == null) {
                      GrayBox(size = 135.dp)
                    } else {
                      PlaylistCover(coverImage, 135.dp)
                    }

                    // Spacer for scaling space dynamically
                    Spacer(modifier = Modifier.weight(0.1f))

                    Box(modifier = Modifier.weight(1f).fillMaxSize()) {
                      // Playlist details
                      Column(
                          modifier = Modifier.fillMaxHeight(),
                          verticalArrangement = Arrangement.SpaceBetween) {
                            IconWithText(
                                "@" + selectedPlaylistState.playlistOwner,
                                "ownerText",
                                Icons.Outlined.AccountCircle,
                                TypographyPlaylist.headlineMedium)
                            if (collabUsernames.isNotEmpty()) {
                              IconWithText(
                                  collabUsernames.joinToString(", "),
                                  "collaboratorsText",
                                  collab,
                                  TypographyPlaylist.headlineSmall)
                            }
                            IconWithText(
                                if (selectedPlaylistState.playlistPublic) "Public" else "Private",
                                "publicText",
                                Icons.Outlined.Lock,
                                TypographyPlaylist.headlineSmall)

                            IconWithText(
                                "${selectedPlaylistState.nbTracks} tracks",
                                "nbTracksText",
                                Icons.Outlined.Star,
                                TypographyPlaylist.headlineSmall)
                            Spacer(modifier = Modifier.height(10.dp))
                            ViewDescriptionButton { showDialogOverlay = true }
                          }
                    }
                  }
              Spacer(modifier = Modifier.height(16.dp))

              // Action Buttons for Playlist Management
              if (isOwner || isCollab) {
                FilledButton(
                    "Add to this playlist",
                    "addToThisPlaylistButton",
                    onClick = { navigationActions.navigateTo(ADD_TRACK_TO_PLAYLIST) })
                Spacer(modifier = Modifier.height(16.dp))
              }

              if (isOwner) {
                PrincipalButton("Export this playlist", "exportButton") {
                  if (selectedPlaylistState.nbTracks > 0) {
                    showDialogExport = true
                  } else {
                    Toast.makeText(context, "No songs added to playlist", Toast.LENGTH_SHORT).show()
                  }
                }
                Spacer(modifier = Modifier.height(16.dp))
              }

              // Display Tracks or Empty State
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
                        // Sort tracks by likes in descending order before displaying
                        val sortedTracks =
                            selectedPlaylistState.playlistTracks.sortedByDescending { it.likes }
                        items(sortedTracks) { track ->
                          TrackVoteCard(
                              playlistTrack = track, // Ensure track is of type PlaylistTrack
                              onVoteChanged = { trackId, _ ->
                                playlistViewModel.updateTrackLikes(
                                    trackId = trackId,
                                    userId =
                                        playlistViewModel.getUserId()
                                            ?: "" // Fallback to empty string if null
                                    )
                              },
                              userId =
                                  playlistViewModel.getUserId() ?: "" // Pass the current user ID
                              )
                        }
                      }
                }
              }
            }
      })
  // Show the description overlay if visible
  if (showDialogOverlay) {
    ViewDescriptionOverlay(
        onDismissRequest = { showDialogOverlay = false },
        description = selectedPlaylistState.playlistDescription)
  }
  /** Show the alert dialog for confirmation of the export of the playlist */
  if (showDialogExport) {
    AlertDialog(
        onDismissRequest = { showDialogExport = false },
        title = {
          Text(text = "Export Playlist to Spotify", style = MaterialTheme.typography.titleLarge)
        },
        text = {
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text =
                    "Are you sure you want to export this playlist to Spotify? Once exported, the playlist will be removed from the app and cannot be recovered.",
                style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
          }
        },
        confirmButton = {
          TextButton(
              modifier = Modifier.testTag("confirmButton"),
              onClick = {
                exportPlaylist(
                    context = context,
                    coroutineScope = coroutineScope,
                    playlistViewModel = playlistViewModel,
                    spotifyViewModel = spotifyViewModel,
                    navigationActions = navigationActions,
                    selectedPlaylistState = selectedPlaylistState,
                    onExportCompleted = { showDialogExport = false })
              }) {
                Text("Confirm")
              }
        },
        dismissButton = {
          TextButton(
              modifier = Modifier.testTag("cancelButton"), onClick = { showDialogExport = false }) {
                Text("Cancel")
              }
        })
  }
}

/**
 * Exports the given playlist to Spotify and removes it from the app upon successful export.
 *
 * This function uses a coroutine to perform the export process. It retrieves the final list of
 * tracks from the playlist, creates a new playlist in Spotify with the same name and description,
 * and optionally uploads a custom cover image for the playlist. If the operation is successful, the
 * playlist is deleted from the app's database.
 *
 * @param context The context used to display toast messages for success or failure feedback.
 * @param coroutineScope The CoroutineScope used to launch the export operation.
 * @param playlistViewModel The ViewModel responsible for playlist-related operations.
 * @param spotifyViewModel The ViewModel responsible for Spotify-related operations.
 * @param navigationActions NavigationActions to manage navigation between screens.
 * @param selectedPlaylistState The current state of the selected playlist to be exported.
 * @param onExportCompleted A callback to execute after the export process is completed, regardless
 *   of success or failure.
 */
private fun exportPlaylist(
    context: android.content.Context,
    coroutineScope: CoroutineScope,
    playlistViewModel: PlaylistViewModel,
    spotifyViewModel: SpotifyApiViewModel,
    navigationActions: NavigationActions,
    selectedPlaylistState: Playlist,
    onExportCompleted: () -> Unit
) {
  coroutineScope.launch {
    val finalList = playlistViewModel.getFinalListTracks()
    if (finalList.isNotEmpty()) {
      spotifyViewModel.createBeatLinkPlaylist(
          playlistName = selectedPlaylistState.playlistName,
          playlistDescription = selectedPlaylistState.playlistDescription,
          tracks = finalList,
          onResult = { idSpotify ->
            if (idSpotify != null) {
              selectedPlaylistState.playlistCover?.let {
                playlistViewModel.preparePlaylistCoverForSpotify()?.let { cover ->
                  spotifyViewModel.addCustomPlaylistCoverImage(idSpotify, cover)
                }
              }
              playlistViewModel.deletePlaylistById(selectedPlaylistState.playlistID)
              onExportCompleted()
              Toast.makeText(context, "Playlist exported successfully", Toast.LENGTH_SHORT).show()
              navigationActions.navigateToAndPop(Screen.LIBRARY, Screen.PLAYLIST_OVERVIEW)
            } else {
              Toast.makeText(context, "Failed to export playlist", Toast.LENGTH_SHORT).show()
              onExportCompleted()
            }
          })
    } else {
      Toast.makeText(context, "No songs added to playlist", Toast.LENGTH_SHORT).show()
      onExportCompleted()
    }
  }
}
