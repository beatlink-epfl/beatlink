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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.epfl.beatlink.model.auth.FirebaseAuthRepository
import com.epfl.beatlink.model.map.user.MapUserRepository
import com.epfl.beatlink.model.profile.ProfileRepository
import com.epfl.beatlink.repository.spotify.auth.SpotifyAuthRepository
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.viewmodel.auth.FirebaseAuthViewModel
import com.epfl.beatlink.viewmodel.library.PlaylistViewModel
import com.epfl.beatlink.viewmodel.map.user.MapUsersViewModel
import com.epfl.beatlink.viewmodel.profile.FriendRequestViewModel
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel
import com.epfl.beatlink.viewmodel.spotify.auth.SpotifyAuthViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class SignOutButtonTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions
  private lateinit var authViewModel: FirebaseAuthViewModel
  private lateinit var authRepository: FirebaseAuthRepository
  private lateinit var profileRepository: ProfileRepository
  private lateinit var profileViewModel: ProfileViewModel
  private lateinit var mapUsersViewModel: MapUsersViewModel
  private lateinit var mapUsersRepository: MapUserRepository
  private lateinit var spotifyRepository: SpotifyAuthRepository
  private lateinit var spotifyAuthViewModel: SpotifyAuthViewModel
  private lateinit var playlistViewModel: PlaylistViewModel

  @Before
  fun setUp() {
    navigationActions = mockk(relaxed = true)

    val application = ApplicationProvider.getApplicationContext<Application>()
    spotifyRepository = SpotifyAuthRepository(client = OkHttpClient()) // or any required client
    spotifyAuthViewModel = SpotifyAuthViewModel(application, spotifyRepository)
    authRepository = mock(FirebaseAuthRepository::class.java)
    authViewModel = FirebaseAuthViewModel(authRepository)
    profileRepository = mock(ProfileRepository::class.java)
    profileViewModel = ProfileViewModel(profileRepository)
    mapUsersRepository = mockk(relaxed = true)
    mapUsersViewModel = MapUsersViewModel(mapUsersRepository)
    playlistViewModel = mockk(relaxed = true)

    // Set the composable for testing
    composeTestRule.setContent {
      SettingsScreen(
          navigationActions = navigationActions,
          firebaseAuthViewModel = authViewModel,
          mapUsersViewModel = mapUsersViewModel,
          profileViewModel = profileViewModel,
          spotifyAuthViewModel = spotifyAuthViewModel,
          playlistViewModel = playlistViewModel,
          friendRequestViewModel = viewModel(factory = FriendRequestViewModel.Factory))
    }
  }

  @Test
  fun signOutButton_isDisplayedCorrectly() {
    composeTestRule.waitForIdle()
    composeTestRule
        .onNodeWithTag("signOutButton", useUnmergedTree = true)
        .performScrollTo()
        .assertIsDisplayed()
        .assertHasClickAction()
        .onChild()
        .assertTextEquals("Sign out")
  }

  @Test
  fun signOutDialog_isDisplayedWhenSignOutButtonClicked() {
    // Click the "Sign out" button
    composeTestRule
        .onNodeWithTag("signOutButton", useUnmergedTree = true)
        .performScrollTo()
        .performClick()

    composeTestRule.waitForIdle()

    // Verify the dialog title and confirm/cancel buttons are displayed
    composeTestRule.onNodeWithTag("confirmButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("cancelButton").assertIsDisplayed()
  }

  @Test
  fun signOutDialog_performsSignOutAndNavigation() = runTest {
    coEvery { mapUsersRepository.deleteMapUser() } returns true
    // Mock the signOut method to simulate a successful callback
    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<() -> Unit>(0)
          onSuccess.invoke()
          null
        }
        .whenever(authRepository)
        .signOut(any(), any())

    // Click the "Sign out" button to show the dialog
    composeTestRule
        .onNodeWithTag("signOutButton", useUnmergedTree = true)
        .performScrollTo()
        .performClick()

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("confirmButton").assertIsDisplayed()

    // Click the confirm button
    composeTestRule.onNodeWithTag("confirmButton").performClick()

    composeTestRule.waitForIdle()
    // Verify that signOut is called on the repository
    verify(authRepository).signOut(any(), any())

    // Verify navigation to the welcome screen
    io.mockk.verify { navigationActions.navigateToAndClearAllBackStack(Screen.WELCOME) }
  }

  @Test
  fun signOutDialog_dismissesOnCancel() {
    // Click the "Sign out" button to show the dialog
    composeTestRule
        .onNodeWithTag("signOutButton", useUnmergedTree = true)
        .performScrollTo()
        .performClick()

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("confirmButton").assertIsDisplayed()

    // Click the cancel button
    composeTestRule.onNodeWithTag("cancelButton").performClick()

    // Verify that the dialog is dismissed
    composeTestRule.onNodeWithTag("confirmButton", useUnmergedTree = true).assertDoesNotExist()
  }
}
