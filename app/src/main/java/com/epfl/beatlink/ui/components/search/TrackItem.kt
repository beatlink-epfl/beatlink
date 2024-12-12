package com.epfl.beatlink.ui.components.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
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
import com.epfl.beatlink.ui.components.CornerIcons
import com.epfl.beatlink.ui.theme.TypographySongs
import com.epfl.beatlink.viewmodel.spotify.api.SpotifyApiViewModel

@Composable
fun TrackItem(track: SpotifyTrack, spotifyApiViewModel: SpotifyApiViewModel) {

  Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier =
          Modifier.fillMaxWidth()
              .padding(vertical = 8.dp)
              .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
              .padding(horizontal = 8.dp)
              .testTag("trackItem")) {
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

        Spacer(modifier = Modifier.width(12.dp))

        // Track name and artist
        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
          Text(
              text = track.name,
              modifier = Modifier.testTag(track.name),
              style = TypographySongs.titleLarge)
          Text(
              text = track.artist,
              modifier = Modifier.testTag(track.artist),
              style = TypographySongs.titleSmall)
        }

        // Action Icons (Like, Add, More)
        Row(horizontalArrangement = Arrangement.End) {
          if (spotifyApiViewModel.playbackActive) {
            CornerIcons(
                onClick = { spotifyApiViewModel.playTrackAlone(track) },
                icon = Icons.Default.PlayArrow,
                modifier = Modifier.testTag("playIcon"),
                contentDescription = "Play")
          }
        }
      }
}
