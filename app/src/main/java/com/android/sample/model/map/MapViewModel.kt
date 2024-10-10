package com.android.sample.model.map

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

const val LOCATION_PERMISSION_REQUEST_CODE = 1001

class MapViewModel : ViewModel() {

  private var locationCallback: com.google.android.gms.location.LocationCallback? = null

  @SuppressLint("MissingPermission")
  fun startLocationUpdates(
      context: Context,
      fusedLocationClient: FusedLocationProviderClient,
      onLocationReceived: (Location) -> Unit,
      onPermissionDenied: () -> Unit
  ) {
    Log.d("MapViewModel", "Checking for location permission")

    // Check if the location permission is granted
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED) {

      Log.d("MapViewModel", "Location permission granted, starting location updates")

      val locationRequest =
          com.google.android.gms.location.LocationRequest.create().apply {
            interval = 5000 // Update every 5 seconds
            fastestInterval = 2000 // Fastest update interval
            priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
          }

      // Initialize the location callback if it hasn't been initialized
      if (locationCallback == null) {
        locationCallback =
            object : com.google.android.gms.location.LocationCallback() {
              override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult == null) {
                  Log.d("MapViewModel", "No location result available")
                  return
                }

                Log.d(
                    "MapViewModel",
                    "Location result received: ${locationResult.locations.size} locations")
                for (location in locationResult.locations) {
                  Log.d("MapViewModel", "Location: ${location.latitude}, ${location.longitude}")
                  onLocationReceived(location) // Pass the new location to the UI
                }
              }
            }
      }

      // Start location updates
      fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback!!, null)
    } else {
      Log.d("MapViewModel", "Location permission not granted, requesting permission")

      // Request permissions if not granted
      ActivityCompat.requestPermissions(
          context as Activity,
          arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
          LOCATION_PERMISSION_REQUEST_CODE)

      // Notify that permission was denied
      onPermissionDenied()
    }
  }

  // Stop location updates when no longer needed
  fun stopLocationUpdates(fusedLocationClient: FusedLocationProviderClient) {
    locationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
  }

  // Create a bitmap for the marker
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
}
