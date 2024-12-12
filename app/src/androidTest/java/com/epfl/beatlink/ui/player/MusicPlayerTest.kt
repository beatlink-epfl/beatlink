package com.epfl.beatlink.ui.player

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.epfl.beatlink.model.spotify.objects.SpotifyAlbum
import com.epfl.beatlink.model.spotify.objects.SpotifyArtist
import com.epfl.beatlink.model.spotify.objects.SpotifyTrack
import com.epfl.beatlink.ui.components.MusicPlayerUI
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.profile.FakeSpotifyApiViewModel
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

class MusicPlayerTest {

  @get:Rule val composeTestRule = createComposeRule()

  private val spotifyTrack =
      SpotifyTrack(name = "This is the song", artist = "Jack Jones", trackId = "1")
  private val spotifyArtist = SpotifyArtist(name = "Jack Jones")
  private val spotifyAlbum =
      SpotifyAlbum(
          spotifyId = "1",
          name = "album name",
          cover = "",
          artist = "Jack Jones",
          year = 2002,
          tracks = emptyList(),
          size = 0,
          genres = emptyList(),
          popularity = 1)

  @Test
  fun testMusicPlayerUI_DisplaysTrackInfoAndControls() {
    val mockSpotifyApiViewModel = FakeSpotifyApiViewModel()
    val mockNavigationActions = mock(NavigationActions::class.java)

    mockSpotifyApiViewModel.setPlaybackState(true)
    mockSpotifyApiViewModel.setTrack(spotifyTrack)
    mockSpotifyApiViewModel.setAlbum(spotifyAlbum)
    mockSpotifyApiViewModel.setArtist(spotifyArtist)

    // Compose UI
    composeTestRule.setContent {
      MusicPlayerUI(
          navigationActions = mockNavigationActions,
          spotifyApiViewModel = mockSpotifyApiViewModel,
          mapUsersViewModel = mockk(relaxed = true))
    }

    // Verify UI elements
    composeTestRule.onNodeWithTag("playerContainer").assertExists()
    composeTestRule.onNodeWithText("This is the song").assertExists()
    composeTestRule.onNodeWithText("Jack Jones - album name").assertExists()

    composeTestRule.onNodeWithTag("playPauseButton").assertExists()
    composeTestRule.onNodeWithTag("playPauseButton").performClick()
    composeTestRule.onNodeWithTag("skipButton").assertExists()
    composeTestRule.onNodeWithTag("skipButton").performClick()

    composeTestRule.onNodeWithTag("playerContainer").performClick()
  }
}
