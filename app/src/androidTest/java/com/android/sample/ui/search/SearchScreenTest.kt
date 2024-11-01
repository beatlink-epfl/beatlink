package com.android.sample.ui.search

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
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
    composeTestRule
        .onNodeWithTag("LIVE MUSIC PARTIESLazyColumn")
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule.onNodeWithTag("LIVE MUSIC PARTIESTitle").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("nonWritableSearchBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("nonWritableSearchBarBox").assertIsDisplayed()
    composeTestRule.onNodeWithTag("TRENDING SONGSLazyColumn").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("TRENDING SONGSTitle").performScrollTo().assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("MOST MATCHED SONGSLazyColumn")
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule.onNodeWithTag("MOST MATCHED SONGSTitle").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("DISCOVER PEOPLELazyColumn").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("DISCOVER PEOPLETitle").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("searchScreenDivider").performScrollTo().assertIsDisplayed()

    // ProfileCard Component
    composeTestRule.onNodeWithTag("profileCardItem").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("profileCardColumn").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("profileCardImage").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("profileCardName").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("profileCardUsername").performScrollTo().assertIsDisplayed()

    // PartyCard Component
    composeTestRule.onNodeWithTag("partyCardItem").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("partyCardColumn").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("partyCardRow").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("partyCardTextColumn").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("partyCardTitle").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("partyCardUsername").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("partyCardDescription").performScrollTo().assertIsDisplayed()

    // SongCard Component
    composeTestRule.onNodeWithTag("Song1songCardItem").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("Song1songCardColumn").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("Song1songCardContainer").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("Song1songCardImage").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("Song1songCardTextBox").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("Song1songCardTextColumn").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("Song1songCardText").performScrollTo().assertIsDisplayed()

    composeTestRule.onNodeWithTag("Song2songCardItem").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("Song2songCardColumn").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("Song2songCardContainer").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("Song2songCardImage").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("Song2songCardTextBox").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("Song2songCardTextColumn").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("Song2songCardText").performScrollTo().assertIsDisplayed()
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
