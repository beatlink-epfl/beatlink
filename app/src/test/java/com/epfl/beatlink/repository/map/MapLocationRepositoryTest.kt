package com.epfl.beatlink.repository.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.LatLng
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class MapLocationRepositoryTest {

  private lateinit var mockContext: Context
  private lateinit var fakeLocationClient: FakeLocationProviderClient
  private lateinit var mapLocationRepository: MapLocationRepository

  @Before
  fun setUp() {
    mockContext = mock(Context::class.java)
    fakeLocationClient = FakeLocationProviderClient()
    mapLocationRepository = MapLocationRepository(mockContext, fakeLocationClient)
  }

  @Test
  fun `isLocationPermissionGranted returns true when permission is granted`() {
    whenever(
            ContextCompat.checkSelfPermission(
                mockContext, Manifest.permission.ACCESS_FINE_LOCATION))
        .thenReturn(PackageManager.PERMISSION_GRANTED)
    whenever(
            ContextCompat.checkSelfPermission(
                mockContext, Manifest.permission.ACCESS_COARSE_LOCATION))
        .thenReturn(PackageManager.PERMISSION_GRANTED)

    val result = mapLocationRepository.isLocationPermissionGranted()
    assertEquals(true, result)
  }

  @Test
  fun `startLocationUpdates emits location when permission is granted`() = runTest {
    // Mock permissions to be granted
    whenever(
            ContextCompat.checkSelfPermission(
                mockContext, Manifest.permission.ACCESS_FINE_LOCATION))
        .thenReturn(PackageManager.PERMISSION_GRANTED)

    // Start location updates
    mapLocationRepository.startLocationUpdates()

    // Trigger a location update from the fake client
    fakeLocationClient.triggerLocationUpdate(40.51915277948766, 7.566736625776037)

    // Use advanceUntilIdle() to wait for coroutine completion
    advanceUntilIdle()

    // Assert the emitted location
    val emittedLocation = mapLocationRepository.locationUpdates.first()
    assertEquals(LatLng(40.51915277948766, 7.566736625776037), emittedLocation)
  }

  @Test
  fun `startLocationUpdates does not emit location when permission is denied`() = runTest {
    // Mock permission to be denied
    whenever(
            ContextCompat.checkSelfPermission(
                mockContext, Manifest.permission.ACCESS_FINE_LOCATION))
        .thenReturn(PackageManager.PERMISSION_DENIED)

    // Start location updates
    mapLocationRepository.startLocationUpdates()

    // Collect the first emitted value from `locationUpdates` (it should emit null)
    val emittedLocation = mapLocationRepository.locationUpdates.first()
    assertNull(emittedLocation)
  }

  @Test
  fun `stopLocationUpdates removes location updates callback`() = runTest {
    // Start location updates to register the callback
    whenever(
            ContextCompat.checkSelfPermission(
                mockContext, Manifest.permission.ACCESS_FINE_LOCATION))
        .thenReturn(PackageManager.PERMISSION_GRANTED)

    mapLocationRepository.startLocationUpdates()

    // Verify that the callback is registered
    assertNotNull(fakeLocationClient.locationCallback)

    // Stop location updates
    mapLocationRepository.stopLocationUpdates()

    // Verify that removeLocationUpdates was called
    assert(fakeLocationClient.removeLocationUpdatesCalled)
    assertNull(fakeLocationClient.locationCallback)
  }
}
