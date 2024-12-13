package com.epfl.beatlink.viewmodel.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.epfl.beatlink.model.auth.AuthState
import com.epfl.beatlink.model.auth.FirebaseAuthRepository
import com.epfl.beatlink.repository.authentication.FirebaseAuthRepositoryFirestore
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FirebaseAuthViewModel(private val authRepository: FirebaseAuthRepository) : ViewModel() {
  private val _authState = MutableStateFlow<AuthState>(AuthState.Idle) // Start with Idle state
  val authState: StateFlow<AuthState> = _authState.asStateFlow()

  fun signUp(email: String, password: String) {
    viewModelScope.launch {
      authRepository.signUp(
          email,
          password,
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

  fun verifyAndChangePassword(currentPassword: String, newPassword: String) {
    viewModelScope.launch {
      val verificationResult = authRepository.verifyPassword(currentPassword)
      if (verificationResult.isSuccess) {
        val changeResult = authRepository.changePassword(newPassword)
        changeResult.fold(
            onSuccess = { _authState.value = AuthState.Success },
            onFailure = { exception ->
              Log.e("AuthViewModel", "Change password failed: ${exception.message}")
              _authState.value = AuthState.Error("Change password failed: ${exception.message}")
            })
      } else {
        Log.e("AuthViewModel", "Verification failed: Current password is incorrect")
        _authState.value = AuthState.Error("Verification failed: Current password is incorrect")
      }
    }
  }

  suspend fun verifyPassword(currentPassword: String): Result<Unit> {
    val verificationResult = authRepository.verifyPassword(currentPassword)
    return verificationResult
  }

  fun deleteAccount(
      currentPassword: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    viewModelScope.launch {
      authRepository.deleteAccount(
          currentPassword,
          onSuccess = {
            resetState()
            onSuccess() // Trigger the success callback
          },
          onFailure = { exception ->
            _authState.value = AuthState.Error("Account deletion failed: ${exception.message}")
            onFailure(exception) // Trigger the failure callback
          })
    }
  }

  fun signOut(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    viewModelScope.launch {
      authRepository.signOut(
          onSuccess = {
            resetState()
            onSuccess()
          },
          onFailure = { exception ->
            _authState.value = AuthState.Error("Sign out failed: ${exception.message}")
            onFailure(exception)
          })
    }
  }

  fun resetState() {
    _authState.value = AuthState.Idle
  }

  fun isSignedIn(): Boolean {
    return authRepository.isUserSignedIn()
  }

  // Create factory
  companion object {
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val firebaseAuth = FirebaseAuth.getInstance()
            return FirebaseAuthViewModel(FirebaseAuthRepositoryFirestore(firebaseAuth)) as T
          }
        }
  }
}
