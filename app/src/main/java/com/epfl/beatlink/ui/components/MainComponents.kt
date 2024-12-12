package com.epfl.beatlink.ui.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.epfl.beatlink.R
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.ui.player.SharedPlayerEffect
import com.epfl.beatlink.ui.theme.BorderColor
import com.epfl.beatlink.ui.theme.IconsGradientBrush
import com.epfl.beatlink.ui.theme.LightGray
import com.epfl.beatlink.ui.theme.PrimaryGradientBrush
import com.epfl.beatlink.ui.theme.PrimaryGray
import com.epfl.beatlink.ui.theme.PrimaryRed
import com.epfl.beatlink.ui.theme.RedGradientBrush
import com.epfl.beatlink.ui.theme.SecondaryGray
import com.epfl.beatlink.ui.theme.ShadowColor
import com.epfl.beatlink.ui.theme.TypographySongs
import com.epfl.beatlink.ui.theme.lightThemeBackground
import com.epfl.beatlink.viewmodel.map.user.MapUsersViewModel
import com.epfl.beatlink.viewmodel.spotify.api.SpotifyApiViewModel

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
    actionButtons: List<@Composable () -> Unit> = emptyList(),
    goBackManagement: () -> Unit = {},
) {
  TopAppBar(
      title = {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
          PageTitle(title, titleTag)
        }
      },
      actions = {
        if (actionButtons.isEmpty()) {
          Spacer(Modifier.width(46.dp))
        } else {
          actionButtons.forEach { actionButton -> actionButton() }
        }
      },
      navigationIcon = {
        Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center) {
          BackArrowButton {
            goBackManagement()
            navigationActions.goBack()
          }
        }
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

@Composable
fun ReusableOverlay(
    onDismissRequest: () -> Unit,
    modifier: Modifier,
    overlayContent: @Composable () -> Unit
) {
  // Semi-transparent background overlay
  Box(
      modifier =
          Modifier.testTag("overlay")
              .fillMaxSize()
              .background(Color.Black.copy(alpha = 0.4f))
              .pointerInput(Unit) { detectTapGestures(onTap = { onDismissRequest() }) },
      contentAlignment = Alignment.BottomCenter) {
        // Inner box to hold the overlay content
        Box(
            modifier =
                modifier
                    .width(384.dp)
                    .padding(bottom = 11.dp)
                    .border(
                        width = 2.dp,
                        brush = PrimaryGradientBrush,
                        shape = RoundedCornerShape(10.dp))
                    .pointerInput(Unit) {
                      detectTapGestures(onTap = { /* Block tap propagation */})
                    },
            contentAlignment = Alignment.Center,
        ) {
          overlayContent()
        }
      }
}

@Composable
fun CustomInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    singleLine: Boolean = true,
    supportingText: String? = null,
    isError: Boolean = false,
    trailingIcon: ImageVector? = null
) {
  Column {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = MaterialTheme.colorScheme.primary) },
        placeholder = { Text(placeholder, color = MaterialTheme.colorScheme.onSecondary) },
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation,
        textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.primary),
        modifier = modifier.width(320.dp),
        colors =
            OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.primary,
                unfocusedTextColor = MaterialTheme.colorScheme.primary,
                cursorColor = MaterialTheme.colorScheme.onPrimary,
                errorTextColor = MaterialTheme.colorScheme.error,
                focusedBorderColor =
                    if (isError) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onPrimary,
                unfocusedBorderColor =
                    if (isError) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.primary,
                focusedLabelColor =
                    if (isError) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onPrimary,
                unfocusedLabelColor =
                    if (isError) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.primary),
        trailingIcon = {
          trailingIcon?.let {
            Icon(
                imageVector = it,
                contentDescription = "Edit",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onValueChange("") })
          }
        },
        maxLines = Int.MAX_VALUE)
    supportingText?.let { Text(text = it, style = MaterialTheme.typography.bodySmall) }
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

@Composable
fun GradientText(
    modifier: Modifier = Modifier,
    text: String,
    style: TextStyle = MaterialTheme.typography.bodyLarge
) {
  Text(
      modifier = modifier,
      text =
          buildAnnotatedString {
            withStyle(style.toSpanStyle().copy(brush = PrimaryGradientBrush)) { append(text) }
          })
}

@Composable
fun GradientTitle(title: String) {
  Text(
      text = title,
      style = MaterialTheme.typography.headlineLarge,
      modifier =
          Modifier.testTag(title + "Title").graphicsLayer(alpha = 0.99f).drawWithCache {
            onDrawWithContent {
              drawContent()
              drawRect(PrimaryGradientBrush, blendMode = BlendMode.SrcAtop)
            }
          })
}

