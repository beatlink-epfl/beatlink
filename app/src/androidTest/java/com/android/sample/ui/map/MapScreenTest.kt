package com.android.sample.ui.map

import android.Manifest
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import com.android.sample.model.map.MapViewModel
import com.android.sample.model.spotify.objects.SpotifyTrack
import com.android.sample.ui.navigation.NavigationActions
import org.junit.Rule
import org.junit.Test

class MapScreenTest {

  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  @Test
  fun mapScreen_displaysLoadingMapText_whenMapIsNotLoaded() {
    // Mock ViewModel with default states
    val fakeMapLocationRepository = FakeMapLocationRepository()
    val mapViewModel =
        MapViewModel(fakeMapLocationRepository).apply {
          isMapLoaded.value = false
          permissionRequired.value = false
        }

    composeTestRule.setContent {
      MapScreen(
          navigationActions = NavigationActions(rememberNavController()),
          mapViewModel = mapViewModel,
          currentMusicPlayed = SpotifyTrack("trackId", "trackName", "trackUri", 10, 10),
          radius = 700.0)
    }

    composeTestRule.onNodeWithTag("MapScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("MapScreenColumn").assertIsDisplayed()
    composeTestRule.onNodeWithTag("MapContainer").assertIsDisplayed()
    composeTestRule.onNodeWithTag("playerContainer").assertIsDisplayed()
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()

    // Verify that "Loading map..." text is displayed when the map is not loaded
    composeTestRule.onNodeWithText("Loading map...").assertIsDisplayed()
  }

  @Test
  fun mapScreen_displaysMap_whenMapIsLoaded() {
    // Mock ViewModel with map loaded
    val mapViewModel =
        MapViewModel(FakeMapLocationRepository()).apply {
          isMapLoaded.value = true // Simulate that the map is loaded
          permissionRequired.value = false
        }

    composeTestRule.setContent {
      MapScreen(
          navigationActions = NavigationActions(rememberNavController()),
          mapViewModel = mapViewModel)
    }

    composeTestRule.onNodeWithTag("MapScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("MapScreenColumn").assertIsDisplayed()
    composeTestRule.onNodeWithTag("MapContainer").assertIsDisplayed()
    composeTestRule.onNodeWithTag("playerContainer").assertIsDisplayed()
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()

    // Verify that the map is displayed when loaded
    composeTestRule.onNodeWithTag("Map").assertIsDisplayed()
    composeTestRule.onNodeWithTag("currentLocationFab").assertIsDisplayed()
  }

  @Test
  fun mapScreen_handlesPermissionResult_correctly() {
    // Mock ViewModel with permission handling states
    val mapViewModel =
        MapViewModel(FakeMapLocationRepository()).apply { permissionRequired.value = true }

    composeTestRule.setContent {
      MapScreen(
          navigationActions = NavigationActions(rememberNavController()),
          mapViewModel = mapViewModel)
    }

    // Simulate permissions granted
    val permissions =
        mapOf(
            Manifest.permission.ACCESS_FINE_LOCATION to true,
            Manifest.permission.ACCESS_COARSE_LOCATION to true)
    val granted =
        permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    mapViewModel.onPermissionResult(granted)
    mapViewModel.setLocationPermissionGranted(granted)

    // Verify that onPermissionResult updates permissionRequired and locationPermitted
    assert(mapViewModel.locationPermitted.value)
    assert(!mapViewModel.permissionRequired.value)
  }

  /*@Test
  fun mapScreen_requestsPermissions_whenPermissionRequired() {
      val mapViewModel = mockk<MapViewModel>(relaxed = true)
      every { mapViewModel.permissionRequired.value } returns true

      composeTestRule.setContent {
          MapScreen(
              navigationActions = NavigationActions(rememberNavController()),
              mapViewModel = mapViewModel
          )
      }

      // Verify that permissionLauncher.launch is triggered when permissionRequired is true
      verify { mapViewModel.permissionRequired.value }
  }*/
}
