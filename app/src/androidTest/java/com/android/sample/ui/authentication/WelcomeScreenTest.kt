package com.android.sample.ui.authentication

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WelcomeScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun elementsAreCorrectlyDisplayed() {
    // Launch the composable under test
    composeTestRule.setContent { WelcomeScreen() }

    // Check if the app logo is displayed
    composeTestRule.onNodeWithTag("appLogo").assertIsDisplayed()

    // Check if the app name is displayed correctly
    composeTestRule.onNodeWithTag("appName").assertIsDisplayed().assertTextContains("BeatLink")

    // Check if the subtitle text is displayed
    composeTestRule
        .onNodeWithTag("appText")
        .assertIsDisplayed()
        .assertTextContains("Link Up Through Music")

    // Check if the sign-in button is displayed
    composeTestRule.onNodeWithTag("loginButton").assertIsDisplayed()

    // Check if the "Sign in with Spotify" text is displayed inside the button
    composeTestRule.onNodeWithText("Sign in with Spotify").assertIsDisplayed()
  }

  @Test
  fun signInButton_clickTriggersAction() {
    // Launch the composable under test
    composeTestRule.setContent { WelcomeScreen() }

    // Perform click action on the sign-in button
    composeTestRule.onNodeWithTag("loginButton").performClick()

    // TODO: Verify the expected behavior when the button is clicked
  }
}
