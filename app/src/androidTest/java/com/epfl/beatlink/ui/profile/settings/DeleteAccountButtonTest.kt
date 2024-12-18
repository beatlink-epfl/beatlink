package com.epfl.beatlink.ui.profile.settings

import android.app.Application
import android.widget.Toast
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.epfl.beatlink.model.profile.ProfileData
import com.epfl.beatlink.repository.authentication.FirebaseAuthRepository
import com.epfl.beatlink.repository.library.PlaylistRepository
import com.epfl.beatlink.repository.map.user.MapUserRepository
import com.epfl.beatlink.repository.profile.ProfileRepository
import com.epfl.beatlink.repository.spotify.auth.SpotifyAuthRepository
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.viewmodel.auth.FirebaseAuthViewModel
import com.epfl.beatlink.viewmodel.library.PlaylistViewModel
import com.epfl.beatlink.viewmodel.map.user.MapUsersViewModel
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel
import com.epfl.beatlink.viewmodel.spotify.auth.SpotifyAuthViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
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
  private lateinit var playlistViewModel: PlaylistViewModel
  private lateinit var playlistRepository: PlaylistRepository
  private lateinit var mapUsersRepository: MapUserRepository
  private lateinit var mapUsersViewModel: MapUsersViewModel

  @Before
  fun setUp() {
    navigationActions = mockk(relaxed = true)
    authRepository = mockk(relaxed = true)
    profileRepository = mockk(relaxed = true)
    playlistRepository = mockk(relaxed = true)
    mapUsersRepository = mockk(relaxed = true)
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
    playlistViewModel = PlaylistViewModel(playlistRepository)
    mapUsersViewModel = MapUsersViewModel(mapUsersRepository)

    // Set the composable for testing
    composeTestRule.setContent {
      SettingsScreen(
          navigationActions = navigationActions,
          firebaseAuthViewModel = authViewModel,
          profileViewModel = profileViewModel,
          spotifyAuthViewModel = spotifyAuthViewModel,
          mapUsersViewModel = mapUsersViewModel,
          playlistViewModel = playlistViewModel)
    }
  }

  @Test
  fun deleteAccountDialog_confirmPerformsFullDeletionSequence() = runTest {
    // Mock password verification success
    coEvery { authRepository.verifyPassword("testPassword") } returns Result.success(Unit)

    // Mock successful account deletion
    coEvery { authRepository.deleteAccount(any(), any(), any()) } answers
        {
          val onSuccess = secondArg<() -> Unit>()
          onSuccess()
        }
    coEvery { profileRepository.deleteProfile("testUserId") } returns true
    coEvery { mapUsersRepository.deleteMapUser() } returns true
    coEvery { playlistRepository.deleteOwnedPlaylists(any(), any()) } answers
        {
          val onSuccess = firstArg<() -> Unit>()
          onSuccess() // Call the success callback
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

    // Verify sequence of deletions and account removal
    coVerify { authRepository.verifyPassword("testPassword") }
    coVerify { profileRepository.deleteProfile(eq("testUserId")) }
    coVerify { mapUsersRepository.deleteMapUser() }
    coVerify { playlistRepository.deleteOwnedPlaylists(any(), any()) }
    coVerify { authRepository.deleteAccount(eq("testPassword"), any(), any()) }

    // Verify navigation to the WELCOME screen
    verify { navigationActions.navigateToAndClearAllBackStack(Screen.WELCOME) }
  }

  @Test
  fun deleteAccountDialog_cancelDoesNotPerformAnyDeletion() = runTest {
    // Perform click on the delete button
    composeTestRule
        .onNodeWithTag("deleteAccountButton", useUnmergedTree = true)
        .performScrollTo()
        .performClick()

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("passwordField").assertIsDisplayed()

    // Click cancel
    composeTestRule.onNodeWithTag("cancelButton").performClick()

    // Verify no repository actions were triggered
    coVerify(exactly = 0) { authRepository.verifyPassword(any()) }
    coVerify(exactly = 0) { authRepository.deleteAccount(any(), any(), any()) }

    // Verify no navigation occurred
    verify(exactly = 0) { navigationActions.navigateToAndClearAllBackStack(Screen.WELCOME) }
  }

  @Test
  fun deleteAccountDialog_handlesEmptyPasswordWithToast() = runTest {
    // Mock Toast
    mockkStatic(Toast::class)
    val mockToast = mockk<Toast>(relaxed = true)
    every { Toast.makeText(any(), any<String>(), any()) } returns mockToast

    // Perform click on the delete button
    composeTestRule
        .onNodeWithTag("deleteAccountButton", useUnmergedTree = true)
        .performScrollTo()
        .performClick()

    composeTestRule.waitForIdle()

    // Ensure dialog is displayed
    composeTestRule.onNodeWithTag("passwordField").assertIsDisplayed()

    // Click confirm without entering a password
    composeTestRule.onNodeWithTag("confirmButton").performClick()

    // Verify Toast is displayed with appropriate message
    verify { Toast.makeText(any(), "Please enter your password", Toast.LENGTH_SHORT) }
    verify { mockToast.show() }

    // Ensure no repository actions were triggered
    coVerify(exactly = 0) { authRepository.verifyPassword(any()) }
    coVerify(exactly = 0) { authRepository.deleteAccount(any(), any(), any()) }

    // Verify no navigation occurred
    verify(exactly = 0) { navigationActions.navigateToAndClearAllBackStack(Screen.WELCOME) }
  }

  @Test
  fun deleteAccountDialog_handlesDeleteProfileFailure() =
      runTest() {
        // Mock Toast
        mockkStatic(Toast::class)
        val mockToast = mockk<Toast>(relaxed = true)
        every { Toast.makeText(any(), any<String>(), any()) } returns mockToast

        // Mock password verification success
        coEvery { authRepository.verifyPassword("testPassword") } returns Result.success(Unit)

        // Mock `deleteProfile` failure
        coEvery { profileRepository.deleteProfile("testUserId") } returns false

        // Mock no other operations should proceed
        coEvery { mapUsersRepository.deleteMapUser() } returns false
        coEvery { playlistRepository.deleteOwnedPlaylists(any(), any()) } answers
            {
              val onFailure = secondArg<() -> Unit>()
              onFailure()
            }
        coEvery { authRepository.deleteAccount(any(), any(), any()) } answers {}

        // Perform click on the delete button
        composeTestRule
            .onNodeWithTag("deleteAccountButton", useUnmergedTree = true)
            .performScrollTo()
            .performClick()

        composeTestRule.waitForIdle()

        // Ensure dialog is displayed
        composeTestRule.onNodeWithTag("passwordField").assertIsDisplayed()

        // Enter correct password
        composeTestRule.onNodeWithTag("passwordField").performTextInput("testPassword")

        // Click confirm
        composeTestRule.onNodeWithTag("confirmButton").performClick()

        // Wait for coroutines to complete
        composeTestRule.waitForIdle()

        // Verify Toast is displayed with the correct error message
        verify {
          Toast.makeText(any(), "Failed to delete all associated data.", Toast.LENGTH_SHORT)
        }
        verify { mockToast.show() }

        // Verify that deleteProfile was called and returned failure
        coVerify(exactly = 1) { profileRepository.deleteProfile("testUserId") }

        // Verify no further operations are triggered
        coVerify(exactly = 0) { authRepository.deleteAccount(any(), any(), any()) }

        // Verify no navigation occurred
        verify(exactly = 0) { navigationActions.navigateToAndClearAllBackStack(Screen.WELCOME) }
      }
}
