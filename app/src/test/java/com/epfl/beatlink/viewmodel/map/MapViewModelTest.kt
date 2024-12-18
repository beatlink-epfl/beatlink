package com.epfl.beatlink.viewmodel.map

import com.epfl.beatlink.repository.map.FakeMapLocationRepository
import com.epfl.beatlink.repository.map.LocationRepository
import com.epfl.beatlink.repository.map.MainCoroutineRule
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

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
  fun `checkAndRequestLocationPermission updates locationPermitted and permissionAsked when permission is granted`() =
      runTest {
        // Grant permission in FakeMapLocationRepository
        fakeMapLocationRepository.setLocationPermissionGranted(true)

        // Call the function under test
        mapViewModel.checkAndRequestLocationPermission()

        // Wait for the coroutine to complete
        advanceUntilIdle()

        // Verify that locationPermitted is true and permissionAsked is false
        assertThat(mapViewModel.locationPermitted.value, `is`(true))
        assertThat(mapViewModel.permissionAsked.value, `is`(false))
      }

  @Test
  fun `checkAndRequestLocationPermission updates locationPermitted and permissionAsked when permission is denied`() =
      runTest {
        // Deny permission in FakeMapLocationRepository
        fakeMapLocationRepository.setLocationPermissionGranted(false)

        // Call the function under test
        mapViewModel.checkAndRequestLocationPermission()

        // Wait for the coroutine to complete
        advanceUntilIdle()

        // Verify that locationPermitted is false and permissionAsked is true
        assertThat(mapViewModel.locationPermitted.value, `is`(false))
        assertThat(mapViewModel.permissionAsked.value, `is`(true))
      }

  @Test
  fun `startLocationUpdates updates currentPosition and isMapLoaded when permission is granted`() =
      runTest {
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
  fun `startLocationUpdates does not update currentPosition when permission is denied`() = runTest {
    // Deny permission in the repository
    fakeMapLocationRepository.setLocationPermissionGranted(false)
    mapViewModel.setLocationPermissionGranted(false)

    // Attempt to start location updates
    mapViewModel.startLocationUpdates()

    advanceUntilIdle()

    // Verify that currentPosition remains null and isMapLoaded is set to true due to
    // permission denial
    assertThat(mapViewModel.currentPosition.value, `is`(nullValue()))
    assertThat(mapViewModel.isMapLoaded.value, `is`(true))
  }

  @Test
  fun `onCleared stops location updates in repository`() {
    // Mock repository to verify interaction
    val mockRepository = mock<LocationRepository>()
    val viewModel = MapViewModel(mockRepository)

    // Call onCleared, which should stop location updates
    viewModel.onCleared()

    // Verify that stopLocationUpdates was called in the repository
    verify(mockRepository).stopLocationUpdates()
  }

  @Test
  fun `onPermissionResult sets permission states correctly when granted is true`() {
    // Call onPermissionResult with granted = true
    mapViewModel.onPermissionResult(true)

    // Verify that permissionRequired is set to false and locationPermitted to true
    assertThat(mapViewModel.permissionRequired.value, `is`(false))
    assertThat(mapViewModel.locationPermitted.value, `is`(false))
  }

  @Test
  fun `onPermissionResult sets permission states correctly when granted is false`() {
    // Call onPermissionResult with granted = false
    mapViewModel.onPermissionResult(false)

    // Verify that permissionRequired is true and locationPermitted remains false
    assertThat(mapViewModel.permissionRequired.value, `is`(true))
    assertThat(mapViewModel.locationPermitted.value, `is`(false))
  }
}
