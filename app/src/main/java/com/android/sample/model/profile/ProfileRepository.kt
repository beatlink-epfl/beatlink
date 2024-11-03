package com.android.sample.model.profile

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await

class ProfileRepository(private val db: FirebaseFirestore, private val auth: FirebaseAuth) {
  suspend fun getProfile(userId: String): ProfileData? {
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

  fun getUserId(): String? {
    val userId = auth.currentUser?.uid
    Log.d("AUTH", "Current user ID: $userId") // Log user ID for debugging
    return userId
  }
}
