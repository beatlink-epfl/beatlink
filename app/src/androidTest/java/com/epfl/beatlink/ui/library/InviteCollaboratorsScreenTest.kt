package com.epfl.beatlink.ui.library

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.epfl.beatlink.model.library.Playlist
import com.epfl.beatlink.model.library.PlaylistRepository
import com.epfl.beatlink.model.profile.ProfileData
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.ui.profile.FakeProfileViewModel
import com.epfl.beatlink.viewmodel.library.PlaylistViewModel
import com.epfl.beatlink.viewmodel.profile.FriendRequestViewModel
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class InviteCollaboratorsScreenTest {
  private lateinit var playlistRepository: PlaylistRepository
  private lateinit var playlistViewModel: PlaylistViewModel
  private lateinit var navigationActions: NavigationActions
  private lateinit var fakeProfileViewModel: FakeProfileViewModel

  private val profiles =
      listOf(ProfileData(username = "username1"), ProfileData(username = "username2"))

  val profile =
      ProfileData(
          bio = "Existing bio",
          links = 3,
          name = "John Doe",
          profilePicture = null,
          username = "TestUser")
  val testProfile =
      ProfileData(
          bio = "", links = 3, name = "Test User", profilePicture = null, username = "testuser")

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    playlistRepository =
        mock(
            PlaylistRepository::class
                .java) // Use relaxed if you don't want to manually mock every behavior
    playlistViewModel = PlaylistViewModel(playlistRepository)

    fakeProfileViewModel = FakeProfileViewModel()
    fakeProfileViewModel.setFakeProfile(profile)
    fakeProfileViewModel.setFakeProfiles(profiles)

    navigationActions = mock(NavigationActions::class.java)

    `when`(navigationActions.currentRoute()).thenReturn(Screen.INVITE_COLLABORATORS)
  }

  @Test
  fun inviteCollaboratorsScreen_initialRender_displaysComponents() {
    composeTestRule.setContent {
      InviteCollaboratorsScreen(navigationActions, fakeProfileViewModel, playlistViewModel)
    }
    composeTestRule.onNodeWithTag("inviteCollaboratorsScreen").assertIsDisplayed()

    composeTestRule.onNodeWithTag("shortSearchBarRow").assertExists()
  }

  @Test
  fun testBackNavigation() {
    composeTestRule.setContent {
      InviteCollaboratorsScreen(navigationActions, fakeProfileViewModel, playlistViewModel)
    }
    composeTestRule.onNodeWithTag("goBackButton").performClick()
    org.mockito.kotlin.verify(navigationActions).goBack()
  }

  @Test
  fun testSearchBarInteraction() {
    composeTestRule.setContent {
      InviteCollaboratorsScreen(navigationActions, fakeProfileViewModel, playlistViewModel)
    }
    composeTestRule.onNodeWithTag("writableSearchBar").performTextInput("John Doe")
    composeTestRule.onNodeWithTag("writableSearchBar").assertTextEquals("John Doe")
  }

  @Test
  fun searchResultsDisplayPeopleWhenSearching() {
    composeTestRule.setContent {
      InviteCollaboratorsScreen(navigationActions, fakeProfileViewModel, playlistViewModel)
    }
    composeTestRule.onNodeWithTag("writableSearchBar").performClick()
    composeTestRule.onNodeWithTag("writableSearchBar").performTextInput("username")
    composeTestRule.onAllNodesWithTag("CollabCard").assertCountEquals(profiles.size)
  }

  @Test
  fun searchResultsDoesNotDisplayCurrentProfile() {
    composeTestRule.setContent {
      InviteCollaboratorsScreen(navigationActions, fakeProfileViewModel, playlistViewModel)
    }
    composeTestRule.onNodeWithTag("writableSearchBar").performClick()
    composeTestRule.onNodeWithTag("writableSearchBar").performTextInput("TestUser")
    composeTestRule.onNodeWithTag("CollabCard").assertDoesNotExist()
  }

  @Test
  fun inviteCollaborators_addsAndRemovesCollaborators() {
    val profileViewModel = mockk<ProfileViewModel>(relaxed = true)
    val friendRequestViewModel = mockk<FriendRequestViewModel>(relaxed = true)
    val playlistViewModel = mockk<PlaylistViewModel>(relaxed = true)

    // Mock data
    val collabIds = mutableListOf("friend2")
    val friendsProfileData =
        listOf(
            ProfileData(username = "friend1"),
            ProfileData(username = "friend2"),
            ProfileData(username = "friend3"))

    every { profileViewModel.getUserIdByUsername("friend1", any()) } answers
        {
          val callback = secondArg<(String?) -> Unit>()
          callback("friend1")
        }
    every { profileViewModel.getUserIdByUsername("friend3", any()) } answers
        {
          val callback = secondArg<(String?) -> Unit>()
          callback("friend3")
        }
    every { playlistViewModel.updateTemporallyCollaborators(any()) } answers
        {
          collabIds.clear()
          collabIds.addAll(firstArg<List<String>>())
        }

    // Test adding a collaborator
    val onAddCallback = slot<() -> Unit>()
    profileViewModel.getUserIdByUsername("friend1") { userId ->
      playlistViewModel.updateTemporallyCollaborators(collabIds + userId!!)
    }

    assert(collabIds.contains("friend1"))

    // Test removing a collaborator
    profileViewModel.getUserIdByUsername("friend1") { userId ->
      playlistViewModel.updateTemporallyCollaborators(collabIds.filter { it != userId })
    }
    assert(!collabIds.contains("friend1"))
  }

  @Test
  fun inviteCollaborators_addsCollaborators() {
    val fakeProfileViewModel = FakeProfileViewModel()
    val fakePlaylistViewModel = FakePlaylistViewModel()

    val playlist =
        Playlist(
            playlistID = "2",
            playlistCover = "",
            playlistName = "Empty Playlist",
            playlistDescription = "No tracks here",
            playlistPublic = true,
            userId = "testUserId",
            playlistOwner = "testOwner",
            playlistCollaborators = emptyList(),
            playlistTracks = emptyList(),
            nbTracks = 0)
    val aliceProfileData = ProfileData(bio = "", links = 1, name = "Alice", username = "alice123")
    val profiles = listOf(aliceProfileData)

    fakeProfileViewModel.setFakeProfile(profile)
    fakeProfileViewModel.setFakeProfiles(profiles)

    fakePlaylistViewModel.selectPlaylist(playlist)
    fakeProfileViewModel.setFakeUserIdByUsername(mapOf("alice123" to "user1"))
    fakeProfileViewModel.setFakeUsernameById(mapOf("user1" to "alice123"))
    fakeProfileViewModel.setFakeProfileDataById(mapOf("user1" to aliceProfileData))

    composeTestRule.setContent {
      InviteCollaboratorsScreen(navigationActions, fakeProfileViewModel, fakePlaylistViewModel)
    }
    // Look for alice123
    composeTestRule.onNodeWithTag("writableSearchBar").performClick()
    composeTestRule.onNodeWithTag("writableSearchBar").performTextInput("alice123")

    composeTestRule.waitForIdle()

    // Simulate adding a collaborator
    composeTestRule.onNodeWithTag("CollabCard").assertIsDisplayed()
    composeTestRule.onNodeWithTag("addButton").performClick() // Add collaborator via UI interaction

    val updatedCollaborators = fakePlaylistViewModel.tempPlaylistCollaborators.value
    assertEquals(listOf("user1"), updatedCollaborators)
    composeTestRule.onNodeWithTag("checkButton").assertIsDisplayed()
  }
}