/** Composable for Gradient Title with an arrow to open it full screen */
@Composable
fun TitleWithArrow(title: String, onClick: () -> Unit) {
  Row(
      modifier =
          Modifier.padding(top = 16.dp).testTag(title + "TitleWithArrow").clickable { onClick() },
      verticalAlignment = Alignment.CenterVertically) {
        GradientTitle(title)
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
  IconButton(
      onClick = onClick,
      modifier = modifier,
  ) {
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
fun PrincipalButton(
    buttonText: String,
    buttonTag: String,
    width: Dp = 320.dp,
    height: Dp = 48.dp,
    isRed: Boolean = false,
    onClick: () -> Unit
) {
  Box(
      modifier =
          Modifier.border(
                  width = 2.dp,
                  brush = if (!isRed) PrimaryGradientBrush else RedGradientBrush,
                  shape = RoundedCornerShape(30.dp))
              .width(width)
              .height(height),
      contentAlignment = Alignment.Center) {
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxSize().testTag(buttonTag),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (!isRed) MaterialTheme.colorScheme.primary else PrimaryRed),
            shape = RoundedCornerShape(30.dp),
            elevation = null) {
              Text(text = buttonText, style = MaterialTheme.typography.labelLarge)
            }
      }
}

@Composable
fun MusicPlayerUI(
    navigationActions: NavigationActions,
    spotifyApiViewModel: SpotifyApiViewModel,
    mapUsersViewModel: MapUsersViewModel
) {

  SharedPlayerEffect(spotifyApiViewModel, mapUsersViewModel)

  if (spotifyApiViewModel.playbackActive) {
    Row(
        modifier =
            Modifier.fillMaxWidth()
                .height(76.dp)
                .background(color = SecondaryGray)
                .testTag("playerContainer")
                .clickable(onClick = { navigationActions.navigateTo(Screen.PLAY_SCREEN) }),
        verticalAlignment = Alignment.CenterVertically,
    ) {
      // Cover image
      Card(
          modifier =
              Modifier.padding(start = 11.dp, top = 11.dp, bottom = 11.dp)
                  .testTag("songCardContainer")
                  .size(55.dp),
          shape = RoundedCornerShape(5.dp),
      ) {
        AsyncImage(
            model = spotifyApiViewModel.currentTrack.cover,
            contentDescription = "Cover",
            modifier = Modifier.fillMaxSize())
      }

      Spacer(modifier = Modifier.width(8.dp))

      // Song title and artist/album information
      Column(verticalArrangement = Arrangement.Center, modifier = Modifier.weight(1f)) {
        Text(text = spotifyApiViewModel.currentTrack.name, style = TypographySongs.titleLarge)
        Text(
            text =
                "${spotifyApiViewModel.currentArtist.name} - ${spotifyApiViewModel.currentAlbum.name}",
            style = TypographySongs.titleSmall)
      }

      Spacer(modifier = Modifier.width(8.dp))

      // Play/Stop button
      IconButton(
          onClick = {
            if (spotifyApiViewModel.isPlaying) {
              spotifyApiViewModel.pausePlayback()
            } else {
              spotifyApiViewModel.playPlayback()
            }
          }) {
            if (spotifyApiViewModel.isPlaying) {
              Icon(
                  painter = painterResource(R.drawable.pause),
                  contentDescription = "Pause",
                  tint = Color.Unspecified,
                  modifier = Modifier.size(35.dp).testTag("pauseButton"))
            } else {
              Icon(
                  painter = painterResource(R.drawable.play),
                  contentDescription = "Play",
                  tint = Color.Unspecified,
                  modifier = Modifier.size(35.dp).testTag("playButton"))
            }
          }

      // Skip button
      IconButton(
          onClick = {
            spotifyApiViewModel.skipSong()
            spotifyApiViewModel.updatePlayer()
          }) {
            Icon(
                painter = painterResource(R.drawable.skip_forward),
                contentDescription = "Skip",
                tint = Color.Unspecified,
                modifier = Modifier.size(35.dp).testTag("skipButton"))
          }
    }
  }
}

@Composable
fun CircleWithIcon(icon: ImageVector, backgroundColor: Color) {
  Box(modifier = Modifier.size(32.dp).background(color = backgroundColor, shape = CircleShape)) {
    Icon(
        imageVector = icon,
        contentDescription = "Edit",
        tint = LightGray,
        modifier = Modifier.padding(6.dp))
  }
}

@Composable
fun ProfilePicture(id: MutableState<Bitmap?>, size: Dp = 100.dp) {
  // Profile picture
  id.value?.let { bitmap ->
    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = "Profile Picture",
        modifier =
            Modifier.size(100.dp)
                .testTag("profilePicture")
                .clip(CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape))
  }
      ?: run {
        Image(
            painter = painterResource(id = R.drawable.default_profile_picture),
            contentDescription = "Profile Picture",
            modifier =
                Modifier.size(size)
                    .testTag("profilePicture")
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape))
      }
}

@Composable
fun IconWithText(text: String, textTag: String, icon: ImageVector, style: TextStyle) {
  Row(
      verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(
        imageVector = icon,
        contentDescription = "icon",
        modifier = Modifier.size(18.dp),
        tint = MaterialTheme.colorScheme.primary)

    Spacer(modifier = Modifier.width(4.dp))
    Text(
        text = text,
        style = style,
        color = MaterialTheme.colorScheme.primary,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.testTag(textTag))
  }
}

@Composable
fun TextInBox(label: String, modifier: Modifier = Modifier, icon: ImageVector? = null) {
  Box(
      modifier =
          modifier
              .width(320.dp)
              .border(1.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(5.dp))
              .padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
              Text(
                  text = label,
                  style = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.primary))
              icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = "Edit",
                    tint = MaterialTheme.colorScheme.primary)
              }
            }
      }
}
