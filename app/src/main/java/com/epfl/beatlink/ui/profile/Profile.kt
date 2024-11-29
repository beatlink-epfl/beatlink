package com.epfl.beatlink.ui.profile

import android.graphics.Bitmap
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.model.library.UserPlaylist
import com.epfl.beatlink.model.spotify.objects.SpotifyArtist
import com.epfl.beatlink.model.spotify.objects.SpotifyTrack
import com.epfl.beatlink.ui.components.ArtistCard
import com.epfl.beatlink.ui.components.CornerIcons
import com.epfl.beatlink.ui.components.GradientTitle
import com.epfl.beatlink.ui.components.MusicPlayerUI
import com.epfl.beatlink.ui.components.PageTopAppBar
import com.epfl.beatlink.ui.components.ProfilePicture
import com.epfl.beatlink.ui.components.TrackCard
import com.epfl.beatlink.ui.components.profile.MusicGenreCard
import com.epfl.beatlink.ui.components.profile.UserPlaylistCard
import com.epfl.beatlink.ui.components.profile.genreGradients
import com.epfl.beatlink.ui.navigation.BottomNavigationMenu
import com.epfl.beatlink.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.ui.navigation.Screen.SETTINGS
import com.epfl.beatlink.ui.theme.PrimaryGradientBrush
import com.epfl.beatlink.ui.theme.lightThemeBackground
import com.epfl.beatlink.viewmodel.map.user.MapUsersViewModel
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel
import com.epfl.beatlink.viewmodel.spotify.api.SpotifyApiViewModel

@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel,
    navigationAction: NavigationActions,
    spotifyApiViewModel: SpotifyApiViewModel,
    mapUsersViewModel: MapUsersViewModel
) {
  LaunchedEffect(Unit) { profileViewModel.fetchProfile() }

  val profileData by profileViewModel.profile.collectAsState()
  val profilePicture = remember { mutableStateOf<Bitmap?>(null) }

  // Load profile picture
  LaunchedEffect(Unit) { profileViewModel.loadProfilePicture { profilePicture.value = it } }
  val topSongsState = remember { mutableStateOf<List<SpotifyTrack>>(emptyList()) }
  val topArtistsState = remember { mutableStateOf<List<SpotifyArtist>>(emptyList()) }
    val userPlaylists = remember { mutableStateOf<List<UserPlaylist>>(emptyList()) }

  // Fetch top songs and top artists
  LaunchedEffect(Unit) {
    spotifyApiViewModel.getCurrentUserTopTracks(
        onSuccess = { tracks -> topSongsState.value = tracks },
        onFailure = { topSongsState.value = emptyList() })
    spotifyApiViewModel.getCurrentUserTopArtists(
        onSuccess = { artists -> topArtistsState.value = artists },
        onFailure = { topArtistsState.value = emptyList() })
      spotifyApiViewModel.getCurrentUserPlaylists(
          onSuccess = { playlist -> userPlaylists.value = playlist},
          onFailure = { userPlaylists.value = emptyList() }
      )

  }

  Scaffold(
      modifier = Modifier.testTag("profileScreen"),
      topBar = {
        PageTopAppBar(
            profileData?.username ?: "",
            "titleUsername",
            listOf {
              CornerIcons(
                  onClick = {},
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
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(paddingValue)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())) {
              Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {

                // Profile picture
                ProfilePicture(profilePicture)

                Spacer(modifier = Modifier.width(24.dp))

                Column {
                  Text(
                      text = "${profileData?.links ?: 0} Links",
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.primary,
                      style = MaterialTheme.typography.bodyLarge,
                      modifier =
                          Modifier.align(Alignment.CenterHorizontally)
                              .padding(18.dp)
                              .testTag("linksCount"))
                  Box(
                      modifier =
                          Modifier.border(1.dp, PrimaryGradientBrush, RoundedCornerShape(30.dp))
                              .testTag("editProfileButtonContainer")
                              .width(233.dp)
                              .height(32.dp)) {
                        Button(
                            onClick = { navigationAction.navigateTo(Screen.EDIT_PROFILE) },
                            modifier = Modifier.fillMaxWidth().testTag("editProfileButton"),
                            colors =
                                ButtonDefaults.buttonColors(containerColor = lightThemeBackground),
                        ) {
                          Text(
                              text = "Edit Profile",
                              fontWeight = FontWeight.Bold,
                              style = MaterialTheme.typography.labelSmall,
                              color = MaterialTheme.colorScheme.primary)
                        }
                      }
                }
              }

              Spacer(modifier = Modifier.padding(vertical = 8.dp))

              // Name
              Text(
                  text = profileData?.name ?: "",
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.primary,
                  style = MaterialTheme.typography.bodyLarge,
                  modifier = Modifier.padding(horizontal = 10.dp).testTag("name"))

              Spacer(modifier = Modifier.height(5.dp))

              // Bio
              Text(
                  text = profileData?.bio ?: "No description provided",
                  color = Color.Black,
                  style = MaterialTheme.typography.bodyMedium,
                  modifier = Modifier.padding(horizontal = 10.dp).testTag("bio"))

              Spacer(modifier = Modifier.height(32.dp))

              // Favorite music genres
              if (profileData?.favoriteMusicGenres?.isNotEmpty() == true) {
                GradientTitle("MUSIC GENRES")
                Row(
                    modifier = Modifier.padding(vertical = 16.dp).testTag("favoriteMusicGenresRow"),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                  profileData!!.favoriteMusicGenres.forEach { genre ->
                    val genreGradient = genreGradients[genre] ?: PrimaryGradientBrush
                    MusicGenreCard(genre = genre, brush = genreGradient, onClick = {})
                  }
                }
              }

              // Display top songs if available
              if (topSongsState.value.isNotEmpty()) {
                GradientTitle("TOP SONGS")
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(11.dp),
                    modifier = Modifier.padding(vertical = 16.dp)) {
                      items(topSongsState.value.size) { i -> TrackCard(topSongsState.value[i]) }
                    }
              }

              // Display top artists if available
              if (topArtistsState.value.isNotEmpty()) {
                GradientTitle("TOP ARTISTS")
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(11.dp),
                    modifier = Modifier.padding(vertical = 16.dp)) {
                      items(topArtistsState.value.size) { i ->
                        ArtistCard(topArtistsState.value[i])
                      }
                    }
              }

            if (userPlaylists.value.isNotEmpty()) {
                GradientTitle("PLAYLISTS")
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(11.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .heightIn(max = 400.dp)) {
                    items(userPlaylists.value.size) { i ->
                        UserPlaylistCard(userPlaylists.value[i])
                    }
                }

            }

            }
      })
}
