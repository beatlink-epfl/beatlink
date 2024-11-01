package com.android.sample.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.ui.theme.PrimaryGradientBrush
import com.android.sample.ui.theme.PrimaryRed

@Composable
fun GradientTitle(title: String, button: Boolean, onClick: () -> Unit) {
  Row(
      modifier =
          Modifier.testTag(title + "Title")
              .height(20.dp)
              .clickable { onClick() }
              .padding(horizontal = 16.dp),
      verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = title,
            style =
                TextStyle(
                    fontSize = 20.sp,
                    lineHeight = 20.sp,
                    fontFamily = FontFamily(Font(R.font.roboto_bold)),
                    fontWeight = FontWeight(700),
                    color = PrimaryRed,
                    letterSpacing = 0.2.sp,
                ),
            modifier =
                Modifier.graphicsLayer(alpha = 0.99f).drawWithCache {
                  onDrawWithContent {
                    drawContent()
                    drawRect(PrimaryGradientBrush, blendMode = BlendMode.SrcAtop)
                  }
                })

        if (button) {
          Spacer(modifier = Modifier.width(5.dp)) // Spacing between text and arrow
          Icon(
              imageVector = Icons.Outlined.KeyboardArrowRight,
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
