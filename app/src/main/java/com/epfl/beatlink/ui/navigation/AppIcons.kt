package com.epfl.beatlink.ui.navigation

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.ui.theme.PrimaryPurple
import com.epfl.beatlink.ui.theme.PrimaryRed

object AppIcons {
  private val iconsGradientBrush =
      Brush.linearGradient(
          colors = listOf(PrimaryPurple, PrimaryRed), start = Offset(0f, 0f), end = Offset(0f, 30f))

  val filledHome: ImageVector
    get() =
        Builder(
                name = "FilledHome",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f)
            .apply {
              path(
                  fill = iconsGradientBrush // Fill color from the original vector
                  ) {
                    moveTo(10f, 20f) // Move to (10, 20)
                    verticalLineTo(14f) // Draw vertical line down to (10, 14)
                    horizontalLineTo(14f) // Draw horizontal line to (14, 14)
                    verticalLineTo(20f) // Draw vertical line down to (14, 20)
                    horizontalLineTo(19f) // Draw horizontal line to (19, 20)
                    verticalLineTo(12f) // Draw vertical line down to (19, 12)
                    lineTo(21f, 12f) // Draw line to (21, 12)
                    lineTo(12f, 3f) // Draw line to (12, 3)
                    lineTo(2f, 12f) // Draw line to (2, 12)
                    lineTo(5f, 12f) // Draw line to (5, 12)
                    verticalLineTo(20f) // Draw vertical line down to (5, 20)
                    close() // Close the path
              }
            }
            .build()

  val filledSearch: ImageVector
    get() =
        Builder(
                name = "FilledLens",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f)
            .apply {
              // Background filled circle for the lens
              path(
                  fill = iconsGradientBrush // Fill color for the lens
                  ) {
                    moveTo(9.5f, 5f) // Start at the top center of the circle
                    arcToRelative(
                        4.5f,
                        4.5f,
                        0f,
                        isMoreThanHalf = true,
                        isPositiveArc = true,
                        dx1 = 0f,
                        dy1 = 9f) // Create the upper semicircle
                    arcToRelative(
                        4.5f,
                        4.5f,
                        0f,
                        isMoreThanHalf = true,
                        isPositiveArc = true,
                        dx1 = 0f,
                        dy1 = -9f) // Create the lower semicircle

                    moveTo(15.5f, 14f) // Start point for the handle
                    lineTo(14.71f, 13.27f) // Draw line to the end of the handle
                    curveTo(
                        15.41f,
                        12.59f,
                        16f,
                        11.11f,
                        16f,
                        9.5f) // Draw curve for the top of the lens
                    curveTo(16f, 5.91f, 13.09f, 3f, 9.5f, 3f) // Draw top side of the circle
                    curveTo(5.91f, 3f, 3f, 5.91f, 3f, 9.5f) // Draw left side of the circle
                    curveTo(3f, 13.09f, 5.91f, 16f, 9.5f, 16f) // Draw bottom side of the circle
                    curveTo(
                        11.11f,
                        16f,
                        12.59f,
                        15.41f,
                        14.71f,
                        14.73f) // Draw right side of the circle
                    lineTo(15.5f, 14f) // Close the handle to the circle

                    moveTo(9.5f, 14f) // Move to the inner circle start point
                    curveTo(7.01f, 14f, 5f, 11.99f, 5f, 9.5f) // Draw left side of the inner circle
                    curveTo(5f, 7.01f, 7.01f, 5f, 9.5f, 5f) // Draw top side of the inner circle
                    curveTo(
                        12.99f, 5f, 15f, 7.01f, 15f, 9.5f) // Draw right side of the inner circle
                    curveTo(
                        15f, 11.99f, 12.99f, 14f, 9.5f, 14f) // Draw bottom side of the inner circle

                    moveTo(15.77f, 17.09f) // Start at the base of the handle
                    lineTo(20.49f, 21f) // Draw line to the end of the handle
                    lineTo(21f, 20.49f) // Draw line to the inner edge of the handle
                    lineTo(16.27f, 15.77f) // Draw line back towards the lens
                    close() // Close the inner circle path

                    close()
                  }
            }
            .build()

