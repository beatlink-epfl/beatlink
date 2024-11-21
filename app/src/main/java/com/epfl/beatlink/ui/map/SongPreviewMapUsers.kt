package com.epfl.beatlink.ui.map

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.R
import com.epfl.beatlink.model.map.user.MapUser
import com.epfl.beatlink.ui.theme.PrimaryGradientBrush
import com.epfl.beatlink.ui.theme.TypographySongs

/**
 * Composable function to display a preview of the currently playing song for a given map user.
 *
 * @param mapUser The user whose currently playing track information will be displayed.
 */
@Composable
fun SongPreviewMapUsers(mapUser: MapUser) {
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
                      Image(
                          painter =
                              painterResource(
                                  id = R.drawable.cover_test2), // TODO change to actual cover when
                          // conversion implemented
                          contentDescription = "Album Cover",
                          modifier =
                              Modifier.clip(RoundedCornerShape(4.dp))
                                  .background(MaterialTheme.colorScheme.background)
                                  .border(
                                      width = 2.dp,
                                      color = MaterialTheme.colorScheme.primary,
                                      shape = RoundedCornerShape(size = 4.dp))
                                  .size(90.dp)
                                  .testTag("albumCover"))

                      Spacer(modifier = Modifier.width(16.dp))

                      Column(modifier = Modifier.align(CenterVertically)) {
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
                            modifier = Modifier.testTag("username"))
                      }
                    }
                  }
            }
      }
}
