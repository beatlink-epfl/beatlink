package com.epfl.beatlink.ui.library
/*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.epfl.beatlink.R
import java.io.ByteArrayOutputStream

val playlistid = "0d4xjwGoV2GUrxgVMdYymZ"
// Decode the drawable resource into a Bitmap
val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.cover_test1)

// Resize the bitmap to reduce its resolution (e.g., 600x600)
val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 600, 600, true)

// Convert the Bitmap to a byte array in JPEG format
val byteArrayOutputStream = ByteArrayOutputStream()
resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream) // Compress as JPEG
val byteArray = byteArrayOutputStream.toByteArray()

// Encode the byte array to Base64
val image =  Base64.encodeToString(byteArray, Base64.NO_WRAP) // NO_WRAP to remove newlines
spotifyApiViewModel.addCustomPlaylistCoverImage(
playlistid, image)

*/