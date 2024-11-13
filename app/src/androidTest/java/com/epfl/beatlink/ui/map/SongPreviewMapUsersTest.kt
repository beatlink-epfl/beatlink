package com.epfl.beatlink.ui.map

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.epfl.beatlink.R
import com.epfl.beatlink.model.CurrentPlayingTrack
import com.epfl.beatlink.model.Location
import com.epfl.beatlink.model.MapUser
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SongPreviewMapUsersTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var testUser: MapUser

  @Before
  fun setUp() {
    testUser =
        MapUser(
            username = "leilahammmm",
            currentPlayingTrack =
                CurrentPlayingTrack(
                    songName = "Die With A Smile",
                    artistName = "Lady Gaga & Bruno Mars",
                    albumName = "Die With A Smile",
                    albumCover = R.drawable.cover_test1.toString()),
            location = Location(0.0, 0.0))
  }

  @Test
  fun songPreviewMapUsers_displaysAlbumCover() {
    composeTestRule.setContent { SongPreviewMapUsers(mapUser = testUser) }

    composeTestRule.onNodeWithTag("albumCover").assertIsDisplayed()
  }

  @Test
  fun songPreviewMapUsers_displaysCorrectSongName() {
    composeTestRule.setContent { SongPreviewMapUsers(mapUser = testUser) }

    composeTestRule
        .onNodeWithTag("songName")
        .assertIsDisplayed()
        .assertTextContains("Die With A Smile")
  }

  @Test
  fun songPreviewMapUsers_displaysCorrectArtistName() {
    composeTestRule.setContent { SongPreviewMapUsers(mapUser = testUser) }

    composeTestRule
        .onNodeWithTag("artistName")
        .assertIsDisplayed()
        .assertTextContains("Lady Gaga & Bruno Mars")
  }

  @Test
  fun songPreviewMapUsers_displaysCorrectAlbumName() {
    composeTestRule.setContent { SongPreviewMapUsers(mapUser = testUser) }

    composeTestRule
        .onNodeWithTag("albumName")
        .assertIsDisplayed()
        .assertTextContains("Die With A Smile")
  }

  @Test
  fun songPreviewMapUsers_displaysUsernameWithUppercase() {
    composeTestRule.setContent { SongPreviewMapUsers(mapUser = testUser) }

    composeTestRule
        .onNodeWithTag("username")
        .assertIsDisplayed()
        .assertTextContains("LISTENED BY @LEILAHAMMMM")
  }

  @Test
  fun songPreviewMapUsers_displaysShadowBox() {
    composeTestRule.setContent { SongPreviewMapUsers(mapUser = testUser) }

    composeTestRule.onNodeWithTag("shadowbox").assertIsDisplayed()
  }

  @Test
  fun songPreviewMapUsers_displaysGradientBrushBox() {
    composeTestRule.setContent { SongPreviewMapUsers(mapUser = testUser) }

    composeTestRule.onNodeWithTag("brushbox").assertIsDisplayed()
  }

  @Test
  fun songPreviewMapUsers_layoutCorrectness() {
    composeTestRule.setContent { SongPreviewMapUsers(mapUser = testUser) }

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
}
