package com.epfl.beatlink.ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.epfl.beatlink.model.library.UserPlaylist
import com.epfl.beatlink.model.spotify.objects.SpotifyArtist
import com.epfl.beatlink.model.spotify.objects.SpotifyTrack
import com.epfl.beatlink.ui.components.CornerIcons
import com.epfl.beatlink.ui.components.MusicPlayerUI
import com.epfl.beatlink.ui.components.PageTopAppBar
import com.epfl.beatlink.ui.components.profile.ProfileColumn
import com.epfl.beatlink.ui.navigation.BottomNavigationMenu
import com.epfl.beatlink.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen.NOTIFICATIONS
import com.epfl.beatlink.ui.navigation.Screen.SETTINGS
import com.epfl.beatlink.viewmodel.map.user.MapUsersViewModel
import com.epfl.beatlink.viewmodel.profile.FriendRequestViewModel
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel
import com.epfl.beatlink.viewmodel.spotify.api.SpotifyApiViewModel

@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel,
    friendRequestViewModel: FriendRequestViewModel,
    navigationAction: NavigationActions,
    spotifyApiViewModel: SpotifyApiViewModel,
    mapUsersViewModel: MapUsersViewModel
) {
  val profileData by profileViewModel.profile.collectAsState()

  // Load Profile Picture
  LaunchedEffect(Unit) {
    profileViewModel.loadProfilePicture { profileViewModel.profilePicture.value = it }
    friendRequestViewModel.getFriendRequests()
    friendRequestViewModel.getAllFriends()
  }

  val topSongsState = remember { mutableStateOf<List<SpotifyTrack>>(emptyList()) }
  val topArtistsState = remember { mutableStateOf<List<SpotifyArtist>>(emptyList()) }
  val userPlaylists = remember { mutableStateOf<List<UserPlaylist>>(emptyList()) }

  // Fetch top songs and top artists
  LaunchedEffect(spotifyApiViewModel) {
    spotifyApiViewModel.getCurrentUserTopTracks(
        onSuccess = { tracks -> topSongsState.value = tracks },
        onFailure = { topSongsState.value = emptyList() })
    spotifyApiViewModel.getCurrentUserTopArtists(
        onSuccess = { artists -> topArtistsState.value = artists },
        onFailure = { topArtistsState.value = emptyList() })
    spotifyApiViewModel.getCurrentUserPlaylists(
        onSuccess = { playlist -> userPlaylists.value = playlist },
        onFailure = { userPlaylists.value = emptyList() })
  }

  Scaffold(
      modifier = Modifier.testTag("profileScreen"),
      topBar = {
        PageTopAppBar(
            profileData?.username ?: "",
            "titleUsername",
            listOf {
              CornerIcons(
                  onClick = { navigationAction.navigateTo(NOTIFICATIONS) },
                  icon = Icons.Filled.Notifications,
                  contentDescription = "Notifications",
                  modifier = Modifier.testTag("profileScreenNotificationsButton"))
              CornerIcons(
                  onClick = { navigationAction.navigateTo(SETTINGS) },
                  icon = Icons.Filled.Settings,
                  contentDescription = "Settings",
                  modifier = Modifier.testTag("profileScreenSettingsButton"))
            })
      },
      bottomBar = {
        Column {
          MusicPlayerUI(navigationAction, spotifyApiViewModel, mapUsersViewModel)
          BottomNavigationMenu(
              onTabSelect = { route -> navigationAction.navigateTo(route) },
              tabList = LIST_TOP_LEVEL_DESTINATION,
              selectedItem = navigationAction.currentRoute())
        }
      },
      content = { paddingValue ->
        ProfileColumn(
            navigationActions = navigationAction,
            profileViewModel = profileViewModel,
            friendRequestViewModel = friendRequestViewModel,
            spotifyApiViewModel = spotifyApiViewModel,
            topSongsState = topSongsState.value,
            topArtistsState = topArtistsState.value,
            userPlaylists = userPlaylists.value,
            paddingValue = paddingValue,
            profilePicture = profileViewModel.profilePicture,
            ownProfile = true)
      })
}
