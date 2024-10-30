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
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.model.auth.AuthRepository
import com.android.sample.model.auth.AuthViewModel
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions
  private lateinit var authViewModel: AuthViewModel
  private lateinit var authRepository: AuthRepository

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    authRepository = mock(AuthRepository::class.java)
    authViewModel = AuthViewModel(authRepository)

    // Set the content for the composable
    composeTestRule.setContent { LoginScreen(navigationActions, authViewModel) }
  }

  @Test
  fun displayAllComponents() {
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

    composeTestRule.onNodeWithTag("inputPassword").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("inputPassword", useUnmergedTree = true)
        .onChildren()
        .filterToOne(hasText("Password", substring = true))
        .assertIsDisplayed()

    composeTestRule.onNodeWithTag("noAccountText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("noAccountText").assertTextContains("Don’t have an account yet ?")

    composeTestRule.onNodeWithTag("signUpText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("signUpText").assertTextEquals("Sign up")
    composeTestRule.onNodeWithTag("signUpText").assertHasClickAction()
  }

  @Test
  fun verifyGoBackButtonNavigatesBack() {
    composeTestRule.onNodeWithTag("goBackButton").performClick()
    verify(navigationActions).goBack()
  }

  @Test
  fun verifySignUpTextNavigatesToSignUpScreen() {
    composeTestRule.onNodeWithTag("signUpText").performClick()
    verify(navigationActions).navigateTo(Screen.REGISTER)
  }

  @Test
  fun loginWithCorrectInputs() {
    // Input email and password
    composeTestRule.onNodeWithTag("inputEmail").performTextInput("test@example.com")
    composeTestRule.onNodeWithTag("inputPassword").performTextInput("password123")

    // Click login button
    composeTestRule.onNodeWithTag("loginButton").performClick()

    // Verify that the login method was called with the correct credentials
    verify(authRepository).login(eq("test@example.com"), eq("password123"), any(), any())
  }
}
