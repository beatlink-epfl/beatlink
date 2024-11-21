package com.epfl.beatlink.ui.components.profile

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.ui.theme.PrimaryGradientBrush
import com.epfl.beatlink.ui.theme.PrimaryRed
import com.epfl.beatlink.ui.theme.classical
import com.epfl.beatlink.ui.theme.electro
import com.epfl.beatlink.ui.theme.jazz
import com.epfl.beatlink.ui.theme.primaryWhite
import com.epfl.beatlink.ui.theme.rap
import com.epfl.beatlink.ui.theme.rock

@Composable
fun MusicGenreCard(genre: String, brush: Brush) {
  Box(
      modifier =
          Modifier.height(100.dp).width(80.dp).clip(RoundedCornerShape(10.dp)).background(brush)) {
        Text(
            text = genre,
            modifier = Modifier.align(Alignment.BottomStart).padding(8.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primaryWhite)
      }
}

val genreGradients =
    mapOf(
        "Pop" to PrimaryGradientBrush,
        "Rap" to Brush.verticalGradient(colors = listOf(PrimaryRed, rap)),
        "Electro" to Brush.verticalGradient(colors = listOf(PrimaryRed, electro)),
        "Rock" to Brush.verticalGradient(colors = listOf(PrimaryRed, rock)),
        "Jazz" to Brush.verticalGradient(colors = listOf(PrimaryRed, jazz)),
        "Classic" to Brush.verticalGradient(colors = listOf(PrimaryRed, classical)))
