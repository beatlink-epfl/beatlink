package com.epfl.beatlink.ui.profile.settings

import android.app.Application
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.epfl.beatlink.model.auth.FirebaseAuthRepository
import com.epfl.beatlink.model.profile.ProfileData
import com.epfl.beatlink.repository.spotify.auth.SpotifyAuthRepository
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.viewmodel.auth.FirebaseAuthViewModel
import com.epfl.beatlink.viewmodel.library.PlaylistViewModel
import com.epfl.beatlink.viewmodel.map.user.MapUsersViewModel
import com.epfl.beatlink.viewmodel.profile.FriendRequestViewModel
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel
import com.epfl.beatlink.viewmodel.spotify.auth.SpotifyAuthViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import okhttp3.OkHttpClient
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
class SettingsScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions
  private lateinit var authRepository: FirebaseAuthRepository
  private lateinit var authViewModel: FirebaseAuthViewModel
  private lateinit var profileViewModel: ProfileViewModel
  private lateinit var spotifyRepository: SpotifyAuthRepository
  private lateinit var spotifyAuthViewModel: SpotifyAuthViewModel
  private lateinit var playlistViewModel: PlaylistViewModel

  private val testEmail = "user@example.com"
  private val testUsername = "testuser"

  @Before
  fun setUp() {
    navigationActions = mockk(relaxed = true)
    every { navigationActions.currentRoute() } returns Screen.SETTINGS

    val application = ApplicationProvider.getApplicationContext<Application>()
    spotifyRepository = SpotifyAuthRepository(client = OkHttpClient()) // or any required client
    spotifyAuthViewModel = SpotifyAuthViewModel(application, spotifyRepository)

    authRepository = mock(FirebaseAuthRepository::class.java)
    authViewModel = FirebaseAuthViewModel(authRepository)

    profileViewModel = mockk(relaxed = true)

    every { profileViewModel.profile } returns
        MutableStateFlow(
            ProfileData(
                username = testUsername,
                email = testEmail,
                bio = "",
                links = 0,
                name = "",
                profilePicture = null,
                favoriteMusicGenres = emptyList()))

    playlistViewModel = mockk(relaxed = true)
  }

  @Test
  fun settingsScreen_rendersCorrectly() {
    composeTestRule.setContent {
      SettingsScreen(
          navigationActions = navigationActions,
          firebaseAuthViewModel = authViewModel,
          mapUsersViewModel = viewModel(factory = MapUsersViewModel.Factory),
          profileViewModel = profileViewModel,
          spotifyAuthViewModel = spotifyAuthViewModel,
          playlistViewModel = playlistViewModel,
          friendRequestViewModel = viewModel(factory = FriendRequestViewModel.Factory))
    }

    // Check if the title is displayed
    composeTestRule.onNodeWithTag("settingScreenTitle").assertIsDisplayed()

    // Check if buttons are displayed
    composeTestRule.onNodeWithTag("settingScreenContent").assertIsDisplayed()
    // Check if email and username are displayed
    composeTestRule.onNodeWithText("E-mail: $testEmail").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithText("Username: $testUsername").performScrollTo().assertIsDisplayed()

    composeTestRule.onNodeWithTag("signOutButton").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("deleteAccountButton").performScrollTo().assertIsDisplayed()
  }

  @Test
  fun settingsScreen_buttonsNavigateCorrectly() {
    composeTestRule.setContent {
      SettingsScreen(
          navigationActions = navigationActions,
          firebaseAuthViewModel = authViewModel,
          mapUsersViewModel = viewModel(factory = MapUsersViewModel.Factory),
          profileViewModel = profileViewModel,
          spotifyAuthViewModel = spotifyAuthViewModel,
          playlistViewModel = playlistViewModel,
          friendRequestViewModel = viewModel(factory = FriendRequestViewModel.Factory))
    }

    // Test "Username" clickable text box
    composeTestRule.onNodeWithText("Username: $testUsername").performScrollTo().performClick()
    verify { navigationActions.navigateTo(Screen.CHANGE_USERNAME) }

    // Test "Change password" clickable text box
    composeTestRule.onNodeWithText("Change password").performScrollTo().performClick()
    verify { navigationActions.navigateTo(Screen.CHANGE_PASSWORD) }
  }
}
