package com.epfl.beatlink.ui.map

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.epfl.beatlink.R
import com.epfl.beatlink.model.map.user.CurrentPlayingTrack
import com.epfl.beatlink.model.map.user.Location
import com.epfl.beatlink.model.map.user.MapUser
import com.epfl.beatlink.repository.spotify.api.SpotifyApiRepository
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel
import com.epfl.beatlink.viewmodel.spotify.api.SpotifyApiViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations

@RunWith(AndroidJUnit4::class)
class GoogleMapViewTest {

  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  @Mock lateinit var profileViewModel: ProfileViewModel
  @Mock lateinit var navigationActions: NavigationActions
  @Mock private lateinit var mockApplication: Application
  @Mock private lateinit var mockApiRepository: SpotifyApiRepository
  private lateinit var spotifyApiViewModel: SpotifyApiViewModel

  private lateinit var testUser: MapUser

  @Before
  fun setUp() {
    // Initialize mocks
    MockitoAnnotations.openMocks(this)

    spotifyApiViewModel = SpotifyApiViewModel(mockApplication, mockApiRepository)

    // Set up a test user with known location and song information
    testUser =
        MapUser(
            username = "testUser",
            currentPlayingTrack =
                CurrentPlayingTrack(
                    trackId = "testTrackId",
                    songName = "Test Song",
                    artistName = "Test Artist",
                    albumName = "Test Album",
                    albumCover = R.drawable.cover_test1.toString()),
            location = Location(latitude = 37.7749, longitude = -122.4194), // San Francisco
            lastUpdated = Timestamp.now())
  }

  // Test to verify the map is displayed with location permitted
  @Test
  fun googleMapView_locationPermitted_displaysMapAndMarker() {
    val currentPosition: MutableState<LatLng?> =
        mutableStateOf(LatLng(37.7749, -122.4194)) // Sample lat/lng for San Francisco
    val moveToCurrentLocation = mutableStateOf(CameraAction.NO_ACTION)
    val mapUsers = listOf(testUser)

    composeTestRule.setContent {
      GoogleMapView(
          currentPosition = currentPosition,
          moveToCurrentLocation = moveToCurrentLocation,
          modifier = Modifier.testTag("MapView"),
          mapUsers = mapUsers,
          profileViewModel = profileViewModel,
          spotifyApiViewModel = spotifyApiViewModel,
          navigationActions = navigationActions,
          locationPermitted = true)
    }

    // Verify that the map is displayed
    composeTestRule.onNodeWithTag("MapView").assertIsDisplayed()

    // Verify that the current location marker is displayed
    composeTestRule.onNodeWithTag("currentLocationButton").assertIsDisplayed()

    // Simulate clicking the current location button
    composeTestRule.onNodeWithTag("currentLocationButton").performClick()
  }

  // Test to verify the map is displayed with location not permitted
  @Test
  fun googleMapView_locationNotPermitted_displaysMapWithoutMarker() {
    val currentPosition: MutableState<LatLng?> =
        mutableStateOf(LatLng(37.7749, -122.4194)) // Sample lat/lng for San Francisco
    val moveToCurrentLocation = mutableStateOf(CameraAction.NO_ACTION)
    val mapUsers = listOf(testUser)

    composeTestRule.setContent {
      GoogleMapView(
          currentPosition = currentPosition,
          moveToCurrentLocation = moveToCurrentLocation,
          modifier = Modifier.testTag("MapView"),
          mapUsers = mapUsers,
          profileViewModel = profileViewModel,
          spotifyApiViewModel = spotifyApiViewModel,
          navigationActions = navigationActions,
          locationPermitted = false)
    }

    // Verify that the map is displayed
    composeTestRule.onNodeWithTag("MapView").assertIsDisplayed()

    // Verify that the current location button is displayed
    composeTestRule.onNodeWithTag("currentLocationButton").assertIsDisplayed()

    // Simulate clicking the current location button
    composeTestRule.onNodeWithTag("currentLocationButton").performClick()
  }

