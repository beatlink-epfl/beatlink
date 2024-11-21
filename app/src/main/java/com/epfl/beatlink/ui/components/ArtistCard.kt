package com.epfl.beatlink.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.epfl.beatlink.model.spotify.objects.SpotifyArtist
import com.epfl.beatlink.ui.theme.TypographySongs

@Composable
fun ArtistCard(artist: SpotifyArtist) {
  Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.testTag("ArtistCard")
  ) {
    Box(modifier = Modifier.size(95.dp).clip(CircleShape)) {
      AsyncImage(
          model = artist.image,
          contentDescription = "Cover for ${artist.name}",
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop)
    }
    Spacer(Modifier.height(5.dp))
    Text(
        text = artist.name,
        style = TypographySongs.bodyLarge,
        color = MaterialTheme.colorScheme.primary,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.width(95.dp))
  }
}
