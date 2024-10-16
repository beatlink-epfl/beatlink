package com.android.sample.ui.authentication

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignUpScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun displayAllComponents() {
    // Set the content of the Composable for testing
    composeTestRule.setContent { SignUpScreen() }

    // Check if the sign up screen is displayed
    composeTestRule.onNodeWithTag("signUpScreen").assertIsDisplayed()

    // Check if the app name is displayed and verify the text
    composeTestRule.onNodeWithTag("appName").assertIsDisplayed().assertTextEquals("BeatLink")

    // Check if the back button is displayed and has a click action
    composeTestRule.onNodeWithTag("backButton").assertIsDisplayed().assertHasClickAction()

    // Check if the greeting text is displayed and verify the text
    composeTestRule
        .onNodeWithTag("greetingText")
        .assertIsDisplayed()
        .assertTextEquals("Create an account now\nto join our community")

    // Check if the email input field is displayed
    composeTestRule.onNodeWithTag("inputEmail").assertIsDisplayed()

    // Check if the username input field is displayed
    composeTestRule.onNodeWithTag("inputUsername").assertIsDisplayed()

    // Check if the password input field is displayed
    composeTestRule.onNodeWithTag("inputPassword").assertIsDisplayed()

    // Check if the confirm password input field is displayed
    composeTestRule.onNodeWithTag("inputConfirmPassword").assertIsDisplayed()

    // Check if the "Link My Spotify Account" button is displayed and clickable
    composeTestRule
        .onNodeWithTag("linkSpotifyText")
        .assertIsDisplayed()
        .assertTextEquals("Link My Spotify Account")
    composeTestRule.onNodeWithTag("linkBox").assertIsDisplayed().assertHasClickAction()

    // Check if the "Create New Account" button is displayed, text is correct, and clickable
    composeTestRule
        .onNodeWithTag("createAccountButton")
        .assertIsDisplayed()
        .assertHasClickAction()
        .assertTextEquals("Create New Account")

    // Check if the "Login" clickable text is displayed, text is correct, and clickable
    composeTestRule
        .onNodeWithTag("loginClickableText")
        .assertIsDisplayed()
        .assertHasClickAction()
        .assertTextEquals("Login")
  }
}
