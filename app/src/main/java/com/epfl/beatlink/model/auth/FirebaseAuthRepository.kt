package com.epfl.beatlink.model.auth

interface FirebaseAuthRepository {
  fun signUp(
      email: String,
      password: String,
      username: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  )

  fun login(email: String, password: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  suspend fun changePassword(newPassword: String): Result<Unit>

  suspend fun verifyPassword(currentPassword: String): Result<Unit>

  fun signOut(onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
}
