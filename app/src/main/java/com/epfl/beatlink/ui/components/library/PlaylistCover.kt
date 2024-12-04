package com.epfl.beatlink.ui.components.library

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import com.epfl.beatlink.ui.theme.PrimaryGray

@Composable
fun PlaylistCover(coverImage: MutableState<Bitmap?>, modifier: Modifier = Modifier) {
  // Playlist Cover
  coverImage.value?.let { bitmap ->
    Image(
        bitmap = bitmap.asImageBitmap(), contentDescription = "Cover Picture", modifier = modifier)
  }
      ?: run {
        // Placeholder content if no image is selected
        Text(
            text = "Add \n Playlist Cover",
            style = MaterialTheme.typography.bodyLarge,
            color = PrimaryGray,
            textAlign = TextAlign.Center,
            modifier = Modifier.testTag("emptyCoverText"))
      }
}
