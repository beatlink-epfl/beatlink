package com.epfl.beatlink.ui.library

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.epfl.beatlink.model.library.PlaylistRepository
import com.epfl.beatlink.model.spotify.objects.SpotifyTrack
import com.epfl.beatlink.model.spotify.objects.State
import com.epfl.beatlink.viewmodel.library.PlaylistViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

class SearchTracksScreenTest {

  private lateinit var playlistRepository: PlaylistRepository
  private lateinit var playlistViewModel: PlaylistViewModel

  private val sampleTrack =
      SpotifyTrack(
          name = "Test Track",
          artist = "Test Artist",
          trackId = "1",
          cover = "",
          duration = 200,
          popularity = 50,
          state = State.PAUSE)

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    playlistRepository = mock(PlaylistRepository::class.java)
    playlistViewModel = PlaylistViewModel(playlistRepository)
  }

  @Test
  fun searchTracksScreen_displaysNoResultsMessage_whenNoTracksAvailable() {
    composeTestRule.setContent {
      SearchTracksLazyColumn(
          tracks = emptyList(), playlistViewModel = playlistViewModel, onClearQuery = {})
    }

    // Verify the "no results" message is displayed
    composeTestRule.onNodeWithText("Search for a song to add and click on it").assertIsDisplayed()
  }

  @Test
  fun searchTracksScreen_displaysTracksInLazyColumn() {
    composeTestRule.setContent {
      SearchTracksLazyColumn(
          tracks = listOf(sampleTrack), playlistViewModel = playlistViewModel, onClearQuery = {})
    }

    // Verify the track name and artist are displayed
    composeTestRule.onNodeWithText(sampleTrack.name).assertIsDisplayed()
    composeTestRule.onNodeWithText(sampleTrack.artist).assertIsDisplayed()
    // Perform click on the track item
    composeTestRule.onNodeWithTag("trackItem").performClick()
  }
}
