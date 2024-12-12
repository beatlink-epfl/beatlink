package com.epfl.beatlink.ui.components.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.ui.theme.PrimaryRed
import com.epfl.beatlink.ui.theme.classical
import com.epfl.beatlink.ui.theme.country
import com.epfl.beatlink.ui.theme.dnb
import com.epfl.beatlink.ui.theme.edm
import com.epfl.beatlink.ui.theme.electro
import com.epfl.beatlink.ui.theme.hiphop
import com.epfl.beatlink.ui.theme.house
import com.epfl.beatlink.ui.theme.jazz
import com.epfl.beatlink.ui.theme.jpop
import com.epfl.beatlink.ui.theme.kpop
import com.epfl.beatlink.ui.theme.lofi
import com.epfl.beatlink.ui.theme.metal
import com.epfl.beatlink.ui.theme.pop
import com.epfl.beatlink.ui.theme.primaryWhite
import com.epfl.beatlink.ui.theme.punk
import com.epfl.beatlink.ui.theme.randb
import com.epfl.beatlink.ui.theme.rap
import com.epfl.beatlink.ui.theme.reggae
import com.epfl.beatlink.ui.theme.reggaeton
import com.epfl.beatlink.ui.theme.rock
import com.epfl.beatlink.ui.theme.soul
import com.epfl.beatlink.ui.theme.techno

@Composable
fun MusicGenreCard(genre: String, brush: Brush, onClick: () -> Unit = {}) {
  Box(
      modifier =
          Modifier.height(90.dp)
              .width(82.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(brush)
              .clickable { onClick() }) {
        Text(
            text = genre,
            modifier =
                Modifier.align(Alignment.BottomStart).padding(8.dp).testTag(genre + "MusicCard"),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primaryWhite)
      }
}

val genreGradients =
    mapOf(
        "Pop" to Brush.verticalGradient(colors = listOf(PrimaryRed, pop)), // Vibrant pink
        "Rap" to Brush.verticalGradient(colors = listOf(PrimaryRed, rap)), // Predefined rap color
        "R&B" to Brush.verticalGradient(colors = listOf(PrimaryRed, randb)), // Deep purple
        "Rock" to
            Brush.verticalGradient(colors = listOf(PrimaryRed, rock)), // Predefined rock color
        "Country" to Brush.verticalGradient(colors = listOf(PrimaryRed, country)), // Warm brown
        "Punk" to Brush.verticalGradient(colors = listOf(PrimaryRed, punk)), // Bright magenta
        "Jazz" to
            Brush.verticalGradient(colors = listOf(PrimaryRed, jazz)), // Predefined jazz color
        "Electro" to
            Brush.verticalGradient(
                colors = listOf(PrimaryRed, electro)), // Predefined electro color
        "Classical" to
            Brush.verticalGradient(
                colors = listOf(PrimaryRed, classical)), // Predefined classical color
        "Hip Hop" to Brush.verticalGradient(colors = listOf(PrimaryRed, hiphop)), // Gold
        "EDM" to Brush.verticalGradient(colors = listOf(PrimaryRed, edm)), // Bright cyan
        "Reggae" to Brush.verticalGradient(colors = listOf(PrimaryRed, reggae)), // Green
        "Reggaeton" to Brush.verticalGradient(colors = listOf(PrimaryRed, reggaeton)), // Orange
        "Metal" to
            Brush.verticalGradient(colors = listOf(PrimaryRed, metal)), // Predefined metal color
        "K-Pop" to Brush.verticalGradient(colors = listOf(PrimaryRed, kpop)), // Bright pink
        "J-Pop" to Brush.verticalGradient(colors = listOf(PrimaryRed, jpop)), // Sky blue
        "House" to Brush.verticalGradient(colors = listOf(PrimaryRed, house)), // Bright green
        "Techno" to Brush.verticalGradient(colors = listOf(PrimaryRed, techno)), // Dark magenta
        "Lo-Fi" to Brush.verticalGradient(colors = listOf(PrimaryRed, lofi)), // Muted green
        "Soul" to
            Brush.verticalGradient(
                colors = listOf(PrimaryRed, soul)), // Coral for warmth and emotion
        "Drum and Bass" to
            Brush.verticalGradient(
                colors = listOf(PrimaryRed, dnb)) // Dark grey for intensity and energy
        )
