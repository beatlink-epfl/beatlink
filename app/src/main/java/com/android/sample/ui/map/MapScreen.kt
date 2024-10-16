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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.android.sample.ui.navigation.NavigationActions
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_BALANCED_POWER_ACCURACY
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

val defaultLocation = LatLng(46.51915277948766, 6.566736625776037)
const val defaultZoom = 15f

enum class CameraAction {
  NO_ACTION,
  MOVE
}

@Composable
fun MapScreen(
    navigationActions: NavigationActions,
    currentMusicPlayed: String? = null,
    radius: Double = 1000.0
) {
  val coroutineScope = rememberCoroutineScope()
  val context = LocalContext.current

  val locationPermitted: MutableState<Boolean?> = remember { mutableStateOf(null) }
  val locationPermissionsAlreadyGranted =
      when {
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED -> {
          true
        }
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED -> {
          true
        }
        else -> false
      }
  val locationPermissions =
      arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
  val locationPermissionLauncher =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.RequestMultiplePermissions(),
          onResult = { permissions ->
            when {
              permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                locationPermitted.value = true
              }
              permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                locationPermitted.value = true
              }
              else -> {
                locationPermitted.value = false
              }
            }
          })
  val locationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
  val currentPosition = remember { mutableStateOf(defaultLocation) }
  var isMapLoaded by remember { mutableStateOf(false) }
  val moveToCurrentLocation = remember { mutableStateOf(CameraAction.NO_ACTION) }
  LaunchedEffect(Unit) {
    if (locationPermissionsAlreadyGranted) {
      locationPermitted.value = true
    } else {
      locationPermissionLauncher.launch(locationPermissions)
    }

    // wait for user input
    while (locationPermitted.value == null) {
      delay(100)
    }

    while (true) {
      coroutineScope.launch {
        if (locationPermitted.value == true) {
          val priority = PRIORITY_BALANCED_POWER_ACCURACY
          val result =
              locationClient
                  .getCurrentLocation(
                      priority,
                      CancellationTokenSource().token,
                  )
                  .await()
          if (result != null) {
            result.let { fetchedLocation ->
              currentPosition.value = LatLng(fetchedLocation.latitude, fetchedLocation.longitude)
              isMapLoaded = true
            }
          } else {
            locationPermitted.value = false
            currentPosition.value = defaultLocation
            isMapLoaded = true
          }
        } else if (locationPermitted.value == false) {
          isMapLoaded = true
        }
      }
      delay(5000) // map is updated every 5s
    }
  }

  Scaffold(
      bottomBar = {
        // Todo Bottom bar with navigation
      },
      modifier = Modifier.fillMaxSize().testTag("MapScreen"),
  ) { innerPadding ->
    Column(modifier = Modifier.fillMaxSize().padding(innerPadding).testTag("MapScreenColumn")) {
      // Map fills most of the screen
      Box(modifier = Modifier.weight(1f).testTag("MapContainer")) {
        if (isMapLoaded) {
          moveToCurrentLocation.value = CameraAction.MOVE
          GoogleMapView(
              currentPosition = currentPosition,
              moveToCurrentLocation = moveToCurrentLocation,
              modifier = Modifier.testTag("Map"),
              locationPermitted = locationPermitted.value!!,
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
                  .background(color = Color(0x215F2A83))
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
