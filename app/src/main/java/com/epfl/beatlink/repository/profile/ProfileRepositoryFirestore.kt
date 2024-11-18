package com.epfl.beatlink.repository.profile

import android.net.Uri
import android.util.Log
import com.epfl.beatlink.model.profile.ProfileData
import com.epfl.beatlink.model.profile.ProfileRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
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
    private val storage: FirebaseStorage
) : ProfileRepository {
  override suspend fun fetchProfile(userId: String): ProfileData? {
    return try {
      val snapshot = db.collection("userProfiles").document(userId).get().await()
      // Manually retrieve fields from the snapshot
      val bio = snapshot.getString("bio")
      val links = snapshot.getLong("links")?.toInt() ?: 0
      val name = snapshot.getString("name")
      val profilePictureUrl = snapshot.getString("profilePicture")
      val username = snapshot.getString("username") ?: ""
      val favoriteMusicGenres = snapshot.get("favoriteMusicGenres") as? List<String> ?: emptyList()

      // Parse profilePicture field to Uri, if it's not null
      val profilePicture = profilePictureUrl?.let { Uri.parse(it) }

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
      db.collection("userProfiles").document(userId).set(profileData).await()
      true
    } catch (e: Exception) {
      e.printStackTrace()
      Log.e("PROFILE_UPDATE_ERROR", "Error updating profile: ${e.message}") // Log error details
      false
    }
  }

  // Uploads the image, saves the URL to Firestore, and returns the URL
  /*override suspend fun uploadProfilePicture(imageUri: File): String? {
    val userId = getUserId() ?: return null
    val uuid = UUID.randomUUID()
    val profilePictureRef = storage.reference.child("profilePictures/$userId/$uuid")

    return try {
      Log.d("UPLOAD_START", "Uploading profile picture $imageUri at profilePictures/$userId/$uuid")
      Log.d("FILE_CHECK", "File exists: ${imageUri.exists()}, can read: ${imageUri.canRead()}")
      Log.d("PROFILE_PICTURE_REF", "Storage reference path: ${profilePictureRef.path}")
      Log.d("AUTH_CHECK", "Current user ID: ${FirebaseAuth.getInstance().currentUser}")

      profilePictureRef.putFile(Uri.fromFile(imageUri)).await()
      Log.d("UPLOAD_SUCCESS", "Upload completed successfully.")

      val downloadUrl = profilePictureRef.downloadUrl.await()
      Log.d("UPLOAD_SUCCESS", "Download URL retrieved: $downloadUrl")

      db.collection("userProfiles")
          .document(userId)
          .update("profilePicture", downloadUrl.toString()) // Store URL as String in Firestore
      downloadUrl.toString()
    } catch (e: Exception) {
      e.printStackTrace()
      Log.e(
          "PROFILE_PICTURE_UPLOAD_ERROR",
          "Error uploading profile picture: ${e.message}") // Log error details
      null
    }
  }*/

  override fun getUserId(): String? {
    val userId = auth.currentUser?.uid
    Log.d("AUTH", "Current user ID: $userId") // Log user ID for debugging
    return userId
  }
}
