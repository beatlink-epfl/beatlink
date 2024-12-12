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
import com.epfl.beatlink.viewmodel.profile.FriendRequestViewModel
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class InviteCollaboratorsOverlayTest {
  private lateinit var navigationActions: NavigationActions

  @get:Rule val composeTestRule = createComposeRule()

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
            "user2" to ProfileData(bio = "", links = 1, name = "Bob", username = "bob123")))
    composeTestRule.setContent {
      InviteCollaboratorsOverlay(
          navigationActions,
          fakeProfileViewModel,
          fakeFriendRequestViewModel,
      ) {}
    }
    composeTestRule.onAllNodesWithTag("CollabCard").assertCountEquals(2)
    composeTestRule.onNodeWithText("Alice").assertExists()
    composeTestRule.onNodeWithText("@ALICE123").assertExists()
    composeTestRule.onNodeWithText("Bob").assertExists()
    composeTestRule.onNodeWithText("@BOB123").assertExists()
  }
}
