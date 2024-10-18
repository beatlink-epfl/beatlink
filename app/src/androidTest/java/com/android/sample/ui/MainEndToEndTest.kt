package com.android.sample.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.android.sample.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainEndToEndTest {

  // Launches the MainActivity
  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

  // Grant fine location permission
  @get:Rule
  var fineLocationPermissionRule =
      GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)

  @Test
  fun testEndToEndFlow() {
    // Step 1: Start at Welcome Screen and verify that it is displayed
    composeTestRule.onNodeWithTag("welcomeScreen").assertIsDisplayed()

    // Step 2: Click the login button and verify navigation to Map Screen
    composeTestRule.onNodeWithTag("loginButton").performClick()
    composeTestRule.onNodeWithTag("mapScreen").assertIsDisplayed()

    // Step 3: Click the search button and verify navigation to Search Screen
    composeTestRule.onNodeWithTag("Search").isDisplayed()
    composeTestRule.onNodeWithTag("Search").performClick()
    composeTestRule.onNodeWithTag("searchScreen").assertIsDisplayed()

    // Step 4 : Click the library button and verify navigation to Library Screen
    composeTestRule.onNodeWithTag("Library").isDisplayed()
    composeTestRule.onNodeWithTag("Library").performClick()
    composeTestRule.onNodeWithTag("libraryScreen").assertIsDisplayed()

    // Step 5: Click the profile button and verify navigation to Profile Screen
    composeTestRule.onNodeWithTag("Profile").isDisplayed()
    composeTestRule.onNodeWithTag("Profile").performClick()
    composeTestRule.onNodeWithTag("profileScreen").assertIsDisplayed()

    // Step 6 : Click the home button and verify navigation to Home Screen
    composeTestRule.onNodeWithTag("Home").isDisplayed()
    composeTestRule.onNodeWithTag("Home").performClick()
    composeTestRule.onNodeWithTag("mapScreen").assertIsDisplayed()
  }
}
