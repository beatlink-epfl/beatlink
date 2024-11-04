package com.epfl.beatlink.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.ui.theme.BorderColor
import com.epfl.beatlink.ui.theme.IconsGradientBrush
import com.epfl.beatlink.ui.theme.PrimaryGradientBrush
import com.epfl.beatlink.ui.theme.ShadowColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PageTopBarApp(
    pageTitle: String,
    pageTitleTag: String,
    icon1OnClick: () -> Unit,
    icon1: ImageVector,
    icon1Description: String,
    icon1Tag: String,
    icon2OnClick: () -> Unit,
    icon2: ImageVector,
    icon2Description: String,
    icon2Tag: String,
) {
  TopAppBar(
      title = { PageTitle(pageTitle, pageTitleTag) },
      actions = {
        // ICON 1
        CornerIcons(
            onClick = icon1OnClick,
            icon = icon1,
            contentDescription = icon1Description,
            modifier = Modifier.testTag(icon1Tag),
        )
        // ICON 2
        CornerIcons(
            onClick = icon2OnClick,
            icon = icon2,
            contentDescription = icon2Description,
            modifier = Modifier.testTag(icon2Tag))
      },
      modifier =
          Modifier.fillMaxWidth()
              .height(48.dp)
              .background(color = MaterialTheme.colorScheme.background)
              .drawWithCache {
                // Apply the bottom border or shadow in dark mode
                onDrawWithContent {
                  drawContent()
                  drawLine(
                      color = BorderColor,
                      strokeWidth = 1.dp.toPx(),
                      start = Offset(0f, size.height), // Bottom left
                      end = Offset(size.width, size.height) // Bottom right
                      )
                }
              }
              .shadow(elevation = 2.dp, spotColor = ShadowColor, ambientColor = ShadowColor),
  )
}

@Composable
fun PageTitle(mainTitle: String, mainTitleTag: String) {
  Box(modifier = Modifier.fillMaxHeight()) {
    Text(
        text = mainTitle,
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.headlineLarge,
        modifier = Modifier.align(Alignment.CenterStart).testTag(mainTitleTag),
    )
  }
}

@Composable
fun TitleWithArrow(title: String, onClick: () -> Unit) {
  Row(
      modifier =
          Modifier.padding(top = 16.dp).testTag(title + "TitleWithArrow").clickable { onClick() },
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

@Composable
fun CornerIcons(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    iconSize: Dp = 28.dp,
    gradientBrush: Brush = IconsGradientBrush
) {
  IconButton(onClick = onClick, modifier = modifier) {
    Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        modifier =
            Modifier.size(iconSize).graphicsLayer(alpha = 0.99f).drawWithCache {
              onDrawWithContent {
                drawContent()
                drawRect(gradientBrush, blendMode = BlendMode.SrcAtop)
              }
            })
  }
}
