package com.android.sample.ui.library

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import org.junit.Rule
import org.junit.Test

class LibraryScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun everythingIsDisplayed() {
    // Launch the composable under test
    composeTestRule.setContent { LibraryScreen() }

    composeTestRule.onNodeWithTag("libraryScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("searchButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("addPlaylistButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()
    composeTestRule.onNodeWithTag("FAVORITESTitleWithArrow").assertIsDisplayed()
    composeTestRule.onNodeWithTag("PLAYLISTSTitleWithArrow").assertIsDisplayed()
  }

  @Test
  fun displayTextsCorrectly() {

    // Launch the composable under test
    composeTestRule.setContent { LibraryScreen() }

    composeTestRule.onNodeWithTag("libraryTitle").assertTextEquals("My Library")
    composeTestRule.onNodeWithTag("FAVORITESTitleWithArrow").assertTextEquals("FAVORITES")
    composeTestRule.onNodeWithTag("PLAYLISTSTitleWithArrow").assertTextEquals("PLAYLISTS")
  }
}
