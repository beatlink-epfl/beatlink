package com.android.sample.ui.map

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.core.content.ContextCompat
import com.android.sample.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
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

  val mapVisible by remember { mutableStateOf(true) }

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
    } else if (moveToCurrentLocation.value == CameraAction.ANIMATE) {
      coroutineScope.launch {
        cameraPositionState.animate(
            update =
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.fromLatLngZoom(currentPosition.value, defaultZoom)),
            durationMs = 1000)
        moveToCurrentLocation.value = CameraAction.NO_ACTION
      }
    }
  }

  val context = LocalContext.current

  if (mapVisible) {
    Box(Modifier.fillMaxSize()) {
      GoogleMap(modifier = modifier, cameraPositionState = cameraPositionState) {
        currentPosition.value.let { location ->
          Marker(
              state = MarkerState(position = location),
              title = "You are here",
              icon = getBitmapDescriptorFromDrawableResource(R.drawable.me_position, context),
              anchor = Offset(0.5f, 0.5f))
          Circle(
              center = location,
              radius = radius,
              strokeColor = Color(0xFF5F2A83),
              strokeWidth = 3f,
              fillColor = Color(0x215F2A83))
        }
      }
      Box(modifier = Modifier.align(Alignment.TopEnd).testTag("currentLocationButton")) {
        CurrentLocationCenterButton(currentPosition.value, cameraPositionState)
      }
    }
  }
}

fun getBitmapDescriptorFromDrawableResource(resourceId: Int, context: Context): BitmapDescriptor {
  val drawable = ContextCompat.getDrawable(context, resourceId)
  val canvas = android.graphics.Canvas()
  val bitmap =
      Bitmap.createBitmap(
          drawable!!.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
  canvas.setBitmap(bitmap)
  drawable.setBounds(0, 0, canvas.width, canvas.height)
  drawable.draw(canvas)
  return BitmapDescriptorFactory.fromBitmap(bitmap)
}
