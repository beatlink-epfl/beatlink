package com.android.sample.ui.map

import android.content.Context
import android.graphics.Bitmap
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

fun getBitmapDescriptorFromDrawableResource(resourceId: Int, context: Context): BitmapDescriptor {
  val drawable = ContextCompat.getDrawable(context, resourceId)
  val canvas = android.graphics.Canvas()
  val bitmap =
      Bitmap.createBitmap(
          drawable!!.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
  canvas.setBitmap(bitmap)
  drawable.setBounds(0, 0, canvas.width, canvas.height)
  drawable.draw(canvas)
  return BitmapDescriptorFactory.fromBitmap(bitmap)
}
