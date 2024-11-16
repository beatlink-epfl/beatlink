package com.epfl.beatlink.ui.authentication

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
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.epfl.beatlink.model.auth.FirebaseAuthRepository
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.viewmodel.auth.FirebaseAuthViewModel
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
  private lateinit var firebaseAuthViewModel: FirebaseAuthViewModel
  private lateinit var firebaseAuthRepository: FirebaseAuthRepository

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    firebaseAuthRepository = mock(FirebaseAuthRepository::class.java)
    firebaseAuthViewModel = FirebaseAuthViewModel(firebaseAuthRepository)

    // Set the content for the composable
    composeTestRule.setContent { LoginScreen(navigationActions, firebaseAuthViewModel) }
  }

  @Test
  fun displayAllComponents() {
    composeTestRule.onNodeWithTag("loginScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("goBackButton").assertIsDisplayed()

    composeTestRule.onNodeWithTag("appName").assertIsDisplayed()
    composeTestRule.onNodeWithTag("appName").assertTextContains("BeatLink")

    composeTestRule.onNodeWithTag("loginTitle").performScrollTo().assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("loginTitle")
        .assertTextContains("Hello again,\nGood to see you back !")

    composeTestRule.onNodeWithTag("inputEmail").performScrollTo().assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("inputEmail", useUnmergedTree = true)
        .onChildren()
        .filterToOne(hasText("Email", substring = true))
        .assertIsDisplayed()

    composeTestRule.onNodeWithTag("inputPassword").performScrollTo().assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("inputPassword", useUnmergedTree = true)
        .onChildren()
        .filterToOne(hasText("Password", substring = true))
        .assertIsDisplayed()

    composeTestRule.onNodeWithTag("noAccountText").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("noAccountText").assertTextContains("Don’t have an account yet ?")

    composeTestRule.onNodeWithTag("signUpText").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("signUpText").assertTextEquals("Sign up")
    composeTestRule.onNodeWithTag("signUpText").assertHasClickAction()
  }

  @Test
  fun verifyGoBackButtonNavigatesToWelcomeScreen() {
    composeTestRule.onNodeWithTag("goBackButton").performClick()
    verify(navigationActions).navigateTo(Screen.WELCOME)
  }

  @Test
  fun verifySignUpTextNavigatesToSignUpScreen() {
    composeTestRule.onNodeWithTag("signUpText").performScrollTo().performClick()
    verify(navigationActions).navigateTo(Screen.REGISTER)
  }

  @Test
  fun loginWithCorrectInputs() {
    // Input email and password
    composeTestRule.onNodeWithTag("inputEmail").performTextInput("test@example.com")
    composeTestRule.onNodeWithTag("inputPassword").performTextInput("password123")

    // Click login button
    composeTestRule.onNodeWithTag("loginButton").performScrollTo().performClick()

    // Verify that the login method was called with the correct credentials
    verify(firebaseAuthRepository).login(eq("test@example.com"), eq("password123"), any(), any())
  }
}
