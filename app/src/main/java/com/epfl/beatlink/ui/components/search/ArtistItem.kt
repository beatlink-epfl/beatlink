package com.epfl.beatlink.ui.components.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.epfl.beatlink.model.spotify.objects.SpotifyArtist
import com.epfl.beatlink.ui.theme.TypographySongs

@Composable
fun ArtistItem(artist: SpotifyArtist) {
  Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Start,
      modifier = Modifier.testTag("artistItem")) {
        // Artist image
        Box(
            modifier =
                Modifier.padding(start = 16.dp)
                    .size(60.dp)
                    .clip(CircleShape)
                    .testTag("artistImage")) {
              AsyncImage(
                  model = artist.image,
                  contentDescription = "Image for ${artist.name}",
                  modifier = Modifier.fillMaxSize(),
                  contentScale = ContentScale.Crop)
            }

        Spacer(modifier = Modifier.width(10.dp))

        // Artist name
        Text(
            text = artist.name,
            style = TypographySongs.titleLarge,
            modifier = Modifier.testTag(artist.name))
      }
}
