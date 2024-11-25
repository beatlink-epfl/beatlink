package com.epfl.beatlink.ui.profile

import android.app.Application
import android.content.SharedPreferences
import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.epfl.beatlink.model.profile.ProfileData
import com.epfl.beatlink.model.spotify.objects.SpotifyArtist
import com.epfl.beatlink.model.spotify.objects.SpotifyTrack
import com.epfl.beatlink.model.spotify.objects.State
import com.epfl.beatlink.repository.profile.ProfileRepositoryFirestore
import com.epfl.beatlink.repository.spotify.api.SpotifyApiRepository
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Route
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.viewmodel.map.user.MapUsersViewModel
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel
import com.epfl.beatlink.viewmodel.spotify.api.SpotifyApiViewModel
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import okhttp3.OkHttpClient
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class ProfileTest {
  @get:Rule val mockitoRule: MockitoRule = MockitoJUnit.rule()
  private val testDispatcher = StandardTestDispatcher()
  private lateinit var navigationActions: NavigationActions

  @Mock lateinit var mockApplication: Application

  private lateinit var spotifyApiRepository: SpotifyApiRepository
  private lateinit var spotifyApiViewModel: SpotifyApiViewModel

  private lateinit var profileRepositoryFirestore: ProfileRepositoryFirestore
  private lateinit var profileViewModel: ProfileViewModel

  private val user =
      ProfileData(
          username = "",
          name = null,
          bio = null,
          links = 0,
          profilePicture = null,
          favoriteMusicGenres = listOf("Pop", "Rock", "Jazz", "Classic"))

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

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    composeTestRule.mainClock.autoAdvance = false // Control clock manually during the test
    MockitoAnnotations.openMocks(this)
    Dispatchers.setMain(testDispatcher)

    // spotifyApiRepository = SpotifyApiRepository(mockClient, mockSharedPreferences)
    // spotifyApiViewModel = SpotifyApiViewModel(mockApplication, spotifyApiRepository)
    // spotifyApiViewModel = mock()
    // spotifyApiRepository = mock()

    profileRepositoryFirestore = mock(ProfileRepositoryFirestore::class.java)
    profileViewModel =
        ProfileViewModel(repository = profileRepositoryFirestore, initialProfile = user)

    spotifyApiRepository = mock(SpotifyApiRepository::class.java)
    spotifyApiViewModel = SpotifyApiViewModel(mockApplication, spotifyApiRepository)

    navigationActions = mock(NavigationActions::class.java)
    `when`(navigationActions.currentRoute()).thenReturn(Route.PROFILE)

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
      ProfileScreen(
          profileViewModel,
          navigationActions,
          spotifyApiViewModel,
          viewModel(factory = MapUsersViewModel.Factory))
    }
    // Check if title is displayed
    /*composeTestRule
    .onNodeWithTag("titleUsername")
    .assertIsDisplayed()
    .assertTextContains(user.username)*/

    // Check if the icons are displayed
    composeTestRule
        .onNodeWithTag("profileScreenNotificationsButton")
        .assertExists()
        .assertContentDescriptionEquals("Notifications")
    composeTestRule
        .onNodeWithTag("profileScreenSettingsButton")
        .assertExists()
        .assertContentDescriptionEquals("Settings")

    // Check if the user's profile picture is displayed
    composeTestRule
        .onNodeWithTag("profilePicture")
        .assertExists()
        .assertContentDescriptionEquals("Profile Picture")

    // Check if the user's link's count is displayed
    composeTestRule
        .onNodeWithTag("linksCount")
        .assertExists()
        .assertTextContains("${user.links} Links")

    // Check if the edit button is displayed
    composeTestRule.onNodeWithTag("editProfileButtonContainer").assertExists()
    composeTestRule
        .onNodeWithTag("editProfileButton")
        .assertExists()
        .assertTextContains("Edit Profile")

    // Check if the user's name is displayed
    composeTestRule.onNodeWithTag("name").assertExists()

    // Check if the user's bio is displayed
    composeTestRule.onNodeWithTag("bio").assertExists()

    composeTestRule.onNodeWithTag("TOP SONGSTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("TOP ARTISTSTitle").assertIsDisplayed()
  }

  @Test
  fun buttonsAreClickable() {
    composeTestRule.setContent {
      ProfileScreen(
          profileViewModel,
          navigationActions,
          spotifyApiViewModel,
          viewModel(factory = MapUsersViewModel.Factory))
    }
    // Perform click action on the notifications button
    composeTestRule.onNodeWithTag("profileScreenNotificationsButton").performClick()

    // Perform click action on the settings button
    composeTestRule.onNodeWithTag("profileScreenSettingsButton").performClick()

    // Perform click action on the edit button
    composeTestRule.onNodeWithTag("editProfileButton").performClick()
  }

  @Test
  fun musicGenresTitleIsDisplayedWhenNotEmpty() {
    composeTestRule.setContent {
      ProfileScreen(
          profileViewModel,
          navigationActions,
          spotifyApiViewModel,
          viewModel(factory = MapUsersViewModel.Factory))
    }
    composeTestRule.onNodeWithTag("MUSIC GENRESTitle").assertIsDisplayed()
    // Check that music genres are displayed
    user.favoriteMusicGenres.forEach { genre ->
      composeTestRule.onNodeWithText(genre).assertExists()
    }
  }

  @Test
  fun musicGenresTitleIsNotDisplayedWhenEmpty() {
    val userEmpty =
        ProfileData(
            username = "",
            name = null,
            bio = null,
            links = 0,
            profilePicture = null,
            favoriteMusicGenres = emptyList())
    profileViewModel =
        ProfileViewModel(repository = profileRepositoryFirestore, initialProfile = userEmpty)
    composeTestRule.setContent {
      ProfileScreen(
          profileViewModel,
          navigationActions,
          spotifyApiViewModel,
          viewModel(factory = MapUsersViewModel.Factory))
    }

    composeTestRule.onNodeWithTag("MUSIC GENRESTitle").assertDoesNotExist()
  }

  @Test
  fun topSongsAreDisplayed() {
    val fakeSpotifyApiViewModel = FakeSpotifyApiViewModel()
    fakeSpotifyApiViewModel.setTopTracks(topSongs)

    composeTestRule.setContent {
      ProfileScreen(
          profileViewModel,
          navigationActions,
          fakeSpotifyApiViewModel,
          viewModel(factory = MapUsersViewModel.Factory))
    }
    composeTestRule.onNodeWithTag("TOP SONGSTitle").assertIsDisplayed()

    topSongs.forEach { song ->
      composeTestRule.onNodeWithText(song.name).assertExists()
      composeTestRule.onNodeWithText(song.artist).assertExists()
    }
    composeTestRule.onAllNodesWithTag("TrackCard").assertCountEquals(topSongs.size)
  }

  @Test
  fun topArtistsAreDisplayed() {
    val fakeSpotifyApiViewModel = FakeSpotifyApiViewModel()
    fakeSpotifyApiViewModel.setTopArtists(topArtists)

    composeTestRule.setContent {
      ProfileScreen(
          profileViewModel,
          navigationActions,
          fakeSpotifyApiViewModel,
          viewModel(factory = MapUsersViewModel.Factory))
    }

    composeTestRule.onNodeWithTag("TOP ARTISTSTitle").assertIsDisplayed()

    topArtists.forEach { artist -> composeTestRule.onNodeWithText(artist.name).assertExists() }

    composeTestRule.onAllNodesWithTag("ArtistCard").assertCountEquals(topArtists.size)
  }

  @Test
  fun editProfileButtonTriggersNavigation() {
    composeTestRule.setContent {
      ProfileScreen(
          profileViewModel,
          navigationActions,
          spotifyApiViewModel,
          viewModel(factory = MapUsersViewModel.Factory))
    }
    composeTestRule.onNodeWithTag("editProfileButton").performClick()
    verify(navigationActions).navigateTo(Screen.EDIT_PROFILE)
  }

  @Test
  fun settingsButtonTriggersNavigation() {
    composeTestRule.setContent {
      ProfileScreen(
          profileViewModel,
          navigationActions,
          spotifyApiViewModel,
          viewModel(factory = MapUsersViewModel.Factory))
    }
    composeTestRule.onNodeWithTag("profileScreenSettingsButton").performClick()
    verify(navigationActions).navigateTo(Screen.SETTINGS)
  }
}
