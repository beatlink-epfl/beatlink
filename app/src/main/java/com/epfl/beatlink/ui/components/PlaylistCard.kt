package com.epfl.beatlink.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.R
import com.epfl.beatlink.model.playlist.Playlist
import com.epfl.beatlink.ui.theme.TypographyPlaylist

@Composable
fun PlaylistCard(playlist: Playlist) {
  Card(
      modifier =
          Modifier.fillMaxWidth()
              .background(
                  color = MaterialTheme.colorScheme.surfaceVariant,
                  shape = RoundedCornerShape(size = 5.dp))
              .testTag("playlistItem")) {
        Row(
            modifier = Modifier.padding(start = 12.dp, end = 12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically) {
              // Cover image
              Image(
                  painter = painterResource(id = R.drawable.cover_test1), // TODO
                  contentDescription = "Playlist cover",
                  modifier = Modifier.size(80.dp).padding(end = 18.dp))

              // Playlist details
              Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = playlist.playlistName,
                    style = TypographyPlaylist.headlineLarge,
                    color = MaterialTheme.colorScheme.primary)
                Text(
                    text = "@" + playlist.playlistOwner,
                    style = TypographyPlaylist.headlineMedium,
                    color = MaterialTheme.colorScheme.primary)
                Text(
                    text = playlist.nbTracks.toString() + " tracks",
                    style = TypographyPlaylist.titleSmall,
                )
              }

              // Play button
              CornerIcons(
                  onClick = {},
                  icon = Icons.Outlined.PlayArrow,
                  contentDescription = "Play Button",
                  modifier = Modifier.testTag("playArrowButton"),
                  iconSize = 35.dp)

              CornerIcons(
                  onClick = {},
                  icon = Icons.Filled.MoreVert,
                  contentDescription = "More Options Button",
                  modifier = Modifier.testTag("moreOptionsButton"),
                  iconSize = 35.dp)
            }
      }
}
