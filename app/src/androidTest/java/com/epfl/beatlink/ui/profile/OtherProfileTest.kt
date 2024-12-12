package com.epfl.beatlink.ui.profile

import android.app.Application
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.epfl.beatlink.model.library.UserPlaylist
import com.epfl.beatlink.model.profile.FriendRequestRepository
import com.epfl.beatlink.model.profile.ProfileData
import com.epfl.beatlink.model.profile.ProfileRepository
import com.epfl.beatlink.model.spotify.objects.SpotifyArtist
import com.epfl.beatlink.model.spotify.objects.SpotifyTrack
import com.epfl.beatlink.model.spotify.objects.State
import com.epfl.beatlink.repository.spotify.api.SpotifyApiRepository
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.ui.navigation.Screen.EDIT_PROFILE
import com.epfl.beatlink.ui.navigation.Screen.LINKS
import com.epfl.beatlink.viewmodel.profile.FriendRequestViewModel
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel
import com.epfl.beatlink.viewmodel.spotify.api.SpotifyApiViewModel
import com.google.firebase.FirebaseApp
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.verify

class OtherProfileTest {

  @get:Rule val mockitoRule: MockitoRule = MockitoJUnit.rule()
  private val testDispatcher = StandardTestDispatcher()
  private lateinit var navigationActions: NavigationActions

  @Mock lateinit var mockApplication: Application

  private lateinit var spotifyApiRepository: SpotifyApiRepository
  private lateinit var spotifyApiViewModel: SpotifyApiViewModel

  private lateinit var mockFriendRequestRepository: FriendRequestRepository
  private lateinit var mockFriendRequestViewModel: FriendRequestViewModel
  private lateinit var mockProfileRepository: ProfileRepository
  private lateinit var mockProfileViewModel: ProfileViewModel

  private val fakeFriendRequestViewModel = FakeFriendRequestViewModel()

  private val userProfile = ProfileData(bio = "", links = 0, name = "user", username = "user")

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

  private val profileData =
      ProfileData(
          username = "username",
          links = 0,
          name = null,
          bio = null,
          profilePicture = null,
          favoriteMusicGenres = listOf("Pop", "Rock", "Jazz", "Classic"),
          topSongs = topSongs,
          topArtists = topArtists)

  @get:Rule val composeTestRule = createComposeRule()

