package com.android.sample.model.authentication

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FirebaseAuthViewModel(private val authRepository: FirebaseAuthRepository) : ViewModel() {
  private val _authState = MutableStateFlow<AuthState>(AuthState.Idle) // Start with Idle state
  val authState: StateFlow<AuthState> = _authState.asStateFlow()

  fun signUp(email: String, password: String, username: String) {
    viewModelScope.launch {
      authRepository.signUp(
          email,
          password,
          username,
          onSuccess = { _authState.value = AuthState.Success },
          onFailure = { exception ->
            Log.e("AuthViewModel", "Sign up failed: ${exception.message}")
            _authState.value = AuthState.Error("Sign up failed: ${exception.message}")
          })
    }
  }

  fun login(email: String, password: String) {
    viewModelScope.launch {
      authRepository.login(
          email,
          password,
          onSuccess = { _authState.value = AuthState.Success },
          onFailure = { exception ->
            Log.e("AuthViewModel", "Login failed: ${exception.message}")
            _authState.value = AuthState.Error("Login failed: ${exception.message}")
          })
    }
  }

  fun resetState() {
    _authState.value = AuthState.Idle
  }

  // ViewModel factory
  companion object {
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val firestore = FirebaseFirestore.getInstance()
            val firebaseAuth = FirebaseAuth.getInstance()
            return FirebaseAuthViewModel(FirebaseAuthRepositoryFirestore(firestore, firebaseAuth))
                as T
          }
        }
  }
}
