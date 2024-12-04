package com.epfl.beatlink.ui.player

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.epfl.beatlink.model.spotify.objects.SpotifyTrack
import com.epfl.beatlink.model.spotify.objects.State
import com.epfl.beatlink.repository.spotify.api.SpotifyApiRepository
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Route
import com.epfl.beatlink.viewmodel.map.user.MapUsersViewModel
import com.epfl.beatlink.viewmodel.spotify.api.SpotifyApiViewModel
import org.json.JSONObject
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.stub

@RunWith(AndroidJUnit4::class)
class PlayScreenTest {
  private lateinit var navigationActions: NavigationActions

  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  @Mock private lateinit var mockApplication: Application

  @Mock private lateinit var mockApiRepository: SpotifyApiRepository

  private lateinit var spotifyApiViewModel: SpotifyApiViewModel

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    navigationActions = mock(NavigationActions::class.java)
    `when`(navigationActions.currentRoute()).thenReturn(Route.PROFILE)
    spotifyApiViewModel = SpotifyApiViewModel(mockApplication, mockApiRepository)
    mockApiRepository.stub { onBlocking { get("me/player") } doReturn Result.success(JSONObject()) }
    // Launch the composable under test
    composeTestRule.setContent {
      PlayScreen(
          navigationActions, spotifyApiViewModel, viewModel(factory = MapUsersViewModel.Factory))
    }
  }

  @Test
  fun testPlayScreenDisplay() {
    composeTestRule.onNodeWithTag("topAppBar").assertIsDisplayed()

    composeTestRule
        .onNodeWithTag("topBarTitle")
        .assertIsDisplayed()
        .assertTextContains("Now Playing")

    composeTestRule.onNodeWithTag("backButton").assertIsDisplayed().assertHasClickAction()

    composeTestRule.onNodeWithTag("playScreenContent").assertIsDisplayed()
  }

  @Test
  fun testPlayScreenLowerBoxDisplaysQueueContent() {
    // Mock the queue with sample data
    spotifyApiViewModel.queue.apply {
      clear()
      addAll(
          listOf(
              SpotifyTrack("Track 1", "Artist 1", "id1", "CoverUrl1", 300, 80, State.PAUSE),
              SpotifyTrack("Track 2", "Artist 2", "id2", "CoverUrl2", 250, 90, State.PLAY)))
    }

    // Assert the tracks in the queue are displayed
    composeTestRule.onNodeWithTag("trackItem 0").assertIsDisplayed()
    composeTestRule.onNodeWithTag("trackItem 1").assertIsDisplayed()
  }

  @Test
  fun testPlayScreenLowerBoxDisplaysEmptyQueueMessage() {
    // Set an empty queue
    spotifyApiViewModel.queue = SnapshotStateList()

    // Verify the empty message is displayed
    composeTestRule.onNodeWithTag("emptyQueue").assertIsDisplayed()
  }
}
