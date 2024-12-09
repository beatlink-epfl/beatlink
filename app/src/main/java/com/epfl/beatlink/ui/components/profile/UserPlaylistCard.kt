package com.epfl.beatlink.ui.components.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.epfl.beatlink.model.library.UserPlaylist
import com.epfl.beatlink.ui.components.PlayButton
import com.epfl.beatlink.ui.theme.TypographyPlaylist
import com.epfl.beatlink.viewmodel.spotify.api.SpotifyApiViewModel

@Composable
fun UserPlaylistCard(playlist: UserPlaylist, spotifyApiViewModel: SpotifyApiViewModel) {
  Card(
      modifier = Modifier.height(88.dp).fillMaxWidth().testTag("userPlaylistCard"),
      shape = RoundedCornerShape(size = 5.dp),
      colors = CardDefaults.cardColors(containerColor = Color.Transparent)) {
        Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
          Spacer(Modifier.width(12.dp))
          // Playlist Cover
          Box(modifier = Modifier.size(70.dp).clip(RoundedCornerShape(4.dp))) {
            AsyncImage(
                model = playlist.playlistCover,
                contentDescription = "Cover for ${playlist.playlistName}",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop)
          }
          // Playlist Name, Owner, Nb of tracks
          Column(modifier = Modifier.padding(horizontal = 12.dp).weight(1f)) {
            Text(
                text = playlist.playlistName,
                style = TypographyPlaylist.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 2)
            Text(
                text = playlist.nbTracks.toString() + " tracks",
                style = TypographyPlaylist.titleSmall,
            )
          }
          PlayButton(onClick = { spotifyApiViewModel.playPlaylist(playlist) })
          Spacer(Modifier.width(12.dp))
        }
      }
}
