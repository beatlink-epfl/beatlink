package com.epfl.beatlink.model.map

import android.app.PendingIntent
import android.location.Location
import android.os.Looper
import com.google.android.gms.common.api.Api
import com.google.android.gms.common.api.internal.ApiKey
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.DeviceOrientationListener
import com.google.android.gms.location.DeviceOrientationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LastLocationRequest
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import io.mockk.every
import io.mockk.mockk
import java.util.concurrent.Executor

class FakeLocationProviderClient : FusedLocationProviderClient {

  var locationCallback: LocationCallback? = null
  var removeLocationUpdatesCalled = false

  override fun getApiKey(): ApiKey<Api.ApiOptions.NoOptions> {
    TODO("Not yet implemented")
  }

  override fun getLastLocation(): Task<Location> {
    TODO("Not yet implemented")
  }

  override fun getLastLocation(p0: LastLocationRequest): Task<Location> {
    TODO("Not yet implemented")
  }

  override fun getCurrentLocation(p0: Int, p1: CancellationToken?): Task<Location> {
    TODO("Not yet implemented")
  }

  override fun getCurrentLocation(
      p0: CurrentLocationRequest,
      p1: CancellationToken?
  ): Task<Location> {
    TODO("Not yet implemented")
  }

  override fun getLocationAvailability(): Task<LocationAvailability> {
    TODO("Not yet implemented")
  }

  override fun requestLocationUpdates(
      p0: LocationRequest,
      p1: Executor,
      p2: LocationListener
  ): Task<Void> {
    TODO("Not yet implemented")
  }

  override fun requestLocationUpdates(
      p0: LocationRequest,
      p1: LocationListener,
      p2: Looper?
  ): Task<Void> {
    TODO("Not yet implemented")
  }

  // Simulate setting up location updates by storing the callback
  override fun requestLocationUpdates(
      request: LocationRequest,
      callback: LocationCallback,
      looper: android.os.Looper?
  ): Task<Void> {
    locationCallback = callback
    return Tasks.forResult(null)
  }

  override fun requestLocationUpdates(
      p0: LocationRequest,
      p1: Executor,
      p2: LocationCallback
  ): Task<Void> {
    TODO("Not yet implemented")
  }

  override fun requestLocationUpdates(p0: LocationRequest, p1: PendingIntent): Task<Void> {
    TODO("Not yet implemented")
  }

  override fun removeLocationUpdates(p0: LocationListener): Task<Void> {
    TODO("Not yet implemented")
  }

  // Simulate stopping location updates
  override fun removeLocationUpdates(callback: LocationCallback): Task<Void> {
    removeLocationUpdatesCalled = true
    if (locationCallback == callback) {
      locationCallback = null
    }
    return Tasks.forResult(null)
  }

  override fun removeLocationUpdates(p0: PendingIntent): Task<Void> {
    TODO("Not yet implemented")
  }

  override fun flushLocations(): Task<Void> {
    TODO("Not yet implemented")
  }

  override fun setMockMode(p0: Boolean): Task<Void> {
    TODO("Not yet implemented")
  }

  override fun setMockLocation(p0: Location): Task<Void> {
    TODO("Not yet implemented")
  }

  override fun requestDeviceOrientationUpdates(
      p0: DeviceOrientationRequest,
      p1: Executor,
      p2: DeviceOrientationListener
  ): Task<Void> {
    TODO("Not yet implemented")
  }

  override fun requestDeviceOrientationUpdates(
      p0: DeviceOrientationRequest,
      p1: DeviceOrientationListener,
      p2: Looper?
  ): Task<Void> {
    TODO("Not yet implemented")
  }

  override fun removeDeviceOrientationUpdates(p0: DeviceOrientationListener): Task<Void> {
    TODO("Not yet implemented")
  }

  // Helper function to trigger a location update
  fun triggerLocationUpdate(latitude: Double, longitude: Double) {
    val location = mockk<Location>()
    every { location.latitude } returns latitude
    every { location.longitude } returns longitude
    val locationResult = LocationResult.create(listOf(location))
    locationCallback?.onLocationResult(locationResult)
  }
}
