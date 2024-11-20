package com.epfl.beatlink.ui.components.library

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.R
import com.epfl.beatlink.model.library.Playlist
import com.epfl.beatlink.ui.components.CornerIcons
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
          Image(
              painter = painterResource(id = R.drawable.cover_test1), // TODO
              contentDescription = "Playlist cover",
              modifier = Modifier.size(90.dp).padding(horizontal = 12.dp))

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

          CornerIcons(
              onClick = {},
              icon = Icons.Filled.MoreVert,
              contentDescription = "More Options Button",
              modifier = Modifier.padding(end = 12.dp).testTag("moreOptionsButton"),
              iconSize = 35.dp)
        }
      }
}
