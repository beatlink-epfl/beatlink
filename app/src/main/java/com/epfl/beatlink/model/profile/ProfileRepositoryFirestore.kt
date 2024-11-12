package com.epfl.beatlink.model.profile

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await

class ProfileRepositoryFirestore(private val db: FirebaseFirestore, private val auth: FirebaseAuth) : ProfileRepository {
  override suspend fun fetchProfile(userId: String): ProfileData? {
    return try {
      val snapshot = db.collection("userProfiles").document(userId).get().await()
      val data = snapshot.toObject<ProfileData>()
      Log.d("PROFILE_FETCH", "Fetched profile data: $data") // Added log for debugging
      data
    } catch (e: Exception) {
      e.printStackTrace()
      Log.e("PROFILE_FETCH_ERROR", "Error fetching profile: ${e.message}") // Log error details
      null
    }
  }

  override fun updateProfile(userId: String, profileData: ProfileData): Boolean {
    return try {
      db.collection("userProfiles").document(userId).set(profileData).isSuccessful
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
}
