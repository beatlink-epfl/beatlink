package com.android.sample.ui.search

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.navigation.Screen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.verify

class SearchScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions

  @Before
  fun setUp() {

    navigationActions = mock(NavigationActions::class.java)

    `when`(navigationActions.currentRoute()).thenReturn(Route.SEARCH)
    composeTestRule.setContent { SearchScreen(navigationActions) }
  }

  @Test
  fun everythingIsDisplayed() {

    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()
    composeTestRule.onNodeWithTag("searchScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("searchScreenColumn").assertIsDisplayed()
    composeTestRule.onNodeWithTag("LIVE MUSIC PARTIESLazyColumn").assertIsDisplayed()
    composeTestRule.onNodeWithTag("LIVE MUSIC PARTIESTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("nonWritableSearchBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("nonWritableSearchBarBox").assertIsDisplayed()
    composeTestRule.onNodeWithTag("TRENDING SONGSLazyColumn").assertIsDisplayed()
    composeTestRule.onNodeWithTag("TRENDING SONGSTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("MOST MATCHED SONGSLazyColumn").assertIsDisplayed()
    composeTestRule.onNodeWithTag("MOST MATCHED SONGSTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("DISCOVER PEOPLELazyColumn").assertIsDisplayed()
    composeTestRule.onNodeWithTag("DISCOVER PEOPLETitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("searchScreenDivider").assertIsDisplayed()

    // ProfileCard Component
    composeTestRule.onNodeWithTag("profileCardItem").assertIsDisplayed()
    composeTestRule.onNodeWithTag("profileCardColumn").assertIsDisplayed()
    composeTestRule.onNodeWithTag("profileCardImage").assertIsDisplayed()
    composeTestRule.onNodeWithTag("profileCardName").assertIsDisplayed()
    composeTestRule.onNodeWithTag("profileCardUsername").assertIsDisplayed()

    // PartyCard Component
    composeTestRule.onNodeWithTag("partyCardItem").assertIsDisplayed()
    composeTestRule.onNodeWithTag("partyCardColumn").assertIsDisplayed()
    composeTestRule.onNodeWithTag("partyCardRow").assertIsDisplayed()
    composeTestRule.onNodeWithTag("partyCardTextColumn").assertIsDisplayed()
    composeTestRule.onNodeWithTag("partyCardTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("partyCardUsername").assertIsDisplayed()
    composeTestRule.onNodeWithTag("partyCardDescription").assertIsDisplayed()

    // SongCard Component
    composeTestRule.onNodeWithTag("Song1songCardItem").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Song1songCardColumn").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Song1songCardContainer").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Song1songCardImage").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Song1songCardTextBox").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Song1songCardTextColumn").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Song1songCardText").assertIsDisplayed()

    composeTestRule.onNodeWithTag("Song2songCardItem").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Song2songCardColumn").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Song2songCardContainer").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Song2songCardImage").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Song2songCardTextBox").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Song2songCardTextColumn").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Song2songCardText").assertIsDisplayed()
  }

  @Test
  fun displayTextsCorrectly() {

    composeTestRule.onNodeWithTag("TRENDING SONGSTitle").assertTextEquals("TRENDING SONGS")
    composeTestRule.onNodeWithTag("MOST MATCHED SONGSTitle").assertTextEquals("MOST MATCHED SONGS")
    composeTestRule.onNodeWithTag("LIVE MUSIC PARTIESTitle").assertTextEquals("LIVE MUSIC PARTIES")
    composeTestRule.onNodeWithTag("DISCOVER PEOPLETitle").assertTextEquals("DISCOVER PEOPLE")
  }

  @Test
  fun testNavigationToSearchBar() {

    composeTestRule.onNodeWithTag("nonWritableSearchBarBox").performClick()
    verify(navigationActions).navigateTo(screen = Screen.SEARCH_BAR)
  }

  @Test
  fun testNavigationToTrendingSongs() {

    composeTestRule.onNodeWithTag("TRENDING SONGSTitle").performClick()
    verify(navigationActions).navigateTo(screen = Screen.TRENDING_SONGS)
  }

  @Test
  fun testNavigationToMostMatchedSongs() {

    composeTestRule.onNodeWithTag("MOST MATCHED SONGSTitle").performClick()
    verify(navigationActions).navigateTo(screen = Screen.MOST_MATCHED_SONGS)
  }

  @Test
  fun testNavigationToLiveMusicParties() {

    composeTestRule.onNodeWithTag("LIVE MUSIC PARTIESTitle").performClick()
    verify(navigationActions).navigateTo(screen = Screen.LIVE_MUSIC_PARTIES)
  }

  @Test
  fun testNavigationToDiscoverPeople() {

    composeTestRule.onNodeWithTag("DISCOVER PEOPLETitle").performClick()
    verify(navigationActions).navigateTo(screen = Screen.DISCOVER_PEOPLE)
  }
}
