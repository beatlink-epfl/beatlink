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

class LiveMusicPartiesScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions

  @Before
  fun setUp() {

    navigationActions = mock(NavigationActions::class.java)

    `when`(navigationActions.currentRoute()).thenReturn(Screen.LIVE_MUSIC_PARTIES)
    composeTestRule.setContent { LiveMusicPartiesScreen(navigationActions) }
  }

  @Test
  fun everythingIsDisplayed() {
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()
    composeTestRule.onNodeWithTag("liveMusicPartiesScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("writableSearchBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("shortSearchBarRow").assertIsDisplayed()
    composeTestRule.onNodeWithTag("backButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("LIVE MUSIC PARTIESTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("partiesSearchColumn").assertIsDisplayed()
    composeTestRule.onNodeWithTag("divider").assertIsDisplayed()
    composeTestRule.onNodeWithTag("lazyColumn").assertIsDisplayed()
    composeTestRule.onNodeWithTag("recentSearch_Party 1").assertIsDisplayed()
  }

  @Test
  fun displayTextsCorrectly() {

    composeTestRule.onNodeWithTag("LIVE MUSIC PARTIESTitle").assertTextEquals("LIVE MUSIC PARTIES")
  }

  @Test
  fun testBackNavigation() {

    composeTestRule.onNodeWithTag("backButton").performClick()
    verify(navigationActions).goBack()
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
