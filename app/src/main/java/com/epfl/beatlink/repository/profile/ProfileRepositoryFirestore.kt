package com.epfl.beatlink.repository.profile

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.epfl.beatlink.model.profile.ProfileData
import com.epfl.beatlink.model.profile.ProfileRepository
import com.epfl.beatlink.utils.ImageUtils.base64ToBitmap
import com.epfl.beatlink.utils.ImageUtils.resizeAndCompressImageFromUri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
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

  override suspend fun getUsername(userId: String): String? {
    return try {
      val docRef = db.collection(collection).document(userId).get().await()
      val username = docRef.getString("username")
      Log.d("GET_USERNAME", "Username retrieved successfully for user: $userId")
      username
    } catch (e: Exception) {
      Log.e("GET_USERNAME_ERROR", "Error retrieving username: ${e.message}")
      null
    }
  }

  override suspend fun getUserIdByUsername(username: String): String? {
    return try {
      val docRef = db.collection(collection).whereEqualTo("username", username).get().await()
      val userId = docRef.documents.first().id
      userId
    } catch (e: Exception) {
      Log.e("GET_USERID_ERROR", "Error retrieving user ID: ${e.message}")
      null
    }
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
              email = snapshot.getString("email") ?: "",
              favoriteMusicGenres =
                  snapshot.get("favoriteMusicGenres") as? List<String> ?: emptyList())
      Log.d("PROFILE_FETCH", "Fetched profile data")
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

            // Add an empty document in the `friendRequests` collection for the user
            val requestsDocRef = db.collection("friendRequests").document(userId)
            transaction.set(
                requestsDocRef,
                mapOf(
                    "ownRequests" to mapOf<String, Boolean>(),
                    "friendRequests" to mapOf<String, Boolean>(),
                    "allFriends" to mapOf<String, String>()))
          }
          .await()
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
            // Reference to the profile document
            val profileDocRef = db.collection(collection).document(userId)

            // Read the current profile
            val userSnapshot = transaction.get(profileDocRef)
            val currentUsername = userSnapshot.getString("username")

            // Update user profile
            transaction.set(profileDocRef, profileData)

            // Check if the username has changed
            if (currentUsername != null && currentUsername != profileData.username) {
              // Delete the current username
              transaction.delete(db.collection("usernames").document(currentUsername))

              // Add the new username
              val newUsernameDocRef = db.collection("usernames").document(profileData.username)
              transaction.set(newUsernameDocRef, mapOf<String, Any>())
            }
          }
          .await()
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
            // Reference to the profile document
            val profileDocRef = db.collection(collection).document(userId)

            // Fetch the username from the profile
            val userSnapshot = transaction.get(profileDocRef)
            val username = userSnapshot.getString("username")

            // Delete the user profile
            transaction.delete(profileDocRef)

            // Delete the username if it exists
            if (username != null) {
              val usernameDocRef = db.collection("usernames").document(username)
              transaction.delete(usernameDocRef)
            }

            // Delete the friendRequests document for the user
            val friendRequestDocRef = db.collection("friendRequests").document(userId)
            transaction.delete(friendRequestDocRef)
            // Clean up references to this user ID in other users' friendRequests
            val friendRequestsCollection = db.collection("friendRequests")
            val allFriendRequestsSnapshot = friendRequestsCollection.get().result
            for (doc in allFriendRequestsSnapshot.documents) {
              val docRef = doc.reference
              val updatedOwnRequests = doc.get("ownRequests") as? Map<String, Boolean>
              val updatedFriendRequests = doc.get("friendRequests") as? Map<String, Boolean>

              // Remove userId from ownRequests and friendRequests
              if (updatedOwnRequests?.containsKey(userId) == true) {
                transaction.update(docRef, "ownRequests.$userId", FieldValue.delete())
              }
              if (updatedFriendRequests?.containsKey(userId) == true) {
                transaction.update(docRef, "friendRequests.$userId", FieldValue.delete())
              }
            }
          }
          .await()
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

  override suspend fun searchUsers(query: String): List<ProfileData> {
    return try {
      val snapshot =
          db.collection(collection)
              .whereGreaterThanOrEqualTo("username", query)
              .whereLessThanOrEqualTo("username", query + "\uf8ff")
              .get()
              .await()
      snapshot.documents.mapNotNull { it.toObject(ProfileData::class.java) }
    } catch (e: Exception) {
      Log.e("SEARCH", "Error searching users: ${e.message}")
      emptyList()
    }
  }

  /**
   * Save a Base64-encoded profile picture to the `userProfiles` collection in Firestore.
   *
   * @param userId The unique identifier of the user.
   * @param base64Image The Base64-encoded string representation of the profile picture.
   */
  fun saveProfilePictureBase64(userId: String, base64Image: String) {
    val userDoc = db.collection(collection).document(userId)
    val profileData = mapOf("profilePicture" to base64Image)

    userDoc.set(profileData, SetOptions.merge())
  }
}
