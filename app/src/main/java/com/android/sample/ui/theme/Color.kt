package com.android.sample.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Primary color definitions for the app's UI elements
val PrimaryRed = Color(0xFFEF3535)
val PrimaryPurple = Color(0xFF5F2A83)
val SecondaryPurple = Color(0x805F2A83) // label input

// Music Listening Box
val LightRed = Color(0xFFF0DDDD)
val LightPurple = Color(0xFFD3CDE3)

val PrimaryGray = Color(0xFF6F6F6F) // text
val SecondaryGray = Color(0xFFD9D9D9) // onglets
val LightGray = Color(0xFFF2F2F2) // search bar

val PrimaryWhite = Color(0xFFFAF8FE) // screen white
val PrimaryBlack = Color(0xFF230643) // screen black

// Shadow of the Box
val ShadowColor = Color(0x1A000000)
// Border of the Box
val BorderColor = Color(0xFFEADDFF)

// Define the vertical gradient brush
val PrimaryGradientBrush = Brush.verticalGradient(colors = listOf(PrimaryRed, PrimaryPurple))
val IconsGradientBrush = Brush.verticalGradient(colors = listOf(PrimaryPurple, PrimaryRed))

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)
