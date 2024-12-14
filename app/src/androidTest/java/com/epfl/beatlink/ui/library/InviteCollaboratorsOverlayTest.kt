package com.epfl.beatlink.ui.library

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.viewmodel.compose.viewModel
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
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class InviteCollaboratorsOverlayTest {
    private lateinit var navigationActions: NavigationActions

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        navigationActions = mock(NavigationActions::class.java)
    }

    @Test
    fun everythingIsDisplayed() {
        composeTestRule.setContent {
            InviteCollaboratorsOverlay(
                navigationActions,
                viewModel(factory = ProfileViewModel.Factory),
                viewModel(factory = FriendRequestViewModel.Factory),
                viewModel(factory = PlaylistViewModel.Factory)
            ) {}
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
                viewModel(factory = PlaylistViewModel.Factory)
            ) {}
        }
        composeTestRule.onNodeWithTag("searchBar").performClick()
        verify(navigationActions).navigateTo(INVITE_COLLABORATORS)
    }

    @Test
    fun overlayCorrectlyDisplaysListOfFriends() {
        val fakeFriendRequestViewModel = FakeFriendRequestViewModel()
        val fakeProfileViewModel = FakeProfileViewModel()

        fakeFriendRequestViewModel.setAllFriends(listOf("user1", "user2"))
        fakeProfileViewModel.setFakeProfileDataById(
            mapOf(
                "user1" to ProfileData(bio = "", links = 1, name = "Alice", username = "alice123"),
                "user2" to ProfileData(bio = "", links = 1, name = "Bob", username = "bob123")
            )
        )
        composeTestRule.setContent {
            InviteCollaboratorsOverlay(
                navigationActions,
                fakeProfileViewModel,
                fakeFriendRequestViewModel,
                viewModel(factory = PlaylistViewModel.Factory)
            ) {}
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
        val friendsProfileData = listOf(
            ProfileData(username = "friend1"),
            ProfileData(username = "friend2"),
            ProfileData(username = "friend3")
        )

        every { profileViewModel.getUserIdByUsername("friend1", any()) } answers {
            val callback = secondArg<(String?) -> Unit>()
            callback("friend1")
        }
        every { profileViewModel.getUserIdByUsername("friend3", any()) } answers {
            val callback = secondArg<(String?) -> Unit>()
            callback("friend3")
        }
        every { playlistViewModel.updateTemporallyCollaborators(any()) } answers {
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
