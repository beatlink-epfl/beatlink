package com.android.sample.model.auth

interface AuthRepository {
  fun signUp(
      email: String,
      password: String,
      username: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  )

  fun login(email: String, password: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
}
