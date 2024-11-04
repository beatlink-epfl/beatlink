package com.epfl.beatlink.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.model.profile.ProfileData
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.collabAdd
import com.epfl.beatlink.ui.theme.BorderColor
import com.epfl.beatlink.ui.theme.IconsGradientBrush
import com.epfl.beatlink.ui.theme.PrimaryGradientBrush
import com.epfl.beatlink.ui.theme.PrimaryGray
import com.epfl.beatlink.ui.theme.ShadowColor
import com.epfl.beatlink.ui.theme.lightThemeBackground

@SuppressLint("ModifierFactoryUnreferencedReceiver")
@Composable
fun Modifier.topAppBarModifier() =
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
        .shadow(elevation = 2.dp, spotColor = ShadowColor, ambientColor = ShadowColor)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenTopAppBar(
    title: String,
    titleTag: String,
    navigationActions: NavigationActions,
    actionButtons: List<@Composable () -> Unit> = emptyList()
) {
  TopAppBar(
      title = { PageTitle(title, titleTag) },
      actions = { actionButtons.forEach { actionButton -> actionButton() } },
      navigationIcon = {
        CornerIcons(
            onClick = { navigationActions.goBack() },
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Go back",
            modifier = Modifier.testTag("goBackButton"),
            iconSize = 30.dp)
      },
      modifier = Modifier.topAppBarModifier())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PageTopAppBar(
    pageTitle: String,
    pageTitleTag: String,
    actionButtons: List<@Composable () -> Unit> = emptyList()
) {
  TopAppBar(
      title = { PageTitle(pageTitle, pageTitleTag) },
      actions = { actionButtons.forEach { actionButton -> actionButton() } },
      modifier = Modifier.topAppBarModifier())
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

@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun GradientSwitch(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
  val thumbColor = lightThemeBackground
  val trackColor = if (checked) IconsGradientBrush else SolidColor(PrimaryGray)

  Box(
      modifier =
          Modifier.width(52.dp).height(32.dp).testTag("gradientSwitch").clickable {
            onCheckedChange(!checked)
          }) {
        Canvas(modifier = Modifier.matchParentSize()) {
          drawRoundRect(brush = trackColor, cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx()))
        }

        // Thumb - Animated movement from left to right
        val thumbPosition = animateFloatAsState(if (checked) 22f else 5f, label = "")

        Box(
            modifier =
                Modifier.offset(x = thumbPosition.value.dp)
                    .size(24.dp)
                    .background(thumbColor, shape = CircleShape)
                    .align(Alignment.CenterStart)
                    .padding(4.dp) // Padding inside the thumb
            )
      }
}

@Composable
fun SettingsSwitch(
    text: String,
    textTag: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
  Box(
      modifier =
          Modifier.border(
                  width = 1.dp,
                  color = MaterialTheme.colorScheme.primary,
                  shape = RoundedCornerShape(size = 5.dp))
              .width(320.dp)
              .height(52.dp)
              .padding(10.dp)) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically, // Center items vertically
            horizontalArrangement = Arrangement.SpaceBetween // Space items apart
            ) {
              // Text aligned to start (left)
              Text(
                  text = text,
                  style = MaterialTheme.typography.bodyLarge,
                  color = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.align(Alignment.CenterVertically).testTag(textTag))

              // Switch aligned to end (right)
              GradientSwitch(
                  checked = checked,
                  onCheckedChange = onCheckedChange,
              )
            }
      }
}

/** Composable for Head Title */
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

/** Composable for the icon buttons */
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

@Composable
fun CollabButton(onClick: () -> Unit) {
  Row(
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
      modifier =
          Modifier.border(
                  width = 1.dp,
                  brush = PrimaryGradientBrush,
                  shape = RoundedCornerShape(size = 20.dp))
              .width(185.dp)
              .height(28.dp)
              .clickable { onClick() }
              .semantics { contentDescription = "Invite Collaborators" }
              .background(
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  shape = RoundedCornerShape(size = 20.dp))
              .padding(start = 16.dp, end = 16.dp)
              .testTag("collabButton")) {
        Text(
            text = "Invite Collaborators",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.CenterVertically))
        Icon(
            imageVector = collabAdd,
            contentDescription = "Collab Add",
            tint = Color.Unspecified,
            modifier = Modifier.align(Alignment.CenterVertically))
      }
}

@Composable
fun CollabList(collaborators: List<ProfileData>) {
  Box(
      modifier =
          Modifier.border(
                  width = 1.dp,
                  color = MaterialTheme.colorScheme.primary,
                  shape = RoundedCornerShape(size = 5.dp))
              .width(320.dp)
              .height(120.dp)
              .background(
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  shape = RoundedCornerShape(size = 5.dp))
              .testTag("collabBox")) {
        if (collaborators.isEmpty()) {
          Text(
              text = "NO COLLABORATORS",
              color = PrimaryGray,
              style = MaterialTheme.typography.bodyLarge,
              modifier = Modifier.align(Alignment.Center).testTag("emptyCollab"))
        } else {
          // TODO
          LazyColumn(
              modifier =
                  Modifier.fillMaxSize() // Fill the available size within the fixed rectangle
                      .padding(14.dp) // Optional padding inside the scrollable area
              ) {}
        }
      }
}

@Composable
fun PrincipalButton(buttonText: String, buttonTag: String, onClick: () -> Unit) {
  Box(
      modifier =
          Modifier.border(
                  width = 2.dp, brush = PrimaryGradientBrush, shape = RoundedCornerShape(30.dp))
              .width(320.dp)
              .height(48.dp),
      contentAlignment = Alignment.Center) {
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxSize().testTag(buttonTag),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(30.dp),
            elevation = null) {
              Text(text = buttonText, style = MaterialTheme.typography.labelLarge)
            }
      }
}