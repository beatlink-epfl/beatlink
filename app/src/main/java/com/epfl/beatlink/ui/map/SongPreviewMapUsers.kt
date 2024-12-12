package com.epfl.beatlink.ui.map

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.epfl.beatlink.model.map.user.MapUser
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.ui.theme.PrimaryGradientBrush
import com.epfl.beatlink.ui.theme.TypographySongs
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel
import com.google.firebase.Timestamp
import java.time.Duration
import java.time.Instant

/**
 * Composable function to display a preview of the currently playing song for a given map user.
 *
 * @param mapUser The user whose currently playing track information will be displayed.
 */
@Composable
fun SongPreviewMapUsers(
    mapUser: MapUser,
    profileViewModel: ProfileViewModel,
    navigationActions: NavigationActions
) {
  val selectedUserUserId = remember { mutableStateOf("") }
  val isIdFetched = remember { mutableStateOf(false) }

  // Check if the user ID is fetched and trigger navigation
  LaunchedEffect(isIdFetched.value) {
    if (isIdFetched.value) {
      profileViewModel.selectSelectedUser(selectedUserUserId.value)
      profileViewModel.fetchUserProfile()
      navigationActions.navigateTo(Screen.OTHER_PROFILE)
    }
  }

  Box(
      modifier =
          Modifier.shadow(
                  elevation = 1.dp,
                  ambientColor = MaterialTheme.colorScheme.outline,
                  spotColor = MaterialTheme.colorScheme.outline,
                  shape = RoundedCornerShape(16.dp))
              .padding(4.dp)
              .testTag("shadowbox")) {
        Box(
            modifier =
                Modifier.background(PrimaryGradientBrush, shape = RoundedCornerShape(16.dp))
                    .padding(2.dp)
                    .testTag("brushbox")) {
              Box(
                  modifier =
                      Modifier.testTag("SongPreviewMapUsers")
                          .width(384.dp)
                          .height(130.dp)
                          .background(
                              color = MaterialTheme.colorScheme.background,
                              shape = RoundedCornerShape(16.dp))
                          .padding(start = 20.dp, end = 10.dp, top = 20.dp, bottom = 20.dp)) {
                    // Content layout (album cover, song name, artist, album name, username)
                    Row(modifier = Modifier.fillMaxSize()) {
                      // Cover image
                      Card(
                          modifier =
                              Modifier.background(MaterialTheme.colorScheme.background)
                                  .border(
                                      width = 2.dp,
                                      color = MaterialTheme.colorScheme.primary,
                                      shape = RoundedCornerShape(8.dp))
                                  .size(90.dp)
                                  .testTag("albumCover")) {
                            AsyncImage(
                                model = mapUser.currentPlayingTrack.albumCover,
                                contentDescription = "Cover",
                                modifier = Modifier.fillMaxSize())
                          }

                      Spacer(modifier = Modifier.width(16.dp))

                      Column(
                          modifier = Modifier.align(CenterVertically),
                          verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = mapUser.currentPlayingTrack.songName,
                                color = MaterialTheme.colorScheme.primary,
                                style = TypographySongs.titleLarge,
                                modifier = Modifier.testTag("songName"))
                            Text(
                                text = mapUser.currentPlayingTrack.artistName,
                                color = MaterialTheme.colorScheme.primary,
                                style = TypographySongs.titleMedium,
                                modifier = Modifier.testTag("artistName"))
                            Text(
                                text = mapUser.currentPlayingTrack.albumName,
                                color = MaterialTheme.colorScheme.primary,
                                style = TypographySongs.titleMedium,
                                modifier = Modifier.testTag("albumName"))
                            Text(
                                text = "LISTENED BY @${mapUser.username.uppercase()}",
                                style = TypographySongs.labelMedium,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                maxLines = 2,
                                modifier =
                                    Modifier.testTag("username").clickable {
                                      profileViewModel.getUserIdByUsername(mapUser.username) { uid
                                        ->
                                        if (uid == null) {
                                          return@getUserIdByUsername
                                        } else {
                                          selectedUserUserId.value = uid
                                          isIdFetched.value = true
                                          Log.d(
                                              "SongPreview",
                                              "selectedUserUserId: ${selectedUserUserId.value}")
                                        }
                                      }
                                    })
                          }
                    }
                  }
            }
        // Add the time since last update
        Text(
            text = getTimeSinceLastUpdate(lastUpdated = mapUser.lastUpdated),
            style = TypographySongs.titleMedium,
            modifier =
                Modifier.align(Alignment.TopEnd).padding(20.dp).testTag("timeSinceLastUpdate"))
      }
}

/**
 * Calculate the relative time from the given timestamp to the current time.
 *
 * @param lastUpdated The `Timestamp` of the last update.
 * @return A `String` representing the time since the last update in minutes.
 */
fun getTimeSinceLastUpdate(lastUpdated: Timestamp): String {
  // Convert lastUpdated to Instant
  val lastUpdatedInstant = lastUpdated.toDate().toInstant()

  // Get the current time as Instant
  val now = Instant.now()

  // Calculate the duration between the two Instants
  val duration = Duration.between(lastUpdatedInstant, now)

  return when {
    duration.toMinutes() < 1 -> "Just now"
    else -> "${duration.toMinutes()} min ago"
  }
}
