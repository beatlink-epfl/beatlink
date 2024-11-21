package com.epfl.beatlink.ui.profile.settings

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.epfl.beatlink.model.auth.FirebaseAuthRepository
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.viewmodel.auth.FirebaseAuthViewModel
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify

@RunWith(AndroidJUnit4::class)
class ChangePasswordScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions
  private lateinit var authViewModel: FirebaseAuthViewModel
  private lateinit var authRepository: FirebaseAuthRepository

  @Before
  fun setUp() {
    navigationActions = mockk(relaxed = true)
    authRepository = mock(FirebaseAuthRepository::class.java)
    authViewModel = FirebaseAuthViewModel(authRepository)

    // Set the content for the composable
    composeTestRule.setContent { ChangePassword(navigationActions, authViewModel) }
  }

  @Test
  fun displayAllComponents() {
    composeTestRule.onNodeWithTag("changePasswordScreen").assertIsDisplayed()

    composeTestRule.onNodeWithTag("goBackButton").assertIsDisplayed().assertHasClickAction()

    composeTestRule
        .onNodeWithTag("instructionText")
        .assertIsDisplayed()
        .assertTextEquals("Please enter your existing\n" + "password and your new password.")

    composeTestRule.onNodeWithTag("inputCurrentPassword").performScrollTo().assertIsDisplayed()

    composeTestRule.onNodeWithTag("inputNewPassword").performScrollTo().assertIsDisplayed()

    composeTestRule.onNodeWithTag("inputConfirmNewPassword").performScrollTo().assertIsDisplayed()

    composeTestRule
        .onNodeWithTag("changePasswordButton")
        .performScrollTo()
        .assertIsDisplayed()
        .assertHasClickAction()
        .assertTextEquals("Save")
  }

  @Test
  fun signUpWithCorrectInputs() = runTest {
    composeTestRule
        .onNodeWithTag("inputCurrentPassword")
        .performScrollTo()
        .performTextInput("123456")
    composeTestRule.onNodeWithTag("inputNewPassword").performScrollTo().performTextInput("newpass")
    composeTestRule
        .onNodeWithTag("inputConfirmNewPassword")
        .performScrollTo()
        .performTextInput("newpass")

    // Click the create account button
    composeTestRule.onNodeWithTag("changePasswordButton").performScrollTo().performClick()

    verify(authRepository).verifyPassword(eq("123456"))
    verify(authRepository).changePassword(eq("newpass"))
  }
}
