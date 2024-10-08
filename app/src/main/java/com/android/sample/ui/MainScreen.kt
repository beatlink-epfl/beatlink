package com.android.sample.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.sample.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
private const val circleRadiusInMeters = 1000.0

@Composable
fun MainScreen() {
  var musique: String? = null
  val defaultLocation = LatLng(46.51915277948766, 6.566736625776037)
  val context = LocalContext.current
  val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

  var currentLocation by remember { mutableStateOf<LatLng?>(null) }
  val cameraPositionState = rememberCameraPositionState()

  // Get current location
  LaunchedEffect(Unit) {
    Log.d("MainScreen", "Launching effect to get current location")
    getCurrentLocation(context, fusedLocationClient) { location ->
      currentLocation = LatLng(location.latitude, location.longitude)
      Log.d("MainScreen", "Current location updated: ${location.latitude}, ${location.longitude}")
    }
  }

  // Update the camera position when currentLocation is set
  LaunchedEffect(currentLocation) {
    currentLocation?.let {
      Log.d("MainScreen", "Camera position updated to current location")
      cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 15f)
    }
  }

  Box(
      modifier = Modifier.fillMaxSize() // The Box fills the entire screen
      ) {
        Scaffold(
            bottomBar = {
              Box(
                  modifier =
                      Modifier.fillMaxWidth()
                          .height(75.dp)
                          .background(color = Color(0xFFEF3535)) // Red background
                  ) {
                    /*to do : bottom navigation bar content*/
                  }
            },
            content = { pd ->
              Column(
                  modifier = Modifier.fillMaxSize().padding(pd),
              ) {
                GoogleMap(
                    modifier = Modifier.fillMaxWidth().weight(1f).testTag("mapScreen"),
                    cameraPositionState = cameraPositionState) {
                      currentLocation?.let { location ->
                        Log.d(
                            "MainScreen",
                            "Adding marker to map at: ${location.latitude}, ${location.longitude}")
                        Marker(
                            state = MarkerState(position = location),
                            title = "You are here",
                            icon = bitmapDescriptorFromVector(context, R.drawable.me_position),
                            anchor = Offset(0.5f, 0.5f) // This centers the marker on the location
                            )
                        Circle(
                            center = location,
                            radius = circleRadiusInMeters, // Radius in meters
                            strokeColor = Color(0xFF5F2A83), // Stroke color
                            strokeWidth = 3f, // Width of the stroke (outline)
                            fillColor = Color(0x215F2A83), // Transparent fill color
                        )
                      }
                    }

                Row(
                    modifier =
                        Modifier.fillMaxWidth()
                            .height(76.dp)
                            .background(color = Color(0x215F2A83))
                            .padding(horizontal = 32.dp, vertical = 26.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                  if (musique != null) {
                    /*to do : musique is showing*/
                  } else {
                    Text(
                        modifier =
                            Modifier.fillMaxWidth().height(24.dp).padding(horizontal = 16.dp),
                        text = "not listening yet",
                        fontSize = Variables.BodyLargeSize,
                        lineHeight = Variables.BodyLargeLineHeight,
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight(400),
                        color = Color(0xFF5F2A83),
                        textAlign = TextAlign.Center,
                        letterSpacing = Variables.BodyLargeTracking,
                    )
                  }
                }
              }
            })

        // FloatingActionButton at the top right corner of the screen
        FloatingActionButton(
            containerColor = Color.White,
            onClick = {
              currentLocation?.let { location ->
                Log.d(
                    "MainScreen",
                    "Focusing camera directly on current location: ${location.latitude}, ${location.longitude}")
                cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(location, 15f))
              } ?: Log.d("MainScreen", "Current location is not available")
            },
            modifier =
                Modifier.align(Alignment.TopEnd)
                    .padding(16.dp)
                    .size(48.dp)
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(8.dp), clip = false)
                    .background(color = Color.White, shape = RoundedCornerShape(8.dp))) {
              Icon(
                  painter = painterResource(id = R.drawable.location_arrow_1),
                  contentDescription = "Current Position",
                  modifier = Modifier.size(30.dp).background(Color.White).align(Alignment.Center),
                  tint = Color.Unspecified)
            }
      }
}

object Variables {
  val BodyLargeSize: TextUnit = 16.sp
  val BodyLargeLineHeight: TextUnit = 24.sp
  val BodyLargeTracking: TextUnit = 0.5.sp
}

@SuppressLint("MissingPermission")
fun getCurrentLocation(
    context: Context,
    fusedLocationClient: FusedLocationProviderClient,
    onLocationReceived: (Location) -> Unit
) {
  Log.d("MainScreen", "Checking for location permission")

  if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
      PackageManager.PERMISSION_GRANTED) {
    Log.d("MainScreen", "Location permission granted")

    val locationRequest =
        com.google.android.gms.location.LocationRequest.create().apply {
          interval = 10000 // 10 seconds
          fastestInterval = 5000 // 5 seconds
          priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
        }

    val locationCallback =
        object : com.google.android.gms.location.LocationCallback() {
          override fun onLocationResult(locationResult: LocationResult) {
            if (locationResult == null) {
              Log.d("MainScreen", "No location result available")
              return
            }

            Log.d(
                "MainScreen",
                "Location result received: ${locationResult.locations.size} locations")

            for (location in locationResult.locations) {
              Log.d("MainScreen", "Location: ${location.latitude}, ${location.longitude}")
              onLocationReceived(location)
            }

            // Optionally stop location updates once we have a location
            fusedLocationClient.removeLocationUpdates(this)
          }
        }

    Log.d("MainScreen", "Requesting location updates")
    fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
  } else {
    Log.d("MainScreen", "Location permission not granted, requesting permission")

    // Request permissions if not granted
    ActivityCompat.requestPermissions(
        context as Activity,
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
        LOCATION_PERMISSION_REQUEST_CODE)
  }
}

fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor {
  val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
  vectorDrawable!!.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
  val bitmap =
      Bitmap.createBitmap(
          vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
  val canvas = Canvas(bitmap)
  vectorDrawable.draw(canvas)
  return BitmapDescriptorFactory.fromBitmap(bitmap)
}
