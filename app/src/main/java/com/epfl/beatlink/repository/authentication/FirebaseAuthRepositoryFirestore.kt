package com.epfl.beatlink.repository.authentication

import android.util.Log
import com.epfl.beatlink.model.auth.FirebaseAuthRepository
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepositoryFirestore(private val auth: FirebaseAuth) : FirebaseAuthRepository {

  override fun signUp(
      email: String,
      password: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
      if (task.isSuccessful) {
        onSuccess()
      } else {
        Log.e("AuthRepositoryFirestore", "Sign up failed: ${task.exception?.message}")
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
