package com.epfl.beatlink.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.epfl.beatlink.R

/** Typography for the overall screens of the app */
val TypographyBeatLink =
    Typography(
        // Large Title or Large Text
        displayLarge =
            TextStyle(
                fontSize = 32.sp,
                lineHeight = 40.sp,
                fontFamily = FontFamily(Font(R.font.roboto_bold)),
                fontWeight = FontWeight(500),
                textAlign = TextAlign.Center,
                color = PrimaryPurple,
                letterSpacing = 0.32.sp),
        // BeatLink logo text
        displayMedium =
            TextStyle(
                fontSize = 30.sp,
                fontFamily = FontFamily(Font(R.font.roboto_bold)),
                fontWeight = FontWeight(700),
                color = PrimaryPurple,
                letterSpacing = 0.3.sp),
        // logo text "Link Up Through Music"
        displaySmall =
            TextStyle(
                fontSize = 18.sp,
                fontFamily = FontFamily(Font(R.font.roboto)),
                fontWeight = FontWeight(700),
                color = PrimaryPurple,
                letterSpacing = 0.18.sp,
            ),
        // Title for each screens + BeatLink small logo text
        headlineLarge =
            TextStyle(
                fontSize = 20.sp,
                fontFamily = FontFamily(Font(R.font.roboto_bold)),
                fontWeight = FontWeight(700),
                color = PrimaryPurple,
                letterSpacing = 0.2.sp,
            ),
        // Bottom Nav Bar Highlighted
        headlineMedium =
            TextStyle(
                fontSize = 12.sp,
                fontFamily = FontFamily(Font(R.font.roboto_bold)),
                fontWeight = FontWeight(500),
                brush = PrimaryGradientBrush,
                textAlign = TextAlign.Center,
                letterSpacing = 0.5.sp),
        // Bottom Nav Bar
        headlineSmall =
            TextStyle(
                fontSize = 12.sp,
                fontFamily = FontFamily(Font(R.font.roboto)),
                fontWeight = FontWeight(500),
                color = PrimaryPurple,
                textAlign = TextAlign.Center,
                letterSpacing = 0.5.sp),
        // Body Title
        bodyLarge =
            TextStyle(
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(R.font.roboto_bold)),
                fontWeight = FontWeight(700),
                color = PrimaryPurple),

        // Body Text
        bodyMedium =
            TextStyle(
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontFamily = FontFamily(Font(R.font.roboto)),
                fontWeight = FontWeight(500),
                color = PrimaryPurple,
                letterSpacing = 0.14.sp,
            ),
        // Gray Text
        bodySmall =
            TextStyle(
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontFamily = FontFamily(Font(R.font.roboto)),
                fontWeight = FontWeight(400),
                fontStyle = FontStyle.Italic,
                color = PrimaryGray,
                letterSpacing = 0.14.sp),
        // Button Large
        labelLarge =
            TextStyle(
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontFamily = FontFamily(Font(R.font.roboto_bold)),
                fontWeight = FontWeight(500),
                letterSpacing = 0.14.sp),
        // Button Underlined
        labelMedium =
            TextStyle(
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(R.font.roboto_bold)),
                fontWeight = FontWeight(500),
                letterSpacing = 0.14.sp,
                brush = PrimaryGradientBrush,
                textDecoration = TextDecoration.Underline),
        // Button Small
        labelSmall =
            TextStyle(
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontFamily = FontFamily(Font(R.font.roboto)),
                fontWeight = FontWeight(500),
                color = PrimaryPurple,
                letterSpacing = 0.14.sp,
            ))

