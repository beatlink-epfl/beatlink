package com.epfl.beatlink.ui.map

import android.content.Context
import android.graphics.Bitmap
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

fun getBitmapDescriptorFromDrawableResource(
    resourceId: Int,
    context: Context,
    width: Int,
    height: Int
): BitmapDescriptor {
  val drawable = ContextCompat.getDrawable(context, resourceId)
  val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
  val canvas = android.graphics.Canvas(bitmap)
  drawable!!.setBounds(0, 0, width, height)
  drawable.draw(canvas)
  return BitmapDescriptorFactory.fromBitmap(bitmap)
}
