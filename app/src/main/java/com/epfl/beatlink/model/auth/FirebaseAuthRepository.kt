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
}
