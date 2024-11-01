package com.android.sample.ui.search

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.verify

class MostMatchedSongsScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions

  @Before
  fun setUp() {

    navigationActions = mock(NavigationActions::class.java)

    `when`(navigationActions.currentRoute()).thenReturn(Screen.MOST_MATCHED_SONGS)
    composeTestRule.setContent { MostMatchedSongsScreen(navigationActions) }
  }

  @Test
  fun everythingIsDisplayed() {
    composeTestRule.onNodeWithTag("writableSearchBar").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("shortSearchBarRow").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("backButton").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("mostMatchedSearchColumn").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("divider").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("placeholderText").performScrollTo().assertIsDisplayed()
  }

  @Test
  fun displayTextsCorrectly() {

    composeTestRule.onNodeWithTag("placeholderText").assertTextEquals("Not Drawn In Figma Yet")
  }

  @Test
  fun testBackNavigation() {

    composeTestRule.onNodeWithTag("backButton").performClick()
    verify(navigationActions).goBack()
  }
}
