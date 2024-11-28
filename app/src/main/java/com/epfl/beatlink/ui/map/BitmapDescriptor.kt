package com.epfl.beatlink.ui.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
 * Creates a `BitmapDescriptor` from an image URL with additional styling options for markers.
 *
 * This function downloads an image from a URL, scales it to the specified width and height, and
 * applies additional styling options like corner radius, stroke, and shadow.
 *
 * @param imageUrl The URL of the image to convert.
 * @param width The desired width of the resulting bitmap.
 * @param height The desired height of the resulting bitmap.
 * @param cornerRadius The radius for the corners of the drawable.
 * @param strokeWidth The width of the stroke around the drawable.
 * @param strokeColor The color of the stroke around the drawable.
 * @param shadowColor The color of the shadow behind the drawable.
 * @param shadowRadius The radius of the shadow behind the drawable.
 * @return A `BitmapDescriptor` representing the styled image.
 */
suspend fun getBitmapDescriptorFromImageUrlSongPopUp(
    imageUrl: String,
    width: Int,
    height: Int,
    cornerRadius: Float = 6f,
    strokeWidth: Float = 4f,
    strokeColor: Int = Color.White.toArgb(), // Default white stroke
    shadowColor: Int = Color.LightGray.toArgb(), // Default light gray shadow
    shadowRadius: Float = 20f // Increased radius for an evenly diffused shadow
): BitmapDescriptor? {
  return withContext(Dispatchers.IO) {
    try {
      val bitmap = Picasso.get().load(imageUrl).resize(width, height).get()

      // Create a bitmap with extra padding for the shadow
      val styledBitmap =
          Bitmap.createBitmap(
              width + shadowRadius.toInt() * 2,
              height + shadowRadius.toInt() * 2,
              Bitmap.Config.ARGB_8888)
      val canvas = Canvas(styledBitmap)

      // Center offset for the shadow
      val offset = shadowRadius

      // Prepare the background paint for the shadow effect
      val shadowPaint =
          Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = shadowColor
            style = Paint.Style.FILL
            setShadowLayer(shadowRadius, 0f, 0f, shadowColor) // No offset for even shadow
          }

      // Draw a rounded rectangle as the shadow layer
      val shadowRect = RectF(offset, offset, width + offset, height + offset)
      val shadowPath =
          Path().apply { addRoundRect(shadowRect, cornerRadius, cornerRadius, Path.Direction.CW) }
      canvas.drawPath(shadowPath, shadowPaint)

      // Prepare the rounded rectangle path for the image
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

      // Draw the image within the clipped canvas
      canvas.drawBitmap(bitmap, null, rect, null)
      canvas.restore()

      // Draw the stroke
      val strokePaint =
          Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = strokeColor
            style = Paint.Style.STROKE
            this.strokeWidth = strokeWidth
          }
      canvas.drawPath(path, strokePaint)

      BitmapDescriptorFactory.fromBitmap(styledBitmap)
    } catch (e: Exception) {
      Log.e("BitmapDescriptorError", "Failed to load image with styling from URL: $imageUrl", e)
      null
    }
  }
}
