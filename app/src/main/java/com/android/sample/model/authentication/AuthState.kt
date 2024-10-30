package com.android.sample.model.authentication

sealed class AuthState {
  object Idle : AuthState() // Represents the initial state

  object Success : AuthState() // Represents successful authentication

  data class Error(val message: String) : AuthState() // Represents error with a message
}
