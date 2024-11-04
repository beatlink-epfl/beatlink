package com.epfl.beatlink.ui.library

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.epfl.beatlink.ui.library.LibraryScreen
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Route
import com.epfl.beatlink.ui.navigation.Screen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class LibraryScreenTest {

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
    composeTestRule.setContent { LibraryScreen(navigationActions) }

    composeTestRule.onNodeWithTag("libraryScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("searchButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("addPlaylistButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()
    composeTestRule.onNodeWithTag("FAVORITESTitleWithArrow").assertIsDisplayed()
    composeTestRule.onNodeWithTag("favoriteItem").assertIsDisplayed()
    composeTestRule.onNodeWithTag("PLAYLISTSTitleWithArrow").assertIsDisplayed()
    composeTestRule.onNodeWithTag("playlistItem").assertIsDisplayed()
  }

  @Test
  fun displayTextsCorrectly() {

    // Launch the composable under test
    composeTestRule.setContent { LibraryScreen(navigationActions) }

    composeTestRule.onNodeWithTag("libraryTitle").assertTextEquals("My Library")
    composeTestRule.onNodeWithTag("FAVORITESTitleWithArrow").assertTextEquals("FAVORITES")
    composeTestRule.onNodeWithTag("PLAYLISTSTitleWithArrow").assertTextEquals("PLAYLISTS")
  }

  @Test
  fun buttonsWorkCorrectly() {
    composeTestRule.setContent { LibraryScreen(navigationActions) }

    composeTestRule.onNodeWithTag("searchButton").performClick()
    composeTestRule.onNodeWithTag("addPlaylistButton").performClick()
    composeTestRule.onNodeWithTag("FAVORITESTitleWithArrow").performClick()
    composeTestRule.onNodeWithTag("PLAYLISTSTitleWithArrow").performClick()
  }

  @Test
  fun verifyAddPlaylistButtonNavigatesToCreateNewPlaylistScreen() {
    composeTestRule.setContent { LibraryScreen(navigationActions) }

    // Perform click action on the sign-in button
    composeTestRule.onNodeWithTag("addPlaylistButton").performClick()

    verify(navigationActions).navigateTo(Screen.CREATE_NEW_PLAYLIST)
  }
}