  val outlinedLibrary: ImageVector
    get() =
        Builder(
                name = "FilledLibrary",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f)
            .apply {
              path(
                  fill = SolidColor(PrimaryPurple),
              ) {
                moveTo(20f, 2f)
                lineTo(8f, 2f)
                curveTo(6.9f, 2f, 6f, 2.9f, 6f, 4f)
                verticalLineTo(16f)
                curveTo(6f, 17.1f, 6.9f, 18f, 8f, 18f)
                horizontalLineTo(20f)
                curveTo(21.1f, 18f, 22f, 17.1f, 22f, 16f)
                lineTo(22f, 4f)
                curveTo(22f, 2.9f, 21.1f, 2f, 20f, 2f)
                close()

                moveTo(20f, 16f)
                lineTo(8f, 16f)
                lineTo(8f, 4f)
                lineTo(20f, 4f)
                lineTo(20f, 16f)
                close()

                moveTo(12.5f, 15f)
                curveTo(13.88f, 15f, 15f, 13.88f, 15f, 12.5f)
                lineTo(15f, 7f)
                horizontalLineTo(18f)
                verticalLineTo(5f)
                horizontalLineTo(14f)
                verticalLineTo(10.51f)
                curveTo(13.58f, 10.19f, 13.07f, 10f, 12.5f, 10f)
                curveTo(11.12f, 10f, 10f, 11.12f, 10f, 12.5f)
                curveTo(10f, 13.88f, 11.12f, 15f, 12.5f, 15f)
                close()

                moveTo(4f, 6f)
                lineTo(2f, 6f)
                verticalLineTo(20f)
                curveTo(2f, 21.1f, 2.9f, 22f, 4f, 22f)
                horizontalLineTo(18f)
                verticalLineTo(20f)
                lineTo(4f, 20f)
                verticalLineTo(6f)
                close()
              }
            }
            .build()

  val filledLibrary: ImageVector
    get() =
        Builder(
                name = "CustomIcon",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f)
            .apply {
              path(fill = iconsGradientBrush) {
                moveTo(20f, 2f) // Move to the starting point of the icon (20, 2)
                lineTo(8f, 2f) // Line to (8, 2)
                curveTo(6.9f, 2f, 6f, 2.9f, 6f, 4f) // Curve to (6, 4)
                verticalLineTo(16f) // Vertical line to (6, 16)
                curveTo(6f, 17.1f, 6.9f, 18f, 8f, 18f) // Curve to (8, 18)
                horizontalLineTo(20f) // Horizontal line to (20, 18)
                curveTo(21.1f, 18f, 22f, 17.1f, 22f, 16f) // Curve to (22, 16)
                lineTo(22f, 4f) // Line to (22, 4)
                curveTo(22f, 2.9f, 21.1f, 2f, 20f, 2f) // Curve to (20, 2)
                close() // Close the path

                moveTo(18f, 7f) // Move to (18, 7)
                lineTo(15f, 7f) // Line to (15, 7)
                verticalLineTo(12.5f) // Vertical line to (15, 12.5)
                curveTo(15f, 13.88f, 13.88f, 15f, 12.5f, 15f) // Curve to (12.5, 15)
                reflectiveCurveTo(10f, 13.88f, 10f, 12.5f) // Reflective curve to (10, 12.5)
                curveTo(10f, 11.12f, 11.12f, 10f, 12.5f, 10f) // Curve to (12.5, 10)
                curveTo(13.07f, 10f, 13.58f, 10.19f, 14f, 10.51f) // Curve to (14, 10.51)
                lineTo(14f, 5f) // Line to (14, 5)
                horizontalLineTo(18f) // Horizontal line to (18, 5)
                verticalLineTo(7f) // Vertical line to (18, 7)
                close() // Close the path

                moveTo(4f, 6f) // Move to (4, 6)
                lineTo(2f, 6f) // Line to (2, 6)
                verticalLineTo(20f) // Vertical line to (2, 20)
                curveTo(2f, 21.1f, 2.9f, 22f, 4f, 22f) // Curve to (4, 22)
                horizontalLineTo(18f) // Horizontal line to (18, 22)
                verticalLineTo(20f) // Vertical line to (18, 20)
                lineTo(4f, 20f) // Line to (4, 20)
                verticalLineTo(6f) // Vertical line to (4, 6)
                close() // Close the path
              }
            }
            .build()

