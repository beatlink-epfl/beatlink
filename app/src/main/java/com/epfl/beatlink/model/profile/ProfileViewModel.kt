package com.epfl.beatlink.model.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: ProfileRepositoryFirestore) : ViewModel() {
  private val _profile = MutableStateFlow<ProfileData?>(null)
  val profile: StateFlow<ProfileData?>
    get() = _profile

  fun fetchProfile() {
    val userId = repository.getUserId() ?: return
    Log.d("USER", "User ID: $userId")
    viewModelScope.launch {
      val userProfile = repository.fetchProfile(userId)
      _profile.value = userProfile
    }
  }

  fun updateProfile(profileData: ProfileData) {
    val userId = repository.getUserId() ?: return
    viewModelScope.launch {
      val success = repository.updateProfile(userId, profileData)
      if (success) {
        _profile.value = profileData
      }
    }
  }

  // create factory
  companion object {
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val firebaseAuth = FirebaseAuth.getInstance()
            return ProfileViewModel(ProfileRepositoryFirestore(Firebase.firestore, firebaseAuth)) as T
          }
        }
  }
}
