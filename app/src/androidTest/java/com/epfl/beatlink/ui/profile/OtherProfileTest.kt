package com.epfl.beatlink.ui.profile

import android.app.Application
import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.epfl.beatlink.model.profile.FriendRequestRepository
import com.epfl.beatlink.model.profile.ProfileData
import com.epfl.beatlink.model.profile.ProfileRepository
import com.epfl.beatlink.model.spotify.objects.SpotifyArtist
import com.epfl.beatlink.model.spotify.objects.SpotifyTrack
import com.epfl.beatlink.model.spotify.objects.State
import com.epfl.beatlink.repository.spotify.api.SpotifyApiRepository
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
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

  private val userProfile = ProfileData(username = "user")
  private val displayedUser1 = ProfileData(username = "username1")
  private val displayedUser2 = ProfileData(username = "username2")
  private val displayedUser3 = ProfileData(username = "username3")

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
          name = null,
          bio = null,
          links = 0,
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
  fun elementsAreDisplayed() {
    composeTestRule.setContent {
      OtherProfileScreen(
          mockProfileViewModel, mockFriendRequestViewModel, navigationActions, spotifyApiViewModel)
    }

    // Check if the icons are displayed
    composeTestRule
        .onNodeWithTag("profileScreenMoreVertButton")
        .assertExists()
        .assertContentDescriptionEquals("MoreVert")

    // Check if the user's profile picture is displayed
    composeTestRule
        .onNodeWithTag("profilePicture")
        .assertExists()
        .assertContentDescriptionEquals("Profile Picture")

    // Check if the user's link's count is displayed
    composeTestRule
        .onNodeWithTag("linksCount")
        .assertExists()
        .assertTextContains("${profileData.links} Links")

    // Check if the edit button is displayed
    composeTestRule.onNodeWithTag("editProfileButton").assertExists()
    // Check if the user's name is displayed
    composeTestRule.onNodeWithTag("name").assertExists()
    // Check if the user's bio is displayed
    composeTestRule.onNodeWithTag("bio").assertExists()

    composeTestRule.onNodeWithTag("editProfileButton").performClick()
    verify(navigationActions).navigateTo(screen = Screen.EDIT_PROFILE)
  }

  @Test
  fun otherProfileScreenDisplaysOtherProfileDetails() {
    val fakeProfileViewModel = FakeProfileViewModel()
    fakeProfileViewModel.setFakeProfile(profileData)

    composeTestRule.setContent {
      OtherProfileScreen(
          fakeProfileViewModel, mockFriendRequestViewModel, navigationActions, spotifyApiViewModel)
    }

    composeTestRule.onNodeWithTag("linkedButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("linkedButton").assertIsDisplayed().assertTextEquals("Link")
  }

  //    @OptIn(ExperimentalCoroutinesApi::class)
  //    @Test
  //    fun linkButtonDisplaysRequest(): Unit = runTest {
  //        val displayedUserId = "TestId1"
  //        val ownRequests = listOf("TestId1", "testId2")
  //        val fakeProfileViewModel = FakeProfileViewModel()
  //        fakeProfileViewModel.setFakeSelectedProfile(displayedUser1)
  //        fakeProfileViewModel.setFakeSelectedId(displayedUserId)
  //
  //        fakeFriendRequestViewModel.setOwnRequests(ownRequests)
  //
  //        composeTestRule.setContent {
  //            OtherProfileScreen(
  //                profileViewModel = fakeProfileViewModel,
  //                friendRequestViewModel = fakeFriendRequestViewModel,
  //                navigationActions,
  //                spotifyApiViewModel)
  //        }
  //        advanceUntilIdle()
  //
  //        composeTestRule.onNodeWithText("Requested").assertExists()
  //        composeTestRule.onNodeWithTag("linkedButton").assertExists()
  //    }

}