  @OptIn(ExperimentalCoroutinesApi::class)
  @Before
  fun setUp() {
    composeTestRule.mainClock.autoAdvance = false // Control clock manually during the test
    MockitoAnnotations.openMocks(this)
    Dispatchers.setMain(testDispatcher)

    mockProfileRepository = mock(ProfileRepository::class.java)
    mockProfileViewModel = mockk(relaxed = true)

    mockFriendRequestRepository = mock(FriendRequestRepository::class.java)
    mockFriendRequestViewModel = mockk(relaxed = true)

    spotifyApiRepository = mock(SpotifyApiRepository::class.java)
    spotifyApiViewModel = SpotifyApiViewModel(mockApplication, spotifyApiRepository)

    navigationActions = mock(NavigationActions::class.java)
    `when`(navigationActions.currentRoute()).thenReturn(Screen.OTHER_PROFILE)

    // Initialize Firebase if necessary
    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @After
  fun tearDown() {
    Dispatchers.resetMain() // Reset the Main dispatcher after tests
  }

  @Test
  fun testInitialUIElements() {
    val fakeProfileViewModel = FakeProfileViewModel()

    composeTestRule.setContent {
      OtherProfileScreen(
          profileViewModel = fakeProfileViewModel,
          friendRequestViewModel = fakeFriendRequestViewModel,
          navigationAction = navigationActions,
          spotifyApiViewModel = spotifyApiViewModel)
    }

    // Check if the top app bar is displayed
    composeTestRule.onNodeWithTag("otherProfileScreen").assertExists()
    composeTestRule.onNodeWithTag("profileScreenMoreVertButton").assertExists()
    // Verify Profile Picture
    composeTestRule.onNodeWithTag("profilePicture").assertExists()
    // Check if links count is displayed
    composeTestRule.onNodeWithTag("linksCount").assertExists()
    // Check if the name is displayed
    composeTestRule.onNodeWithTag("name").assertExists()
    // Check if the bio is displayed
    composeTestRule.onNodeWithTag("bio").assertExists()
  }

  @Test
  fun testDynamicContent() {
    val fakeProfileViewModel = FakeProfileViewModel()
    fakeProfileViewModel.setFakeSelectedProfile(
        ProfileData(bio = "Test Bio", links = 3, name = "Test User Name", username = "testuser"))

    composeTestRule.setContent {
      OtherProfileScreen(
          profileViewModel = fakeProfileViewModel,
          friendRequestViewModel = fakeFriendRequestViewModel,
          navigationAction = navigationActions,
          spotifyApiViewModel = spotifyApiViewModel)
    }

    composeTestRule.onNodeWithTag("titleUsername").assertTextEquals("testuser")
    composeTestRule.onNodeWithTag("name").assertTextEquals("Test User Name")
    composeTestRule.onNodeWithTag("bio").assertTextEquals("Test Bio")
    composeTestRule.onNodeWithTag("linksCount").assertTextContains("3 Links")
  }

  @Test
  fun testLinkButtonStates() {
    fakeFriendRequestViewModel.setOwnRequests(listOf("testUserId"))
    val fakeProfileViewModel = FakeProfileViewModel()
    fakeProfileViewModel.setFakeProfile(userProfile)
    fakeProfileViewModel.setFakeSelectedId("testUserId")
    fakeProfileViewModel.setFakeSelectedProfile(profileData)

    composeTestRule.setContent {
      OtherProfileScreen(
          profileViewModel = fakeProfileViewModel,
          friendRequestViewModel = fakeFriendRequestViewModel,
          navigationAction = navigationActions,
          spotifyApiViewModel = spotifyApiViewModel)
    }
    composeTestRule.waitForIdle()
    composeTestRule.waitUntil(5_000) {
      composeTestRule.onAllNodesWithText("Requested").fetchSemanticsNodes().isNotEmpty()
    }
    composeTestRule.onNodeWithText("Requested").assertExists()
    composeTestRule.onNodeWithText("Requested").performClick()
  }

  @Test
  fun testNavigationActionsWhenOwnProfile() {
    val fakeProfileViewModel = FakeProfileViewModel()
    fakeProfileViewModel.setFakeProfile(userProfile)
    fakeProfileViewModel.setFakeSelectedProfile(userProfile)

    composeTestRule.setContent {
      OtherProfileScreen(
          profileViewModel = fakeProfileViewModel,
          friendRequestViewModel = fakeFriendRequestViewModel,
          navigationAction = navigationActions,
          spotifyApiViewModel = spotifyApiViewModel)
    }

    composeTestRule.onNodeWithTag("linksCount").performClick()
    verify(navigationActions).navigateTo(LINKS)

    composeTestRule.onNodeWithTag("editProfileButton").performClick()
    verify(navigationActions).navigateTo(EDIT_PROFILE)
  }

  @Test
  fun testPlaylistsAreDisplayed() {
    val fakeProfileViewModel = FakeProfileViewModel()
    val fakeSpotifyApiViewModel = FakeSpotifyApiViewModel()
    val playlists =
        listOf(
            UserPlaylist(
                playlistID = "1",
                ownerID = "userId",
                playlistCover = "",
                playlistName = "Playlist 1",
                playlistPublic = true,
                playlistTracks = emptyList(),
                nbTracks = 0),
            UserPlaylist(
                playlistID = "2",
                ownerID = "userId",
                playlistCover = "",
                playlistName = "Playlist 2",
                playlistPublic = true,
                playlistTracks = emptyList(),
                nbTracks = 0),
        )
    fakeSpotifyApiViewModel.setUserPlaylists(playlists)
    fakeProfileViewModel.setFakeProfile(profileData)

    composeTestRule.setContent {
      OtherProfileScreen(
          profileViewModel = fakeProfileViewModel,
          friendRequestViewModel = fakeFriendRequestViewModel,
          navigationAction = navigationActions,
          spotifyApiViewModel = fakeSpotifyApiViewModel)
    }

    // composeTestRule.onNodeWithTag("PLAYLISTSTitle").performScrollTo().assertIsDisplayed()
    // composeTestRule.onNodeWithText("Playlist 1").performScrollTo().assertExists()
    // composeTestRule.onNodeWithText("Playlist 2").assertExists()
  }

  @Test
  fun testTopSongsAndArtistsAreDisplayed() {
    val fakeProfileViewModel = FakeProfileViewModel()
    val fakeSpotifyApiViewModel = FakeSpotifyApiViewModel()
    fakeProfileViewModel.setFakeSelectedProfile(
        ProfileData(
            topSongs = listOf(SpotifyTrack(name = "Song 1"), SpotifyTrack(name = "Song 2")),
            topArtists =
                listOf(SpotifyArtist(name = "Artist 1"), SpotifyArtist(name = "Artist 2"))))

    composeTestRule.setContent {
      OtherProfileScreen(
          profileViewModel = fakeProfileViewModel,
          friendRequestViewModel = fakeFriendRequestViewModel,
          navigationAction = navigationActions,
          spotifyApiViewModel = fakeSpotifyApiViewModel)
    }

    composeTestRule.onNodeWithText("Song 1").assertExists()
    composeTestRule.onNodeWithText("Song 2").assertExists()

    composeTestRule.onNodeWithText("Artist 1").assertExists()
    composeTestRule.onNodeWithText("Artist 2").assertExists()
  }
}
