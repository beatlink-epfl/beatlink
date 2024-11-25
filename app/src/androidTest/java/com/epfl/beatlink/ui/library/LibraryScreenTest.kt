package com.epfl.beatlink.ui.library

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.lifecycle.viewmodel.compose.viewModel
import com.epfl.beatlink.model.library.Playlist
import com.epfl.beatlink.model.library.PlaylistRepository
import com.epfl.beatlink.repository.spotify.api.SpotifyApiRepository
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Route
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.ui.navigation.Screen.PLAYLIST_OVERVIEW
import com.epfl.beatlink.ui.navigation.TopLevelDestinations
import com.epfl.beatlink.viewmodel.library.PlaylistViewModel
import com.epfl.beatlink.viewmodel.map.user.MapUsersViewModel
import com.epfl.beatlink.viewmodel.spotify.api.SpotifyApiViewModel
import org.json.JSONObject
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.stub

class LibraryScreenTest {

  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  private lateinit var playlistRepository: PlaylistRepository
  private lateinit var playlistViewModel: PlaylistViewModel
  private lateinit var navigationActions: NavigationActions
  @Mock private lateinit var mockApplication: Application
  @Mock private lateinit var mockApiRepository: SpotifyApiRepository
  private lateinit var spotifyApiViewModel: SpotifyApiViewModel

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

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    playlistRepository = mock(PlaylistRepository::class.java)
    playlistViewModel = PlaylistViewModel(playlistRepository)
    navigationActions = mock(NavigationActions::class.java)
    spotifyApiViewModel = SpotifyApiViewModel(mockApplication, mockApiRepository)
    mockApiRepository.stub { onBlocking { get("me/player") } doReturn Result.success(JSONObject()) }
    `when`(navigationActions.currentRoute()).thenReturn(Route.LIBRARY)

    composeTestRule.setContent { LibraryScreen(navigationActions, playlistViewModel, spotifyApiViewModel, viewModel(factory = MapUsersViewModel.Factory)) }
  }

  @Test
  fun everythingIsDisplayed() {
    composeTestRule.onNodeWithTag("libraryScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("searchButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("addButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()
    composeTestRule.onNodeWithTag("MY PLAYLISTSTitleWithArrow").assertIsDisplayed()
    composeTestRule.onNodeWithTag("SHARED WITH METitleWithArrow").assertIsDisplayed()
    composeTestRule.onNodeWithTag("PUBLICTitleWithArrow").assertIsDisplayed()
  }

  @Test
  fun displayTextsCorrectly() {
    composeTestRule.onNodeWithTag("libraryTitle").assertTextEquals("My Library")
    composeTestRule.onNodeWithTag("MY PLAYLISTSTitleWithArrow").assertTextEquals("MY PLAYLISTS")
    composeTestRule.onNodeWithTag("SHARED WITH METitleWithArrow").assertTextEquals("SHARED WITH ME")
    composeTestRule.onNodeWithTag("PUBLICTitleWithArrow").assertTextEquals("PUBLIC")
  }

  @Test
  fun buttonsWorkCorrectly() {
    composeTestRule.onNodeWithTag("searchButton").performClick()
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
  fun verifySharedPlaylistsButtonNavigatesToSharedPlaylistsScreen() {
    // Perform click action on the sign-in button
    composeTestRule.onNodeWithTag("SHARED WITH METitleWithArrow").performClick()

    verify(navigationActions).navigateTo(Screen.SHARED_WITH_ME_PLAYLISTS)
  }

  @Test
  fun verifyPublicPlaylistsButtonNavigatesToPublicPlaylistsScreen() {
    // Perform click action on the sign-in button
    composeTestRule.onNodeWithTag("PUBLICTitleWithArrow").performClick()

    verify(navigationActions).navigateTo(Screen.PUBLIC_PLAYLISTS)
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
