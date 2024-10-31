package com.android.sample.model.authentication

interface FirebaseAuthRepository {
  fun signUp(
      email: String,
      password: String,
      username: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  )

  fun login(email: String, password: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
}
