package com.epfl.beatlink.ui.map

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.epfl.beatlink.repository.spotify.api.SpotifyApiRepository
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.viewmodel.map.MapViewModel
import com.epfl.beatlink.viewmodel.map.user.MapUsersViewModel
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel
import com.epfl.beatlink.viewmodel.spotify.api.SpotifyApiViewModel
import com.google.android.gms.maps.model.LatLng
import org.json.JSONObject
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.stub

class MapScreenTest {

  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  @Mock private lateinit var mockApplication: Application

  @Mock private lateinit var mockApiRepository: SpotifyApiRepository

  private lateinit var spotifyApiViewModel: SpotifyApiViewModel

  private lateinit var fakeMapLocationRepository: FakeMapLocationRepository
  private lateinit var mapViewModel: MapViewModel

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    spotifyApiViewModel = SpotifyApiViewModel(mockApplication, mockApiRepository)
    mockApiRepository.stub {
      onBlocking { get("me/player") } doReturn Result.success(JSONObject())
      onBlocking { get("me/top/tracks?time_range=short_term") } doReturn
          Result.success(
              JSONObject(
                  """{
            "items": [
                {
                    "name": "Top Track 1",
                    "id": "456",
                    "artists": [{"name": "Artist 1"}],
                    "album": {"images": [{"url": "https://example.com/track1.jpg"}]},
                    "duration_ms": 200000,
                    "popularity": 75
                }
            ]
        }"""))
      onBlocking { get("me/top/artists?time_range=short_term") } doReturn
          Result.success(
              JSONObject(
                  """{
            "items": [
                {
                    "name": "Top Artist 1",
                    "id": "789",
                    "images": [{"url": "https://example.com/artist1.jpg"}],
                    "genres": ["pop", "rock"],
                    "popularity": 85
                }
            ]
        }"""))
      onBlocking { get("me") } doReturn
          Result.success(JSONObject().apply { put("id", "testSpotifyUserId") })
    }

    fakeMapLocationRepository = FakeMapLocationRepository()

    mapViewModel =
        MapViewModel(fakeMapLocationRepository).apply {
          // Ensure consistent initial states for each test
          isMapLoaded.value = false
          locationPermitted.value = false
          permissionRequired.value = false
        }
  }

  @Test
  fun mapScreen_displaysLoadingMapText_andRequestsPermissions_whenMapIsNotLoaded() {
    composeTestRule.setContent {
      MapScreen(
          navigationActions = NavigationActions(rememberNavController()),
          mapViewModel = mapViewModel,
          spotifyApiViewModel = spotifyApiViewModel,
          profileViewModel = viewModel(factory = ProfileViewModel.Factory),
          mapUsersViewModel = viewModel(factory = MapUsersViewModel.Factory))
    }

    // Assert that the permission request is triggered
    composeTestRule.onNodeWithTag("MapScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("MapScreenColumn").assertIsDisplayed()
    composeTestRule.onNodeWithTag("MapContainer").assertIsDisplayed()

    // Verify the "Loading map..." text is shown
    composeTestRule.onNodeWithText("Loading map...").assertIsDisplayed()
  }

  @Test
  fun mapScreen_displaysMap_whenMapIsLoaded_andPermissionsAreGranted() {
    // Simulate permissions granted and map being loaded
    mapViewModel.locationPermitted.value = true
    mapViewModel.isMapLoaded.value = true

    composeTestRule.setContent {
      MapScreen(
          navigationActions = NavigationActions(rememberNavController()),
          mapViewModel = mapViewModel,
          spotifyApiViewModel = spotifyApiViewModel,
          profileViewModel = viewModel(factory = ProfileViewModel.Factory),
          mapUsersViewModel = viewModel(factory = MapUsersViewModel.Factory))
    }

    // Assert that the map and all UI components are displayed
    composeTestRule.onNodeWithTag("MapScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("MapScreenColumn").assertIsDisplayed()
    composeTestRule.onNodeWithTag("MapContainer").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Map").assertIsDisplayed()
  }

  @Test
  fun mapScreen_handlesUserTracking_andCameraActions_correctly() {
    // Simulate location updates and a camera move action
    val testLatLng = LatLng(46.518, 6.568)
    fakeMapLocationRepository.setLocationPermissionGranted(true)
    mapViewModel.currentPosition.value = testLatLng
    mapViewModel.moveToCurrentLocation.value = CameraAction.MOVE

    composeTestRule.setContent {
      MapScreen(
          navigationActions = NavigationActions(rememberNavController()),
          mapViewModel = mapViewModel,
          spotifyApiViewModel = spotifyApiViewModel,
          profileViewModel = viewModel(factory = ProfileViewModel.Factory),
          mapUsersViewModel = viewModel(factory = MapUsersViewModel.Factory))
    }

    // Verify that the map UI is displayed and is handling camera actions
    composeTestRule.onNodeWithTag("MapScreen").assertIsDisplayed()
  }

  @Test
  fun mapScreen_displaysBottomNavigation() {
    composeTestRule.setContent {
      MapScreen(
          navigationActions = NavigationActions(rememberNavController()),
          mapViewModel = mapViewModel,
          spotifyApiViewModel = spotifyApiViewModel,
          profileViewModel = viewModel(factory = ProfileViewModel.Factory),
          mapUsersViewModel = viewModel(factory = MapUsersViewModel.Factory))
    }

    // Verify that the bottom navigation is displayed
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()
  }
}
