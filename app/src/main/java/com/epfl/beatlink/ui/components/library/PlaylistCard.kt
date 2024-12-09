package com.epfl.beatlink.ui.components.library

import android.graphics.Bitmap
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.model.library.Playlist
import com.epfl.beatlink.ui.components.MoreOptionsButton
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen.PLAYLIST_OVERVIEW
import com.epfl.beatlink.ui.theme.TypographyPlaylist
import com.epfl.beatlink.viewmodel.library.PlaylistViewModel

@Composable
fun PlaylistCard(
    playlist: Playlist,
    navigationActions: NavigationActions,
    playlistViewModel: PlaylistViewModel
) {
  // Load cover image
  val coverImage = remember { mutableStateOf<Bitmap?>(null) }
  LaunchedEffect(Unit) { playlistViewModel.loadPlaylistCover(playlist) { coverImage.value = it } }
  Card(
      modifier =
          Modifier.fillMaxWidth()
              .clickable {
                playlistViewModel.selectPlaylist(playlist)
                navigationActions.navigateTo(PLAYLIST_OVERVIEW)
              }
              .testTag("playlistItem"),
      shape = RoundedCornerShape(size = 5.dp),
      colors = CardDefaults.cardColors(containerColor = Color.Transparent)) {
        Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
          // Cover image
          if (coverImage.value == null) {
            GrayBox()
          } else {
            PlaylistCover(coverImage)
          }
          Spacer(modifier = Modifier.width(8.dp))

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

          MoreOptionsButton {}
          Spacer(Modifier.width(12.dp))
        }
      }
}
