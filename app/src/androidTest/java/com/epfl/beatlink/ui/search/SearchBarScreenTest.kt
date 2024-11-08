package com.epfl.beatlink.ui.search

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.ui.navigation.TopLevelDestinations
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
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()
    composeTestRule.onNodeWithTag("searchBarScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("writableSearchBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("shortSearchBarRow").assertIsDisplayed()
    composeTestRule.onNodeWithTag("backButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("RECENT SEARCHESTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("recentSearchesColumn").assertIsDisplayed()
    composeTestRule.onNodeWithTag("divider").assertIsDisplayed()
    composeTestRule.onNodeWithTag("lazyColumn").assertIsDisplayed()
    composeTestRule.onNodeWithTag("recentSearch_Song 1").assertIsDisplayed()
    composeTestRule.onNodeWithTag("SongscategoryButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("EventscategoryButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("PeoplecategoryButton").assertIsDisplayed()
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

  @Test
  fun selectCategoryTest() {
    // Select the "Songs" category and verify recent searches for "Songs"
    composeTestRule.onNodeWithTag("SongscategoryButton").performClick()
    composeTestRule.onNodeWithTag("recentSearch_Song A").assertExists().assertTextEquals("Song A")

    composeTestRule.onNodeWithTag("recentSearch_Song B").assertExists().assertTextEquals("Song B")

    composeTestRule.onNodeWithTag("recentSearch_Song C").assertExists().assertTextEquals("Song C")

    // Select the "Events" category and verify recent searches for "Events"
    composeTestRule.onNodeWithTag("EventscategoryButton").performClick()
    composeTestRule.onNodeWithTag("recentSearch_Event A").assertExists().assertTextEquals("Event A")

    composeTestRule.onNodeWithTag("recentSearch_Event B").assertExists().assertTextEquals("Event B")

    composeTestRule.onNodeWithTag("recentSearch_Event C").assertExists().assertTextEquals("Event C")

    // Select the "People" category and verify recent searches for "People"
    composeTestRule.onNodeWithTag("PeoplecategoryButton").performClick()
    composeTestRule
        .onNodeWithTag("recentSearch_Person A")
        .assertExists()
        .assertTextEquals("Person A")

    composeTestRule
        .onNodeWithTag("recentSearch_Person B")
        .assertExists()
        .assertTextEquals("Person B")

    composeTestRule
        .onNodeWithTag("recentSearch_Person C")
        .assertExists()
        .assertTextEquals("Person C")
  }

  @Test
  fun testNavigationToHome() {
    composeTestRule.onNodeWithTag("Home").performClick()
    verify(navigationActions).navigateTo(destination = TopLevelDestinations.HOME)
  }

  @Test
  fun testNavigationToSearch() {
    composeTestRule.onNodeWithTag("Search").performClick()
    verify(navigationActions).navigateTo(destination = TopLevelDestinations.SEARCH)
  }

  @Test
  fun testNavigationToLibrary() {
    composeTestRule.onNodeWithTag("Library").performClick()
    verify(navigationActions).navigateTo(destination = TopLevelDestinations.LIBRARY)
  }

  @Test
  fun testNavigationToProfile() {
    composeTestRule.onNodeWithTag("Profile").performClick()
    verify(navigationActions).navigateTo(destination = TopLevelDestinations.PROFILE)
  }
}
