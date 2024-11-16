package com.epfl.beatlink.repository.map

import com.epfl.beatlink.model.map.LocationRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeMapLocationRepository : LocationRepository {
  private val _locationUpdates = MutableStateFlow<LatLng?>(null)
  override val locationUpdates: StateFlow<LatLng?> = _locationUpdates

  // Mocked permission state
  private var _permissionGranted = false

  override fun isLocationPermissionGranted(): Boolean {
    return _permissionGranted
  }

  // Set permission state from the test
  fun setLocationPermissionGranted(granted: Boolean) {
    _permissionGranted = granted
  }

  override fun startLocationUpdates() {
    if (_permissionGranted) {
      // Emit the test location immediately
      _locationUpdates.value = LatLng(20.51915277948766, 20.566736625776038)
    }
  }

  override fun stopLocationUpdates() {
    _locationUpdates.value = null
  }
}
