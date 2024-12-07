package com.epfl.beatlink.ui.search.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.ui.theme.PrimaryGradientBrush

@Composable
fun GradientTitle(title: String, arrowNeed: Boolean, onClick: () -> Unit) {
  Row(
      modifier =
          Modifier.testTag(title + "Title").clickable { onClick() }.padding(horizontal = 16.dp),
      verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge,
            modifier =
                Modifier.graphicsLayer(alpha = 0.99f).drawWithCache {
                  onDrawWithContent {
                    drawContent()
                    drawRect(PrimaryGradientBrush, blendMode = BlendMode.SrcAtop)
                  }
                })

        if (arrowNeed) {
          Spacer(modifier = Modifier.width(6.dp)) // Spacing between text and arrow
          Icon(
              imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
              contentDescription = "arrow right",
              modifier =
                  Modifier.size(24.dp).graphicsLayer(alpha = 0.99f).drawWithCache {
                    onDrawWithContent {
                      drawContent()
                      drawRect(PrimaryGradientBrush, blendMode = BlendMode.SrcAtop)
                    }
                  })
        }
      }
}
