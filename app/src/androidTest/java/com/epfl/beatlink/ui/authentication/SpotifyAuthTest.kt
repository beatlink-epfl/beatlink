package com.epfl.beatlink.ui.authentication

import android.app.Application
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.epfl.beatlink.model.spotify.auth.SpotifyAuthRepository
import com.epfl.beatlink.ui.theme.BeatLinkAppTheme
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
    composeTestRule.setContent { BeatLinkAppTheme { SpotifyAuth(spotifyViewModel = viewModel) } }

    composeTestRule.waitUntil(timeoutMillis = 5000) {
      composeTestRule.onAllNodesWithTag("SpotifyAuthCard").fetchSemanticsNodes().isNotEmpty()
    }

    composeTestRule.onNodeWithTag("SpotifyAuthCard").assertIsDisplayed()
  }
}
