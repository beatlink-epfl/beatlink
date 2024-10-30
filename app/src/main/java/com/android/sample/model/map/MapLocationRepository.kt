package com.android.sample.model.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority.PRIORITY_BALANCED_POWER_ACCURACY
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MapLocationRepository(
    private val context: Context,
    private val locationClient: FusedLocationProviderClient
) : LocationRepository {

  private val _locationUpdates = MutableStateFlow<LatLng?>(null)
  override val locationUpdates: StateFlow<LatLng?> = _locationUpdates

  // Define the LocationRequest with minimum update distance
  private val locationRequest =
      LocationRequest.Builder(PRIORITY_BALANCED_POWER_ACCURACY, 5000L)
          .setMinUpdateDistanceMeters(10f) // Minimum distance of 10 meters
          .build()

  private val locationCallback =
      object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
          locationResult.lastLocation?.let {
            _locationUpdates.value = LatLng(it.latitude, it.longitude)
          }
        }
      }

  // Check if location permissions are granted
  override fun isLocationPermissionGranted(): Boolean {
    return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
  }

  // Start listening for location updates
  @SuppressLint("MissingPermission")
  override fun startLocationUpdates() {
    if (isLocationPermissionGranted()) {
      locationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }
  }

  // Stop location updates
  override fun stopLocationUpdates() {
    locationClient.removeLocationUpdates(locationCallback)
  }
}
