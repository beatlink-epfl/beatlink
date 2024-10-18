package com.android.sample.ui.authentication

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
  }

  @Test
  fun displayAllComponents() {
    composeTestRule.setContent { LoginScreen(navigationActions) }

    composeTestRule.onNodeWithTag("loginScreen").assertIsDisplayed()

    composeTestRule.onNodeWithTag("goBackButton").assertIsDisplayed()

    composeTestRule.onNodeWithTag("appName").assertIsDisplayed()
    composeTestRule.onNodeWithTag("appName").assertTextContains("BeatLink")

    composeTestRule.onNodeWithTag("loginTitle").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("loginTitle")
        .assertTextContains("Hello again,\nGood to see you back !")

    composeTestRule.onNodeWithTag("inputEmail").assertIsDisplayed()

    composeTestRule
        .onNodeWithTag("inputEmail", useUnmergedTree = true)
        .onChildren()
        .filterToOne(hasText("Email", substring = true))
        .assertIsDisplayed()

    composeTestRule
        .onNodeWithTag("inputEmail", useUnmergedTree = true)
        .performClick()
        .onChildren()
        .filterToOne(hasText("Enter email address", substring = true))
        .assertIsDisplayed()

    composeTestRule.onNodeWithTag("inputPassword").assertIsDisplayed()

    composeTestRule
        .onNodeWithTag("inputPassword", useUnmergedTree = true)
        .onChildren()
        .filterToOne(hasText("Password", substring = true))
        .assertIsDisplayed()

    composeTestRule
        .onNodeWithTag("inputPassword", useUnmergedTree = true)
        .performClick()
        .onChildren()
        .filterToOne(hasText("Enter password", substring = true))
        .assertIsDisplayed()

    composeTestRule.onNodeWithTag("noAccountText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("noAccountText").assertTextContains("Don’t have an account yet ?")

    composeTestRule.onNodeWithTag("signUpText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("signUpText").assertTextEquals("Sign up")
    composeTestRule.onNodeWithTag("signUpText").assertHasClickAction()
  }

  /**
   * @Test fun successfulLogin() { composeTestRule.setContent { LoginScreen(navigationActions) }
   *
   * composeTestRule.onNodeWithTag("inputEmail").performTextInput("test@example.com")
   * composeTestRule.onNodeWithTag("inputPassword").performTextInput("password123")
   * composeTestRule.onNodeWithTag("loginButton").performClick()
   *
   * // TODO: Verify expected behavior after login button is clicked }
   */
  @Test
  fun verifyGoBackButtonNavigatesBack() {
    composeTestRule.setContent { LoginScreen(navigationActions) }

    composeTestRule.onNodeWithTag("goBackButton").performClick()
    verify(navigationActions).goBack()
  }

  @Test
  fun verifySignUpTextNavigatesToSignUpScreen() {
    composeTestRule.setContent { LoginScreen(navigationActions) }

    composeTestRule.onNodeWithTag("signUpText").performClick()
    verify(navigationActions).navigateTo(Screen.REGISTER)
  }
}
