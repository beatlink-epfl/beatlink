package com.epfl.beatlink.ui.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

/**
 * Creates a `BitmapDescriptor` from a drawable resource.
 *
 * This function takes a drawable resource ID and converts it into a `BitmapDescriptor` that can be
 * used with Google Maps API. The drawable is scaled to the specified width and height.
 *
 * @param resourceId The resource ID of the drawable to convert.
 * @param context The context to access the drawable resource.
 * @param width The desired width of the resulting bitmap.
 * @param height The desired height of the resulting bitmap.
 * @return A `BitmapDescriptor` representing the drawable.
 */
@Composable
fun getBitmapDescriptorFromDrawableResource(
    resourceId: Int,
    context: Context,
    width: Int,
    height: Int
): BitmapDescriptor {
  val drawable = ContextCompat.getDrawable(context, resourceId)
  val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
  val canvas = Canvas(bitmap)
  drawable!!.setBounds(0, 0, width, height)
  drawable.draw(canvas)
  return BitmapDescriptorFactory.fromBitmap(bitmap)
}

/**
 * Creates a `BitmapDescriptor` from a drawable resource with additional styling options for the
 * popup songs markers.
 *
 * This function takes a drawable resource ID and converts it into a `BitmapDescriptor` that can be
 * used with Google Maps API. The drawable is scaled to the specified width and height, and
 * additional styling options such as corner radius, stroke, and shadow are applied.
 *
 * @param resourceId The resource ID of the drawable to convert.
 * @param context The context to access the drawable resource.
 * @param width The desired width of the resulting bitmap.
 * @param height The desired height of the resulting bitmap.
 * @param cornerRadius The radius for the corners of the drawable.
 * @param strokeWidth The width of the stroke around the drawable.
 * @param strokeColor The color of the stroke around the drawable.
 * @param shadowColor The color of the shadow behind the drawable.
 * @param shadowRadius The radius of the shadow behind the drawable.
 * @return A `BitmapDescriptor` representing the styled drawable.
 */
@Composable
fun getBitmapDescriptorFromDrawableResourceSongPopUp(
    resourceId: Int,
    context: Context,
    width: Int,
    height: Int,
    cornerRadius: Float = 6f,
    strokeWidth: Float = 4f,
    strokeColor: Int = MaterialTheme.colorScheme.background.toArgb(),
    shadowColor: Int =
        MaterialTheme.colorScheme.outline
            .toArgb(), // Very light black for a subtle shadow (10% opacity)
    shadowRadius: Float = 20f // Increased radius for an evenly diffused shadow
): BitmapDescriptor {
  val drawable =
      ContextCompat.getDrawable(context, resourceId)
          ?: return BitmapDescriptorFactory.defaultMarker()

  // Create a bitmap with extra padding for the shadow
  val bitmap =
      Bitmap.createBitmap(
          width + shadowRadius.toInt() * 2,
          height + shadowRadius.toInt() * 2,
          Bitmap.Config.ARGB_8888)
  val canvas = Canvas(bitmap)

  // Center offset for the shadow
  val offset = shadowRadius

  // Prepare the background paint for the shadow effect
  val shadowPaint =
      Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = shadowColor
        style = Paint.Style.FILL
        setShadowLayer(shadowRadius, 0f, 0f, shadowColor) // No offset for even shadow around
      }

  // Draw a rounded rectangle as the shadow layer behind the drawable
  val shadowRect = RectF(offset, offset, width + offset, height + offset)
  val shadowPath =
      Path().apply { addRoundRect(shadowRect, cornerRadius, cornerRadius, Path.Direction.CW) }

  // Draw shadow
  canvas.drawPath(shadowPath, shadowPaint)

  // Prepare the rounded rectangle path for the drawable
  val rect =
      RectF(
          offset + strokeWidth,
          offset + strokeWidth,
          width + offset - strokeWidth,
          height + offset - strokeWidth)
  val path = Path().apply { addRoundRect(rect, cornerRadius, cornerRadius, Path.Direction.CW) }

  // Clip the canvas to the rounded rectangle path
  canvas.save()
  canvas.clipPath(path)

  // Draw the drawable within the clipped canvas
  drawable.apply {
    setBounds(
        (offset + strokeWidth).toInt(),
        (offset + strokeWidth).toInt(),
        (width + offset - strokeWidth).toInt(),
        (height + offset - strokeWidth).toInt())
    draw(canvas)
  }
  canvas.restore()

  // Draw the stroke
  val strokePaint =
      Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = strokeColor
        style = Paint.Style.STROKE
        this.strokeWidth = strokeWidth
      }
  canvas.drawPath(path, strokePaint)

  return BitmapDescriptorFactory.fromBitmap(bitmap)
}
