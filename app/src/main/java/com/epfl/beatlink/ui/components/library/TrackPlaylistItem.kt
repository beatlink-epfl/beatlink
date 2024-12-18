package com.epfl.beatlink.ui.components.library

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.epfl.beatlink.model.library.PlaylistTrack
import com.epfl.beatlink.model.spotify.objects.SpotifyTrack
import com.epfl.beatlink.ui.theme.SecondaryPurple
import com.epfl.beatlink.ui.theme.TypographySongs
import com.epfl.beatlink.viewmodel.library.PlaylistViewModel

@Composable
fun TrackPlaylistItem(
    track: SpotifyTrack,
    playlistViewModel: PlaylistViewModel,
    onClearQuery: () -> Unit
) {
  val context = LocalContext.current
  Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier =
          Modifier.fillMaxWidth()
              .padding(vertical = 8.dp)
              .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
              .padding(horizontal = 8.dp)
              .testTag("trackItem-${track.trackId}") // Unique testTag for the Row
              .clickable {
                playlistViewModel.addTrack(
                    PlaylistTrack(track, 0, mutableListOf()),
                    onSuccess = {
                      Toast.makeText(context, "Track added to playlist!", Toast.LENGTH_SHORT).show()
                      onClearQuery()
                    },
                    onFailure = { e ->
                      Toast.makeText(
                              context, "Failed to add track: ${e.message}", Toast.LENGTH_SHORT)
                          .show()
                    })
                onClearQuery()
              }) {
        // Album cover
        Card(
            modifier =
                Modifier.padding(start = 8.dp)
                    .size(55.dp)
                    .testTag("trackAlbumCover-${track.trackId}"),
            shape = RoundedCornerShape(5.dp)) {
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
              modifier = Modifier.testTag("trackName-${track.trackId}"),
              style = TypographySongs.titleLarge)
          Text(
              text = track.artist,
              modifier = Modifier.testTag("trackArtist-${track.trackId}"),
              color = SecondaryPurple,
              style = TypographySongs.titleSmall)
        }
      }
}
