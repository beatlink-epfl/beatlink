package com.epfl.beatlink.ui.components.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.ui.theme.PrimaryGray
import com.epfl.beatlink.ui.theme.SecondaryGray

@Composable
fun PlaylistCover(coverImage: String) {
  // Playlist Cover
  Box(
      modifier =
          Modifier.background(color = SecondaryGray, shape = RoundedCornerShape(size = 10.dp))
              .clickable(onClick = { /* Opens option gallery or camera */})
              .width(100.dp)
              .height(100.dp)
              .testTag("playlistCover"),
      contentAlignment = Alignment.Center) {
        if (coverImage == "something") {
          // Show the selected cover image
          // TODO

        } else {
          // Placeholder content if no image is selected
          Text(
              text = "Add \n Playlist Cover",
              style = MaterialTheme.typography.bodyLarge,
              color = PrimaryGray,
              textAlign = TextAlign.Center,
              modifier = Modifier.testTag("emptyCoverText"))
        }
      }
}
