package com.android.sample.ui.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.android.sample.R
import com.android.sample.ui.theme.Purple40
import com.android.sample.ui.theme.Purple80
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
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
    radius: Double = 1000.0
) {
  val coroutineScope = rememberCoroutineScope()

  val cameraPositionState = rememberCameraPositionState()

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

  val context = LocalContext.current

  Box(Modifier.fillMaxSize()) {
    GoogleMap(modifier = modifier, cameraPositionState = cameraPositionState) {
      if (locationPermitted) {
        currentPosition.value.let { location ->
          Marker(
              state = MarkerState(position = location),
              title = "You are here",
              icon = getBitmapDescriptorFromDrawableResource(R.drawable.me_position, context),
              anchor = Offset(0.5f, 0.5f))
          Circle(
              center = location,
              radius = radius,
              strokeColor = Purple40,
              strokeWidth = 3f,
              fillColor = Purple80)
        }
      }
    }
    Box(modifier = Modifier.align(Alignment.TopEnd).testTag("currentLocationButton")) {
      CurrentLocationCenterButton(currentPosition.value, cameraPositionState)
    }
  }
}