  val filledProfile: ImageVector
    get() =
        Builder(
                name = "FilledUser",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f)
            .apply {
              path(fill = iconsGradientBrush) {
                moveTo(12f, 2f) // Move to (12, 2)
                curveTo(6.48f, 2f, 2f, 6.48f, 2f, 12f) // Create a curve for the outer circle
                curveTo(2f, 17.52f, 6.48f, 22f, 12f, 22f) // Create a curve for the outer circle
                curveTo(17.52f, 22f, 22f, 17.52f, 22f, 12f) // Create a curve for the outer circle
                curveTo(22f, 6.48f, 17.52f, 2f, 12f, 2f) // Create a curve for the outer circle

                moveTo(12f, 6.5f) // Center of the head circle
                arcToRelative(
                    3.5f,
                    3.5f,
                    0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = 0f,
                    dy1 = 7f) // Draw head circle
                arcToRelative(
                    3.5f,
                    3.5f,
                    0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = 0f,
                    dy1 = -7f) // Complete head circle

                moveTo(12f, 20f) // Move to (12, 20)
                curveTo(9.97f, 20f, 7.57f, 19.18f, 6.86f, 17.12f) // Create a curve for bottom area
                curveTo(7.55f, 15.8f, 9.68f, 15f, 12f, 15f) // Create a curve for bottom area
                curveTo(
                    14.45f, 15f, 16.43f, 15.8f, 17.14f, 17.12f) // Create a curve for bottom area
                curveTo(16.43f, 19.18f, 14.03f, 20f, 12f, 20f) // Create a curve for bottom area
              }
            }
            .build()

  val collab: ImageVector
    get() =
        Builder(
                name = "collab",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f)
            .apply {
              path(
                  fill = iconsGradientBrush,
              ) {
                moveTo(8f, 12f)
                curveTo(10.21f, 12f, 12f, 10.21f, 12f, 8f)
                reflectiveCurveTo(10.21f, 4f, 8f, 4f)
                reflectiveCurveTo(4f, 5.79f, 4f, 8f)
                reflectiveCurveTo(5.79f, 12f, 8f, 12f)
                close()
                moveTo(8f, 6f)
                curveTo(9.1f, 6f, 10f, 6.9f, 10f, 8f)
                reflectiveCurveTo(9.1f, 10f, 8f, 10f)
                reflectiveCurveTo(6f, 9.1f, 6f, 8f)
                reflectiveCurveTo(6.9f, 6f, 8f, 6f)
                close()
              }
              path(
                  fill = iconsGradientBrush,
              ) {
                moveTo(8f, 13f)
                curveTo(5.33f, 13f, 0f, 14.34f, 0f, 17f)
                verticalLineTo(20f)
                horizontalLineTo(16f)
                verticalLineTo(17f)
                curveTo(16f, 14.34f, 10.67f, 13f, 8f, 13f)
                close()
                moveTo(14f, 18f)
                horizontalLineTo(2f)
                verticalLineTo(17.01f)
                curveTo(2.2f, 16.29f, 5.3f, 15f, 8f, 15f)
                reflectiveCurveTo(13.8f, 16.29f, 14f, 17.01f)
                verticalLineTo(18f)
                close()
              }
              path(
                  fill = iconsGradientBrush,
              ) {
                moveTo(12.51f, 4.05f)
                curveTo(13.43f, 5.11f, 14f, 6.49f, 14f, 8f)
                reflectiveCurveTo(13.43f, 10.89f, 12.51f, 11.95f)
                curveTo(14.47f, 11.7f, 16f, 10.04f, 16f, 8f)
                reflectiveCurveTo(14.47f, 4.3f, 12.51f, 4.05f)
                close()
              }
              path(
                  fill = iconsGradientBrush,
              ) {
                moveTo(16.53f, 13.83f)
                curveTo(17.42f, 14.66f, 18f, 15.7f, 18f, 17f)
                verticalLineTo(20f)
                horizontalLineTo(20f)
                verticalLineTo(17f)
                curveTo(20f, 15.55f, 18.41f, 14.49f, 16.53f, 13.83f)
                close()
              }
            }
            .build()