  // Test to verify the map camera moves when instructed
  @Test
  fun googleMapView_cameraMovesOnAction() {
    val currentPosition: MutableState<LatLng?> =
        mutableStateOf(LatLng(37.7749, -122.4194)) // Sample lat/lng for San Francisco
    val moveToCurrentLocation = mutableStateOf(CameraAction.MOVE)
    val mapUsers = listOf(testUser)

    composeTestRule.setContent {
      GoogleMapView(
          currentPosition = currentPosition,
          moveToCurrentLocation = moveToCurrentLocation,
          modifier = Modifier.testTag("MapView"),
          mapUsers = mapUsers,
          profileViewModel = profileViewModel,
          spotifyApiViewModel = spotifyApiViewModel,
          navigationActions = navigationActions,
          locationPermitted = true)
    }

    // Verify that the map is displayed
    composeTestRule.onNodeWithTag("MapView").assertIsDisplayed()

    // Wait for the camera to move
    composeTestRule.waitForIdle()

    // Verify that the current location button is displayed after the camera moves
    composeTestRule.onNodeWithTag("currentLocationButton").assertIsDisplayed()
  }

  @Test
  fun googleMapView_clickOnUserMarker_displaysSongPreviewMapUsers() {
    val currentPosition: MutableState<LatLng?> = mutableStateOf(LatLng(37.7749, -122.4194))
    val moveToCurrentLocation = mutableStateOf(CameraAction.NO_ACTION)
    val selectedUser = mutableStateOf<MapUser?>(null) // Controlled state for test
    val mapUsers = listOf(testUser)

    composeTestRule.setContent {
      GoogleMapView(
          currentPosition = currentPosition,
          moveToCurrentLocation = moveToCurrentLocation,
          modifier = Modifier.testTag("MapView"),
          locationPermitted = true,
          mapUsers = mapUsers,
          profileViewModel = profileViewModel,
          spotifyApiViewModel = spotifyApiViewModel,
          navigationActions = navigationActions,
          selectedUser = selectedUser // Pass the test-controlled selectedUser state
          )
    }

    // Simulate selecting the test user
    composeTestRule.runOnUiThread { selectedUser.value = testUser }

    // Verify that SongPreviewMapUsers displays the correct song information
    composeTestRule.onNodeWithTag("SongPreviewMap").assertIsDisplayed()
    composeTestRule.onNodeWithTag("songName").assertIsDisplayed()
    composeTestRule.onNodeWithText("Test Song").assertIsDisplayed()
    composeTestRule.onNodeWithText("Test Artist").assertIsDisplayed()
    composeTestRule.onNodeWithText("Test Album").assertIsDisplayed()
  }

  // Test to verify that clicking outside SongPreviewMapUsers dismisses it
  @Test
  fun googleMapView_clickOutsideSongPreviewMapUsers_dismissesIt() {
    val currentPosition: MutableState<LatLng?> = mutableStateOf(LatLng(37.7749, -122.4194))
    val moveToCurrentLocation = mutableStateOf(CameraAction.NO_ACTION)
    val selectedUser = mutableStateOf<MapUser?>(null) // Controlled state for test
    val mapUsers = listOf(testUser)

    composeTestRule.setContent {
      GoogleMapView(
          currentPosition = currentPosition,
          moveToCurrentLocation = moveToCurrentLocation,
          modifier = Modifier.testTag("MapView"),
          locationPermitted = true,
          mapUsers = mapUsers,
          profileViewModel = profileViewModel,
          spotifyApiViewModel = spotifyApiViewModel,
          navigationActions = navigationActions,
          selectedUser = selectedUser // Pass controlled selectedUser state
          )
    }

    // Simulate selecting the test user to display SongPreviewMapUsers
    composeTestRule.runOnUiThread { selectedUser.value = testUser }

    // Verify that the SongPreviewMapUsers composable is displayed
    composeTestRule.onNodeWithTag("SongPreviewMap").assertIsDisplayed()
    composeTestRule.onNodeWithTag("songName").assertIsDisplayed()

    // Click outside to dismiss SongPreviewMapUsers by clicking on the "clickbox"
    composeTestRule.onNodeWithTag("clickbox").performClick()

    // Verify that the SongPreviewMapUsers composable is no longer displayed
    composeTestRule.onNodeWithTag("SongPreviewMap").assertDoesNotExist()
  }
}
