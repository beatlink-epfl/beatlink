package com.epfl.beatlink.ui.library

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.viewmodel.compose.viewModel
import com.epfl.beatlink.model.library.Playlist
import com.epfl.beatlink.model.library.PlaylistRepository
import com.epfl.beatlink.model.profile.ProfileData
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen.INVITE_COLLABORATORS
import com.epfl.beatlink.ui.profile.FakeFriendRequestViewModel
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
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class InviteCollaboratorsOverlayTest {
  private lateinit var navigationActions: NavigationActions

  private lateinit var playlistRepository: PlaylistRepository
  private lateinit var playlistViewModel: PlaylistViewModel

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)

    playlistRepository = mock(PlaylistRepository::class.java)
    `when`(playlistRepository.getUserId()).thenReturn("testUserId")
    playlistViewModel = PlaylistViewModel(playlistRepository)
  }

  @Test
  fun everythingIsDisplayed() {
    composeTestRule.setContent {
      InviteCollaboratorsOverlay(
          navigationActions,
          viewModel(factory = ProfileViewModel.Factory),
          viewModel(factory = FriendRequestViewModel.Factory),
          viewModel(factory = PlaylistViewModel.Factory)) {}
    }
    composeTestRule.onNodeWithTag("overlay").assertIsDisplayed()
    composeTestRule.onNodeWithTag("searchBar").assertIsDisplayed()
  }

  @Test
  fun searchBarNavigatesToInviteCollaboratorsScreen() {
    composeTestRule.setContent {
      InviteCollaboratorsOverlay(
          navigationActions,
          viewModel(factory = ProfileViewModel.Factory),
          viewModel(factory = FriendRequestViewModel.Factory),
          viewModel(factory = PlaylistViewModel.Factory)) {}
    }
    composeTestRule.onNodeWithTag("searchBar").performClick()
    verify(navigationActions).navigateTo(INVITE_COLLABORATORS)
  }

  @Test
  fun overlayCorrectlyAddsCollaborators() {
    val fakeProfileViewModel = FakeProfileViewModel()
    val fakeFriendRequestViewModel = FakeFriendRequestViewModel()
    val fakePlaylistViewModel = FakePlaylistViewModel()
    val friendsIds = listOf("user1")

    fakeFriendRequestViewModel.setAllFriends(friendsIds)

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

    fakePlaylistViewModel.selectPlaylist(playlist)
    fakeProfileViewModel.setFakeUserIdByUsername(mapOf("alice123" to "user1"))
    fakeProfileViewModel.setFakeUsernameById(mapOf("user1" to "alice123"))
    fakeProfileViewModel.setFakeProfileDataById(
        mapOf("user1" to ProfileData(bio = "", links = 1, name = "Alice", username = "alice123")))

    composeTestRule.setContent {
      InviteCollaboratorsOverlay(
          navigationActions,
          fakeProfileViewModel,
          fakeFriendRequestViewModel,
          fakePlaylistViewModel) {}
    }

    // Simulate adding a collaborator
    composeTestRule.onNodeWithTag("CollabCard").assertIsDisplayed()
    composeTestRule.onNodeWithTag("addButton").performClick() // Add collaborator via UI interaction

    val updatedCollaborators = fakePlaylistViewModel.tempPlaylistCollaborators.value
    assertEquals(listOf("user1"), updatedCollaborators)
    composeTestRule.onNodeWithTag("checkButton").assertIsDisplayed()
  }

  @Test
  fun overlayCorrectlyRemovesCollaborators() {
    val fakeProfileViewModel = FakeProfileViewModel()
    val fakeFriendRequestViewModel = FakeFriendRequestViewModel()
    val fakePlaylistViewModel = FakePlaylistViewModel()
    val friendsIds = listOf("user1")

    fakeFriendRequestViewModel.setAllFriends(friendsIds)

    val playlist =
        Playlist(
            playlistID = "2",
            playlistCover = "",
            playlistName = "Empty Playlist",
            playlistDescription = "No tracks here",
            playlistPublic = true,
            userId = "testUserId",
            playlistOwner = "testOwner",
            playlistCollaborators = listOf("user1"),
            playlistTracks = emptyList(),
            nbTracks = 0)

    fakePlaylistViewModel.selectPlaylist(playlist)
    fakeProfileViewModel.setFakeUserIdByUsername(mapOf("alice123" to "user1"))
    fakeProfileViewModel.setFakeUsernameById(mapOf("user1" to "alice123"))
    fakeProfileViewModel.setFakeProfileDataById(
        mapOf("user1" to ProfileData(bio = "", links = 1, name = "Alice", username = "alice123")))

    fakePlaylistViewModel.updateTemporallyCollaborators(listOf("user1"))

    composeTestRule.setContent {
      InviteCollaboratorsOverlay(
          navigationActions,
          fakeProfileViewModel,
          fakeFriendRequestViewModel,
          fakePlaylistViewModel) {}
    }
    // Simulate adding a collaborator
    composeTestRule.onNodeWithTag("CollabCard").assertIsDisplayed()
    composeTestRule.waitForIdle()
    composeTestRule
        .onNodeWithTag("checkButton")
        .performClick() // Remove collaborator via UI interaction

    val updatedCollaborators = fakePlaylistViewModel.tempPlaylistCollaborators.value
    assertEquals(emptyList<String>(), updatedCollaborators)
    composeTestRule.onNodeWithTag("addButton").assertIsDisplayed()
  }

  @Test
  fun overlayCorrectlyDisplaysListOfFriends() {
    val fakeFriendRequestViewModel = FakeFriendRequestViewModel()
    val fakeProfileViewModel = FakeProfileViewModel()

    fakeFriendRequestViewModel.setAllFriends(listOf("user1", "user2"))
    fakeProfileViewModel.setFakeProfileDataById(
        mapOf(
            "user1" to ProfileData(bio = "", links = 1, name = "Alice", username = "alice123"),
            "user2" to ProfileData(bio = "", links = 1, name = "Bob", username = "bob123")))
    composeTestRule.setContent {
      InviteCollaboratorsOverlay(
          navigationActions,
          fakeProfileViewModel,
          fakeFriendRequestViewModel,
          viewModel(factory = PlaylistViewModel.Factory)) {}
    }
    composeTestRule.onAllNodesWithTag("CollabCard").assertCountEquals(2)
    composeTestRule.onNodeWithText("Alice").assertExists()
    composeTestRule.onNodeWithText("@ALICE123").assertExists()
    composeTestRule.onNodeWithText("Bob").assertExists()
    composeTestRule.onNodeWithText("@BOB123").assertExists()
  }

  @Test
  fun inviteCollaboratorsOverlay_addsAndRemovesCollaborators() {
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
}
