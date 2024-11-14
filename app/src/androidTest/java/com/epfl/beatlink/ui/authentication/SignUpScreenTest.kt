package com.epfl.beatlink.ui.authentication

import android.app.Application
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.epfl.beatlink.model.authentication.FirebaseAuthRepository
import com.epfl.beatlink.model.authentication.FirebaseAuthViewModel
import com.epfl.beatlink.model.spotify.auth.SpotifyAuthRepository
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
import okhttp3.OkHttpClient
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify

@RunWith(AndroidJUnit4::class)
class SignUpScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions
  private lateinit var spotifyAuthViewModel: SpotifyAuthViewModel
  private lateinit var authViewModel: FirebaseAuthViewModel
  private lateinit var authRepository: FirebaseAuthRepository
  private lateinit var spotifyRepository: SpotifyAuthRepository

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    authRepository = mock(FirebaseAuthRepository::class.java)
    authViewModel = FirebaseAuthViewModel(authRepository)

    val application = ApplicationProvider.getApplicationContext<Application>()
    // Create a real instance of SpotifyAuthRepository with OkHttpClient, etc.
    spotifyRepository = SpotifyAuthRepository(client = OkHttpClient()) // or any required client
    spotifyAuthViewModel = SpotifyAuthViewModel(application, spotifyRepository)

    // Set the content for the composable
    composeTestRule.setContent {
      SignUpScreen(navigationActions, spotifyAuthViewModel, authViewModel)
    }
  }

  @Test
  fun displayAllComponents() {
    composeTestRule.onNodeWithTag("signUpScreen").assertIsDisplayed()

    composeTestRule.onNodeWithTag("appName").assertIsDisplayed().assertTextEquals("BeatLink")

    composeTestRule.onNodeWithTag("goBackButton").assertIsDisplayed().assertHasClickAction()

    composeTestRule
        .onNodeWithTag("greetingText")
        .assertIsDisplayed()
        .assertTextEquals("Create an account now\nto join our community")

    composeTestRule.onNodeWithTag("inputEmail").performScrollTo().assertIsDisplayed()

    composeTestRule.onNodeWithTag("inputUsername").performScrollTo().assertIsDisplayed()

    composeTestRule.onNodeWithTag("inputPassword").performScrollTo().assertIsDisplayed()

    composeTestRule.onNodeWithTag("inputConfirmPassword").performScrollTo().assertIsDisplayed()

    composeTestRule
        .onNodeWithTag("createAccountButton")
        .performScrollTo()
        .assertIsDisplayed()
        .assertHasClickAction()
        .assertTextEquals("Create New Account")

    composeTestRule
        .onNodeWithTag("accountText")
        .performScrollTo()
        .assertIsDisplayed()
        .assertTextEquals("Already have an account ?")

    composeTestRule
        .onNodeWithTag("loginText")
        .performScrollTo()
        .assertIsDisplayed()
        .assertHasClickAction()
        .assertTextEquals("Login")
  }

  @Test
  fun verifyGoBackButtonNavigatesToWelcomeScreen() {
    composeTestRule.onNodeWithTag("goBackButton").performClick()
    verify(navigationActions).navigateTo(Screen.WELCOME)
  }

  @Test
  fun verifyLoginTextNavigatesToSignUpScreen() {
    composeTestRule.onNodeWithTag("loginText").performScrollTo().performClick()
    verify(navigationActions).navigateTo(Screen.LOGIN)
  }

  @Test
  fun signUpWithCorrectInputs() {
    // Input email, username, password, and confirm password
    composeTestRule
        .onNodeWithTag("inputEmail")
        .performScrollTo()
        .performTextInput("test@example.com")
    composeTestRule.onNodeWithTag("inputUsername").performScrollTo().performTextInput("testuser")
    composeTestRule.onNodeWithTag("inputPassword").performScrollTo().performTextInput("password123")
    composeTestRule
        .onNodeWithTag("inputConfirmPassword")
        .performScrollTo()
        .performTextInput("password123")

    // Click the create account button
    composeTestRule.onNodeWithTag("createAccountButton").performScrollTo().performClick()

    // Verify that the signUp method was called with the correct credentials
    verify(authRepository)
        .signUp(eq("test@example.com"), eq("password123"), eq("testuser"), any(), any())
  }
}
