package com.android.sample.ui.map

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.android.sample.ui.navigation.NavigationActions
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MapScreenWithCoarseLocationPermissionTest {

  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  // Grant coarse location permission
  @get:Rule
  var coarseLocationPermissionRule =
      GrantPermissionRule.grant(android.Manifest.permission.ACCESS_COARSE_LOCATION)

  @Test
  fun mapScreen_withCoarseLocationPermission() {
    composeTestRule.setContent {
      MapScreen(
          navigationActions = NavigationActions(rememberNavController()), currentMusicPlayed = null)
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
