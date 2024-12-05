package com.epfl.beatlink.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import java.io.ByteArrayOutputStream

object ImageUtils {
  /**
   * Resize and compress an image from a URI and convert it to a Base64-encoded string.
   *
   * @param uri The URI of the image.
   * @param context The application context.
   * @param maxWidth The maximum width for resizing the image (default is 512 pixels).
   * @param maxHeight The maximum height for resizing the image (default is 512 pixels).
   * @param quality The quality level for JPEG compression, ranging from 0 to 100 (default is 80,
   *   where 100 is maximum quality).
   * @return A Base64-encoded string representing the resized and compressed image, or `null` if the
   *   operation fails.
   */
  fun resizeAndCompressImageFromUri(
      uri: Uri,
      context: Context,
      maxWidth: Int = 512,
      maxHeight: Int = 512,
      quality: Int = 80
  ): String? {
    return try {
      val contentResolver = context.contentResolver
      val inputStream = contentResolver.openInputStream(uri)
      val originalBitmap = BitmapFactory.decodeStream(inputStream)
      inputStream?.close()

      // Resize the bitmap
      val aspectRatio = originalBitmap.width.toFloat() / originalBitmap.height
      val resizedBitmap =
          if (aspectRatio > 1) {
            // Landscape image
            Bitmap.createScaledBitmap(
                originalBitmap, maxWidth, (maxWidth / aspectRatio).toInt(), true)
          } else {
            // Portrait image
            Bitmap.createScaledBitmap(
                originalBitmap, (maxHeight * aspectRatio).toInt(), maxHeight, true)
          }

      // Compress the resized bitmap
      val outputStream = ByteArrayOutputStream()
      resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
      val compressedBytes = outputStream.toByteArray()

      // Convert to Base64
      Base64.encodeToString(compressedBytes, Base64.DEFAULT)
    } catch (e: Exception) {
      Log.e("COMPRESS", "Error resizing and compressing image: ${e.message}")
      null
    }
  }

  /**
   * Convert a Base64-encoded string to a Bitmap.
   *
   * @param base64 The Base64-encoded string representation of the image.
   * @return A Bitmap object if the conversion is successful, or `null` if an error occurs.
   */
  fun base64ToBitmap(base64: String): Bitmap? {
    return try {
      val bytes = Base64.decode(base64, Base64.DEFAULT)
      BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    } catch (e: Exception) {
      Log.e("BASE64", "Error decoding Base64 to Bitmap: ${e.message}")
      null
    }
  }

  /**
   * Handle the result of a permission request.
   *
   * @param isGranted `true` if the permission was granted, `false` otherwise.
   * @param galleryLauncher The launcher to open the gallery if permission is granted.
   * @param context The application context.
   */
  fun handlePermissionResult(
      isGranted: Boolean,
      galleryLauncher: ManagedActivityResultLauncher<String, Uri?>,
      context: Context
  ) {
    if (isGranted) {
      galleryLauncher.launch("image/*")
    } else {
      Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
    }
  }

  @Composable
  /**
   * Create a permission launcher for requesting storage permissions and opening the gallery.
   *
   * @param context The application context.
   * @param onResult A callback function that is called with the URI of the selected image.
   * @return A [ManagedActivityResultLauncher] for requesting permissions.
   */
  fun permissionLauncher(
      context: Context,
      onResult: (Uri?) -> Unit
  ): ManagedActivityResultLauncher<String, Boolean> {
    val galleryLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(), onResult = onResult)
    val permissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {
            isGranted: Boolean ->
          handlePermissionResult(isGranted, galleryLauncher, context)
        }
    return permissionLauncher
  }
}
