package com.epfl.beatlink.ui.map

import android.widget.Toast
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.R
import com.epfl.beatlink.model.map.user.MapUser
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

@Composable
fun GoogleMapView(
    currentPosition: MutableState<LatLng>,
    moveToCurrentLocation: MutableState<CameraAction>,
    modifier: Modifier,
    locationPermitted: Boolean,
    mapUsers: List<MapUser>,
    selectedUser: MutableState<MapUser?> = remember { mutableStateOf(null) }
) {
  // Create a coroutine scope for launching coroutines
  val coroutineScope = rememberCoroutineScope()

  // Remember the camera position state and set the initial position
  val cameraPositionState = rememberCameraPositionState {
    position = CameraPosition.fromLatLngZoom(currentPosition.value, defaultZoom)
  }

  // Get the current context
  val context = LocalContext.current

  // Define map properties including map type and style
  val mapProperties =
      MapProperties(
          mapType = MapType.NORMAL,
          mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, R.raw.style_maps))

  // Launch an effect to move the camera to the current location when requested
  LaunchedEffect(moveToCurrentLocation.value, Unit) {
    if (moveToCurrentLocation.value == CameraAction.MOVE) {
      coroutineScope.launch {
        cameraPositionState.move(
            update =
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.fromLatLngZoom(currentPosition.value, defaultZoom)))
        moveToCurrentLocation.value = CameraAction.NO_ACTION
      }
    }
  }

  // Main container for the map view
  Box(Modifier.fillMaxSize()) {
    // Google Map composable
    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        properties = mapProperties) {
          // Add a marker and circle for the current location if location is permitted
          if (locationPermitted) {
            currentPosition.value.let { location ->
              Marker(
                  state = MarkerState(position = location),
                  title = "You are here",
                  icon =
                      getBitmapDescriptorFromDrawableResource(
                          R.drawable.me_position, context, 100, 100),
                  anchor = Offset(0.5f, 0.5f),
                  onClick = {
                    Toast.makeText(context, "You are here", Toast.LENGTH_SHORT).show()
                    true
                  })
              Circle(
                  center = location,
                  radius = radius,
                  strokeColor = MaterialTheme.colorScheme.onSurface,
                  strokeWidth = 3f,
                  fillColor = MaterialTheme.colorScheme.onBackground)
            }
          }
          // Add markers for each user in the mutable list
          mapUsers.forEach { user ->
            Marker(
                contentDescription = "markerMapUser",
                state =
                    MarkerState(position = LatLng(user.location.latitude, user.location.longitude)),
                title = user.username,
                icon =
                    getBitmapDescriptorFromDrawableResourceSongPopUp(
                        R.drawable.cover_test1,
                        context,
                        100,
                        100), // TODO change to actual cover when conversion implemented
                anchor = Offset(0.5f, 0.5f),
                onClick = {
                  selectedUser.value = user // Set the selected user when marker is clicked
                  true // Return true to consume the click event
                })
          }
        }
    // Button to center the map on the current location
    Box(modifier = Modifier.align(Alignment.TopEnd).testTag("currentLocationButton")) {
      CurrentLocationCenterButton(currentPosition.value, cameraPositionState)
    }

    // Detect taps outside SongPreviewMapUsers to clear the selected user
    if (selectedUser.value != null) {
      Box(
          Modifier.testTag("clickbox").fillMaxSize().pointerInput(Unit) {
            detectTapGestures(
                onTap = {
                  selectedUser.value = null // Clear selected user when tapping outside
                })
          })
    }

    // Display SongPreviewMapUsers if a user is selected
    selectedUser.value?.let { user ->
      Box(
          modifier =
              Modifier.testTag("SongPreviewMap")
                  .align(Alignment.BottomCenter)
                  .padding(16.dp)
                  .pointerInput(Unit) {
                    detectTapGestures {} // Consume clicks within SongPreviewMapUsers
                  }) {
            SongPreviewMapUsers(mapUser = user)
          }
    }
  }
}
