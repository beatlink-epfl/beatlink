package com.epfl.beatlink.ui.library

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.epfl.beatlink.model.library.Playlist
import com.epfl.beatlink.model.library.PlaylistRepository
import com.epfl.beatlink.model.spotify.objects.SpotifyTrack
import com.epfl.beatlink.model.spotify.objects.State
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.ui.navigation.TopLevelDestinations
import com.epfl.beatlink.ui.profile.FakeSpotifyApiViewModel
import com.epfl.beatlink.viewmodel.library.PlaylistViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any

class SearchTracksScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions
  private lateinit var fakeSpotifyApiViewModel: FakeSpotifyApiViewModel
  private lateinit var playlistRepository: PlaylistRepository
  private lateinit var playlistViewModel: PlaylistViewModel

  private val topSongs =
      listOf(
          SpotifyTrack(
              name = "Track 1",
              artist = "Artist A",
              trackId = "1",
              cover = "",
              duration = 4,
              popularity = 1,
              state = State.PAUSE),
          SpotifyTrack(
              name = "Track 2",
              artist = "Artist B",
              trackId = "2",
              cover = "",
              duration = 3,
              popularity = 2,
              state = State.PAUSE))

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    `when`(navigationActions.currentRoute()).thenReturn(Screen.ADD_TRACK_TO_PLAYLIST)

    fakeSpotifyApiViewModel = FakeSpotifyApiViewModel()
    fakeSpotifyApiViewModel.setTopTracks(topSongs)

    playlistRepository = mock(PlaylistRepository::class.java)
    playlistViewModel = PlaylistViewModel(playlistRepository)

    composeTestRule.setContent {
      SearchTracksScreen(
          navigationActions = navigationActions,
          spotifyApiViewModel = fakeSpotifyApiViewModel,
          playlistViewModel = playlistViewModel)
    }
  }

  @Test
  fun everythingIsDisplayed() {
    composeTestRule.onNodeWithTag("searchScaffold").assertIsDisplayed()
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()
    composeTestRule.onNodeWithTag("shortSearchBarRow").assertIsDisplayed()
    composeTestRule.onNodeWithTag("goBackButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("writableSearchBar").assertIsDisplayed()
  }

  @Test
  fun searchResultsDisplayTracks() {
    // Perform a search
    composeTestRule.onNodeWithTag("writableSearchBar").performTextInput("Track")

    // Ensure all tracks are displayed
    topSongs.forEach { track ->
      // Check each track's name and artist are displayed
      composeTestRule.onNodeWithText(track.name).assertExists()
      composeTestRule.onNodeWithText(track.artist).assertExists()

      // Verify that each track row (with unique testTag) is displayed
      composeTestRule.onNodeWithTag("trackItem-${track.trackId}").assertExists()
    }
  }

  @Test
  fun addTrackToPlaylistSuccess() {
    // Select a playlist
    val testPlaylist =
        Playlist(
            playlistID = "testPlaylistId",
            playlistName = "Test Playlist",
            playlistDescription = "A test playlist",
            playlistCover = "",
            playlistPublic = false,
            userId = "userId",
            playlistOwner = "owner",
            playlistCollaborators = emptyList(),
            playlistTracks = emptyList(),
            nbTracks = 0)

    `when`(playlistRepository.getOwnedPlaylists(any(), any())).thenAnswer {
      (it.arguments[0] as (List<Playlist>) -> Unit).invoke(listOf(testPlaylist))
    }

    playlistViewModel.selectPlaylist(testPlaylist)

    // Perform a search
    composeTestRule.onNodeWithTag("writableSearchBar").performTextInput("Track 1")

    // Simulate the `updatePlaylist` call
    `when`(playlistRepository.updatePlaylist(any(), any(), any())).thenAnswer {
      (it.arguments[1] as () -> Unit).invoke()
    }

    // Click the track item
    composeTestRule.onNodeWithTag("trackItem-1").performClick()

    // Verify that updatePlaylist was called
    verify(playlistRepository).updatePlaylist(any(), any(), any())
  }

  @Test
  fun testBackNavigation() {
    composeTestRule.onNodeWithTag("goBackButton").performClick()
    verify(navigationActions).goBack()
  }

  @Test
  fun testNavigationToHome() {
    composeTestRule.onNodeWithTag("Home").performClick()
    org.mockito.kotlin.verify(navigationActions).navigateTo(destination = TopLevelDestinations.HOME)
  }

  @Test
  fun testNavigationToSearch() {
    composeTestRule.onNodeWithTag("Search").performClick()
    org.mockito.kotlin
        .verify(navigationActions)
        .navigateTo(destination = TopLevelDestinations.SEARCH)
  }

  @Test
  fun testNavigationToLibrary() {
    composeTestRule.onNodeWithTag("Library").performClick()
    org.mockito.kotlin
        .verify(navigationActions)
        .navigateTo(destination = TopLevelDestinations.LIBRARY)
  }

  @Test
  fun testNavigationToProfile() {
    composeTestRule.onNodeWithTag("Profile").performClick()
    org.mockito.kotlin
        .verify(navigationActions)
        .navigateTo(destination = TopLevelDestinations.PROFILE)
  }
}
