package com.epfl.beatlink.repository.profile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.epfl.beatlink.model.profile.ProfileData
import com.epfl.beatlink.model.profile.ProfileRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.io.ByteArrayOutputStream
import kotlinx.coroutines.tasks.await

@Suppress("UNCHECKED_CAST")
/**
 * A repository for managing user profiles using Firestore.
 *
 * @param db The Firestore instance to use for database operations.
 * @param auth The FirebaseAuth instance to use for authentication operations.
 */
open class ProfileRepositoryFirestore(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
) : ProfileRepository {
  private val collection = "userProfiles"

  override fun getUserId(): String? {
    val userId = auth.currentUser?.uid
    Log.d("AUTH", "Current user ID: $userId") // Log user ID for debugging
    return userId
  }

  override suspend fun fetchProfile(userId: String): ProfileData? {
    return try {
      val snapshot = db.collection(collection).document(userId).get().await()
      val profileData =
          ProfileData(
              bio = snapshot.getString("bio"),
              links = snapshot.getLong("links")?.toInt() ?: 0,
              name = snapshot.getString("name"),
              profilePicture = snapshot.getString("profilePicture"),
              username = snapshot.getString("username") ?: "",
              favoriteMusicGenres =
                  snapshot.get("favoriteMusicGenres") as? List<String> ?: emptyList())
      Log.d("PROFILE_FETCH", "Fetched profile data: $profileData")
      profileData
    } catch (e: Exception) {
      Log.e("PROFILE_FETCH_ERROR", "Error fetching profile: ${e.message}")
      null
    }
  }

  override suspend fun addProfile(userId: String, profileData: ProfileData): Boolean {
    return try {
      db.runTransaction { transaction ->
        // Add profile to `userProfiles` collection
        transaction.set(db.collection(collection).document(userId), profileData)

        // Add the username to the `usernames` collection
        val usernameDocRef = db.collection("usernames").document(profileData.username)
        transaction.set(usernameDocRef, mapOf<String, Any>())
      }.await()
      Log.d("PROFILE_ADD", "Profile and username added successfully for user: $userId")
      true
    } catch (e: Exception) {
      Log.e("PROFILE_ADD_ERROR", "Error adding profile: ${e.message}")
      false
    }
  }

  override suspend fun updateProfile(userId: String, profileData: ProfileData): Boolean {
    return try {
      db.runTransaction { transaction ->
        // Update user profile
        val profileDocRef = db.collection(collection).document(userId)
        transaction.set(profileDocRef, profileData)

        // Get the current username from the profile
        val userSnapshot = transaction.get(profileDocRef)
        val currentUsername = userSnapshot.getString("username")

        // Check if the username has changed
        if (currentUsername != null && currentUsername != profileData.username) {
          // Delete the current username
          transaction.delete(db.collection("usernames").document(currentUsername))

          // Add the new username
          val newUsernameDocRef = db.collection("usernames").document(profileData.username)
          transaction.set(newUsernameDocRef, mapOf<String, Any>())
        }
      }.await()
      Log.d("PROFILE_UPDATE", "Profile and username updated successfully for user: $userId")
      true
    } catch (e: Exception) {
      Log.e("PROFILE_UPDATE_ERROR", "Error updating profile: ${e.message}")
      false
    }
  }

  override suspend fun deleteProfile(userId: String): Boolean {
    return try {
      db.runTransaction { transaction ->
        // Delete the user profile
        val profileDocRef = db.collection(collection).document(userId)
        transaction.delete(profileDocRef)

        // Fetch the username from the profile
        val userSnapshot = transaction.get(profileDocRef)
        val username = userSnapshot.getString("username")
        if (username != null) {
          // Delete the username
          val usernameDocRef = db.collection("usernames").document(username)
          transaction.delete(usernameDocRef)
        }
      }.await()
      Log.d("PROFILE_DELETE", "Profile and username deleted successfully for user: $userId")
      true
    } catch (e: Exception) {
      Log.e("PROFILE_DELETE_ERROR", "Error deleting profile: ${e.message}")
      false
    }
  }

  override fun uploadProfilePicture(imageUri: Uri, context: Context, userId: String) {
    val base64Image = resizeAndCompressImageFromUri(imageUri, context)
    if (base64Image != null) {
      saveProfilePictureBase64(userId, base64Image)
    } else {
      Log.e("UPLOAD_PROFILE_PICTURE_ERROR", "Failed to convert image to Base64")
    }
  }

  override fun loadProfilePicture(userId: String, onBitmapLoaded: (Bitmap?) -> Unit) {
    val userDoc = db.collection(collection).document(userId)

    userDoc
        .get()
        .addOnSuccessListener { document ->
          if (document != null && document.contains("profilePicture")) {
            val base64Image = document.getString("profilePicture")
            val bitmap = base64Image?.let { base64ToBitmap(it) }
            onBitmapLoaded(bitmap) // Pass the bitmap to the composable
          } else {
            onBitmapLoaded(null) // Handle the case where no image is found
          }
        }
        .addOnFailureListener { onBitmapLoaded(null) }
  }

  override suspend fun isUsernameAvailable(username: String): Boolean {
    return try {
      // Check if the collection "usernames" exists
      val collectionSnapshot = db.collection("usernames").limit(1).get().await()

      // If the collection does not exist or is empty, return true
      if (collectionSnapshot.isEmpty) {
        return true
      }
      val snapshot = db.collection("usernames").document(username).get().await()
      !snapshot.exists()
    } catch (e: Exception) {
      Log.e("USERNAME_CHECK_ERROR", "Error checking username availability: ${e.message}")
      false
    }
  }

  /**
   * Resize and compress an image from a URI and convert it to a Base64-encoded string.
   *
   * @param uri The URI of the image.
   * @param context The application context.
   * @param maxWidth The maximum width for resizing the image (default is 512 pixels).
   * @param maxHeight The maximum height for resizing the image (default is 512 pixels).
   * @param quality The quality level for JPEG compression, ranging from 0 to 100
   *                (default is 80, where 100 is maximum quality).
   * @return A Base64-encoded string representing the resized and compressed image,
   *         or `null` if the operation fails.
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
   * Save a Base64-encoded profile picture to the `userProfiles` collection in Firestore.
   *
   * @param userId The unique identifier of the user.
   * @param base64Image The Base64-encoded string representation of the profile picture.
   */
  private fun saveProfilePictureBase64(userId: String, base64Image: String) {
    val userDoc = db.collection(collection).document(userId)
    val profileData = mapOf("profilePicture" to base64Image)

    userDoc.set(profileData, SetOptions.merge())
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
}
