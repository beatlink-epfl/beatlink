package com.epfl.beatlink.viewmodel.map

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.epfl.beatlink.model.map.LocationRepository
import com.epfl.beatlink.repository.map.MapLocationRepository
import com.epfl.beatlink.ui.map.CameraAction
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

val defaultLocation = LatLng(46.51857768346759, 6.568608689392836)

open class MapViewModel(val mapLocationRepository: LocationRepository) : ViewModel() {

  val permissionAsked: MutableState<Boolean> = mutableStateOf(false)
  val locationPermitted: MutableState<Boolean> = mutableStateOf(false)
  val currentPosition: MutableState<LatLng?> = mutableStateOf(null)
  val isMapLoaded: MutableState<Boolean> = mutableStateOf(false)
  val moveToCurrentLocation: MutableState<CameraAction> = mutableStateOf(CameraAction.NO_ACTION)
  val permissionRequired: MutableState<Boolean> = mutableStateOf(true)

  // Check if location permission is granted and request it if not
  fun checkAndRequestLocationPermission() {
    viewModelScope.launch {
      if (!mapLocationRepository.isLocationPermissionGranted()) {
        locationPermitted.value = false
        permissionAsked.value = true
      } else {
        locationPermitted.value = true
        permissionAsked.value = false
      }
    }
    startLocationUpdates()
  }

  // Set the location permission status
  fun setLocationPermissionGranted(granted: Boolean) {
    locationPermitted.value = granted
  }

  // Start location updates
  fun startLocationUpdates() {
    if (!locationPermitted.value) {
      isMapLoaded.value = true
    } else {
      var isInitialLoad = true
      viewModelScope.launch {
        mapLocationRepository.startLocationUpdates()

        mapLocationRepository.locationUpdates.collect { latLng ->
          if (latLng != null) {
            currentPosition.value = latLng
            isMapLoaded.value = true
            if (isInitialLoad) {
              moveToCurrentLocation.value = CameraAction.MOVE // Move only initially
              isInitialLoad = false
            } else {
              moveToCurrentLocation.value = CameraAction.NO_ACTION // No move on subsequent updates
            }
          } else {
            currentPosition.value = null
            isMapLoaded.value = true
          }
        }
      }
    }
  }

  // Stop location updates
  public override fun onCleared() {
    super.onCleared()
    mapLocationRepository.stopLocationUpdates()
  }

  // Handle the result of the location permission request
  fun onPermissionResult(granted: Boolean) {
    if (granted) {
      permissionRequired.value = false
      // Start location updates if permission is granted
      checkAndRequestLocationPermission()
    } else {
      permissionRequired.value = true
    }
  }

  // Factory for creating the ViewModel
  companion object {
    fun provideFactory(mapLocationRepository: MapLocationRepository): ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
              return MapViewModel(mapLocationRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
          }
        }
  }
}
