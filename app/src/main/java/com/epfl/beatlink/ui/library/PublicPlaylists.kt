package com.epfl.beatlink.ui.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.ui.components.library.LibraryScaffold
import com.epfl.beatlink.ui.components.library.PlaylistCard
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.theme.primaryGray
import com.epfl.beatlink.viewmodel.library.PlaylistViewModel

@Composable
fun PublicPlaylistsScreen(
    navigationActions: NavigationActions,
    playlistViewModel: PlaylistViewModel
) {

  val publicPlaylistListFlow by playlistViewModel.publicPlaylistList.collectAsState()

  LibraryScaffold(
      title = "Public Playlists",
      titleTag = "publicPlaylists",
      navigationActions = navigationActions) {

        if (publicPlaylistListFlow.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().testTag("emptyPlaylistsPrompt"),
                contentAlignment = Alignment.Center) {
                Text(
                    text = "No playlists found",
                    color = MaterialTheme.colorScheme.primaryGray,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.Center))
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp),
            ) {
                items(publicPlaylistListFlow.size) { i ->
                    PlaylistCard(publicPlaylistListFlow[i], navigationActions, playlistViewModel)
                }
            }
         }
      }
}
