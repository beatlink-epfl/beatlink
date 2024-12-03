package com.epfl.beatlink.repository.authentication

import android.util.Log
import com.epfl.beatlink.model.auth.FirebaseAuthRepository
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

private const val USER_NOT_AUTHENTICATED = "User not authenticated"

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

  override fun isUserSignedIn() = auth.currentUser != null

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
      Result.failure(Exception(USER_NOT_AUTHENTICATED))
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
      Result.failure(Exception(USER_NOT_AUTHENTICATED))
    }
  }

  override fun deleteAccount(
      currentPassword: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val user: FirebaseUser? = auth.currentUser
    if (user == null) {
      onFailure(Exception(USER_NOT_AUTHENTICATED))
      return
    }

    val email = user.email
    if (email == null) {
      onFailure(Exception("User email not available"))
      return
    }

    val credential = EmailAuthProvider.getCredential(email, currentPassword)
    user.reauthenticate(credential).addOnCompleteListener { reauthTask ->
      if (reauthTask.isSuccessful) {
        deleteUser(user, onSuccess, onFailure)
      } else {
        Log.e(
            "AuthRepositoryFirestore", "Reauthentication failed: ${reauthTask.exception?.message}")
        reauthTask.exception?.let { onFailure(it) }
      }
    }
  }

  private fun deleteUser(
      user: FirebaseUser,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    user.delete().addOnCompleteListener { deleteTask ->
      if (deleteTask.isSuccessful) {
        onSuccess()
      } else {
        Log.e(
            "AuthRepositoryFirestore", "Account deletion failed: ${deleteTask.exception?.message}")
        deleteTask.exception?.let { onFailure(it) }
      }
    }
  }
}
