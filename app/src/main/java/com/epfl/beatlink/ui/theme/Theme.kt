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
        secondary = darkThemeLightPurple,
        onSecondary = darkThemeLightPurple,
        tertiary = darkThemePurple, // music listening box
        onTertiary = darkThemeRed, // music listening box
        background = darkThemeBackground,
        surface = darkThemeBackground,
        surfaceVariant = darkThemeGray1,
        onSurfaceVariant = darkThemeGray2,
        error = PrimaryRed)

private val LightColorScheme =
    lightColorScheme(
        primary = PrimaryPurple, // text
        onPrimary = PrimaryPurple,
        secondary = PrimaryRed,
        onSecondary = SecondaryPurple,
        tertiary = lightThemePurple, // music listening box
        onTertiary = lightThemeRed, // music listening box
        background = lightThemeBackground,
        surface = lightThemeBackground,
        surfaceVariant = lightThemeBackground,
        onSurfaceVariant = lightThemeBackground,
        error = PrimaryRed)

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