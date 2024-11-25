package com.epfl.beatlink.ui.library

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.epfl.beatlink.model.library.Playlist
import com.epfl.beatlink.model.library.PlaylistRepository
import com.epfl.beatlink.model.profile.ProfileData
import com.epfl.beatlink.repository.profile.ProfileRepositoryFirestore
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Route
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.ui.navigation.TopLevelDestinations
import com.epfl.beatlink.viewmodel.library.PlaylistViewModel
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class PlaylistOverviewScreenTest {
    private lateinit var profileRepository: ProfileRepositoryFirestore
  private lateinit var playlistRepository: PlaylistRepository
    private lateinit var profileViewModel: ProfileViewModel
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
          userId = "testUserId",
          playlistOwner = "luna",
          playlistCollaborators = listOf("collab1"),
          playlistTracks = emptyList(),
          nbTracks = 10)

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
      profileRepository = mockk(relaxed = true)
    playlistRepository = mock(PlaylistRepository::class.java)
    playlistViewModel = PlaylistViewModel(playlistRepository)
    navigationActions = mock(NavigationActions::class.java)
    `when`(navigationActions.currentRoute()).thenReturn(Route.LIBRARY)
    `when`(navigationActions.currentRoute()).thenReturn(Screen.PLAYLIST_OVERVIEW)

      // Initialize ProfileViewModel with an initial profile state
      profileViewModel =
          ProfileViewModel(
              repository = profileRepository,
              initialProfile =
              ProfileData(
                  bio = "testDescription",
                  links = 5,
                  name = "testName",
                  profilePicture = null,
                  username = "johndoe")
          )
  }

  @Test
  fun everythingIsDisplayed() {
      whenever(playlistViewModel.getUserId()).thenReturn("testUserId")
      playlistViewModel.selectPlaylist(playlist2)
      composeTestRule.setContent { PlaylistOverviewScreen(navigationActions,
          profileViewModel, playlistViewModel) }
    composeTestRule.onNodeWithTag("playlistOverviewScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("playlistName").assertIsDisplayed()
    composeTestRule.onNodeWithTag("goBackButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()
    composeTestRule.onNodeWithTag("editButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("playlistCoverCard").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("playlistTitle").performScrollTo().assertIsDisplayed()
  }

    @Test
    fun editButton_AddToThisPlaylistButton_ExportPlaylistButton_AreNotDisplayedWhenNotOwner() {
        val mockPlaylist = Playlist(
            playlistID = "playlist1",
            playlistCover = "",
            playlistName = "Test Playlist",
            playlistDescription = "Test Description",
            playlistPublic = true,
            userId = "otherUserId", // Different user ID
            playlistOwner = "otherUserId", // Different owner
            playlistCollaborators = emptyList(),
            playlistTracks = emptyList(),
            nbTracks = 0
        )

        whenever(playlistViewModel.getUserId()).thenReturn("testUserId")
        playlistViewModel.selectPlaylist(mockPlaylist)
        composeTestRule.setContent { PlaylistOverviewScreen(navigationActions,
            profileViewModel, playlistViewModel) }
        composeTestRule.onNodeWithTag("editButton").assertDoesNotExist()
        composeTestRule.onNodeWithTag("addToThisPlaylistButton").assertDoesNotExist()
        composeTestRule.onNodeWithTag("exportButton").assertDoesNotExist()
    }

  @Test
  fun trackCardDisplaysWhenNotEmpty() {
      playlistViewModel.selectPlaylist(playlist2)
      composeTestRule.setContent { PlaylistOverviewScreen(navigationActions,
          profileViewModel, playlistViewModel) }
    composeTestRule.onNodeWithTag("trackVoteCard").performScrollTo().assertIsDisplayed()
  }

  @Test
  fun textDisplaysWhenEmpty() {
      playlistViewModel.selectPlaylist(playlist)
      composeTestRule.setContent { PlaylistOverviewScreen(navigationActions,
          profileViewModel, playlistViewModel) }
    composeTestRule.onNodeWithTag("emptyPlaylistPrompt").performScrollTo().assertIsDisplayed()
  }

  @Test
  fun inputsHaveInitialValue() {
    Thread.sleep(10000)
      playlistViewModel.selectPlaylist(playlist2)
      composeTestRule.setContent { PlaylistOverviewScreen(navigationActions,
          profileViewModel, playlistViewModel) }

    composeTestRule.onNodeWithTag("playlistTitle").assertTextContains(playlist.playlistName)
    composeTestRule.onNodeWithTag("ownerText").assertTextContains("@" + playlist.playlistOwner)
    composeTestRule.onNodeWithTag("publicText").assertTextContains("Private")
    composeTestRule
        .onNodeWithTag("collaboratorsText")
        .assertTextContains(playlist.playlistCollaborators[0])
  }

  @Test
  fun textIsShownWhenNoTracks() {
      playlistViewModel.selectPlaylist(playlist)
      composeTestRule.setContent { PlaylistOverviewScreen(navigationActions,
          profileViewModel, playlistViewModel) }
    composeTestRule.onNodeWithTag("emptyPlaylistPrompt").assertIsDisplayed()
  }

    @Test
    fun testNavigation() {
        playlistViewModel.selectPlaylist(playlist2)
        composeTestRule.setContent { PlaylistOverviewScreen(navigationActions,
            profileViewModel, playlistViewModel) }
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
