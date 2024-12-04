package com.epfl.beatlink.ui.e2e

import android.Manifest
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.epfl.beatlink.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class M1e2eTest {

  // Launches the MainActivity
  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

  @get:Rule
  val permissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(
          Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

  @Test
  fun testEndToEndFlow() {

    if (composeTestRule.onNodeWithTag("welcomeScreen").isDisplayed()) {

      // Step 1: Start at Welcome Screen and verify that it is displayed
      composeTestRule.onNodeWithTag("welcomeScreen").assertIsDisplayed()

      // Step 2: Click the login button and verify navigation to Login Screen
      composeTestRule.onNodeWithTag("welcomeLoginButton").performScrollTo().performClick()

      // Step 3: Log in with test user credentials
      composeTestRule.onNodeWithTag("loginScreen").assertIsDisplayed()
      composeTestRule
          .onNodeWithTag("inputEmail")
          .performScrollTo()
          .performTextInput("testuser@gmail.com")
      composeTestRule
          .onNodeWithTag("inputPassword")
          .performScrollTo()
          .performTextInput("testuserbeatlink")
      composeTestRule.onNodeWithTag("loginButton").performScrollTo().performClick()

      // Step 5: Click the search button and verify navigation to Search Screen
      composeTestRule.waitForIdle()
      composeTestRule.waitUntil(5000) { composeTestRule.onNodeWithTag("MapScreen").isDisplayed() }
    }
    composeTestRule.onNodeWithTag("Search").isDisplayed()
    composeTestRule.onNodeWithTag("Search").performClick()
    composeTestRule.onNodeWithTag("searchScreen").assertIsDisplayed()

    // Step 6: Click the library button and verify navigation to Library Screen
    composeTestRule.onNodeWithTag("Library").isDisplayed()
    composeTestRule.onNodeWithTag("Library").performClick()
    composeTestRule.onNodeWithTag("libraryScreen").assertIsDisplayed()

    // Step 7: Click the profile button and verify navigation to Profile Screen
    composeTestRule.onNodeWithTag("Profile").isDisplayed()
    composeTestRule.onNodeWithTag("Profile").performClick()
    composeTestRule.onNodeWithTag("profileScreen").assertIsDisplayed()
  }
}
