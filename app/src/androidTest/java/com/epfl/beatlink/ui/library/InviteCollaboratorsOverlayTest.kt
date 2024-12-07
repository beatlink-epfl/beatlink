package com.epfl.beatlink.ui.library

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen.INVITE_COLLABORATORS
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

    composeTestRule.setContent { InviteCollaboratorsOverlay(navigationActions, {}) }
  }

  @Test
  fun everythingIsDisplayed() {
    composeTestRule.onNodeWithTag("overlay").assertIsDisplayed()
    composeTestRule.onNodeWithTag("searchBar").assertIsDisplayed()
  }

  @Test
  fun searchBarNavigatesToInviteCollaboratorsScreen() {
    composeTestRule.onNodeWithTag("searchBar").performClick()
    verify(navigationActions).navigateTo(INVITE_COLLABORATORS)
  }
}
