package com.epfl.beatlink.ui.e2e

import android.Manifest
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.epfl.beatlink.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileE2ETest {

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

      // Wait for the map screen to be displayed
      composeTestRule.waitUntil(4000) { composeTestRule.onNodeWithTag("MapScreen").isDisplayed() }
    }
    // Step 4: Click the profile button and verify navigation to Profile Screen
    composeTestRule.onNodeWithTag("Profile").isDisplayed()
    composeTestRule.onNodeWithTag("Profile").performClick()
    composeTestRule.onNodeWithTag("profileScreen").assertIsDisplayed()

    // Step 5: Click the settings button and verify navigation to Settings Screen
    composeTestRule.onNodeWithTag("profileScreenSettingsButton").performClick()
    composeTestRule.waitUntil(6000) { composeTestRule.onNodeWithTag("settingScreen").isDisplayed() }
    composeTestRule.onNodeWithTag("settingScreen").assertIsDisplayed()

    // Step 6: Click the back arrow button and verify navigation to Profile Screen
    composeTestRule.onNodeWithTag("goBackButton").performClick()
    composeTestRule.waitUntil(4000) { composeTestRule.onNodeWithTag("profileScreen").isDisplayed() }
    composeTestRule.onNodeWithTag("profileScreen").assertIsDisplayed()

    // Step 7: Click the edit profile button and verify navigation to Edit Profile Screen
    composeTestRule.onNodeWithTag("editProfileButton").performClick()
    composeTestRule.waitUntil(6000) {
      composeTestRule.onNodeWithTag("editProfileScreen").isDisplayed()
    }
    composeTestRule.onNodeWithTag("editProfileScreen").assertIsDisplayed()

    // Step 8: Edit the profile
    composeTestRule.onNodeWithTag("editProfileNameInput").performTextClearance()
    composeTestRule.onNodeWithTag("editProfileNameInput").performTextInput("John Doe")
    composeTestRule.onNodeWithTag("editProfileDescriptionInput").performTextClearance()
    composeTestRule
        .onNodeWithTag("editProfileDescriptionInput")
        .performTextInput("This is a test bio.")
    composeTestRule.onNodeWithTag("saveProfileButton").performClick()
  }
}
