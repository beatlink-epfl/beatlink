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
import com.android.sample.ui.theme.PrimaryPurple
import com.android.sample.ui.theme.SecondaryPurple
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
    radius: Double = 1000.0
) {
  val coroutineScope = rememberCoroutineScope()

  val cameraPositionState = rememberCameraPositionState()

  val context = LocalContext.current

  val mapProperties =
      MapProperties(
          mapType = MapType.NORMAL,
          mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, R.raw.style_maps))

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

  Box(Modifier.fillMaxSize()) {
    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        properties = mapProperties) {
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
                  strokeColor = PrimaryPurple,
                  strokeWidth = 3f,
                  fillColor = SecondaryPurple)
            }
          }
        }
    Box(modifier = Modifier.align(Alignment.TopEnd).testTag("currentLocationButton")) {
      CurrentLocationCenterButton(currentPosition.value, cameraPositionState)
    }
  }
}
