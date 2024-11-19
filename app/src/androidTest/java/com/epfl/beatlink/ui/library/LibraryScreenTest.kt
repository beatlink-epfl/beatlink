package com.epfl.beatlink.ui.library

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.epfl.beatlink.model.library.Playlist
import com.epfl.beatlink.model.library.PlaylistRepository
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Route
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.ui.navigation.Screen.PLAYLIST_OVERVIEW
import com.epfl.beatlink.ui.navigation.TopLevelDestinations
import com.epfl.beatlink.viewmodel.library.PlaylistViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any

class LibraryScreenTest {

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
          playlistTracks = emptyList(),
          nbTracks = 0)

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    playlistRepository = mock(PlaylistRepository::class.java)
    playlistViewModel = PlaylistViewModel(playlistRepository)
    navigationActions = mock(NavigationActions::class.java)
    `when`(navigationActions.currentRoute()).thenReturn(Route.LIBRARY)

    composeTestRule.setContent { LibraryScreen(navigationActions, playlistViewModel) }
  }

  @Test
  fun everythingIsDisplayed() {
    composeTestRule.onNodeWithTag("libraryScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("searchButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("addButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()
    composeTestRule.onNodeWithTag("MY PLAYLISTSTitleWithArrow").assertIsDisplayed()
    composeTestRule.onNodeWithTag("SHARED PLAYLISTSTitleWithArrow").assertIsDisplayed()
  }

  @Test
  fun displayTextsCorrectly() {
    composeTestRule.onNodeWithTag("libraryTitle").assertTextEquals("My Library")
    composeTestRule.onNodeWithTag("MY PLAYLISTSTitleWithArrow").assertTextEquals("MY PLAYLISTS")
    composeTestRule
        .onNodeWithTag("SHARED PLAYLISTSTitleWithArrow")
        .assertTextEquals("SHARED PLAYLISTS")
  }

  @Test
  fun buttonsWorkCorrectly() {
    composeTestRule.onNodeWithTag("searchButton").performClick()
    composeTestRule.onNodeWithTag("addButton").performClick()
    composeTestRule.onNodeWithTag("MY PLAYLISTSTitleWithArrow").performClick()
    composeTestRule.onNodeWithTag("SHARED PLAYLISTSTitleWithArrow").performClick()
  }

  @Test
  fun verifyAddPlaylistButtonNavigatesToCreateNewPlaylistScreen() {
    // Perform click action on the sign-in button
    composeTestRule.onNodeWithTag("addButton").performClick()

    verify(navigationActions).navigateTo(Screen.CREATE_NEW_PLAYLIST)
  }

  @Test
  fun verifyPlaylistsButtonNavigatesToMyPlaylistsScreen() {
    // Perform click action on the sign-in button
    composeTestRule.onNodeWithTag("MY PLAYLISTSTitleWithArrow").performClick()

    verify(navigationActions).navigateTo(Screen.MY_PLAYLISTS)
  }

  @Test
  fun playlistItemIsNotDisplayedWhenNoPlaylists() {
    // Mock `getPlaylists` to return an empty list
    `when`(playlistRepository.getPlaylists(any(), any())).then {
      it.getArgument<(List<Playlist>) -> Unit>(0)(listOf()) // Return an empty list
    }

    // Add a playlist to the view model (if needed, depending on your app's logic)
    playlistViewModel.addPlaylist(playlist)

    // Ensure that the "playlistItem" is NOT displayed when there are no playlists
    composeTestRule.onNodeWithTag("playlistItem").assertDoesNotExist()
  }

  @Test
  fun playlistItemsAreDisplayed() {
    `when`(playlistRepository.getPlaylists(any(), any())).then {
      it.getArgument<(List<Playlist>) -> Unit>(0)(listOf(playlist))
    }
    playlistViewModel.getPlaylists()

    composeTestRule.onNodeWithTag("playlistItem").assertIsDisplayed()
  }

  @Test
  fun playlistItemsNavigatesToPlaylistOverview() {
    `when`(playlistRepository.getPlaylists(any(), any())).then {
      it.getArgument<(List<Playlist>) -> Unit>(0)(listOf(playlist))
    }
    playlistViewModel.getPlaylists()

    composeTestRule.onNodeWithTag("playlistItem").assertIsDisplayed().performClick()
    playlistViewModel.selectPlaylist(playlist)
    verify(navigationActions).navigateTo(PLAYLIST_OVERVIEW)
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
