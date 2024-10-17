package com.android.sample.ui.authentication

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollTo
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.ui.navigation.NavigationActions
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
class SignUpScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationDestination: NavDestination
  private lateinit var navHostController: NavHostController
  private lateinit var navigationActions: NavigationActions

  @Before
  fun setUp() {
    navigationDestination = mock(NavDestination::class.java)
    navHostController = mock(NavHostController::class.java)
    navigationActions = NavigationActions(navHostController)
  }

  @Test
  fun displayAllComponents() {
    composeTestRule.setContent { SignUpScreen(navigationActions) }

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
