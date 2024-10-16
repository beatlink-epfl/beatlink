package com.android.sample.ui.authentication

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun loginScreen_elementsAreDisplayed() {
    // Launch the composable under test
    composeTestRule.setContent { LoginScreen() }

    // Check if the login screen is displayed
    composeTestRule.onNodeWithTag("loginScreen").assertIsDisplayed()

    // Check if the app name is displayed correctly
    composeTestRule.onNodeWithTag("appName").assertIsDisplayed().assertTextContains("BeatLink")

    // Check if the login title is displayed
    composeTestRule
        .onNodeWithTag("loginTitle")
        .assertIsDisplayed()
        .assertTextContains("Hello again,\nGood to see you back !")

    // Check if the email input field is displayed
    composeTestRule.onNodeWithTag("inputEmail").assertIsDisplayed()

    // Check if the password input field is displayed
    composeTestRule.onNodeWithTag("inputPassword").assertIsDisplayed()

    // Check if the "Don't have an account" text is displayed
    composeTestRule
        .onNodeWithTag("noAccountText")
        .assertIsDisplayed()
        .assertTextContains("Don’t have an account yet ?")

    // Check if the sign-up text is displayed and clickable
    composeTestRule.onNodeWithTag("signUpText").assertIsDisplayed()
  }

  @Test
  fun loginScreen_performLogin() {
    // Launch the composable under test
    composeTestRule.setContent { LoginScreen() }

    // Enter email into the email input field
    composeTestRule.onNodeWithTag("inputEmail").performTextInput("test@example.com")

    // Enter password into the password input field
    composeTestRule.onNodeWithTag("inputPassword").performTextInput("password123")

    // Perform click action on the login button
    composeTestRule.onNodeWithTag("loginButton").performClick()

    // TODO: Verify expected behavior after login button is clicked
  }

  @Test
  fun goBackButton_clickNavigatesBack() {
    // Launch the composable under test
    composeTestRule.setContent { LoginScreen() }

    // Perform click on the back button
    composeTestRule.onNodeWithTag("goBackButton").performClick()

    // TODO: Verify navigation back action is triggered
  }

  @Test
  fun signUpText_clickNavigatesToSignUp() {
    // Launch the composable under test
    composeTestRule.setContent { LoginScreen() }

    // Perform click on the sign-up text
    composeTestRule.onNodeWithTag("signUpText").performClick()

    // TODO: Verify navigation to sign-up screen
  }
}
