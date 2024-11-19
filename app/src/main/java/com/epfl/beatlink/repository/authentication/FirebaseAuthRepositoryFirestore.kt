package com.epfl.beatlink.repository.authentication

import android.util.Log
import com.epfl.beatlink.model.auth.FirebaseAuthRepository
import com.google.firebase.auth.FirebaseAuth

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
}
