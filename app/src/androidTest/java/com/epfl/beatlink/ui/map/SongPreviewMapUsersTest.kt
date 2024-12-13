package com.epfl.beatlink.ui.map

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.epfl.beatlink.R
import com.epfl.beatlink.model.map.user.CurrentPlayingTrack
import com.epfl.beatlink.model.map.user.Location
import com.epfl.beatlink.model.map.user.MapUser
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel
import com.google.firebase.Timestamp
import java.time.Instant
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify

class SongPreviewMapUsersTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Mock lateinit var profileViewModel: ProfileViewModel
  @Mock lateinit var navigationActions: NavigationActions

  private lateinit var testUser: MapUser

  @Before
  fun setUp() {
    // Initialize mocks
    MockitoAnnotations.openMocks(this)

    testUser =
        MapUser(
            username = "leilahammmm",
            currentPlayingTrack =
                CurrentPlayingTrack(
                    trackId = "testTrackId",
                    songName = "Die With A Smile",
                    artistName = "Lady Gaga & Bruno Mars",
                    albumName = "Die With A Smile",
                    albumCover = R.drawable.cover_test1.toString()),
            location = Location(0.0, 0.0),
            lastUpdated = Timestamp.now())
  }

  @Test
  fun songPreviewMapUsers_displaysAlbumCover() {
    composeTestRule.setContent {
      SongPreviewMapUsers(mapUser = testUser, profileViewModel, navigationActions)
    }

    composeTestRule.onNodeWithTag("albumCover").assertIsDisplayed()
  }

  @Test
  fun songPreviewMapUsers_displaysCorrectSongName() {
    composeTestRule.setContent {
      SongPreviewMapUsers(mapUser = testUser, profileViewModel, navigationActions)
    }

    composeTestRule
        .onNodeWithTag("songName")
        .assertIsDisplayed()
        .assertTextContains("Die With A Smile")
  }

  @Test
  fun songPreviewMapUsers_displaysCorrectArtistName() {
    composeTestRule.setContent {
      SongPreviewMapUsers(mapUser = testUser, profileViewModel, navigationActions)
    }

    composeTestRule
        .onNodeWithTag("artistName")
        .assertIsDisplayed()
        .assertTextContains("Lady Gaga & Bruno Mars")
  }

  @Test
  fun songPreviewMapUsers_displaysCorrectAlbumName() {
    composeTestRule.setContent {
      SongPreviewMapUsers(mapUser = testUser, profileViewModel, navigationActions)
    }

    composeTestRule
        .onNodeWithTag("albumName")
        .assertIsDisplayed()
        .assertTextContains("Die With A Smile")
  }

  @Test
  fun songPreviewMapUsers_displaysUsernameWithUppercase() {
    composeTestRule.setContent {
      SongPreviewMapUsers(mapUser = testUser, profileViewModel, navigationActions)
    }

    composeTestRule
        .onNodeWithTag("username")
        .assertIsDisplayed()
        .assertTextContains("LISTENED BY @LEILAHAMMMM")
  }

  @Test
  fun songPreviewMapUsers_displaysTimeSinceLastUpdate() {
    composeTestRule.setContent {
      SongPreviewMapUsers(mapUser = testUser, profileViewModel, navigationActions)
    }

    composeTestRule
        .onNodeWithTag("timeSinceLastUpdate")
        .assertIsDisplayed()
        .assertTextContains("Just now")
  }

  @Test
  fun songPreviewMapUsers_displaysShadowBox() {
    composeTestRule.setContent {
      SongPreviewMapUsers(mapUser = testUser, profileViewModel, navigationActions)
    }

    composeTestRule.onNodeWithTag("shadowbox").assertIsDisplayed()
  }

  @Test
  fun songPreviewMapUsers_displaysGradientBrushBox() {
    composeTestRule.setContent {
      SongPreviewMapUsers(mapUser = testUser, profileViewModel, navigationActions)
    }

    composeTestRule.onNodeWithTag("brushbox").assertIsDisplayed()
  }

  @Test
  fun songPreviewMapUsers_layoutCorrectness() {
    composeTestRule.setContent {
      SongPreviewMapUsers(mapUser = testUser, profileViewModel, navigationActions)
    }

    // Check if elements are displayed within expected structure
    composeTestRule.onNodeWithTag("shadowbox").assertIsDisplayed()
    composeTestRule.onNodeWithTag("brushbox").assertIsDisplayed()
    composeTestRule.onNodeWithTag("SongPreviewMapUsers").assertIsDisplayed()
    composeTestRule.onNodeWithTag("albumCover").assertIsDisplayed()
    composeTestRule.onNodeWithTag("songName").assertIsDisplayed()
    composeTestRule.onNodeWithTag("artistName").assertIsDisplayed()
    composeTestRule.onNodeWithTag("albumName").assertIsDisplayed()
    composeTestRule.onNodeWithTag("username").assertIsDisplayed()
  }

  @Test
  fun testLaunchedEffectTriggersProfileViewModelAndNavigation() {
    // Arrange: Set initial values for selectedUserUserId and isIdFetched
    val selectedUserUserId = mutableStateOf("testUserId")
    val isIdFetched = mutableStateOf(false)

    composeTestRule.setContent {
      SongPreviewMapUsers(
          mapUser = testUser,
          profileViewModel = profileViewModel,
          navigationActions = navigationActions)
    }

    // Act: Change the state to trigger the LaunchedEffect
    isIdFetched.value = true

    // Assert: Verify that the view model methods and navigation were called
    verify(profileViewModel).selectSelectedUser(selectedUserUserId.value)
    verify(profileViewModel).fetchUserProfile()
    verify(navigationActions).navigateTo(Screen.OTHER_PROFILE)
  }

  @Test
  fun getTimeSinceLastUpdate_shouldReturnJustNow() {
    val now = Instant.now()
    val timestamp = Timestamp(now.epochSecond, now.nano)

    val result = getTimeSinceLastUpdate(timestamp)

    assertEquals("Just now", result)
  }

  @Test
  fun getTimeSinceLastUpdate_shouldReturnOneMinAgo() {
    val now = Instant.now()
    val oneMinuteAgo = now.minusSeconds(60)
    val timestamp = Timestamp(oneMinuteAgo.epochSecond, oneMinuteAgo.nano)

    val result = getTimeSinceLastUpdate(timestamp)

    assertEquals("1 min ago", result)
  }
}
