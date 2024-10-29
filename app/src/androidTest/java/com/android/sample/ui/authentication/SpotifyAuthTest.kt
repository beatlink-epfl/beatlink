package com.android.sample.ui.authentication

import android.app.Application
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.model.spotify.SpotifyAuthRepository
import com.android.sample.ui.theme.SampleAppTheme
import okhttp3.OkHttpClient
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SpotifyAuthTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var viewModel: SpotifyAuthViewModel

  private lateinit var repository: SpotifyAuthRepository

  @Before
  fun setUp() {
    val application = ApplicationProvider.getApplicationContext<Application>()
    // Create a real instance of SpotifyAuthRepository with OkHttpClient, etc.
    repository = SpotifyAuthRepository(client = OkHttpClient()) // or any required client
    viewModel = SpotifyAuthViewModel(application, repository)
  }

  @Test
  fun spotifyAuth_showsCorrectUi() {
    composeTestRule.setContent { SampleAppTheme { SpotifyAuth(spotifyViewModel = viewModel) } }

    composeTestRule.onNodeWithTag("SpotifyAuthCard").assertIsDisplayed()
    composeTestRule.onNodeWithTag("SpotifyLogo").assertIsDisplayed()

    composeTestRule.onNodeWithTag("AuthStateText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("AuthStateText").assertTextEquals("Link your Spotify account")

    composeTestRule.onNodeWithTag("AuthActionButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("AuthActionButton").assertTextEquals("Link")
  }
}
