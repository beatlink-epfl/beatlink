package com.android.sample.ui.map

import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.model.map.MapViewModel
import com.android.sample.model.spotify.objects.SpotifyTrack
import com.android.sample.ui.navigation.NavigationActions
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MapScreenWithoutLocationPermissionTest {

  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  @Test
  fun mapScreen_withNoLocationPermission() {
    composeTestRule.setContent {
      val mapViewModel = MapViewModel(LocalContext.current)
      mapViewModel.setLocationPermissionAsked(true)
      mapViewModel.setLocationPermissionGranted(false)
      MapScreen(
          navigationActions = NavigationActions(rememberNavController()),
          mapViewModel = mapViewModel,
          currentMusicPlayed = SpotifyTrack("trackId", "trackName", "trackUri", 10, 10),
      )
    }

    // Verify UI elements
    composeTestRule.onNodeWithTag("MapScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("MapScreenColumn").assertIsDisplayed()

    // Wait for map to load
    composeTestRule.waitUntil(timeoutMillis = 10000) {
      composeTestRule.onNodeWithTag("Map").isDisplayed()
    }

    // Click current location button
    composeTestRule.onNodeWithTag("currentLocationFab").assertIsDisplayed()
    composeTestRule.onNodeWithTag("currentLocationFab").performClick()
  }
}
