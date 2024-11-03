package com.epfl.beatlink.ui.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState

@Composable
fun CurrentLocationCenterButton(
    currentLocation: LatLng?,
    cameraPositionState: CameraPositionState
) {
  FloatingActionButton(
      containerColor = Color.White,
      onClick = {
        currentLocation?.let { location ->
          cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(location, defaultZoom))
        }
      },
      modifier =
          Modifier.padding(16.dp)
              .size(48.dp)
              .shadow(elevation = 8.dp, shape = RoundedCornerShape(8.dp), clip = false)
              .background(color = Color.Transparent, shape = RoundedCornerShape(8.dp))
              .testTag("currentLocationFab")) {
        Icon(
            painter = painterResource(id = R.drawable.location_arrow_1),
            contentDescription = "Current Position",
            modifier = Modifier.size(30.dp).background(Color.White).testTag("currentLocationIcon"),
            tint = Color.Unspecified)
      }
}
