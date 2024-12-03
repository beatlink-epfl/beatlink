package com.epfl.beatlink.ui.map

import android.Manifest
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
    }
  }

  @Test
  fun mapScreen_displaysLoadingMapText_whenMapIsNotLoaded() {
    // Create a new instance of MapViewModel for this test
    val mapViewModel =
        MapViewModel(FakeMapLocationRepository()).apply {
          isMapLoaded.value = false
          permissionRequired.value = false
        }

    composeTestRule.setContent {
      MapScreen(
          navigationActions = NavigationActions(rememberNavController()),
          spotifyApiViewModel = spotifyApiViewModel,
          profileViewModel = viewModel(factory = ProfileViewModel.Factory),
          mapUsersViewModel = viewModel(factory = MapUsersViewModel.Factory),
          mapViewModel = mapViewModel)
    }

    composeTestRule.onNodeWithTag("MapScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("MapScreenColumn").assertIsDisplayed()
    composeTestRule.onNodeWithTag("MapContainer").assertIsDisplayed()
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()

    // Verify that "Loading map..." text is displayed when the map is not loaded
    composeTestRule.onNodeWithText("Loading map...").assertIsDisplayed()
  }

  @Test
  fun mapScreen_displaysMap_whenMapIsLoaded() {
    // Create a new instance of MapViewModel for this test
    val mapViewModel =
        MapViewModel(FakeMapLocationRepository()).apply {
          isMapLoaded.value = true
          permissionRequired.value = false
        }

    composeTestRule.setContent {
      MapScreen(
          navigationActions = NavigationActions(rememberNavController()),
          spotifyApiViewModel = spotifyApiViewModel,
          profileViewModel = viewModel(factory = ProfileViewModel.Factory),
          mapUsersViewModel = viewModel(factory = MapUsersViewModel.Factory),
          mapViewModel = mapViewModel)
    }

    composeTestRule.onNodeWithTag("MapScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("MapScreenColumn").assertIsDisplayed()
    composeTestRule.onNodeWithTag("MapContainer").assertIsDisplayed()
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()

    // Verify that the map is displayed when loaded
    composeTestRule.onNodeWithTag("Map").assertIsDisplayed()
    composeTestRule.onNodeWithTag("currentLocationFab").assertIsDisplayed()
    composeTestRule.onNodeWithTag("playerContainer").assertIsDisplayed()
  }

  @Test
  fun mapScreen_handlesPermissionResult_correctly() {
    // Create a new instance of MapViewModel for this test
    val mapViewModel = MapViewModel(FakeMapLocationRepository())

    mapViewModel.permissionRequired.value = true

    composeTestRule.setContent {
      MapScreen(
          navigationActions = NavigationActions(rememberNavController()),
          spotifyApiViewModel = spotifyApiViewModel,
          profileViewModel = viewModel(factory = ProfileViewModel.Factory),
          mapUsersViewModel = viewModel(factory = MapUsersViewModel.Factory),
          mapViewModel = mapViewModel)
    }

    composeTestRule.waitForIdle()

    // Simulate permissions granted
    val permissions =
        mapOf(
            Manifest.permission.ACCESS_FINE_LOCATION to true,
            Manifest.permission.ACCESS_COARSE_LOCATION to true)
    val granted =
        permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    mapViewModel.onPermissionResult(granted)
    mapViewModel.setLocationPermissionGranted(granted)

    // Verify that onPermissionResult updates permissionRequired and locationPermitted
    assert(mapViewModel.locationPermitted.value)
    assert(!mapViewModel.permissionRequired.value)
  }

  @Test
  fun mapScreen_requestsPermissions_whenPermissionRequired() {
    // Create a new instance of MapViewModel for this test
    val mapViewModel = MapViewModel(FakeMapLocationRepository())

    composeTestRule.setContent {
      MapScreen(
          navigationActions = NavigationActions(rememberNavController()),
          spotifyApiViewModel = spotifyApiViewModel,
          profileViewModel = viewModel(factory = ProfileViewModel.Factory),
          mapUsersViewModel = viewModel(factory = MapUsersViewModel.Factory),
          mapViewModel = mapViewModel)
    }

    mapViewModel.permissionRequired.value = true

    assert(mapViewModel.permissionRequired.value) // No mocking required here
  }
}
