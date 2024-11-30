package com.epfl.beatlink.ui.library

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.epfl.beatlink.model.library.Playlist
import com.epfl.beatlink.model.library.PlaylistRepository
import com.epfl.beatlink.model.library.PlaylistTrack
import com.epfl.beatlink.model.spotify.objects.SpotifyTrack
import com.epfl.beatlink.model.spotify.objects.State
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.ui.navigation.TopLevelDestinations
import com.epfl.beatlink.viewmodel.library.PlaylistViewModel
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.verify

class PlaylistOverviewScreenTest {

  private lateinit var playlistRepository: PlaylistRepository
  private lateinit var playlistViewModel: PlaylistViewModel
  private lateinit var navigationActions: NavigationActions

  private val sampleTrack =
      PlaylistTrack(
          track =
              SpotifyTrack(
                  name = "Test Track",
                  artist = "Test Artist",
                  trackId = "1",
                  cover = "",
                  duration = 200,
                  popularity = 50,
                  state = State.PAUSE,
              ),
          likes = 5,
          likedBy = mutableListOf("collab1") // Simulate an existing like by "collab1"
          )

  private val playlistWithTracks =
      Playlist(
          playlistID = "1",
          playlistCover = "",
          playlistName = "Playlist with Tracks",
          playlistDescription = "Test Playlist Description",
          playlistPublic = true,
          userId = "testUserId",
          playlistOwner = "testOwner",
          playlistCollaborators = listOf("collab1"),
          playlistTracks = listOf(sampleTrack),
          nbTracks = 1)

  private val emptyPlaylist =
      Playlist(
          playlistID = "2",
          playlistCover = "",
          playlistName = "Empty Playlist",
          playlistDescription = "No tracks here",
          playlistPublic = true,
          userId = "testUserId",
          playlistOwner = "testOwner",
          playlistCollaborators = listOf(),
          playlistTracks = emptyList(),
          nbTracks = 0)

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    playlistRepository = mock(PlaylistRepository::class.java)
    playlistViewModel = PlaylistViewModel(playlistRepository)
    navigationActions = mock(NavigationActions::class.java)
    `when`(navigationActions.currentRoute()).thenReturn(Screen.PLAYLIST_OVERVIEW)
  }

  @Test
  fun playlistOverviewScreen_displaysPlaylistDetails() {
    playlistViewModel.selectPlaylist(playlistWithTracks)

    composeTestRule.setContent {
      PlaylistOverviewScreen(
          navigationActions = navigationActions,
          profileViewModel = mock(ProfileViewModel::class.java),
          playlistViewModel = playlistViewModel)
    }

    // Check playlist details are displayed
    composeTestRule
        .onNodeWithTag("playlistTitle")
        .assertTextContains(playlistWithTracks.playlistName)
    composeTestRule
        .onNodeWithTag("ownerText")
        .assertTextContains("@" + playlistWithTracks.playlistOwner)
    composeTestRule.onNodeWithTag("publicText").assertTextContains("Public")
  }

  @Test
  fun playlistOverviewScreen_displaysTracksWhenNotEmpty() {
    playlistViewModel.selectPlaylist(playlistWithTracks)

    composeTestRule.setContent {
      PlaylistOverviewScreen(
          navigationActions = navigationActions,
          profileViewModel = mock(ProfileViewModel::class.java),
          playlistViewModel = playlistViewModel)
    }

    // Check track is displayed in TrackVoteCard
    composeTestRule.onNodeWithTag("trackVoteCard").performScrollTo().assertIsDisplayed()
  }

  @Test
  fun playlistOverviewScreen_displaysEmptyPromptWhenNoTracks() {
    playlistViewModel.selectPlaylist(emptyPlaylist)

    composeTestRule.setContent {
      PlaylistOverviewScreen(
          navigationActions = navigationActions,
          profileViewModel = mock(ProfileViewModel::class.java),
          playlistViewModel = playlistViewModel)
    }

    // Check empty playlist prompt is displayed
    composeTestRule.onNodeWithTag("emptyPlaylistPrompt").assertIsDisplayed()
  }

  @Test
  fun voteButton_updatesTrackLikes() {
    playlistViewModel.selectPlaylist(playlistWithTracks)

    composeTestRule.setContent {
      PlaylistOverviewScreen(
          navigationActions = navigationActions,
          profileViewModel = mock(ProfileViewModel::class.java),
          playlistViewModel = playlistViewModel)
    }

    // Perform click on the vote button
    composeTestRule.onNodeWithTag("voteButton").performClick()

    // Verify that updateTrackLikes is called with the correct parameters
    verify(playlistRepository).updatePlaylist(any(), any(), any())
  }

  @Test
  fun view_description_button_opens_overlay() {
    playlistViewModel.selectPlaylist(playlistWithTracks)
    composeTestRule.setContent {
      PlaylistOverviewScreen(
          navigationActions = navigationActions,
          profileViewModel = mock(ProfileViewModel::class.java),
          playlistViewModel = playlistViewModel)
    }
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("overlay").assertDoesNotExist()
    // Perform click on the "Invite Collaborators" button
    composeTestRule.onNodeWithTag("viewDescriptionButton").performScrollTo().performClick()
    // Verify the overlay is visible after the click
    composeTestRule.onNodeWithTag("overlay").assertIsDisplayed()
  }

  @Test
  fun navigationButtons_workCorrectly() {
    playlistViewModel.selectPlaylist(playlistWithTracks)

    composeTestRule.setContent {
      PlaylistOverviewScreen(
          navigationActions = navigationActions,
          profileViewModel = mock(ProfileViewModel::class.java),
          playlistViewModel = playlistViewModel)
    }

    // Navigate to Home
    composeTestRule.onNodeWithTag("Home").performClick()
    verify(navigationActions).navigateTo(TopLevelDestinations.HOME)
    // Navigate to Search
    composeTestRule.onNodeWithTag("Search").performClick()
    verify(navigationActions).navigateTo(TopLevelDestinations.SEARCH)
    // Navigate to Library
    composeTestRule.onNodeWithTag("Library").performClick()
    verify(navigationActions).navigateTo(TopLevelDestinations.LIBRARY)
    // Navigate to Profile
    composeTestRule.onNodeWithTag("Profile").performClick()
    verify(navigationActions).navigateTo(TopLevelDestinations.PROFILE)
  }
}
