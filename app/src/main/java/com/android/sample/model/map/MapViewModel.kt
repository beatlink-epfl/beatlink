package com.android.sample.model.map

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.android.sample.ui.map.CameraAction
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_BALANCED_POWER_ACCURACY
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

val defaultLocation = LatLng(46.51915277948766, 6.566736625776037)

class MapViewModel(context: Context) : ViewModel() {

  val permissionAsked: MutableState<Boolean> = mutableStateOf(false)
  val locationPermitted: MutableState<Boolean> = mutableStateOf(false)
  val currentPosition: MutableState<LatLng> = mutableStateOf(defaultLocation)
  val isMapLoaded: MutableState<Boolean> = mutableStateOf(false)
  val moveToCurrentLocation: MutableState<CameraAction> = mutableStateOf(CameraAction.NO_ACTION)

  private val locationClient = LocationServices.getFusedLocationProviderClient(context)

  init {
    Log.d("MapViewModel", "locationPermitted: ${locationPermitted.value}")
    startLocationUpdates()
  }

  // create factory
  companion object {
    fun provideFactory(context: Context): ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
              return MapViewModel(context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
          }
        }
  }

  fun setLocationPermissionAsked(asked: Boolean) {
    permissionAsked.value = asked
  }

  fun setLocationPermissionGranted(granted: Boolean) {
    locationPermitted.value = granted
  }

  private fun startLocationUpdates() {
    viewModelScope.launch {
      while (true) {
        Log.d("MapViewModel", "locationPermitted: ${locationPermitted.value}")

        if (locationPermitted.value) {
          val result = getCurrentLocation()
          result?.let { fetchedLocation ->
            currentPosition.value = LatLng(fetchedLocation.latitude, fetchedLocation.longitude)
            Log.d("MapViewModel", "currentPosition: ${currentPosition.value}")
            isMapLoaded.value = true
          }
              ?: run {
                currentPosition.value = defaultLocation
                isMapLoaded.value = true
              }
        } else if (permissionAsked.value) {
          isMapLoaded.value = true
          break
        }
        delay(5000) // Update location every 5 seconds
      }
    }
  }

  // Get the current location
  @SuppressLint("MissingPermission")
  private suspend fun getCurrentLocation(): android.location.Location? {
    val priority = PRIORITY_BALANCED_POWER_ACCURACY
    val result =
        locationClient
            .getCurrentLocation(
                priority,
                CancellationTokenSource().token,
            )
            .await()
    return result
  }
}
