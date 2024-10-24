package com.android.sample.ui.map

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.sample.model.map.MapViewModel
import com.android.sample.model.spotify.objects.SpotifyTrack
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.theme.Purple80
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

val defaultLocation = LatLng(46.51915277948766, 6.566736625776037)
const val defaultZoom = 15f

enum class CameraAction {
  NO_ACTION,
  MOVE
}

@Composable
fun MapScreen(
    navigationActions: NavigationActions,
    mapViewModel: MapViewModel =
        viewModel(factory = MapViewModel.provideFactory(LocalContext.current)),
    currentMusicPlayed: SpotifyTrack? = null,
    radius: Double = 1000.0
) {
  val context = LocalContext.current
  val locationPermitted by mapViewModel.locationPermitted
  val isMapLoaded by mapViewModel.isMapLoaded

  // Permission logic
  val locationPermissions =
      arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

  val locationPermissionLauncher =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.RequestMultiplePermissions(),
          onResult = { permissions ->
            when {
              permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                mapViewModel.setLocationPermissionGranted(true)
              }
              permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                mapViewModel.setLocationPermissionGranted(true)
              }
              else -> {
                mapViewModel.setLocationPermissionGranted(false)
              }
            }
            mapViewModel.setLocationPermissionAsked(true)
          })

  // Check permissions and launch permission request if necessary
  LaunchedEffect(Unit) {
    if (mapViewModel.permissionAsked.value) {
      mapViewModel.setLocationPermissionGranted(false)
    } else if (ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED) {
      mapViewModel.setLocationPermissionGranted(true)
    } else {
      locationPermissionLauncher.launch(locationPermissions)
    }
  }

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
          }

          // Player is placed just above the bottom bar
          Row(
              modifier =
                  Modifier.fillMaxWidth()
                      .height(76.dp)
                      .background(color = Purple80)
                      .padding(horizontal = 32.dp, vertical = 26.dp)
                      .testTag("playerContainer"),
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically,
          ) {
            PlayerCurrentMusicItem(currentMusicPlayed)
          }
        }
      }
}
