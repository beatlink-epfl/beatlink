package com.epfl.beatlink.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Primary color definitions for the app's UI elements
val PrimaryRed = Color(0xFFEF3535)
val SecondaryRed = Color(0x80EF3535) // label input
val PrimaryPurple = Color(0xFF5F2A83)
val SecondaryPurple = Color(0x805F2A83) // label input
val PrimaryOrange = Color(0xFFFF7C1E)
val PrimaryBlue = Color(0xFF35A1EF)

// Define the vertical gradient brush
val PrimaryGradientBrush = Brush.verticalGradient(colors = listOf(PrimaryRed, PrimaryPurple))
val IconsGradientBrush = Brush.verticalGradient(colors = listOf(PrimaryPurple, PrimaryRed))
val RedGradientBrush = Brush.verticalGradient(colors = listOf(PrimaryRed, PrimaryRed))
val PositiveGradientBrush = Brush.verticalGradient(colors = listOf(PrimaryOrange, PrimaryRed))
val NegativeGradientBrush = Brush.verticalGradient(colors = listOf(PrimaryBlue, PrimaryPurple))

// Define the circle color
val CircleColor = Color(0x3E5F2A83)
val CircleStrokeColor = Color(0xA35F2A83)
val CircleColorDark = Color(0x3EEF3535)
val CircleStrokeColorDark = Color(0xA3EF3535)

// Music Listening Box LIGHT THEME
val lightThemePurple = Color(0xFFD3CDE3)

// Music Listening Box DARK THEME
val darkThemePurple = Color(0xFF473858)

val PrimaryGray = Color(0xFF6F6F6F) // text
val SecondaryGray = Color(0xFFD9D9D9) // onglets
val LightGray = Color(0xFFF2F2F2) // search bar

val lightThemeBackground = Color(0xFFFAF8FE) // screen white

// Shadow of the Box
val ShadowColor = Color(0x1A000000)
// Border of the Box
val BorderColor = Color(0xFFEADDFF)

// Offline background
val OfflineBackground = Color(0x99a09da1)

// DARK THEME
val darkThemeBackground = Color(0xFF121212) // screen black
val darkThemeGray1 = Color(0xFF2F2A31)
val darkThemeGray2 = Color(0xFF454147)
val darkThemeLightPurple = Color(0xFF956EAC)
val darkThemeWhite = Color.White

val lightThemePlaylistCard = Color(0x145F2A83)
val darkThemePlaylistCard = Brush.verticalGradient(colors = listOf(PrimaryRed, PrimaryPurple))

// Music Genres colors
val pop = Color(0xFFFF69B4)
val randb = Color(0xFF8A2BE2)
val rock = Color(0xFF5C4949)
val country = Color(0xFF8B4513)
val metal = Color(0xFF777777)
val electro = Color(0xFFD6CC96)
val jazz = Color(0xFF2A5883)
val rap = Color(0xFF5C46BD)
val classical = Color(0xFFEA8DD0)
val punk = Color(0xFFFF00FF)
val hiphop = Color(0xFFF1D04A)
val edm = Color(0xFF00FFFF)
val reggae = Color(0xFF008000)
val reggaeton = Color(0xFFFF4500)
val kpop = Color(0xFFFF1493)
val jpop = Color(0xFF00BFFF)
val house = Color(0xFF7FFF00)
val techno = Color(0xFFFF6DF9)
val lofi = Color(0xFF9075D8)
val soul = Color(0xFFFF7F50)
val dnb = Color(0xFF2D2587)
