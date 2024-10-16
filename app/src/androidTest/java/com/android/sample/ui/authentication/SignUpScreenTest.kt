package com.android.sample.ui.authentication

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignUpScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun displayAllComponents() {
    composeTestRule.setContent { SignUpScreen() }

    composeTestRule.onNodeWithTag("signUpScreen").assertIsDisplayed()

    composeTestRule.onNodeWithTag("appName").assertIsDisplayed().assertTextEquals("BeatLink")

    composeTestRule.onNodeWithTag("backButton").assertIsDisplayed().assertHasClickAction()

    composeTestRule
        .onNodeWithTag("greetingText")
        .assertIsDisplayed()
        .assertTextEquals("Create an account now\nto join our community")

    composeTestRule.onNodeWithTag("inputEmail").performScrollTo().assertIsDisplayed()

    composeTestRule.onNodeWithTag("inputUsername").performScrollTo().assertIsDisplayed()

    composeTestRule.onNodeWithTag("inputPassword").performScrollTo().assertIsDisplayed()

    composeTestRule.onNodeWithTag("inputConfirmPassword").performScrollTo().assertIsDisplayed()

    composeTestRule
        .onNodeWithTag("linkSpotifyText")
        .performScrollTo()
        .assertIsDisplayed()
        .assertTextEquals("Link My Spotify Account")
    composeTestRule
        .onNodeWithTag("linkBox")
        .performScrollTo()
        .assertIsDisplayed()
        .assertHasClickAction()

    composeTestRule
        .onNodeWithTag("createAccountButton")
        .performScrollTo()
        .assertIsDisplayed()
        .assertHasClickAction()
        .assertTextEquals("Create New Account")

    composeTestRule
        .onNodeWithTag("loginClickableText")
        .performScrollTo()
        .assertIsDisplayed()
        .assertHasClickAction()
        .assertTextEquals("Login")
  }
}
