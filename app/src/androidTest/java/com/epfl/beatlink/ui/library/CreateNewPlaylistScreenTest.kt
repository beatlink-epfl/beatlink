package com.android.sample.ui.library

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class CreateNewPlaylistScreenTest {
  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    `when`(navigationActions.currentRoute()).thenReturn(Route.LIBRARY)
  }

  @Test
  fun everythingIsDisplayed() {
    // Launch the composable under test
    composeTestRule.setContent { CreateNewPlaylistScreen(navigationActions) }

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
    composeTestRule.setContent { CreateNewPlaylistScreen(navigationActions) }

    composeTestRule.onNodeWithTag("playlistCover").performScrollTo().performClick()
    composeTestRule.onNodeWithTag("collabButton").performScrollTo().performClick()
    composeTestRule.onNodeWithTag("gradientSwitch").performScrollTo().performClick()
    composeTestRule.onNodeWithTag("createPlaylist").performScrollTo().performClick()
  }
}