/** Typography for the display of the songs individually */
val TypographySongs =
    Typography(
        // Song name in played song screen
        headlineLarge =
            TextStyle(
                fontSize = 22.sp,
                fontFamily = FontFamily(Font(R.font.roboto_bold)),
                fontWeight = FontWeight(700),
                color = PrimaryPurple,
                letterSpacing = 0.22.sp,
            ),
        // Artist name in played song screen
        headlineMedium =
            TextStyle(
                fontSize = 18.sp,
                fontFamily = FontFamily(Font(R.font.roboto)),
                fontWeight = FontWeight(500),
                color = PrimaryPurple,
                letterSpacing = 0.18.sp,
                textDecoration = TextDecoration.Underline,
            ),
        // Album name - year in played song screen
        headlineSmall =
            TextStyle(
                fontSize = 18.sp,
                fontFamily = FontFamily(Font(R.font.roboto)),
                fontWeight = FontWeight(500),
                color = PrimaryGray,
                letterSpacing = 0.18.sp),
        // Song name in rectangle view
        titleLarge =
            TextStyle(
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(R.font.roboto_bold)),
                fontWeight = FontWeight(700),
                color = PrimaryPurple,
                letterSpacing = 0.5.sp,
            ),
        // Artist / Album name in rectangle view
        titleMedium =
            TextStyle(
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(R.font.roboto)),
                fontWeight = FontWeight(400),
                color = PrimaryPurple,
                letterSpacing = 0.5.sp,
            ),
        // Username in rectangle view
        labelMedium =
            TextStyle(
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(R.font.roboto)),
                fontWeight = FontWeight(400),
                fontStyle = FontStyle.Italic,
                color = PrimaryGray,
                letterSpacing = 0.18.sp,
            ))

/** Typography of the display of the playlist */
val TypographyPlaylist =
    Typography(
        // "NO SONGS ADDED" text
        displayMedium =
            TextStyle(
                fontSize = 20.sp,
                fontFamily = FontFamily(Font(R.font.roboto_bold)),
                fontWeight = FontWeight(700),
                color = PrimaryGray,
                letterSpacing = 0.2.sp,
            ),
        // "NO COLLABORATORS" text
        displaySmall =
            TextStyle(
                fontSize = 15.sp,
                fontFamily = FontFamily(Font(R.font.roboto_bold)),
                fontWeight = FontWeight(700),
                fontStyle = FontStyle.Italic,
                color = PrimaryGray,
                letterSpacing = 0.15.sp,
            ),
        // Playlist name in playlist overview screen
        headlineLarge =
            TextStyle(
                fontSize = 20.sp,
                fontFamily = FontFamily(Font(R.font.roboto_bold)),
                fontWeight = FontWeight(700),
                color = PrimaryPurple,
                letterSpacing = 0.2.sp,
            ),
        // Playlist owner in playlist overview screen
        headlineMedium =
            TextStyle(
                fontSize = 18.sp,
                fontFamily = FontFamily(Font(R.font.roboto_bold)),
                fontWeight = FontWeight(700),
                fontStyle = FontStyle.Italic,
                color = PrimaryPurple,
                letterSpacing = 0.18.sp,
            ),
        // Playlist other texts
        headlineSmall =
            TextStyle(
                fontSize = 18.sp,
                lineHeight = 20.sp,
                fontFamily = FontFamily(Font(R.font.roboto)),
                fontWeight = FontWeight(500),
                color = PrimaryPurple,
                letterSpacing = 0.18.sp,
            ),
        // Playlist name in rectangle view
        titleLarge =
            TextStyle(
                fontSize = 18.sp,
                fontFamily = FontFamily(Font(R.font.roboto_bold)),
                fontWeight = FontWeight(700),
                color = PrimaryPurple,
                letterSpacing = 0.5.sp,
            ),
        // Playlist owner in rectangle view
        titleMedium =
            TextStyle(
                fontSize = 15.sp,
                fontFamily = FontFamily(Font(R.font.roboto_bold)),
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight(700),
                color = PrimaryPurple,
                letterSpacing = 0.5.sp,
            ),
        // Number of tracks in playlist in rectangle view
        titleSmall =
            TextStyle(
                fontSize = 16.sp,
                lineHeight = 20.sp, // for "add playlist cover"h
                fontFamily = FontFamily(Font(R.font.roboto_bold)),
                fontWeight = FontWeight(700),
                color = PrimaryGray,
                letterSpacing = 0.5.sp,
            ))
