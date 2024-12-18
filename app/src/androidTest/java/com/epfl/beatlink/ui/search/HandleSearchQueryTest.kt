package com.epfl.beatlink.ui.search

import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.createComposeRule
import com.epfl.beatlink.ui.components.search.HandleSearchQuery
import com.epfl.beatlink.viewmodel.spotify.api.SpotifyApiViewModel
import com.epfl.beatlink.viewmodel.spotify.auth.AuthState
import com.epfl.beatlink.viewmodel.spotify.auth.SpotifyAuthViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.Rule
import org.junit.Test

class HandleSearchQueryTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun whenAuthStateIsIdleShowToastAndCallOnFailure() {
    // Mock dependencies
    val spotifyApiViewModel = mockk<SpotifyApiViewModel>(relaxed = true)
    val spotifyAuthViewModel = mockk<SpotifyAuthViewModel>(relaxed = true)
    val onFailure = mockk<() -> Unit>(relaxed = true)

    // Mock Toast
    mockkStatic(Toast::class)
    val mockToast = mockk<Toast>(relaxed = true)
    every { Toast.makeText(any(), any<String>(), any()) } returns mockToast

    // Set authState to Idle
    val authState = mutableStateOf<AuthState>(AuthState.Idle)
    every { spotifyAuthViewModel.authState } returns authState

    // Launch the composable
    composeTestRule.setContent {
      HandleSearchQuery(
          query = "testQuery",
          onResults = { _, _ -> },
          onFailure = onFailure,
          spotifyApiViewModel = spotifyApiViewModel,
          spotifyAuthViewModel = spotifyAuthViewModel)
    }

    // Verify that onFailure() is called
    verify { onFailure.invoke() }

    // Verify Toast is displayed with appropriate message
    verify {
      Toast.makeText(
          any(),
          "For spotify searches, please connect your Spotify account to the app.",
          Toast.LENGTH_SHORT)
    }
    verify { mockToast.show() }
  }
}
