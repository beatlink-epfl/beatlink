package com.epfl.beatlink.ui.library

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.viewmodel.compose.viewModel
import com.epfl.beatlink.model.library.Playlist
import com.epfl.beatlink.model.library.PlaylistRepository
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.ui.navigation.Screen.MY_PLAYLISTS
import com.epfl.beatlink.ui.navigation.Screen.PLAYLIST_OVERVIEW
import com.epfl.beatlink.ui.navigation.TopLevelDestinations
import com.epfl.beatlink.viewmodel.library.PlaylistViewModel
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.verify

class EditPlaylistScreenTest {
  private lateinit var playlistRepository: PlaylistRepository
  private lateinit var playlistViewModel: PlaylistViewModel
  private lateinit var profileViewModel: ProfileViewModel
  private lateinit var navigationActions: NavigationActions

  private val playlist =
      Playlist(
          playlistID = "mockPlaylistID",
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
    profileViewModel = mockk(relaxed = true)

    navigationActions = mock(NavigationActions::class.java)
    `when`(navigationActions.currentRoute()).thenReturn(Screen.EDIT_PLAYLIST)

    playlistViewModel.selectPlaylist(playlist)

    composeTestRule.setContent {
      EditPlaylistScreen(
          navigationActions, viewModel(factory = ProfileViewModel.Factory), playlistViewModel)
    }
  }

  @Test
  fun everythingIsDisplayed() {
    // The screen is displayed
    composeTestRule.onNodeWithTag("editPlaylistScreen").assertIsDisplayed()
    // The title is displayed
    composeTestRule
        .onNodeWithTag("editPlaylistTitle")
        .assertIsDisplayed()
        .assertTextEquals("Edit " + playlistViewModel.selectedPlaylist.value!!.playlistName)

    assertEquals("playlist 1", playlistViewModel.selectedPlaylist.value!!.playlistName)
    assertEquals("testing", playlistViewModel.selectedPlaylist.value!!.playlistDescription)

    // The back button is displayed
    composeTestRule.onNodeWithTag("goBackButton").assertIsDisplayed()
    // The delete button is displayed
    composeTestRule.onNodeWithTag("deleteButton").assertIsDisplayed()
    // The bottom nav bar is displayed
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()
    // The playlist cover rectangle is displayed
    composeTestRule.onNodeWithTag("playlistCover").assertIsDisplayed()
    // The input field for title is displayed
    composeTestRule.onNodeWithTag("inputPlaylistTitle").performScrollTo().assertIsDisplayed()
    // The input field for description is displayed
    composeTestRule.onNodeWithTag("inputPlaylistDescription").performScrollTo().assertIsDisplayed()
    // The setting switch button is displayed
    composeTestRule.onNodeWithTag("makePlaylistPublicText").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("makePlaylistPublicText").assertTextEquals("Make Playlist Public")
    composeTestRule.onNodeWithTag("gradientSwitch").performScrollTo().assertIsDisplayed()
    // The collaborators section is displayed
    composeTestRule.onNodeWithTag("collaboratorsTitle").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("collaboratorsTitle").assertTextEquals("Collaborators")
    composeTestRule.onNodeWithTag("collabButton").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("collabBox").performScrollTo().assertIsDisplayed()
    // The create button is displayed
    composeTestRule.onNodeWithTag("saveEditPlaylist").performScrollTo().assertIsDisplayed()
  }

  @Test
  fun editPlaylistScreen_deletesPlaylist() {
    composeTestRule.onNodeWithTag("deleteButton").performClick()

    Mockito.verify(navigationActions).navigateToAndClearBackStack(MY_PLAYLISTS, 2)
  }

  @Test
  fun editPlaylistScreen_updatesPlaylist() {
    composeTestRule
        .onNodeWithTag("inputPlaylistTitle")
        .assertIsDisplayed()
        .performTextInput("Updated Playlist Title")
    composeTestRule
        .onNodeWithTag("inputPlaylistDescription")
        .assertIsDisplayed()
        .performTextInput("This is a valid playlist description.")
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("saveEditPlaylist").performScrollTo().performClick()

    val newPlaylist =
        Playlist(
            playlistID = "mockPlaylistID",
            playlistCover = "",
            playlistName = "Updated Playlist Title",
            playlistDescription = "This is a valid playlist description.",
            playlistPublic = false,
            userId = "",
            playlistOwner = "luna",
            playlistCollaborators = emptyList(),
            playlistTracks = emptyList(),
            nbTracks = 0)
    playlistViewModel.updatePlaylist(newPlaylist)
    playlistViewModel.selectPlaylist(newPlaylist)

    // verify that the playlist data was updated
    val updatedPlaylist = playlistViewModel.selectedPlaylist.value!!
    assertEquals("Updated Playlist Title", updatedPlaylist.playlistName)
    assertEquals("This is a valid playlist description.", updatedPlaylist.playlistDescription)
  }

  @Test
  fun deletePlaylistButtonWorks() {
    composeTestRule.onNodeWithTag("deleteButton").performClick()

    verify(playlistRepository).deletePlaylistById(any(), any(), any())
  }

  @Test
  fun updatePlaylistButtonWorks() {
    composeTestRule.onNodeWithTag("saveEditPlaylist").performScrollTo().performClick()
    verify(playlistRepository).updatePlaylist(any(), any(), any())
  }

  @Test
  fun invite_collaborators_button_opens_overlay() {
    composeTestRule.onNodeWithTag("inviteCollaboratorsOverlay").assertDoesNotExist()
    // Perform click on the "Invite Collaborators" button
    composeTestRule.onNodeWithTag("collabButton").performClick()
    // Verify the overlay is visible after the click
    composeTestRule.onNodeWithTag("inviteCollaboratorsOverlay").assertIsDisplayed()
  }

  @Test
  fun testNavigationAfterPlaylistUpdate() {
    composeTestRule.onNodeWithTag("saveEditPlaylist").performScrollTo().performClick()
    verify(navigationActions).navigateToAndClearBackStack(PLAYLIST_OVERVIEW, 1)
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
