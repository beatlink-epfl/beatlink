package com.android.sample.model.profile

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await

class ProfileRepository(private val firestore: FirebaseFirestore) {
  suspend fun getProfile(userId: String): ProfileData? {
    return try {
      val snapshot = firestore.collection("userProfiles").document(userId).get().await()
      snapshot.toObject<ProfileData>()
    } catch (e: Exception) {
      e.printStackTrace()
      null
    }
  }
}
