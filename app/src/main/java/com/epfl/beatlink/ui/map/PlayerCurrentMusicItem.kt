package com.epfl.beatlink.ui.map

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.ui.theme.TypographySongs

@Composable
fun PlayerCurrentMusicItem(musique: String?) {
  if (musique != null) {
    Text(
        modifier =
            Modifier.fillMaxWidth()
                .height(24.dp)
                .padding(horizontal = 16.dp)
                .testTag("playerText music"),
        text = "listening : $musique",
        style = TypographySongs.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        textAlign = TextAlign.Center)
  } else {
    Text(
        modifier =
            Modifier.fillMaxWidth()
                .height(24.dp)
                .padding(horizontal = 16.dp)
                .testTag("playerText no music"),
        text = "not listening yet",
        style = TypographySongs.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        textAlign = TextAlign.Center)
  }
}
