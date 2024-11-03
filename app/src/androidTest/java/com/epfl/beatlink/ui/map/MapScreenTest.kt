package com.epfl.beatlink.ui.map

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.epfl.beatlink.ui.navigation.NavigationActions
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MapScreenTest {

  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  // Grant fine location permission
  @get:Rule
  var fineLocationPermissionRule =
      GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)

  @Test
  fun mapScreen_withFineLocationPermission() {

    fineLocationPermissionRule.apply {
      composeTestRule.setContent {
        MapScreen(
            navigationActions = NavigationActions(rememberNavController()),
            currentMusicPlayed = "lalala",
            radius = 2000.0)
      }
    }

    // Verify UI elements
    composeTestRule.onNodeWithTag("MapScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("MapScreenColumn").assertIsDisplayed()
    composeTestRule.onNodeWithTag("MapContainer").assertIsDisplayed()

    // Check for loading and map
    composeTestRule.onNodeWithTag("playerText music").assertIsDisplayed()

    composeTestRule.waitUntil(timeoutMillis = 10000) {
      composeTestRule.onNodeWithTag("Map").isDisplayed()
    }

    // Verify current location button and click it
    composeTestRule.onNodeWithTag("currentLocationFab").assertIsDisplayed()
    composeTestRule.onNodeWithTag("currentLocationFab").performClick()
  }
}
