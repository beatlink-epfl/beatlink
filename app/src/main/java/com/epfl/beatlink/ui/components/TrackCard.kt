package com.epfl.beatlink.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.epfl.beatlink.model.spotify.objects.SpotifyTrack
import com.epfl.beatlink.ui.theme.TypographySongs

@Composable
fun TrackCard(track: SpotifyTrack) {
  Column(
      horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Box(modifier = Modifier.size(95.dp).clip(RoundedCornerShape(4.dp))) {
      AsyncImage(
          model = track.cover,
          contentDescription = "Cover for ${track.name}",
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop)
    }
    Spacer(Modifier.height(5.dp))
    Text(
        text = track.name,
        style = TypographySongs.bodyLarge,
        color = MaterialTheme.colorScheme.primary,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.width(95.dp))
    Text(
        text = track.artist,
        style = TypographySongs.bodyMedium,
        color = MaterialTheme.colorScheme.primary,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.width(95.dp))
  }
}
