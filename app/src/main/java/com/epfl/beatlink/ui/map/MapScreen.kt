package com.epfl.beatlink.ui.map

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.model.map.MapViewModel
import com.epfl.beatlink.model.spotify.objects.SpotifyTrack
import com.epfl.beatlink.ui.components.MusicPlayerUI
import com.epfl.beatlink.ui.navigation.BottomNavigationMenu
import com.epfl.beatlink.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.google.android.gms.maps.model.LatLng

val defaultLocation = LatLng(46.51915277948766, 6.566736625776037)
const val defaultZoom = 15f

enum class CameraAction {
  NO_ACTION,
  MOVE
}

@Composable
fun MapScreen(
    navigationActions: NavigationActions,
    mapViewModel: MapViewModel,
    currentMusicPlayed: SpotifyTrack? = null,
    radius: Double = 1000.0
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

  var connectedDevice by remember { mutableStateOf(false) }

  Scaffold(
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            selectedItem = navigationActions.currentRoute(),
            tabList = LIST_TOP_LEVEL_DESTINATION)
      },
      modifier = Modifier.fillMaxSize().testTag("MapScreen")) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).testTag("MapScreenColumn")) {
          // Map fills most of the screen
          Box(modifier = Modifier.weight(1f).testTag("MapContainer")) {
            if (isMapLoaded) {
              GoogleMapView(
                  currentPosition = mapViewModel.currentPosition,
                  moveToCurrentLocation = mapViewModel.moveToCurrentLocation,
                  modifier = Modifier.testTag("Map"),
                  locationPermitted = locationPermitted,
                  radius = radius)
            } else {
              Text("Loading map...", modifier = Modifier.padding(16.dp))
            }

            Button(
                onClick = { connectedDevice = !connectedDevice },
                modifier = Modifier.align(Alignment.BottomCenter).testTag("deviceButton")) {
                  Text(if (connectedDevice) "Disconnect Device" else "Connect Device")
                }
          }

          MusicPlayerUI(connectedDevice = connectedDevice)
        }
      }
}
