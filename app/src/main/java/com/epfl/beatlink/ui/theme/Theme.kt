package com.epfl.beatlink.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme =
    darkColorScheme(
        primary = darkThemeWhite, // text
        onPrimary = darkThemeLightPurple,
        primaryContainer = darkThemeGray1, // text user
        secondary = darkThemeLightPurple,
        onSecondary = darkThemeLightPurple,
        tertiary = darkThemePurple, // music listening box
        onTertiary = darkThemeRed, // music listening box
        background = darkThemeBackground,
        onBackground = CircleColorDark, // radius circle
        surface = darkThemeBackground,
        onSurface = CircleStrokeColorDark, // radius circle stroke
        surfaceVariant = darkThemeGray1,
        onSurfaceVariant = darkThemeGray2,
        error = PrimaryRed,
        outline = ShadowColor) // shadow of the box

private val LightColorScheme =
    lightColorScheme(
        primary = PrimaryPurple, // text
        onPrimary = PrimaryPurple,
        primaryContainer = PrimaryGray, // text user
        secondary = PrimaryRed,
        onSecondary = SecondaryPurple,
        tertiary = lightThemePurple, // music listening box
        onTertiary = lightThemeRed, // music listening box
        background = lightThemeBackground,
        onBackground = CircleColor, // radius circle
        surface = lightThemeBackground,
        onSurface = CircleStrokeColor, // radius circle stroke
        surfaceVariant = lightThemeBackground,
        onSurfaceVariant = lightThemeBackground,
        error = PrimaryRed,
        outline = ShadowColor) // shadow of the box

@Composable
fun BeatLinkAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
  val colorScheme =
      when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
          val context = LocalContext.current
          if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
      }
  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as Activity).window
      window.statusBarColor = PrimaryPurple.toArgb()
      WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
    }
  }

  MaterialTheme(colorScheme = colorScheme, typography = TypographyBeatLink, content = content)
}
