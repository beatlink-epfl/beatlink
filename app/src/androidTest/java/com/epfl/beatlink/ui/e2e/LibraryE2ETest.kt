package com.epfl.beatlink.ui.e2e

import android.Manifest
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.rule.GrantPermissionRule
import com.epfl.beatlink.MainActivity
import org.junit.Rule
import org.junit.Test

class LibraryE2ETest {
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

      // Step 4: Click the library button and verify navigation to Library Screen
      composeTestRule.waitForIdle()
      composeTestRule.waitUntil(5000) { composeTestRule.onNodeWithTag("MapScreen").isDisplayed() }
    }
    composeTestRule.onNodeWithTag("Library").isDisplayed()
    composeTestRule.onNodeWithTag("Library").performClick()
    composeTestRule.onNodeWithTag("libraryScreen").assertIsDisplayed()

    // Step 5: Click the add button and verify navigation to Create New Playlist Screen
    composeTestRule.onNodeWithTag("addButton").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.waitUntil(2000) {
      composeTestRule.onNodeWithTag("createNewPlaylistScreen").isDisplayed()
    }
    composeTestRule.onNodeWithTag("createNewPlaylistScreen").assertExists()

    // Step 6: Fill in the playlist title and description
    composeTestRule
        .onNodeWithTag("inputPlaylistTitle")
        .performScrollTo()
        .performTextInput("Test Playlist")

    composeTestRule
        .onNodeWithTag("inputPlaylistDescription")
        .performScrollTo()
        .performTextInput("This is a test playlist")

    // Step 7: Click the create button and verify navigation to Playlist Overview Screen
    composeTestRule.onNodeWithTag("createPlaylist").performScrollTo().performClick()

    composeTestRule.onNodeWithTag("Library").performClick()
    composeTestRule.onNodeWithTag("libraryScreen").assertExists()

    composeTestRule.onNodeWithTag("MY PLAYLISTSTitleWithArrow").performScrollTo().performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithText("Test Playlist").performScrollTo().performClick()
    composeTestRule.waitForIdle()

    // Step 8: Click the edit button and verify navigation to Edit Playlist Screen
    composeTestRule.onNodeWithTag("editButton").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.waitUntil(6000) {
      composeTestRule.onNodeWithTag("editPlaylistScreen").isDisplayed()
    }

    // Step 9: Click the delete button and verify navigation to Library Screen
    composeTestRule.onNodeWithTag("deleteButton").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.waitUntil(7000) {
      composeTestRule.onNodeWithTag("myPlaylistsScreen").isDisplayed()
    }
    composeTestRule.onNodeWithTag("myPlaylistsScreen").assertIsDisplayed()
  }
}
