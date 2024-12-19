package com.epfl.beatlink.ui.map

import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.model.spotify.objects.SpotifyArtist
import com.epfl.beatlink.model.spotify.objects.SpotifyTrack
import com.epfl.beatlink.ui.components.MusicPlayerUI
import com.epfl.beatlink.ui.navigation.BottomNavigationMenu
import com.epfl.beatlink.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.viewmodel.map.MapViewModel
import com.epfl.beatlink.viewmodel.map.user.MapUsersViewModel
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel
import com.epfl.beatlink.viewmodel.spotify.api.SpotifyApiViewModel
import com.epfl.beatlink.viewmodel.spotify.auth.SpotifyAuthViewModel

const val defaultZoom = 16f
const val radius = 200.0

enum class CameraAction {
  NO_ACTION,
  MOVE
}

@Composable
fun MapScreen(
    navigationActions: NavigationActions,
    mapViewModel: MapViewModel,
    spotifyApiViewModel: SpotifyApiViewModel,
    spotifyAuthViewModel: SpotifyAuthViewModel,
    profileViewModel: ProfileViewModel,
    mapUsersViewModel: MapUsersViewModel
) {

  val topSongsState = remember { mutableStateOf<List<SpotifyTrack>>(emptyList()) }
  val topArtistsState = remember { mutableStateOf<List<SpotifyArtist>>(emptyList()) }
  val spotifyId = remember { mutableStateOf("") }
  val profileData by profileViewModel.profile.collectAsState()
  val isProfileUpdated by profileViewModel.isProfileUpdated.collectAsState()

  val context = LocalContext.current

  // Fetch the profile when the screen loads
  LaunchedEffect(Unit) {
    profileViewModel.fetchProfile()
    if (spotifyAuthViewModel.isRefreshNeeded()) {
      spotifyAuthViewModel.refreshAccessToken(context)
    }
  }

  // Fetch the user's top songs and artists
  LaunchedEffect(isProfileUpdated) {
    if (!isProfileUpdated) {
      var tracksGotten = false
      var artistsGotten = false
      var spotifyIdGotten = false
      spotifyApiViewModel.getCurrentUserTopTracks(
          onSuccess = { tracks ->
            topSongsState.value = tracks
            tracksGotten = true
            if (artistsGotten && spotifyIdGotten) {
              profileViewModel.markProfileAsUpdated()
            }
            Log.d("MapScreen", "Fetched ${tracks.size} top songs.")
          },
          onFailure = { Log.e("MapScreen", "Failed to fetch top songs.") })

      spotifyApiViewModel.getCurrentUserTopArtists(
          onSuccess = { artists ->
            topArtistsState.value = artists
            artistsGotten = true
            if (tracksGotten && spotifyIdGotten) {
              profileViewModel.markProfileAsUpdated()
            }
            Log.d("MapScreen", "Fetched ${artists.size} top artists.")
          },
          onFailure = { Log.e("MapScreen", "Failed to fetch top artists.") })

      spotifyApiViewModel.getCurrentUserId(
          onSuccess = { id ->
            spotifyId.value = id
            spotifyIdGotten = true
            if (tracksGotten && artistsGotten) {
              profileViewModel.markProfileAsUpdated()
            }
            Log.d("MapScreen", "Fetched user's spotify id.")
          },
          onFailure = { Log.e("MapScreen", "Failed to fetch user's spotify id.") })
    }
  }

  // Update the profile once top songs, top artists, and the profile are available
  LaunchedEffect(topSongsState.value, topArtistsState.value, spotifyId.value, profileData) {
    if (topSongsState.value.isNotEmpty() &&
        topArtistsState.value.isNotEmpty() &&
        spotifyId.value.isNotEmpty() &&
        profileData != null) {
      val updatedProfile =
          profileData!!.copy(
              topSongs = topSongsState.value,
              topArtists = topArtistsState.value,
              spotifyId = spotifyId.value)
      profileViewModel.updateProfile(updatedProfile)
      Log.d("MapScreen", "Profile updated successfully.")
    }
  }

  // Permission launcher to handle permission request
  val permissionLauncher =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted =
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            mapViewModel.onPermissionResult(granted)
          }

  // Observe permissionRequired from ViewModel
  val permissionRequired by mapViewModel.permissionRequired

  LaunchedEffect(permissionRequired) {
    if (permissionRequired) {
      permissionLauncher.launch(
          arrayOf(
              Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
    }
  }
  val locationPermitted by mapViewModel.locationPermitted
  val isMapLoaded by mapViewModel.isMapLoaded
  val mapUsers by mapUsersViewModel.mapUsers.collectAsState()

  Scaffold(
      bottomBar = {
        Column {
          MusicPlayerUI(navigationActions, spotifyApiViewModel, mapUsersViewModel)
          BottomNavigationMenu(
              onTabSelect = { route -> navigationActions.navigateTo(route) },
              selectedItem = navigationActions.currentRoute(),
              tabList = LIST_TOP_LEVEL_DESTINATION)
        }
      },
      modifier = Modifier.fillMaxSize().testTag("MapScreen")) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).testTag("MapScreenColumn")) {
          MapUserTrackingView(mapUsersViewModel, mapViewModel, profileViewModel, radius)
          // Map fills most of the screen
          Box(modifier = Modifier.weight(1f).testTag("MapContainer")) {
            if (isMapLoaded) {
              GoogleMapView(
                  currentPosition = mapViewModel.currentPosition,
                  moveToCurrentLocation = mapViewModel.moveToCurrentLocation,
                  modifier = Modifier.testTag("Map"),
                  mapUsers = mapUsers,
                  profileViewModel = profileViewModel,
                  spotifyApiViewModel = spotifyApiViewModel,
                  navigationActions = navigationActions,
                  locationPermitted = locationPermitted)
            } else {
              Text("Loading map...", modifier = Modifier.padding(16.dp))
            }
          }
        }
      }
}
