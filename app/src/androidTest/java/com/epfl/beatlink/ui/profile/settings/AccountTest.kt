package com.epfl.beatlink.ui.profile.settings

import android.app.Application
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.epfl.beatlink.model.profile.ProfileData
import com.epfl.beatlink.repository.spotify.auth.SpotifyAuthRepository
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
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

@RunWith(AndroidJUnit4::class)
class AccountScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions
  private lateinit var spotifyAuthViewModel: SpotifyAuthViewModel
  private lateinit var profileViewModel: ProfileViewModel
  private lateinit var spotifyRepository: SpotifyAuthRepository

  private val testEmail = "user@example.com"
  private val testUsername = "testuser"

  @Before
  fun setUp() {
    navigationActions = mockk(relaxed = true)

    val application = ApplicationProvider.getApplicationContext<Application>()
    spotifyRepository = SpotifyAuthRepository(client = OkHttpClient()) // or any required client
    spotifyAuthViewModel = SpotifyAuthViewModel(application, spotifyRepository)
    profileViewModel = mockk(relaxed = true)

    every { navigationActions.currentRoute() } returns Screen.ACCOUNT
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
  }

  @Test
  fun accountScreen_rendersCorrectly() {
    composeTestRule.setContent {
      AccountScreen(
          navigationActions = navigationActions,
          spotifyAuthViewModel = spotifyAuthViewModel,
          editProfileViewModel = profileViewModel)
    }

    // Check if title is displayed
    composeTestRule.onNodeWithTag("accountScreenTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("accountScreenContent").assertIsDisplayed()

    // Check if email and username are displayed
    composeTestRule.onNodeWithText("E-mail: $testEmail").assertIsDisplayed()
    composeTestRule.onNodeWithText("Username: $testUsername").assertIsDisplayed()
  }

  @Test
  fun accountScreen_buttonsAndTextBoxesNavigateCorrectly() {
    composeTestRule.setContent {
      AccountScreen(
          navigationActions = navigationActions,
          spotifyAuthViewModel = spotifyAuthViewModel,
          editProfileViewModel = profileViewModel)
    }

    // Test "Username" clickable text box
    composeTestRule.onNodeWithText("Username: $testUsername").performClick()
    verify { navigationActions.navigateTo(Screen.CHANGE_USERNAME) }

    // Test "Change password" clickable text box
    composeTestRule.onNodeWithText("Change password").performClick()
    verify { navigationActions.navigateTo(Screen.CHANGE_PASSWORD) }
  }
}
