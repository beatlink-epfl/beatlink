package com.android.sample.ui.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.sample.model.map.MapViewModel
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.sample.ui.navigation.NavigationActions
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun MapScreen(
    mapViewModel: MapViewModel,
    navigationActions: NavigationActions,
    circleRadiusInMeters: Double = 1000.0,
    currentMusicPlayed: String? = null
) {
  val context = LocalContext.current
  val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

  var currentLocation by remember { mutableStateOf<LatLng?>(null) }
  val cameraPositionState = rememberCameraPositionState()
  var cameraInitiallySet by remember { mutableStateOf(false) }

  // Default location in case permission is denied
  val defaultLocation = LatLng(46.51915277948766, 6.566736625776037)

  // Handle location updates and permission checks
  LaunchedEffect(Unit) {
    mapViewModel.startLocationUpdates(
        context = context,
        fusedLocationClient = fusedLocationClient,
        onLocationReceived = { location ->
          currentLocation = LatLng(location.latitude, location.longitude)

          // Set the camera position initially, but do not update later
          if (!cameraInitiallySet) {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(currentLocation!!, 15f)
            cameraInitiallySet = true
          }
        },
        onPermissionDenied = {
          // Show default location if permission is denied
          if (!cameraInitiallySet) {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(defaultLocation, 10f)
            cameraInitiallySet = true
          }
        })
  }

  DisposableEffect(Unit) { onDispose { mapViewModel.stopLocationUpdates(fusedLocationClient) } }

  // UI Elements (Same as before)
  Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
        modifier = Modifier.testTag("mapScreen"),
        bottomBar = {
            BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = navigationActions.currentRoute())
        },
        content = { padding ->
          Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Box(modifier = Modifier.weight(1f).testTag("mapContainer")) {
              MapItem(mapViewModel, cameraPositionState, currentLocation, circleRadiusInMeters)
            }

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
        })
    Box(modifier = Modifier.align(Alignment.TopEnd).testTag("currentLocationButton")) {
      CurrentLocationCenterButton(currentLocation, cameraPositionState)
    }
  }
}

@Composable
fun MapItem(
    mapViewModel: MapViewModel,
    cameraPositionState: CameraPositionState,
    currentLocation: LatLng?,
    radius: Double
) {
  GoogleMap(
      modifier = Modifier.fillMaxWidth().testTag("GoogleMap"),
      cameraPositionState = cameraPositionState) {
        currentLocation?.let { location ->
          Marker(
              state = MarkerState(position = location),
              title = "You are here",
              icon =
                  mapViewModel.bitmapDescriptorFromVector(
                      LocalContext.current, R.drawable.me_position),
              anchor = Offset(0.5f, 0.5f))
          Circle(
              center = location,
              radius = radius,
              strokeColor = Color(0xFF5F2A83),
              strokeWidth = 3f,
              fillColor = Color(0x215F2A83))
        }
      }
}

@Composable
fun PlayerCurrentMusicItem(musique: String?) {
  if (musique != null) {
    /*to do : musique is showing*/
  } else {
    Text(
        modifier =
            Modifier.fillMaxWidth().height(24.dp).padding(horizontal = 16.dp).testTag("playerText"),
        text = "not listening yet",
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight(400),
        color = Color(0xFF5F2A83),
        textAlign = TextAlign.Center)
  }
}

@Composable
fun CurrentLocationCenterButton(
    currentLocation: LatLng?,
    cameraPositionState: CameraPositionState
) {
  FloatingActionButton(
      containerColor = Color.White,
      onClick = {
        currentLocation?.let { location ->
          cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(location, 15f))
        }
      },
      modifier =
          Modifier.padding(16.dp)
              .size(48.dp)
              .shadow(elevation = 8.dp, shape = RoundedCornerShape(8.dp), clip = false)
              .background(color = Color.White, shape = RoundedCornerShape(8.dp))
              .testTag("currentLocationFab")) {
        Icon(
            painter = painterResource(id = R.drawable.location_arrow_1),
            contentDescription = "Current Position",
            modifier = Modifier.size(30.dp).background(Color.White).testTag("currentLocationIcon"),
            tint = Color.Unspecified)
      }
}
