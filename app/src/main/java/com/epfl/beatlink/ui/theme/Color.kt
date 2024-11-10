package com.epfl.beatlink.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Primary color definitions for the app's UI elements
val PrimaryRed = Color(0xFFEF3535)
val PrimaryPurple = Color(0xFF5F2A83)
val SecondaryPurple = Color(0x805F2A83) // label input

// Define the vertical gradient brush
val PrimaryGradientBrush = Brush.verticalGradient(colors = listOf(PrimaryRed, PrimaryPurple))
val IconsGradientBrush = Brush.verticalGradient(colors = listOf(PrimaryPurple, PrimaryRed))

// Define the circle color
val CircleColor = Color(0x3E5F2A83)
val CircleStrokeColor = Color(0xA35F2A83)
val CircleColorDark = Color(0x3EEF3535)
val CircleStrokeColorDark = Color(0xA3EF3535)

// Music Listening Box LIGHT THEME
val lightThemeRed = Color(0xFFF0DDDD)
val lightThemePurple = Color(0xFFD3CDE3)

val PrimaryGray = Color(0xFF6F6F6F) // text
val SecondaryGray = Color(0xFFD9D9D9) // onglets
val LightGray = Color(0xFFF2F2F2) // search bar

val lightThemeBackground = Color(0xFFFAF8FE) // screen white

// Shadow of the Box
val ShadowColor = Color(0x1A000000)
// Border of the Box
val BorderColor = Color(0xFFEADDFF)

// DARK THEME
val darkThemeBackground = Color(0xFF121212) // screen black
val darkThemeGray1 = Color(0xFF2F2A31)
val darkThemeGray2 = Color(0xFF454147)
val darkThemeLightPurple = Color(0xFF956EAC)
val darkThemeWhite = Color.White

// Music Listening Box DARK THEME
val darkThemePurple = Color(0xFF473858)
val darkThemeRed = Color(0x00000000) // TODO
