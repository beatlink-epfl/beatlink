package com.android.sample.model.authentication

import android.util.Log
import com.android.sample.model.profile.ProfileData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepositoryFirestore(private val db: FirebaseFirestore, private val auth: FirebaseAuth) :
    AuthRepository {
  private val collectionPath = "userProfiles"

  override fun signUp(
      email: String,
      password: String,
      username: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
      if (task.isSuccessful) {
        val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
        addUsername(userId, username, onSuccess, onFailure)
      } else {
        Log.e("AuthRepositoryFirestore", "User sign up failed: ${task.exception?.message}")
        task.exception?.let { onFailure(it) }
      }
    }
  }

  override fun login(
      email: String,
      password: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
      if (task.isSuccessful) {
        onSuccess()
      } else {
        Log.e("AuthRepositoryFirestore", "Login failed: ${task.exception?.message}")
        task.exception?.let { onFailure(it) }
      }
    }
  }

  private fun addUsername(
      userId: String,
      username: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val profileData =
        ProfileData(username = username, name = null, bio = null, links = 0, profilePicture = null)

    db.collection(collectionPath).document(userId).set(profileData).addOnCompleteListener { task ->
      if (task.isSuccessful) {
        onSuccess()
      } else {
        Log.e("AuthRepositoryFirestore", "Error adding username: ${task.exception?.message}")
        task.exception?.let { exception -> onFailure(exception) }
      }
    }
  }
}
