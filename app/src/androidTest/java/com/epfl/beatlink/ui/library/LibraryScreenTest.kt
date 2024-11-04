package com.epfl.beatlink.ui.library

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import com.epfl.beatlink.ui.navigation.NavigationActions
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

class LibraryScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationDestination: NavDestination
  private lateinit var navHostController: NavHostController
  private lateinit var navigationActions: NavigationActions

  @Before
  fun setUp() {
    navigationDestination = mock(NavDestination::class.java)
    navHostController = mock(NavHostController::class.java)
    navigationActions = NavigationActions(navHostController)
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
}
