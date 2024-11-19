package com.epfl.beatlink.ui.profile

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
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
import org.mockito.Mockito.verify
import org.mockito.kotlin.any

@RunWith(AndroidJUnit4::class)
class SignOutButtonTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions
  private lateinit var authViewModel: FirebaseAuthViewModel
  private lateinit var authRepository: FirebaseAuthRepository

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    authRepository = mock(FirebaseAuthRepository::class.java)
    authViewModel = FirebaseAuthViewModel(authRepository)

    // Set the composable for testing
    composeTestRule.setContent {
      SignOutButton(navigationActions = navigationActions, firebaseAuthViewModel = authViewModel)
    }
  }

  @Test
  fun signOutButton_isDisplayedCorrectly() {
    composeTestRule
        .onNodeWithTag("signOutButton")
        .assertIsDisplayed()
        .assertHasClickAction()
        .assertTextEquals("Sign out")
  }

  @Test
  fun signOutButton_performsSignOutAndNavigation() {

    // Perform a click on the sign-out button
    composeTestRule.onNodeWithTag("signOutButton").performClick()

    verify(authRepository).signOut(any(), any())

    // Verify that navigation to the welcome screen is triggered
    verify(navigationActions).navigateTo(Screen.WELCOME)
  }
}
