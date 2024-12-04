package com.epfl.beatlink.ui.components

import android.graphics.Bitmap
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.epfl.beatlink.ui.components.library.PlaylistCover
import org.junit.Rule
import org.junit.Test

@Suppress("UNCHECKED_CAST")
class PlaylistCoverTest {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun playlistCoverShouldDisplayTheCoverImageWhenProvided() {
    val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    val coverImage = mutableStateOf(bitmap) as MutableState<Bitmap?>

    composeTestRule.setContent { PlaylistCover(coverImage = coverImage) }

    // Verify that the Image is displayed
    composeTestRule.onNodeWithTag("playlistCover").assertExists()
    composeTestRule.onNode(hasTestTag("emptyCoverText")).assertDoesNotExist()
  }
}
