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
import com.epfl.beatlink.viewmodel.library.PlaylistViewModel
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.verify

class CreateNewPlaylistScreenTest {
  private lateinit var playlistRepository: PlaylistRepository
  // private  lateinit var profileRepository: ProfileRepository
  private lateinit var playlistViewModel: PlaylistViewModel
  // private lateinit var profileViewModel: ProfileViewModel
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
    // profileRepository = mock(ProfileRepository::class.java)
    playlistViewModel = PlaylistViewModel(playlistRepository)
    // profileViewModel = ProfileViewModel(profileRepository)

    navigationActions = mock(NavigationActions::class.java)
    `when`(navigationActions.currentRoute()).thenReturn(Screen.CREATE_NEW_PLAYLIST)

    composeTestRule.setContent {
      CreateNewPlaylistScreen(
          navigationActions, viewModel(factory = ProfileViewModel.Factory), playlistViewModel)
    }
  }

  @Test
  fun everythingIsDisplayed() {
    // The screen is displayed
    composeTestRule.onNodeWithTag("createNewPlaylistScreen").assertIsDisplayed()
    // The title is displayed
    composeTestRule.onNodeWithTag("createNewPlaylistTitle").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("createNewPlaylistTitle")
        .assertTextEquals("Create a new playlist")
    // The back button is displayed
    composeTestRule.onNodeWithTag("goBackButton").assertIsDisplayed()
    // The bottom nav bar is displayed
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()
    // The playlist cover rectangle is displayed
    composeTestRule.onNodeWithTag("playlistCover").assertIsDisplayed()
    // composeTestRule.onNodeWithTag("emptyCoverText").assertExists().assertIsDisplayed()
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
    composeTestRule.onNodeWithTag("createPlaylist").performScrollTo().assertIsDisplayed()
  }

  @Test
  fun buttonsWorkCorrectly() {
    composeTestRule.onNodeWithTag("playlistCover").performScrollTo().performClick()
    composeTestRule.onNodeWithTag("collabButton").performScrollTo().performClick()
    composeTestRule.onNodeWithTag("gradientSwitch").performScrollTo().performClick()
  }

  @Test
  fun createPlaylistButtonWorks() {
    // Mock the getNewUid() method to return a known value

    val mockPlaylistID = "mockPlaylistID"
    `when`(playlistViewModel.getNewUid()).thenReturn(mockPlaylistID)

    // Set values for title and description
    composeTestRule.onNodeWithTag("inputPlaylistTitle").performTextInput("New Playlist")
    composeTestRule
        .onNodeWithTag("inputPlaylistDescription")
        .performTextInput("Playlist Description")

    // Click the "Create" button
    composeTestRule.onNodeWithTag("createPlaylist").performScrollTo().performClick()

    verify(playlistRepository).addPlaylist(any(), any(), any())
  }
}
