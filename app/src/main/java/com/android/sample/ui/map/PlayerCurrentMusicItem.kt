package com.android.sample.ui.map

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.android.sample.model.spotify.objects.SpotifyTrack
import com.android.sample.ui.theme.PrimaryPurple

@Composable
fun PlayerCurrentMusicItem(musique: SpotifyTrack?) {
  if (musique != null) {
    Text(
        modifier =
            Modifier.fillMaxWidth()
                .height(24.dp)
                .padding(horizontal = 16.dp)
                .testTag("playerText music"),
        text = "listening : ${musique.name}",
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight(400),
        color = PrimaryPurple,
        textAlign = TextAlign.Center)
  } else {
    Text(
        modifier =
            Modifier.fillMaxWidth()
                .height(24.dp)
                .padding(horizontal = 16.dp)
                .testTag("playerText no music"),
        text = "not listening yet",
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight(400),
        color = PrimaryPurple,
        textAlign = TextAlign.Center)
  }
}
