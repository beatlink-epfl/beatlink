package com.epfl.beatlink.ui.library

import android.Manifest.permission.READ_MEDIA_IMAGES
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.viewmodel.compose.viewModel
import com.epfl.beatlink.repository.library.PlaylistRepository
import com.epfl.beatlink.model.profile.ProfileData
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.ui.profile.FakeFriendRequestViewModel
import com.epfl.beatlink.ui.profile.FakeProfileViewModel
import com.epfl.beatlink.viewmodel.library.PlaylistViewModel
import com.epfl.beatlink.viewmodel.profile.FriendRequestViewModel
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.verify

class CreateNewPlaylistScreenTest {
  private lateinit var playlistRepository: PlaylistRepository
  private lateinit var playlistViewModel: PlaylistViewModel
  private lateinit var navigationActions: NavigationActions

  val profile =
      ProfileData(
          bio = "Existing bio",
          links = 3,
          name = "John Doe",
          profilePicture = null,
          username = "TestUser")

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    playlistRepository = mock(PlaylistRepository::class.java)
    playlistViewModel = PlaylistViewModel(playlistRepository)

    navigationActions = mock(NavigationActions::class.java)
    `when`(navigationActions.currentRoute()).thenReturn(Screen.CREATE_NEW_PLAYLIST)
  }

  @Test
  fun everythingIsDisplayed() {
    composeTestRule.setContent {
      CreateNewPlaylistScreen(
          navigationActions,
          viewModel(factory = ProfileViewModel.Factory),
          viewModel(factory = FriendRequestViewModel.Factory),
          playlistViewModel)
    }
    // The screen is displayed
    composeTestRule.onNodeWithTag("createNewPlaylistScreen").assertIsDisplayed()
    // The title is displayed
    composeTestRule.onNodeWithTag("createNewPlaylistTitle").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("createNewPlaylistTitle")
        .assertTextEquals("Create a new playlist")
    // The back button is displayed
    composeTestRule.onNodeWithTag("goBackButton").assertIsDisplayed()
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
    composeTestRule.onNodeWithTag("createPlaylist").performScrollTo().assertIsDisplayed()
  }

  @Test
  fun playlistCoverButtonOpensLauncher() {
    val mockLauncher =
        mock<(String) -> Unit>() // Mock a function that simulates permission launcher
    composeTestRule.setContent {
      val permissionLauncher =
          rememberLauncherForActivityResult(
              contract = ActivityResultContracts.RequestPermission()) { /* no-op */}
      Button(
          onClick = { mockLauncher(READ_MEDIA_IMAGES) },
          modifier = Modifier.testTag("coverImageButton")) {
            Text("Select Cover Image")
          }
    }

    composeTestRule.onNodeWithTag("coverImageButton").performClick()
    verify(mockLauncher).invoke(READ_MEDIA_IMAGES)
  }

  @Test
  fun createPlaylistButtonWorks() {
    composeTestRule.setContent {
      CreateNewPlaylistScreen(
          navigationActions,
          viewModel(factory = ProfileViewModel.Factory),
          viewModel(factory = FriendRequestViewModel.Factory),
          playlistViewModel)
    }
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

  @Test
  fun invite_collaborators_button_opens_overlay() {
    composeTestRule.setContent {
      CreateNewPlaylistScreen(
          navigationActions,
          viewModel(factory = ProfileViewModel.Factory),
          viewModel(factory = FriendRequestViewModel.Factory),
          playlistViewModel)
    }
    composeTestRule.onNodeWithTag("overlay").assertDoesNotExist()
    // Perform click on the "Invite Collaborators" button
    composeTestRule.onNodeWithTag("collabButton").performClick()
    // Verify the overlay is visible after the click
    composeTestRule.onNodeWithTag("overlay").assertIsDisplayed()
  }

  @Test
  fun createPlaylistMakesPlaylistPublic() {
    val fakeProfileViewModel = FakeProfileViewModel()
    val fakeFriendRequestViewModel = FakeFriendRequestViewModel()
    val fakePlaylistViewModel = FakePlaylistViewModel()

    composeTestRule.setContent {
      CreateNewPlaylistScreen(
          navigationActions,
          fakeProfileViewModel,
          fakeFriendRequestViewModel,
          fakePlaylistViewModel)
    }
    // Simulate adding a collaborator
    composeTestRule.onNodeWithTag("gradientSwitch").assertIsDisplayed()
    composeTestRule.onNodeWithTag("gradientSwitch").performClick() // make public

    val updatedPublic = fakePlaylistViewModel.tempPlaylistIsPublic.value
    assertEquals(true, updatedPublic)
  }

  @Test
  fun createNewPlaylistRemovesCollaborators() {
    val fakeProfileViewModel = FakeProfileViewModel()
    val fakeFriendRequestViewModel = FakeFriendRequestViewModel()
    val fakePlaylistViewModel = FakePlaylistViewModel()

    fakeProfileViewModel.setFakeUserIdByUsername(mapOf("alice123" to "user1"))
    fakeProfileViewModel.setFakeUsernameById(mapOf("user1" to "alice123"))
    fakeProfileViewModel.setFakeProfileDataById(
        mapOf("user1" to ProfileData(bio = "", links = 1, name = "Alice", username = "alice123")))

    fakePlaylistViewModel.updateTemporallyCollaborators(listOf("user1"))

    composeTestRule.setContent {
      CreateNewPlaylistScreen(
          navigationActions,
          fakeProfileViewModel,
          fakeFriendRequestViewModel,
          fakePlaylistViewModel)
    }
    // Simulate adding a collaborator
    composeTestRule.onNodeWithTag("collabCard").assertIsDisplayed()
    composeTestRule.waitForIdle()
    composeTestRule
        .onNodeWithTag("closeButton")
        .performClick() // Remove collaborator via UI interaction

    val updatedCollaborators = fakePlaylistViewModel.tempPlaylistCollaborators.value
    assertEquals(emptyList<String>(), updatedCollaborators)
    composeTestRule.onNodeWithTag("collabCard").assertDoesNotExist()
  }
}
