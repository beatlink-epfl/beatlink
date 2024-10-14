package com.android.sample.ui.map

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.android.sample.ui.navigation.NavigationActions
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MapScreenTest {
  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()
  @get:Rule
  var permissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)

  @Test
  fun MapScreenTest() {
    composeTestRule.setContent { MapScreen(NavigationActions(rememberNavController())) }

    composeTestRule.onNodeWithTag("MapScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("MapContainer").assertIsDisplayed()

    // Wait for the map to load
    composeTestRule.waitUntil(timeoutMillis = 10000) {
      composeTestRule.onNodeWithTag("Map").isDisplayed()
    }
    composeTestRule.onNodeWithTag("playerContainer").assertIsDisplayed()
    composeTestRule.onNodeWithTag("currentLocationButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("playerText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("currentLocationFab").assertIsDisplayed()
  }
}
