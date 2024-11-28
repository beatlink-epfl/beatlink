package com.epfl.beatlink.ui.search

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.epfl.beatlink.model.spotify.objects.SpotifyArtist
import com.epfl.beatlink.model.spotify.objects.SpotifyTrack
import com.epfl.beatlink.model.spotify.objects.State
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.ui.navigation.TopLevelDestinations
import com.epfl.beatlink.ui.profile.FakeSpotifyApiViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.verify

class SearchBarScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions

  private var topSongs =
      listOf(
          SpotifyTrack(
              name = "hello1",
              artist = "Jack",
              trackId = "1",
              cover = "",
              duration = 4,
              popularity = 1,
              state = State.PAUSE),
          SpotifyTrack(
              name = "hello2",
              artist = "John",
              trackId = "2",
              cover = "",
              duration = 4,
              popularity = 1,
              state = State.PAUSE))

  private val topArtists =
      listOf(
          SpotifyArtist(image = "1", name = "Artist 1", genres = emptyList(), popularity = 23),
          SpotifyArtist(image = "2", name = "Artist 2", genres = emptyList(), popularity = 24))

  @Before
  fun setUp() {

    navigationActions = mock(NavigationActions::class.java)

    `when`(navigationActions.currentRoute()).thenReturn(Screen.SEARCH_BAR)

    val fakeSpotifyApiViewModel = FakeSpotifyApiViewModel()

    fakeSpotifyApiViewModel.setTopTracks(topSongs)
    fakeSpotifyApiViewModel.setTopArtists(topArtists)

    composeTestRule.setContent { SearchBarScreen(navigationActions, fakeSpotifyApiViewModel) }
  }

  @Test
  fun everythingIsDisplayed() {
    composeTestRule.onNodeWithTag("searchBarScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()
    composeTestRule.onNodeWithTag("shortSearchBarRow").assertIsDisplayed()
    composeTestRule.onNodeWithTag("goBackButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("writableSearchBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("categoryButtons").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("writableSearchBarIcon", useUnmergedTree = true)
        .assertIsDisplayed()
    composeTestRule.onNodeWithTag("Songs categoryButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Songs categoryText", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("Artists categoryButton").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("Artists categoryText", useUnmergedTree = true)
        .assertIsDisplayed()
    composeTestRule.onNodeWithTag("Events categoryButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Events categoryText", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("RECENT SEARCHESTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("searchResultsColumn").assertIsDisplayed()
  }

  @Test
  fun displayTextsCorrectly() {

    composeTestRule.onNodeWithTag("RECENT SEARCHESTitle").assertTextEquals("RECENT SEARCHES")
    composeTestRule
        .onNodeWithTag("Songs categoryText", useUnmergedTree = true)
        .assertTextEquals("Songs")
    composeTestRule
        .onNodeWithTag("Artists categoryText", useUnmergedTree = true)
        .assertTextEquals("Artists")
    composeTestRule
        .onNodeWithTag("Events categoryText", useUnmergedTree = true)
        .assertTextEquals("Events")
  }

  @Test
  fun testBackNavigation() {
    composeTestRule.onNodeWithTag("goBackButton").performClick()
    verify(navigationActions).goBack()
  }

  @Test
  fun searchResultsDisplayTracksWhenCategoryIsSongs() {
    // Simulate selecting the "Songs" category
    composeTestRule.onNodeWithTag("Songs categoryButton").performClick()

    composeTestRule.onNodeWithTag("writableSearchBar").performTextInput("hello")

    // Check all tracks are displayed
    topSongs.forEach { track ->
      composeTestRule.onNodeWithText(track.name).assertExists()
      composeTestRule.onNodeWithText(track.artist).assertExists()
    }

    composeTestRule.onAllNodesWithTag("trackItem").assertCountEquals(topSongs.size)
    composeTestRule.onAllNodesWithTag("trackAlbumCover").assertCountEquals(topSongs.size)
    composeTestRule.onAllNodesWithTag("favoriteIcon").assertCountEquals(topSongs.size)
    composeTestRule.onAllNodesWithTag("addIcon").assertCountEquals(topSongs.size)
    composeTestRule.onAllNodesWithTag("moreIcon").assertCountEquals(topSongs.size)
  }

  @Test
  fun searchResultsDisplayArtistsWhenCategoryIsArtists() {
    // Simulate selecting the "Artists" category
    composeTestRule.onNodeWithTag("Artists categoryButton").performClick()

    composeTestRule.onNodeWithTag("writableSearchBar").performTextInput("hello")

    // Check all artists are displayed
    topArtists.forEach { artist -> composeTestRule.onNodeWithText(artist.name).assertExists() }

    composeTestRule.onAllNodesWithTag("artistItem").assertCountEquals(topArtists.size)
    composeTestRule.onAllNodesWithTag("artistImage").assertCountEquals(topArtists.size)
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
