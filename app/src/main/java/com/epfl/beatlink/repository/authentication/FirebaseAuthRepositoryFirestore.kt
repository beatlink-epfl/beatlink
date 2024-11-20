package com.epfl.beatlink.repository.authentication

import android.util.Log
import com.epfl.beatlink.model.auth.FirebaseAuthRepository
import com.epfl.beatlink.repository.profile.ProfileData
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepositoryFirestore(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) : FirebaseAuthRepository {
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

  override fun signOut(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    try {
      auth.signOut()
      onSuccess()
    } catch (e: Exception) {
      Log.e("AuthRepositoryFirestore", "Sign out failed: ${e.message}")
      onFailure(e)
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

  override suspend fun verifyPassword(currentPassword: String): Result<Unit> {
    val user: FirebaseUser? = auth.currentUser
    return if (user != null) {
      val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
      try {
        user.reauthenticate(credential).await()
        Result.success(Unit)
      } catch (e: Exception) {
        Result.failure(e)
      }
    } else {
      Result.failure(Exception("User not authenticated"))
    }
  }

  override suspend fun changePassword(newPassword: String): Result<Unit> {
    val user: FirebaseUser? = auth.currentUser
    return if (user != null) {
      try {
        user.updatePassword(newPassword).await()
        Result.success(Unit)
      } catch (e: Exception) {
        Result.failure(e)
      }
    } else {
      Result.failure(Exception("User not authenticated"))
    }
  }
}
