package com.android.sample.ui.map

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun PlayerCurrentMusicItem(musique: String?) {
  if (musique != null) {
    Text(
        modifier =
            Modifier.fillMaxWidth().height(24.dp).padding(horizontal = 16.dp).testTag("playerText"),
        text = "listening : $musique",
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight(400),
        color = Color(0xFF5F2A83),
        textAlign = TextAlign.Center)
  } else {
    Text(
        modifier =
            Modifier.fillMaxWidth().height(24.dp).padding(horizontal = 16.dp).testTag("playerText"),
        text = "not listening yet",
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight(400),
        color = Color(0xFF5F2A83),
        textAlign = TextAlign.Center)
  }
}
