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
 * A repository for managing user profiles using Firestore.
 *
 * @param db The Firestore instance to use for database operations.
 * @param auth The FirebaseAuth instance to use for authentication operations.
 * @param storage The FirebaseStorage instance to use for file storage operations.
 */
class ProfileRepositoryFirestore(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage
) : ProfileRepository {

  private val collectionPath = "userProfiles"

  override fun getUserId(): String? {
    val userId = auth.currentUser?.uid
    Log.d("PROFILE_GET_UID", "Current user ID: $userId")
    return userId
  }

  override suspend fun fetchProfile(userId: String): ProfileData? {
    return try {
      val snapshot = db.collection(collectionPath).document(userId).get().await()
      val profilePictureUrl = snapshot.getString("profilePicture")
      val profileData =
          ProfileData(
              bio = snapshot.getString("bio"),
              links = snapshot.getLong("links")?.toInt() ?: 0,
              name = snapshot.getString("name"),
              profilePicture = profilePictureUrl?.let { Uri.parse(it) },
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
      db.collection(collectionPath).document(userId).set(profileData).await()
      Log.d("PROFILE_ADD", "Profile added successfully for user: $userId")
      true
    } catch (e: Exception) {
      Log.e("PROFILE_ADD_ERROR", "Error adding profile: ${e.message}")
      false
    }
  }

  override suspend fun updateProfile(userId: String, profileData: ProfileData): Boolean {
    return try {
      db.collection(collectionPath).document(userId).set(profileData).await()
      Log.d("PROFILE_UPDATE", "Profile updated successfully for user: $userId")
      true
    } catch (e: Exception) {
      Log.e("PROFILE_UPDATE_ERROR", "Error updating profile: ${e.message}")
      false
    }
  }

  override suspend fun deleteProfile(userId: String): Boolean {
    return try {
      db.collection(collectionPath).document(userId).delete().await()
      Log.d("PROFILE_DELETE", "Profile deleted successfully for user: $userId")
      true
    } catch (e: Exception) {
      Log.e("PROFILE_DELETE_ERROR", "Error deleting profile: ${e.message}")
      false
    }
  }

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
}
