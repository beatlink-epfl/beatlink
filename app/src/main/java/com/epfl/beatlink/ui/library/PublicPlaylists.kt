package com.epfl.beatlink.ui.library

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.epfl.beatlink.ui.components.library.LibraryScaffold
import com.epfl.beatlink.ui.navigation.NavigationActions
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
        /* // TODO need the repository to test
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

          */

      }
}
