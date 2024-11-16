package com.epfl.beatlink.ui.library

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.epfl.beatlink.model.library.Playlist
import com.epfl.beatlink.model.library.PlaylistRepository
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Route
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.viewmodel.library.PlaylistViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any

class MyPlaylistsScreenTest {
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
          playlistCollaborators = emptyList(),
          playlistSongs = emptyList(),
          nbTracks = 0)

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    playlistRepository = mock(PlaylistRepository::class.java)
    playlistViewModel = PlaylistViewModel(playlistRepository)
    navigationActions = mock(NavigationActions::class.java)
    `when`(navigationActions.currentRoute()).thenReturn(Route.LIBRARY)

    composeTestRule.setContent { MyPlaylistsScreen(navigationActions, playlistViewModel) }
  }

  @Test
  fun displayTextWhenEmpty() {
    `when`(playlistRepository.getPlaylists(any(), any())).then {
      it.getArgument<(List<Playlist>) -> Unit>(0)(listOf())
    }
    playlistViewModel.getPlaylists()

    composeTestRule.onNodeWithTag("emptyPlaylistsPrompt").assertIsDisplayed()
  }

  @Test
  fun displayPlaylistsWhenNotEmpty() {
    `when`(playlistRepository.getPlaylists(any(), any())).then {
      it.getArgument<(List<Playlist>) -> Unit>(0)(listOf(playlist))
    }
    playlistViewModel.getPlaylists()

    composeTestRule.onNodeWithTag("playlistItem").assertIsDisplayed()
  }

  @Test
  fun everythingIsDisplayed() {
    composeTestRule.onNodeWithTag("myPlaylistsScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("addButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("goBackButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()
    composeTestRule.onNodeWithTag("myPlaylistsTitle").assertIsDisplayed()
  }

  @Test
  fun createPlaylistButtonCallsNavActions() {
    composeTestRule.onNodeWithTag("addButton").performClick()
    org.mockito.kotlin.verify(navigationActions).navigateTo(screen = Screen.CREATE_NEW_PLAYLIST)
  }

  @Test
  fun goBackCallsNavActions() {
    composeTestRule.onNodeWithTag("goBackButton").performClick()
    org.mockito.kotlin.verify(navigationActions).goBack()
  }
}
