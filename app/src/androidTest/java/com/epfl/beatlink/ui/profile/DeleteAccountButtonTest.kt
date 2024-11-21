package com.epfl.beatlink.ui.profile

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.epfl.beatlink.model.auth.FirebaseAuthRepository
import com.epfl.beatlink.model.profile.ProfileRepository
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.viewmodel.auth.FirebaseAuthViewModel
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class DeleteAccountButtonTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions
  private lateinit var authViewModel: FirebaseAuthViewModel
  private lateinit var profileViewModel: ProfileViewModel
  private lateinit var authRepository: FirebaseAuthRepository
  private lateinit var profileRepository: ProfileRepository

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    authRepository = mock(FirebaseAuthRepository::class.java)
    profileRepository = mock(ProfileRepository::class.java)
    authViewModel = FirebaseAuthViewModel(authRepository)
    profileViewModel = ProfileViewModel(profileRepository)

    // Set the composable for testing
    composeTestRule.setContent {
      DeleteAccountButton(
          navigationActions = navigationActions,
          firebaseAuthViewModel = authViewModel,
          profileViewModel = profileViewModel)
    }
  }

  @Test
  fun deleteAccountButton_isDisplayedCorrectly() {
    composeTestRule
        .onNodeWithTag("deleteAccountButton")
        .assertIsDisplayed()
        .assertHasClickAction()
        .assertTextEquals("Delete Account")
  }

  @Test
  fun deleteAccountButton_showsDialogOnClick() {
    // Perform click on the delete button
    composeTestRule.onNodeWithTag("deleteAccountButton").performClick()

    // Check if the dialog is displayed
    composeTestRule.onNodeWithTag("passwordField").assertIsDisplayed()
  }

  @Test
  fun deleteAccountDialog_performsAccountDeletion() = runTest {
    // Mock `getUserId` to return a valid user ID
    whenever(profileRepository.getUserId()).thenReturn("testUserId")

    // Simulate a successful account deletion
    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<() -> Unit>(1)
          onSuccess.invoke()
        }
        .whenever(authRepository)
        .deleteAccount(any(), any(), any())

    // Simulate successful profile deletion
    whenever(profileRepository.deleteProfile("testUserId")).thenReturn(true)

    // Perform click on the delete button
    composeTestRule.onNodeWithTag("deleteAccountButton").performClick()

    // Enter password
    composeTestRule.onNodeWithTag("passwordField").performTextInput("testPassword")

    // Confirm deletion
    composeTestRule.onNodeWithTag("confirmButton").performClick()

    // Verify that deleteAccount is called on the auth repository
    verify(authRepository).deleteAccount(any(), any(), any())

    // Verify that deleteProfile is called on the profile repository with the correct user ID
    verify(profileRepository).deleteProfile("testUserId")

    // Verify navigation to the login screen
    verify(navigationActions).navigateTo(Screen.LOGIN)
  }

  @Test
  fun deleteAccountDialog_cancelDoesNotPerformDeletion() = runTest {
    // Perform click on the delete button
    composeTestRule.onNodeWithTag("deleteAccountButton").performClick()

    // Click cancel
    composeTestRule.onNodeWithTag("cancelButton").performClick()

    // Verify that deleteAccount is NOT called
    verify(authRepository, times(0)).deleteAccount(any(), any(), any())

    // Verify that deleteProfile is NOT called
    verify(profileRepository, times(0)).deleteProfile(any())

    // Verify no navigation to the login screen
    verify(navigationActions, times(0)).navigateTo(Screen.LOGIN)
  }
}
