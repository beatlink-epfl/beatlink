package com.epfl.beatlink.repository.profile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.epfl.beatlink.model.profile.ProfileRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.io.ByteArrayOutputStream
import kotlinx.coroutines.tasks.await

@Suppress("UNCHECKED_CAST")
/**
 * A repository for fetching and updating user profiles from Firestore.
 *
 * @param db The Firestore instance to use for database operations.
 * @param auth The FirebaseAuth instance to use for authentication operations.
 */
class ProfileRepositoryFirestore(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
) : ProfileRepository {
  private val collection = "userProfiles"

  override suspend fun fetchProfile(userId: String): ProfileData? {
    return try {
      val snapshot = db.collection(collection).document(userId).get().await()
      // Manually retrieve fields from the snapshot
      val bio = snapshot.getString("bio")
      val links = snapshot.getLong("links")?.toInt() ?: 0
      val name = snapshot.getString("name")
      val profilePicture = snapshot.getString("profilePicture")
      val username = snapshot.getString("username") ?: ""
      val favoriteMusicGenres = snapshot.get("favoriteMusicGenres") as? List<String> ?: emptyList()

      // Parse profilePicture field to Uri, if it's not null
      // val profilePicture = profilePictureUrl?.let { Uri.parse(it) }

      // Construct the ProfileData instance
      val data =
          ProfileData(
              bio = bio,
              links = links,
              name = name,
              profilePicture = profilePicture,
              username = username,
              favoriteMusicGenres = favoriteMusicGenres)
      Log.d("PROFILE_FETCH", "Fetched profile data: $data") // Added log for debugging
      data
    } catch (e: Exception) {
      e.printStackTrace()
      Log.e("PROFILE_FETCH_ERROR", "Error fetching profile: ${e.message}") // Log error details
      null
    }
  }

  override suspend fun updateProfile(userId: String, profileData: ProfileData): Boolean {
    return try {
      db.collection(collection).document(userId).set(profileData).await()
      true
    } catch (e: Exception) {
      e.printStackTrace()
      Log.e("PROFILE_UPDATE_ERROR", "Error updating profile: ${e.message}") // Log error details
      false
    }
  }

  override fun getUserId(): String? {
    val userId = auth.currentUser?.uid
    Log.d("AUTH", "Current user ID: $userId") // Log user ID for debugging
    return userId
  }

  fun base64ToBitmap(base64: String): Bitmap? {
    return try {
      val bytes = Base64.decode(base64, Base64.DEFAULT)
      BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    } catch (e: Exception) {
      Log.e("BASE64", "Error decoding Base64 to Bitmap: ${e.message}")
      null
    }
  }

  private fun saveProfilePictureBase64(userId: String, base64Image: String) {
    val userDoc = db.collection(collection).document(userId)
    val profileData = mapOf("profilePicture" to base64Image)

    userDoc
        .set(profileData, SetOptions.merge())
        .addOnSuccessListener { Log.d("FIRESTORE", "Base64 image saved successfully!") }
        .addOnFailureListener { e -> Log.e("FIRESTORE", "Error saving Base64 image: ${e.message}") }
  }

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

  fun uploadProfilePicture(imageUri: Uri, context: Context, userId: String) {
    val base64Image = resizeAndCompressImageFromUri(imageUri, context)
    if (base64Image != null) {
      saveProfilePictureBase64(userId, base64Image)
    } else {
      Log.e("UPLOAD", "Failed to convert image to Base64")
    }
  }

  fun loadProfilePicture(userId: String, onBitmapLoaded: (Bitmap?) -> Unit) {
    val userDoc = db.collection(collection).document(userId)

    userDoc
        .get()
        .addOnSuccessListener { document ->
          if (document != null && document.contains("profilePicture")) {
            val base64Image = document.getString("profilePicture")
            val bitmap = base64Image?.let { base64ToBitmap(it) }
            onBitmapLoaded(bitmap) // Pass the bitmap to the composable
          } else {
            Log.e("LOAD", "Profile picture not found")
            onBitmapLoaded(null) // Handle the case where no image is found
          }
        }
        .addOnFailureListener { e ->
          Log.e("LOAD", "Error retrieving profile picture: ${e.message}")
          onBitmapLoaded(null) // Handle errors gracefully
        }
  }
}
