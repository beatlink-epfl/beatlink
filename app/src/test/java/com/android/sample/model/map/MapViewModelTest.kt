package com.android.sample.model.map

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MapViewModelTest {

  @ExperimentalCoroutinesApi @get:Rule var mainCoroutineRule = MainCoroutineRule()

  private lateinit var fakeMapLocationRepository: FakeMapLocationRepository
  private lateinit var mapViewModel: MapViewModel

  @Before
  fun setUp() {
    // Initialize the fake repository and the ViewModel with it
    fakeMapLocationRepository = FakeMapLocationRepository()
    mapViewModel = MapViewModel(fakeMapLocationRepository)
  }

  @Test
  fun `initial state has permissionAsked as false and locationPermitted as false`() {
    assertThat(mapViewModel.permissionAsked.value, `is`(false))
    assertThat(mapViewModel.locationPermitted.value, `is`(false))
  }

  @Test
  fun `setLocationPermissionGranted updates locationPermitted state`() {
    mapViewModel.setLocationPermissionGranted(true)
    assertThat(mapViewModel.locationPermitted.value, `is`(true))

    mapViewModel.setLocationPermissionGranted(false)
    assertThat(mapViewModel.locationPermitted.value, `is`(false))
  }

  @Test
  fun `checkAndRequestLocationPermission updates locationPermitted and permissionAsked when permission is denied`() {
    // Initially permission is denied in FakeMapLocationRepository
    fakeMapLocationRepository.setLocationPermissionGranted(false)
    mapViewModel.setLocationPermissionAsked(true) // Simulate that the permission has been requested

    mapViewModel.setLocationPermissionGranted(
        fakeMapLocationRepository.isLocationPermissionGranted())

    // Verify permission states
    assertThat(mapViewModel.locationPermitted.value, `is`(false))
    assertThat(mapViewModel.permissionAsked.value, `is`(true))
  }

  @Test
  fun `startLocationUpdates updates currentPosition and isMapLoaded when permission is granted`() =
      runTest() {
        // Grant permission in the repository and update ViewModel accordingly
        fakeMapLocationRepository.setLocationPermissionGranted(true)
        mapViewModel.setLocationPermissionGranted(true)

        // Start location updates
        mapViewModel.startLocationUpdates()

        advanceUntilIdle()

        // The FakeMapLocationRepository should emit the new location immediately
        assertThat(
            mapViewModel.currentPosition.value, `is`(LatLng(20.51915277948766, 20.566736625776038)))
        assertThat(mapViewModel.isMapLoaded.value, `is`(true))
      }

  @Test
  fun `startLocationUpdates does not update currentPosition when permission is denied`() =
      runTest() {
        // Deny permission in the repository
        fakeMapLocationRepository.setLocationPermissionGranted(false)
        mapViewModel.setLocationPermissionGranted(false)

        // Attempt to start location updates
        mapViewModel.startLocationUpdates()

        advanceUntilIdle()

        // Verify that currentPosition remains at default and isMapLoaded is set to true due to
        // permission denial
        assertThat(mapViewModel.currentPosition.value, `is`(defaultLocation))
        assertThat(mapViewModel.isMapLoaded.value, `is`(true))
      }
}