  val collabAdd: ImageVector
    get() =
        Builder(
                name = "collabApp",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f)
            .apply {
              // First path
              path(
                  fill = iconsGradientBrush,
              ) {
                moveTo(22f, 9f)
                lineTo(22f, 7f)
                lineTo(20f, 7f)
                lineTo(20f, 9f)
                lineTo(18f, 9f)
                lineTo(18f, 11f)
                lineTo(20f, 11f)
                lineTo(20f, 13f)
                lineTo(22f, 13f)
                lineTo(22f, 11f)
                lineTo(24f, 11f)
                lineTo(24f, 9f)
                close()
              }

              // Second path
              path(
                  fill = iconsGradientBrush,
              ) {
                moveTo(8f, 12f)
                curveTo(10.21f, 12f, 12f, 10.21f, 12f, 8f)
                reflectiveCurveTo(10.21f, 4f, 8f, 4f)
                reflectiveCurveTo(4f, 5.79f, 4f, 8f)
                reflectiveCurveTo(5.79f, 12f, 8f, 12f)
                close()
                moveTo(8f, 6f)
                curveTo(9.1f, 6f, 10f, 6.9f, 10f, 8f)
                reflectiveCurveTo(9.1f, 10f, 8f, 10f)
                reflectiveCurveTo(6f, 9.1f, 6f, 8f)
                reflectiveCurveTo(6.9f, 6f, 8f, 6f)
                close()
              }

              // Third path
              path(
                  fill = iconsGradientBrush,
              ) {
                moveTo(8f, 13f)
                curveTo(5.33f, 13f, 0f, 14.34f, 0f, 17f)
                verticalLineTo(20f)
                horizontalLineTo(16f)
                verticalLineTo(17f)
                curveTo(16f, 14.34f, 10.67f, 13f, 8f, 13f)
                close()
                moveTo(14f, 18f)
                horizontalLineTo(2f)
                verticalLineTo(17.01f)
                curveTo(2.2f, 16.29f, 5.3f, 15f, 8f, 15f)
                reflectiveCurveTo(13.8f, 16.29f, 14f, 17.01f)
                verticalLineTo(18f)
                close()
              }

              // Fourth path
              path(
                  fill = iconsGradientBrush,
              ) {
                moveTo(12.51f, 4.05f)
                curveTo(13.43f, 5.11f, 14f, 6.49f, 14f, 8f)
                reflectiveCurveTo(13.43f, 10.89f, 12.51f, 11.95f)
                curveTo(14.47f, 11.7f, 16f, 10.04f, 16f, 8f)
                reflectiveCurveTo(14.47f, 4.3f, 12.51f, 4.05f)
                close()
              }

              // Fifth path
              path(
                  fill = iconsGradientBrush,
              ) {
                moveTo(16.53f, 13.83f)
                curveTo(17.42f, 14.66f, 18f, 15.7f, 18f, 17f)
                verticalLineTo(20f)
                horizontalLineTo(20f)
                verticalLineTo(17f)
                curveTo(20f, 15.55f, 18.41f, 14.49f, 16.53f, 13.83f)
                close()
              }
            }
            .build()
}
