package com.epfl.beatlink.ui.components.library

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.ui.theme.PrimaryGray
import com.epfl.beatlink.ui.theme.SecondaryGray

/**
 * Composable that displays the cover image of a playlist.
 *
 * @param coverImage The cover image of the playlist.
 * @param modifier The modifier for the composable.
 * @param isClickable Whether the cover image is clickable.
 * @param onClick The action to perform when the cover image is clicked.
 */
@Composable
fun PlaylistCover(
    coverImage: MutableState<Bitmap?>,
    size: Dp = 80.dp,
    isClickable: Boolean = false,
    onClick: (() -> Unit)? = null
) {
  // Playlist Cover
  GrayBox(
      modifier =
          Modifier.then(
              if (isClickable && onClick != null) Modifier.clickable { onClick.invoke() }
              else Modifier),
      size = size) {
        coverImage.value?.let { bitmap ->
          Image(
              bitmap = bitmap.asImageBitmap(),
              contentDescription = "Cover Picture",
              contentScale = ContentScale.Crop)
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
}

@Composable
fun GrayBox(modifier: Modifier = Modifier, size: Dp = 80.dp, content: @Composable () -> Unit = {}) {
  Box(
      modifier =
          modifier
              .padding(vertical = 8.dp, horizontal = 12.dp)
              .background(color = SecondaryGray, shape = RoundedCornerShape(size = 10.dp))
              .clip(RoundedCornerShape(size = 10.dp))
              .size(size)
              .testTag("playlistCover"),
      contentAlignment = Alignment.Center) {
        content()
      }
}
