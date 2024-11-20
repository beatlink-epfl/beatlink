package com.epfl.beatlink.model.auth

interface FirebaseAuthRepository {
  /**
   * Sign up a new user with email and password.
   *
   * @param email The user's email address.
   * @param password The user's password.
   * @param onSuccess Callback on successful sign-up.
   * @param onFailure Callback that is invoked if an error occurs.
   */
  fun signUp(email: String, password: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Log in an existing user with email and password.
   *
   * @param email The user's email address.
   * @param password The user's password.
   * @param onSuccess Callback on successful login.
   * @param onFailure Callback that is invoked if an error occurs.
   */
  fun login(email: String, password: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Change the password of the currently authenticated user.
   *
   * @param newPassword The new password to be set.
   * @return A [Result] object containing [Unit] if the password was successfully changed, or an
   *   [Exception] if an error occurred.
   */
  suspend fun changePassword(newPassword: String): Result<Unit>

  /**
   * Verify the current password of the authenticated user.
   *
   * @param currentPassword The current password of the user.
   * @return A [Result] object containing [Unit] if the password was successfully verified, or an
   *   [Exception] if an error occurred.
   */
  suspend fun verifyPassword(currentPassword: String): Result<Unit>
}
