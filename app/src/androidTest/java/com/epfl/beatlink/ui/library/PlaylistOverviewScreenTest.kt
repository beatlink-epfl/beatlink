package com.epfl.beatlink.ui.library

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.epfl.beatlink.model.library.Playlist
import com.epfl.beatlink.model.library.PlaylistRepository
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Route
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.ui.navigation.TopLevelDestinations
import com.epfl.beatlink.viewmodel.library.PlaylistViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.verify

class PlaylistOverviewScreenTest {
  private lateinit var playlistRepository: PlaylistRepository
  private lateinit var playlistViewModel: PlaylistViewModel
  private lateinit var navigationActions: NavigationActions

  private val playlist =
      Playlist(
          playlistID = "1",
          playlistCover = "",
          playlistName = "playlist 1",
          playlistDescription = "testing",
          playlistPublic = false,
          userId = "",
          playlistOwner = "luna",
          playlistCollaborators = listOf("collab1"),
          playlistTracks = emptyList(),
          nbTracks = 0)

  private val playlist2 =
      Playlist(
          playlistID = "1",
          playlistCover = "",
          playlistName = "playlist 1",
          playlistDescription = "testing",
          playlistPublic = false,
          userId = "",
          playlistOwner = "luna",
          playlistCollaborators = listOf("collab1"),
          playlistTracks = emptyList(),
          nbTracks = 10)

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    playlistRepository = mock(PlaylistRepository::class.java)
    playlistViewModel = PlaylistViewModel(playlistRepository)
    navigationActions = mock(NavigationActions::class.java)
    `when`(navigationActions.currentRoute()).thenReturn(Route.LIBRARY)
    `when`(navigationActions.currentRoute()).thenReturn(Screen.PLAYLIST_OVERVIEW)

    playlistViewModel.selectPlaylist(playlist2)
    composeTestRule.setContent { PlaylistOverviewScreen(navigationActions, playlistViewModel) }
  }

  @Test
  fun everythingIsDisplayed() {
    composeTestRule.onNodeWithTag("playlistOverviewScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("playlistName").assertIsDisplayed()
    composeTestRule.onNodeWithTag("goBackButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()
    composeTestRule.onNodeWithTag("editButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("playlistCoverCard").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("playlistTitle").performScrollTo().assertIsDisplayed()
    /*
    composeTestRule.onNodeWithTag("ownerText").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("collaboratorsText").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("ownerText").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("publicText").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("viewDescriptionButton").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("addToThisPlaylistButton").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("exportButton").performScrollTo().assertIsDisplayed()

       */
  }

  @Test
  fun TrackCardDisplaysWhenNotEmpty() {
    playlistViewModel.selectPlaylist(playlist2)
    composeTestRule.onNodeWithTag("trackVoteCard").performScrollTo().assertIsDisplayed()
  }

  @Test
  fun TextDisplaysWhenEmpty() {
    playlistViewModel.selectPlaylist(playlist)
    composeTestRule.onNodeWithTag("emptyPlaylistPrompt").performScrollTo().assertIsDisplayed()
  }

  @Test
  fun inputsHaveInitialValue() {
    Thread.sleep(10000)

    composeTestRule.onNodeWithTag("playlistTitle").assertTextContains(playlist.playlistName)
    composeTestRule.onNodeWithTag("ownerText").assertTextContains("@" + playlist.playlistOwner)
    composeTestRule.onNodeWithTag("publicText").assertTextContains("Private")
    composeTestRule
        .onNodeWithTag("collaboratorsText")
        .assertTextContains(playlist.playlistCollaborators[0])
  }

  @Test
  fun TextIsShownWhenNoTracks() {
    playlistViewModel.selectPlaylist(playlist)
    composeTestRule.onNodeWithTag("emptyPlaylistPrompt").assertIsDisplayed()
  }

  @Test
  fun testNavigationToHome() {
    composeTestRule.onNodeWithTag("Home").performClick()
    verify(navigationActions).navigateTo(destination = TopLevelDestinations.HOME)
  }

  @Test
  fun testNavigationToSearch() {
    composeTestRule.onNodeWithTag("Search").performClick()
    verify(navigationActions).navigateTo(destination = TopLevelDestinations.SEARCH)
  }

  @Test
  fun testNavigationToLibrary() {
    composeTestRule.onNodeWithTag("Library").performClick()
    verify(navigationActions).navigateTo(destination = TopLevelDestinations.LIBRARY)
  }

  @Test
  fun testNavigationToProfile() {
    composeTestRule.onNodeWithTag("Profile").performClick()
    verify(navigationActions).navigateTo(destination = TopLevelDestinations.PROFILE)
  }
}
