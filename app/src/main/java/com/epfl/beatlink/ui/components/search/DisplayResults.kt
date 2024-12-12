package com.epfl.beatlink.ui.components.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.model.profile.ProfileData
import com.epfl.beatlink.model.spotify.objects.SpotifyArtist
import com.epfl.beatlink.model.spotify.objects.SpotifyTrack
import com.epfl.beatlink.ui.components.library.TrackPlaylistItem
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.viewmodel.library.PlaylistViewModel
import com.epfl.beatlink.viewmodel.profile.FriendRequestViewModel
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel
import com.epfl.beatlink.viewmodel.spotify.api.SpotifyApiViewModel

@Composable
fun DisplayResults(
    tracks: List<SpotifyTrack>? = null,
    artists: List<SpotifyArtist>? = null,
    people: List<ProfileData?>? = null,
    playlistViewModel: PlaylistViewModel? = null,
    profileViewModel: ProfileViewModel? = null,
    navigationActions: NavigationActions? = null,
    friendRequestViewModel: FriendRequestViewModel? = null,
    spotifyApiViewModel: SpotifyApiViewModel? = null,
    onClearQuery: (() -> Unit)? = null
) {
  if (profileViewModel != null && navigationActions != null) {

    LaunchedEffect(Unit) { profileViewModel.clearSelectedUser() }
  }

  if (tracks.isNullOrEmpty() && artists.isNullOrEmpty() && people.isNullOrEmpty()) {
    // Empty state
    Column(
        modifier = Modifier.fillMaxSize().testTag("noResultsMessage"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
              text = "No results found. Try searching for something.",
              style = MaterialTheme.typography.bodyLarge)
        }
  } else {
    // Display tracks or artists
    LazyColumn(modifier = Modifier.padding(horizontal = 16.dp).testTag("searchResultsColumn")) {
      tracks?.let {
        items(it) { track ->
          if (playlistViewModel != null && onClearQuery != null) {
            TrackPlaylistItem(
                track = track, playlistViewModel = playlistViewModel, onClearQuery = onClearQuery)
          } else {
            if (spotifyApiViewModel != null) {
              TrackItem(track = track, spotifyApiViewModel = spotifyApiViewModel)
            }
          }
        }
      }
      artists?.let {
        items(it) { artist ->
          ArtistItem(artist = artist)
          Spacer(modifier = Modifier.height(16.dp))
        }
      }
      people?.let {
        items(it) { person ->
          if (profileViewModel != null &&
              navigationActions != null &&
              friendRequestViewModel != null) {
            PeopleItem(
                person,
                navigationActions = navigationActions,
                profileViewModel = profileViewModel,
                friendRequestViewModel = friendRequestViewModel)
          }
        }
      }
    }
  }
}
