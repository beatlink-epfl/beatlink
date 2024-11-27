package com.epfl.beatlink.ui.library

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.epfl.beatlink.model.library.Playlist
import com.epfl.beatlink.model.library.PlaylistRepository
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Route
import com.epfl.beatlink.ui.navigation.TopLevelDestinations
import com.epfl.beatlink.viewmodel.library.PlaylistViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.verify

class PublicPlaylistsScreenTest {
  private lateinit var playlistRepository: PlaylistRepository
  private lateinit var playlistViewModel: PlaylistViewModel
  private lateinit var navigationActions: NavigationActions

  private val playlist =
      Playlist(
          playlistID = "1",
          playlistCover = "",
          playlistName = "playlist 1",
          playlistDescription = "testing",
          playlistPublic = true,
          userId = "",
          playlistOwner = "someone",
          playlistCollaborators = emptyList(),
          playlistTracks = emptyList(),
          nbTracks = 0)

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    playlistRepository = mock(PlaylistRepository::class.java)
    playlistViewModel = PlaylistViewModel(playlistRepository)
    navigationActions = mock(NavigationActions::class.java)
    `when`(navigationActions.currentRoute()).thenReturn(Route.LIBRARY)

    composeTestRule.setContent { PublicPlaylistsScreen(navigationActions, playlistViewModel) }
  }

  @Test
  fun everythingIsDisplayed() {
    composeTestRule.onNodeWithTag("publicPlaylistsScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("goBackButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()
    composeTestRule.onNodeWithTag("publicPlaylistsTitle").assertIsDisplayed()
  }

  @Test
  fun displayTextWhenEmpty() {
    `when`(playlistRepository.getPublicPlaylists(any(), any())).then {
      it.getArgument<(List<Playlist>) -> Unit>(0)(listOf())
    }
    playlistViewModel.getPublicPlaylists()

    composeTestRule.onNodeWithTag("emptyPlaylistsPrompt").assertIsDisplayed()
  }

  @Test
  fun displayPlaylistsWhenNotEmpty() {
    `when`(playlistRepository.getPublicPlaylists(any(), any())).then {
      it.getArgument<(List<Playlist>) -> Unit>(0)(listOf(playlist))
    }
    playlistViewModel.getPublicPlaylists()

    composeTestRule.onNodeWithTag("playlistItem").assertIsDisplayed()
  }

  @Test
  fun goBackCallsNavActions() {
    composeTestRule.onNodeWithTag("goBackButton").performClick()
    verify(navigationActions).goBack()
  }

  @Test
  fun testNavigation() {
    composeTestRule.onNodeWithTag("Home").performClick()
    verify(navigationActions).navigateTo(destination = TopLevelDestinations.HOME)
    composeTestRule.onNodeWithTag("Search").performClick()
    verify(navigationActions).navigateTo(destination = TopLevelDestinations.SEARCH)
    composeTestRule.onNodeWithTag("Library").performClick()
    verify(navigationActions).navigateTo(destination = TopLevelDestinations.LIBRARY)
    composeTestRule.onNodeWithTag("Profile").performClick()
    verify(navigationActions).navigateTo(destination = TopLevelDestinations.PROFILE)
  }
}
