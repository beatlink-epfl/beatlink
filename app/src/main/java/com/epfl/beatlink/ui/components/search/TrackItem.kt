package com.epfl.beatlink.ui.components.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.epfl.beatlink.model.spotify.objects.SpotifyTrack
import com.epfl.beatlink.ui.theme.TypographySongs

@Composable
fun TrackItem(track: SpotifyTrack, onClick: (() -> Unit)? = null) {
  Row(
      modifier =
          Modifier.fillMaxWidth()
              .padding(8.dp)
              .clickable { onClick?.invoke() }
              .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
              .padding(8.dp)
              .testTag("trackItem"),
      verticalAlignment = Alignment.CenterVertically) {
        // Album cover
        Card(
            modifier = Modifier.padding(start = 8.dp).testTag("trackAlbumCover").size(55.dp),
            shape = RoundedCornerShape(5.dp),
        ) {
          AsyncImage(
              model = track.cover,
              contentDescription = "Cover for ${track.name}",
              contentScale = ContentScale.Crop)
        }
        Spacer(modifier = Modifier.width(8.dp))
        // Track details
        Column {
          Text(text = track.name, style = TypographySongs.titleLarge)
          Text(text = track.artist, style = TypographySongs.titleSmall)
        }
      }
}
