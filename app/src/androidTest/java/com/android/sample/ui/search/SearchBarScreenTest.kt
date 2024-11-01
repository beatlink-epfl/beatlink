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

class SearchBarScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions

  @Before
  fun setUp() {

    navigationActions = mock(NavigationActions::class.java)

    `when`(navigationActions.currentRoute()).thenReturn(Screen.SEARCH_BAR)
    composeTestRule.setContent { SearchBarScreen(navigationActions) }
  }

  @Test
  fun everythingIsDisplayed() {
    composeTestRule.onNodeWithTag("writableSearchBar").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("shortSearchBarRow").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("backButton").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("RECENT SEARCHESTitle").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("recentSearchesColumn").assertIsDisplayed()
    composeTestRule.onNodeWithTag("divider").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("lazyColumn").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("placeholderText").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("SongscategoryButton").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("EventscategoryButton").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("PeoplecategoryButton").performScrollTo().assertIsDisplayed()
  }

  @Test
  fun displayTextsCorrectly() {

    composeTestRule.onNodeWithTag("RECENT SEARCHESTitle").assertTextEquals("RECENT SEARCHES")
    composeTestRule.onNodeWithTag("SongscategoryButton").assertTextEquals("Songs")
    composeTestRule.onNodeWithTag("EventscategoryButton").assertTextEquals("Events")
    composeTestRule.onNodeWithTag("PeoplecategoryButton").assertTextEquals("People")
  }

  @Test
  fun testBackNavigation() {

    composeTestRule.onNodeWithTag("backButton").performClick()
    verify(navigationActions).goBack()
  }
}
