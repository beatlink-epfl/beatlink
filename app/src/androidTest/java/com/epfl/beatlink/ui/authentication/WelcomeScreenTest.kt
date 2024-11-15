package com.epfl.beatlink.ui.authentication

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify

@RunWith(AndroidJUnit4::class)
class WelcomeScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
  }

  @Test
  fun displayAllComponents() {
    composeTestRule.setContent { WelcomeScreen(navigationActions) }

    // Check if the app logo is displayed
    composeTestRule.onNodeWithTag("appLogo").assertIsDisplayed()

    // Check if the app name is displayed correctly
    composeTestRule.onNodeWithTag("appName").assertIsDisplayed().assertTextContains("BeatLink")

    // Check if the subtitle text is displayed
    composeTestRule
        .onNodeWithTag("appText")
        .assertIsDisplayed()
        .assertTextContains("Link Up Through Music")

    // Check if the sign up button is displayed
    composeTestRule.onNodeWithTag("signUpButton").performScrollTo().assertIsDisplayed()

    // Check if the login button is displayed
    composeTestRule.onNodeWithTag("welcomeLoginButton").performScrollTo().assertIsDisplayed()
  }

  @Test
  fun verifyLoginButtonNavigatesToLoginScreen() {
    composeTestRule.setContent { WelcomeScreen(navigationActions) }

    // Perform click action on the sign-in button
    composeTestRule.onNodeWithTag("welcomeLoginButton").performScrollTo().performClick()

    verify(navigationActions).navigateTo(Screen.LOGIN)
  }
}
