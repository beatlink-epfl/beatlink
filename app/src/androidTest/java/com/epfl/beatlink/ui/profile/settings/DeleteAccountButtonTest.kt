package com.epfl.beatlink.ui.profile.settings

import android.app.Application
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChild
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.epfl.beatlink.model.auth.FirebaseAuthRepository
import com.epfl.beatlink.model.profile.ProfileData
import com.epfl.beatlink.model.profile.ProfileRepository
import com.epfl.beatlink.repository.spotify.auth.SpotifyAuthRepository
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.viewmodel.auth.FirebaseAuthViewModel
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel
import com.epfl.beatlink.viewmodel.spotify.auth.SpotifyAuthViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DeleteAccountButtonTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions
  private lateinit var authViewModel: FirebaseAuthViewModel
  private lateinit var profileViewModel: ProfileViewModel
  private lateinit var authRepository: FirebaseAuthRepository
  private lateinit var profileRepository: ProfileRepository
  private lateinit var spotifyRepository: SpotifyAuthRepository
  private lateinit var spotifyAuthViewModel: SpotifyAuthViewModel

  @Before
  fun setUp() {
    navigationActions = mockk(relaxed = true)
    authRepository = mockk(relaxed = true)
    profileRepository = mockk(relaxed = true)
    every { profileRepository.getUserId() } returns "testUserId"
    // Mock Profile Data
    val mockProfile =
        ProfileData(
            username = "testUser",
            email = "test@example.com",
            bio = "Test Bio",
            links = 0,
            name = "Test Name",
            profilePicture = null,
            favoriteMusicGenres = listOf("Pop", "Rock"))
    coEvery { profileRepository.fetchProfile("testUserId") } returns mockProfile
    authViewModel = FirebaseAuthViewModel(authRepository)
    profileViewModel = ProfileViewModel(profileRepository)
    val application = ApplicationProvider.getApplicationContext<Application>()
    spotifyRepository = SpotifyAuthRepository(client = OkHttpClient()) // or any required client
    spotifyAuthViewModel = SpotifyAuthViewModel(application, spotifyRepository)

    // Set the composable for testing
    composeTestRule.setContent {
      AccountScreen(
          navigationActions = navigationActions,
          firebaseAuthViewModel = authViewModel,
          profileViewModel = profileViewModel,
          spotifyAuthViewModel = spotifyAuthViewModel)
    }
  }

  @Test
  fun deleteAccountButton_isDisplayedCorrectly() {
    composeTestRule.waitForIdle()
    composeTestRule
        .onNodeWithTag("deleteAccountButton", useUnmergedTree = true)
        .performScrollTo()
        .assertIsDisplayed()
        .assertHasClickAction()
        .onChild()
        .assertTextEquals("Delete account")
  }

  @Test
  fun deleteAccountButton_showsDialogOnClick() {
    // Perform click on the delete button
    composeTestRule
        .onNodeWithTag("deleteAccountButton", useUnmergedTree = true)
        .performScrollTo()
        .performClick()

    composeTestRule.waitForIdle()

    // Check if the dialog is displayed
    composeTestRule.onNodeWithTag("passwordField").assertIsDisplayed()
  }

  @Test
  fun deleteAccountDialog_confirmPerformsAccountDeletion() = runTest {
    // Mock `getUserId` to return a valid user ID
    every { profileRepository.getUserId() } returns "testUserId"

    // Mock successful profile deletion
    coEvery { profileRepository.deleteProfile(any()) } returns true

    // Mock successful account deletion
    coEvery { authRepository.deleteAccount(any(), any(), any()) } answers
        {
          val onSuccess = secondArg<() -> Unit>()
          onSuccess()
        }

    // Perform click on the delete button
    composeTestRule
        .onNodeWithTag("deleteAccountButton", useUnmergedTree = true)
        .performScrollTo()
        .performClick()

    composeTestRule.waitForIdle()

    // Ensure dialog is displayed
    composeTestRule.onNodeWithTag("passwordField").assertIsDisplayed()

    // Enter password
    composeTestRule.onNodeWithTag("passwordField").performTextInput("testPassword")

    // Click confirm
    composeTestRule.onNodeWithTag("confirmButton").performClick()

    composeTestRule.waitForIdle()

    // Verify that deleteAccount is called with the correct parameters
    coVerify {
      authRepository.deleteAccount(
          eq("testPassword"),
          any(), // onSuccess callback
          any() // onFailure callback
          )
    }

    // Verify that deleteProfile is called with the correct parameters
    coVerify { profileRepository.deleteProfile(eq("testUserId")) }

    // Verify navigation to the WELCOME screen
    verify { navigationActions.navigateTo(Screen.WELCOME) }
  }

  @Test
  fun deleteAccountDialog_cancelDoesNotPerformDeletion() = runTest {
    // Perform click on the delete button
    composeTestRule
        .onNodeWithTag("deleteAccountButton", useUnmergedTree = true)
        .performScrollTo()
        .performClick()

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("passwordField").assertIsDisplayed()

    // Click cancel
    composeTestRule.onNodeWithTag("cancelButton").performClick()

    // Verify that deleteAccount is NOT called
    coVerify(exactly = 0) { authRepository.deleteAccount(any(), any(), any()) }

    // Verify that deleteProfile is NOT called
    coVerify(exactly = 0) { profileRepository.deleteProfile(any()) }

    // Verify no navigation to the login screen
    verify(exactly = 0) { navigationActions.navigateTo(Screen.WELCOME) }
  }

  @Test
  fun deleteAccountDialog_confirmHandlesAccountDeletionFailure() = runTest {
    // Mock `getUserId` to return a valid user ID
    every { profileRepository.getUserId() } returns "testUserId"

    // Mock successful profile deletion
    coEvery { profileRepository.deleteProfile(any()) } returns true

    // Mock account deletion failure
    coEvery { authRepository.deleteAccount(any(), any(), any()) } answers
        {
          val onFailure = thirdArg<(Throwable) -> Unit>()
          onFailure(Exception("Mocked deletion failure"))
        }

    // Mock restoring the profile
    coEvery { profileRepository.addProfile(any(), any()) } returns true

    // Perform click on the delete button
    composeTestRule
        .onNodeWithTag("deleteAccountButton", useUnmergedTree = true)
        .performScrollTo()
        .performClick()

    composeTestRule.waitForIdle()

    // Ensure dialog is displayed
    composeTestRule.onNodeWithTag("passwordField").assertIsDisplayed()

    // Enter password
    composeTestRule.onNodeWithTag("passwordField").performTextInput("testPassword")

    // Click confirm
    composeTestRule.onNodeWithTag("confirmButton").performClick()

    composeTestRule.waitForIdle()

    // Verify that deleteAccount is called with the correct parameters
    coVerify {
      authRepository.deleteAccount(
          eq("testPassword"),
          any(), // onSuccess callback
          any() // onFailure callback
          )
    }

    // Verify that addProfile is called to restore the profile
    coVerify { profileRepository.addProfile(eq("testUserId"), any()) }

    // Verify the dialog is dismissed
    composeTestRule.onNodeWithTag("passwordField").assertDoesNotExist()

    // Verify that no navigation occurs
    verify(exactly = 0) { navigationActions.navigateTo(Screen.WELCOME) }
  }
}
