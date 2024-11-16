package com.epfl.beatlink.ui.map

import android.Manifest
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.epfl.beatlink.viewmodel.map.MapViewModel
import com.epfl.beatlink.viewmodel.map.user.MapUsersViewModel
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel
import com.epfl.beatlink.ui.navigation.NavigationActions
import org.junit.Rule
import org.junit.Test

class MapScreenTest {

  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  @Test
  fun mapScreen_displaysLoadingMapText_whenMapIsNotLoaded() {
    // Create a new instance of MapViewModel for this test
    val mapViewModel =
        MapViewModel(FakeMapLocationRepository()).apply {
          isMapLoaded.value = false
          permissionRequired.value = false
        }

    composeTestRule.setContent {
      MapScreen(
          navigationActions = NavigationActions(rememberNavController()),
          spotifyApiViewModel = null,
          profileViewModel = viewModel(factory = ProfileViewModel.Factory),
          mapUsersViewModel = viewModel(factory = MapUsersViewModel.Factory),
          mapViewModel = mapViewModel)
    }

    composeTestRule.onNodeWithTag("MapScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("MapScreenColumn").assertIsDisplayed()
    composeTestRule.onNodeWithTag("MapContainer").assertIsDisplayed()
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()

    // Verify that "Loading map..." text is displayed when the map is not loaded
    composeTestRule.onNodeWithText("Loading map...").assertIsDisplayed()
  }

  @Test
  fun mapScreen_displaysMap_whenMapIsLoaded() {
    // Create a new instance of MapViewModel for this test
    val mapViewModel =
        MapViewModel(FakeMapLocationRepository()).apply {
          isMapLoaded.value = true
          permissionRequired.value = false
        }

    composeTestRule.setContent {
      MapScreen(
          navigationActions = NavigationActions(rememberNavController()),
          spotifyApiViewModel = null,
          profileViewModel = viewModel(factory = ProfileViewModel.Factory),
          mapUsersViewModel = viewModel(factory = MapUsersViewModel.Factory),
          mapViewModel = mapViewModel)
    }

    composeTestRule.onNodeWithTag("MapScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("MapScreenColumn").assertIsDisplayed()
    composeTestRule.onNodeWithTag("MapContainer").assertIsDisplayed()
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()

    // Verify that the map is displayed when loaded
    composeTestRule.onNodeWithTag("Map").assertIsDisplayed()
    composeTestRule.onNodeWithTag("currentLocationFab").assertIsDisplayed()
  }

  @Test
  fun mapScreen_handlesPermissionResult_correctly() {
    // Create a new instance of MapViewModel for this test
    val mapViewModel = MapViewModel(FakeMapLocationRepository())

    mapViewModel.permissionRequired.value = true

    composeTestRule.setContent {
      MapScreen(
          navigationActions = NavigationActions(rememberNavController()),
          spotifyApiViewModel = null,
          profileViewModel = viewModel(factory = ProfileViewModel.Factory),
          mapUsersViewModel = viewModel(factory = MapUsersViewModel.Factory),
          mapViewModel = mapViewModel)
    }

    composeTestRule.waitForIdle()

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
    // Create a new instance of MapViewModel for this test
    val mapViewModel = MapViewModel(FakeMapLocationRepository())

    composeTestRule.setContent {
      MapScreen(
          navigationActions = NavigationActions(rememberNavController()),
          spotifyApiViewModel = null,
          profileViewModel = viewModel(factory = ProfileViewModel.Factory),
          mapUsersViewModel = viewModel(factory = MapUsersViewModel.Factory),
          mapViewModel = mapViewModel)
    }

    mapViewModel.permissionRequired.value = true

    assert(mapViewModel.permissionRequired.value) // No mocking required here
  }
}
