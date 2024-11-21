package com.epfl.beatlink.ui.map

import android.Manifest
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.ui.components.MusicPlayerUI
import com.epfl.beatlink.ui.navigation.BottomNavigationMenu
import com.epfl.beatlink.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.viewmodel.map.MapViewModel
import com.epfl.beatlink.viewmodel.map.user.MapUsersViewModel
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel
import com.epfl.beatlink.viewmodel.spotify.api.SpotifyApiViewModel

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
    profileViewModel: ProfileViewModel,
    mapUsersViewModel: MapUsersViewModel
) {
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
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            selectedItem = navigationActions.currentRoute(),
            tabList = LIST_TOP_LEVEL_DESTINATION)
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
                  locationPermitted = locationPermitted)
            } else {
              Text("Loading map...", modifier = Modifier.padding(16.dp))
            }
          }

          MusicPlayerUI(spotifyApiViewModel, mapUsersViewModel)
        }
      }
}
