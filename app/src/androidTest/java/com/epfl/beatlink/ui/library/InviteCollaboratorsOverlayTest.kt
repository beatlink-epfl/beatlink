package com.epfl.beatlink.ui.library

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.lifecycle.viewmodel.compose.viewModel
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen.INVITE_COLLABORATORS
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
}
