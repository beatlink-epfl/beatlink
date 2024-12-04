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

  /**
   * Delete the account of the currently authenticated user.
   *
   * @param currentPassword The current password of the user.
   * @param onSuccess Callback on successful account deletion.
   * @param onFailure Callback that is invoked if an error occurs.
   */
  fun deleteAccount(
      currentPassword: String,
      onSuccess: () -> Unit,
      onFailure: (Exception /* = Exception */) -> Unit
  )

  /**
   * Sign out the currently authenticated user.
   *
   * @param onSuccess Callback on successful sign-out.
   * @param onFailure Callback that is invoked if an error occurs.
   */
  fun signOut(onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Get the signing state of the currently authenticated user.
   *
   * @return A boolean indicating whether the user is signed in.
   */
  fun isUserSignedIn(): Boolean
}
