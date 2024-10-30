package com.android.sample.model.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: ProfileRepository) : ViewModel() {
  private val _profile = MutableStateFlow<ProfileData?>(null)
  val profile: StateFlow<ProfileData?> = _profile

  fun fetchProfile(userId: String) {
    viewModelScope.launch {
      val userProfile = repository.getProfile(userId)
      _profile.value = userProfile
    }
  }

  // create factory
  companion object {
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProfileViewModel(ProfileRepository(Firebase.firestore)) as T
          }
        }
  }
}
