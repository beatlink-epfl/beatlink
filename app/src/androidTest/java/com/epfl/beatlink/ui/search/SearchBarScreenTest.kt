package com.epfl.beatlink.ui.search

import android.app.Application
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.viewmodel.compose.viewModel
import com.epfl.beatlink.model.profile.ProfileData
import com.epfl.beatlink.model.spotify.objects.SpotifyArtist
import com.epfl.beatlink.model.spotify.objects.SpotifyTrack
import com.epfl.beatlink.model.spotify.objects.State
import com.epfl.beatlink.repository.spotify.auth.SpotifyAuthRepository
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.ui.navigation.TopLevelDestinations
import com.epfl.beatlink.ui.profile.FakeFriendRequestViewModel
import com.epfl.beatlink.ui.profile.FakeProfileViewModel
import com.epfl.beatlink.ui.profile.FakeSpotifyApiViewModel
import com.epfl.beatlink.ui.spotify.FakeSpotifyAuthViewModel
import com.epfl.beatlink.viewmodel.map.user.MapUsersViewModel
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.verify

class SearchBarScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions
  private lateinit var application: Application
  private lateinit var spotifyAuthRepository: SpotifyAuthRepository
  private lateinit var spotifyAuthViewModel: FakeSpotifyAuthViewModel

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

  private val topProfiles = listOf(ProfileData(username = "user1"), ProfileData(username = "user2"))

  private val ownRequests = listOf("user1", "user2")
  private val friendRequests = listOf("wanna be friend 1", "wanna be friend 2")
  private val allFriends = listOf("friend1", "friend2")

  @Before
  fun setUp() {

    navigationActions = mock(NavigationActions::class.java)

    `when`(navigationActions.currentRoute()).thenReturn(Screen.SEARCH)

    val fakeSpotifyApiViewModel = FakeSpotifyApiViewModel()
    val fakeProfileViewModel = FakeProfileViewModel()
    val fakeFriendRequestViewModel = FakeFriendRequestViewModel()

    fakeProfileViewModel.setFakeProfiles(topProfiles)

    fakeFriendRequestViewModel.setOwnRequests(ownRequests)
    fakeFriendRequestViewModel.setFriendRequests(friendRequests)
    fakeFriendRequestViewModel.setAllFriends(allFriends)

    fakeSpotifyApiViewModel.setTopTracks(topSongs)
    fakeSpotifyApiViewModel.setTopArtists(topArtists)

    fakeSpotifyApiViewModel.updatePlayer()

    application = mockk<Application>(relaxed = true)
    spotifyAuthRepository = mockk<SpotifyAuthRepository>(relaxed = true)

    spotifyAuthViewModel =
        FakeSpotifyAuthViewModel(application = application, repository = spotifyAuthRepository)

    composeTestRule.setContent {
      SearchBarScreen(
          navigationActions,
          fakeSpotifyApiViewModel,
          spotifyAuthViewModel,
          viewModel(factory = MapUsersViewModel.Factory),
          fakeProfileViewModel,
          fakeFriendRequestViewModel)
    }
  }

  @Test
  fun everythingIsDisplayed() {
    composeTestRule.onNodeWithTag("searchScaffold").assertIsDisplayed()
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()
    composeTestRule.onNodeWithTag("shortSearchBarRow").assertIsDisplayed()
    composeTestRule.onNodeWithTag("writableSearchBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("categoryButtons").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Songs categoryButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Artists categoryButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("People categoryButton").assertIsDisplayed()
  }

  @Test
  fun displayTextsCorrectly() {
    composeTestRule
        .onNodeWithTag("Songs categoryText", useUnmergedTree = true)
        .assertTextEquals("Songs")
    composeTestRule
        .onNodeWithTag("Artists categoryText", useUnmergedTree = true)
        .assertTextEquals("Artists")
    composeTestRule
        .onNodeWithTag("People categoryText", useUnmergedTree = true)
        .assertTextEquals("People")
  }

  @Test
  fun searchResultsDisplayTracksWhenCategoryIsSongs() {
    // Simulate selecting the "Songs" category
    composeTestRule.onNodeWithTag("Songs categoryButton").performClick()

    // Perform search
    composeTestRule.onNodeWithTag("writableSearchBar").performTextInput("hello")

    // Check all tracks are displayed
    topSongs.forEach { track ->
      composeTestRule.onNodeWithText(track.name).assertExists()
      composeTestRule.onNodeWithText(track.artist).assertExists()
    }

    composeTestRule.onAllNodesWithTag("trackItem").assertCountEquals(topSongs.size)
  }

  @Test
  fun searchResultsDisplayArtistsWhenCategoryIsArtists() {
    // Simulate selecting the "Artists" category
    composeTestRule.onNodeWithTag("Artists categoryButton").performClick()

    // Perform search
    composeTestRule.onNodeWithTag("writableSearchBar").performTextInput("hello")

    // Check all artists are displayed
    topArtists.forEach { artist -> composeTestRule.onNodeWithText(artist.name).assertExists() }

    composeTestRule.onAllNodesWithTag("artistItem").assertCountEquals(topArtists.size)
  }

  @Test
  fun categoryButtonsFunctionCorrectly() {
    // Ensure category buttons are displayed
    composeTestRule.onNodeWithTag("categoryButtons").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Songs categoryButton").performClick()
    composeTestRule
        .onNodeWithTag("Songs categoryText", useUnmergedTree = true)
        .assertTextEquals("Songs")

    composeTestRule.onNodeWithTag("Artists categoryButton").performClick()
    composeTestRule
        .onNodeWithTag("Artists categoryText", useUnmergedTree = true)
        .assertTextEquals("Artists")

    composeTestRule.onNodeWithTag("People categoryButton").performClick()
    composeTestRule
        .onNodeWithTag("People categoryText", useUnmergedTree = true)
        .assertTextEquals("People")
  }

  @Test
  fun categoryButtonsDoNotBreakResultsDisplay() {
    // Start with "Songs"
    composeTestRule.onNodeWithTag("Songs categoryButton").performClick()
    composeTestRule.onNodeWithTag("writableSearchBar").performTextInput("hello")
    composeTestRule.onAllNodesWithTag("trackItem").assertCountEquals(topSongs.size)

    // Switch to "Artists"
    composeTestRule.onNodeWithTag("Artists categoryButton").performClick()
    composeTestRule.onAllNodesWithTag("artistItem").assertCountEquals(topArtists.size)

    // Switch to "People"
    composeTestRule.onNodeWithTag("People categoryButton").performClick()
    composeTestRule.onNodeWithTag("searchResultsColumn").assertDoesNotExist()
  }

  @Test
  fun searchResultsDisplayPeopleWhenCategoryIsPeople() {
    // Simulate selecting the "People" category
    composeTestRule.onNodeWithTag("People categoryButton").performClick()

    composeTestRule.onNodeWithTag("writableSearchBar").performTextInput("user")

    // Check all profiles are displayed
    topProfiles.forEach { profile ->
      composeTestRule.onNodeWithText("@${profile.username.uppercase()}").assertExists()
    }

    composeTestRule.onAllNodesWithTag("peopleItem").assertCountEquals(topProfiles.size)
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
