package com.epfl.beatlink.repository.map

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.StateFlow

interface LocationRepository {
  val locationUpdates: StateFlow<LatLng?>

  fun isLocationPermissionGranted(): Boolean

  fun startLocationUpdates()

  fun stopLocationUpdates()
}
