package com.epfl.beatlink.ui.map

import android.Manifest
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import com.epfl.beatlink.model.map.MapViewModel
import com.epfl.beatlink.ui.navigation.NavigationActions
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
          mapViewModel = mapViewModel)
    }

    composeTestRule.onNodeWithTag("MapScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("MapScreenColumn").assertIsDisplayed()
    composeTestRule.onNodeWithTag("MapContainer").assertIsDisplayed()
    composeTestRule.onNodeWithTag("noPlayerContainer").assertIsDisplayed()
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
    composeTestRule.onNodeWithTag("noPlayerContainer").assertIsDisplayed()
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

  @Test
  fun mapScreen_requestsPermissions_whenPermissionRequired() {
    val mapViewModel =
        MapViewModel(FakeMapLocationRepository()).apply { permissionRequired.value = true }

    composeTestRule.setContent {
      MapScreen(
          navigationActions = NavigationActions(rememberNavController()),
          mapViewModel = mapViewModel)
    }

    assert(mapViewModel.permissionRequired.value) // No mocking required here
  }

  @Test
  fun mapScreen_DeviceButtonToggle() {
    val mapViewModel =
        MapViewModel(FakeMapLocationRepository()).apply { permissionRequired.value = false }

    composeTestRule.setContent {
      MapScreen(
          navigationActions = NavigationActions(rememberNavController()),
          mapViewModel = mapViewModel)
    }

    // Find the device button by tag and verify initial state
    val deviceButton = composeTestRule.onNodeWithTag("deviceButton")
    deviceButton.assertIsDisplayed()
    deviceButton.assert(hasText("Connect Device"))

    // Click the device button to toggle connection state
    deviceButton.performClick()
    deviceButton.assert(hasText("Disconnect Device"))

    // Click again to toggle back
    deviceButton.performClick()
    deviceButton.assert(hasText("Connect Device"))
  }

  @Test
  fun mapScreen_PlayerComponentsVisibleWhenConnected() {
    val mapViewModel =
        MapViewModel(FakeMapLocationRepository()).apply { permissionRequired.value = false }

    composeTestRule.setContent {
      MapScreen(
          navigationActions = NavigationActions(rememberNavController()),
          mapViewModel = mapViewModel)
    }

    composeTestRule.onNodeWithTag("noPlayerContainer").assertIsDisplayed()

    val deviceButton = composeTestRule.onNodeWithTag("deviceButton")
    deviceButton.performClick()

    // Verify that the play/pause button, skip button, and song information are displayed
    composeTestRule.onNodeWithTag("playerContainer").assertIsDisplayed()
  }

  @Test
  fun mapScreen_PlayPauseButtonTogglesState() {
    val mapViewModel =
        MapViewModel(FakeMapLocationRepository()).apply { permissionRequired.value = false }

    composeTestRule.setContent {
      MapScreen(
          navigationActions = NavigationActions(rememberNavController()),
          mapViewModel = mapViewModel)
    }

    val deviceButton = composeTestRule.onNodeWithTag("deviceButton")
    deviceButton.performClick()

    // Initial state should be PAUSE, so the play icon should be displayed
    composeTestRule
        .onNodeWithTag("playButton", useUnmergedTree = true)
        .assertContentDescriptionEquals("Play")

    // Click to toggle state to PLAY, should show pause icon
    composeTestRule.onNodeWithTag("playButton", useUnmergedTree = true).performClick()
    composeTestRule
        .onNodeWithTag("pauseButton", useUnmergedTree = true)
        .assertContentDescriptionEquals("Pause")

    // Click again to toggle state back to PAUSE, should show play icon
    composeTestRule.onNodeWithTag("pauseButton", useUnmergedTree = true).performClick()
    composeTestRule
        .onNodeWithTag("playButton", useUnmergedTree = true)
        .assertContentDescriptionEquals("Play")

    composeTestRule.onNodeWithTag("skipButton", useUnmergedTree = true).performClick()
  }
}
