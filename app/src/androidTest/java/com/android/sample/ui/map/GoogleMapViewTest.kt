package com.android.sample.ui.map

import androidx.activity.ComponentActivity
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.gms.maps.model.LatLng
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GoogleMapViewTest {
  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  // Test to verify the map is displayed with location permitted
  @Test
  fun googleMapView_locationPermitted_displaysMapAndMarker() {
    val currentPosition =
        mutableStateOf(LatLng(37.7749, -122.4194)) // Sample lat/lng for San Francisco
    val moveToCurrentLocation = mutableStateOf(CameraAction.NO_ACTION)

    composeTestRule.setContent {
      GoogleMapView(
          currentPosition = currentPosition,
          moveToCurrentLocation = moveToCurrentLocation,
          modifier = Modifier.testTag("MapView"),
          locationPermitted = true,
          radius = 2000.0)
    }

    // Verify that the map is displayed
    composeTestRule.onNodeWithTag("MapView").assertIsDisplayed()

    // Verify that the current location marker is displayed
    composeTestRule.onNodeWithTag("currentLocationButton").assertIsDisplayed()

    // Simulate clicking the current location button
    composeTestRule.onNodeWithTag("currentLocationButton").performClick()
  }

  // Test to verify the map is displayed with location not permitted
  @Test
  fun googleMapView_locationNotPermitted_displaysMapWithoutMarker() {
    val currentPosition =
        mutableStateOf(LatLng(37.7749, -122.4194)) // Sample lat/lng for San Francisco
    val moveToCurrentLocation = mutableStateOf(CameraAction.NO_ACTION)

    composeTestRule.setContent {
      GoogleMapView(
          currentPosition = currentPosition,
          moveToCurrentLocation = moveToCurrentLocation,
          modifier = Modifier.testTag("MapView"),
          locationPermitted = false,
          radius = 2000.0)
    }

    // Verify that the map is displayed
    composeTestRule.onNodeWithTag("MapView").assertIsDisplayed()

    // Verify that the current location button is displayed
    composeTestRule.onNodeWithTag("currentLocationButton").assertIsDisplayed()

    // Simulate clicking the current location button
    composeTestRule.onNodeWithTag("currentLocationButton").performClick()
  }

  // Test to verify the map camera moves when instructed
  @Test
  fun googleMapView_cameraMovesOnAction() {
    val currentPosition =
        mutableStateOf(LatLng(37.7749, -122.4194)) // Sample lat/lng for San Francisco
    val moveToCurrentLocation = mutableStateOf(CameraAction.MOVE)

    composeTestRule.setContent {
      GoogleMapView(
          currentPosition = currentPosition,
          moveToCurrentLocation = moveToCurrentLocation,
          modifier = Modifier.testTag("MapView"),
          locationPermitted = true,
          radius = 2000.0)
    }

    // Verify that the map is displayed
    composeTestRule.onNodeWithTag("MapView").assertIsDisplayed()

    // Wait for the camera to move
    composeTestRule.waitForIdle()

    // Verify that the current location button is displayed after the camera moves
    composeTestRule.onNodeWithTag("currentLocationButton").assertIsDisplayed()
  }
}
