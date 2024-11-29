package com.epfl.beatlink.ui.components.library

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.epfl.beatlink.R
import com.epfl.beatlink.model.library.PlaylistTrack
import com.epfl.beatlink.ui.components.VoteButton
import com.epfl.beatlink.ui.theme.TypographySongs

@Composable
fun TrackVoteCard(
    playlistTrack: PlaylistTrack,
    onVoteChanged: (String, Boolean) -> Unit,
    userId: String
) {
  Card(
      modifier = Modifier.fillMaxSize().testTag("trackVoteCard"),
      colors = CardDefaults.cardColors(containerColor = Color.Transparent)) {
        Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
          // Album cover
          Box(
              modifier =
                  Modifier.size(55.dp)
                      .padding(horizontal = 5.dp, vertical = 5.dp)
                      .clip(RoundedCornerShape(4.dp))) {
                AsyncImage(
                    model = playlistTrack.track.cover,
                    contentDescription = "Cover for ${playlistTrack.track.name}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop)
              }
          // Track details
          Column(modifier = Modifier.weight(1f)) {
            Text(
                text = playlistTrack.track.name,
                style = TypographySongs.titleLarge,
                color = MaterialTheme.colorScheme.primary)
            Text(
                text = playlistTrack.track.artist,
                style = TypographySongs.titleMedium,
            )
          }

          // Vote button
          VoteButton(
              painterResource(R.drawable.fire),
              playlistTrack = playlistTrack,
              userId = userId,
              onVoteChanged = onVoteChanged)
        }
      }
}
