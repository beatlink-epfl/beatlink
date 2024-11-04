package com.epfl.beatlink.ui.player

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.epfl.beatlink.model.spotify.objects.SpotifyAlbum
import com.epfl.beatlink.model.spotify.objects.SpotifyTrack
import com.epfl.beatlink.model.spotify.objects.State
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Route
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@RunWith(AndroidJUnit4::class)
class PlayScreenTest {
  private lateinit var navigationActions: NavigationActions
  private val track = SpotifyTrack("track", "trackId", "cover", 100, 100, State.PLAY)
  private val album =
      SpotifyAlbum(
          "spotifyId",
          "name",
          "cover",
          "artist",
          2024,
          listOf(track),
          10,
          listOf("genre1", "genre2"),
          100)

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    `when`(navigationActions.currentRoute()).thenReturn(Route.PROFILE)
    // Launch the composable under test
    composeTestRule.setContent { PlayScreen(navigationActions, track, album) }
  }

  @Test
  fun testPlayScreenDisplay() {
    composeTestRule.onNodeWithTag("topAppBar").assertIsDisplayed()

    composeTestRule
        .onNodeWithTag("topBarTitle")
        .assertIsDisplayed()
        .assertTextContains("Now Playing")

    composeTestRule.onNodeWithTag("backButton").assertIsDisplayed().assertHasClickAction()

    composeTestRule.onNodeWithTag("playScreenContent").assertIsDisplayed()
  }

  @Test
  fun testPlayScreenButton() {
    // Test the PlayScreen button
  }
}
