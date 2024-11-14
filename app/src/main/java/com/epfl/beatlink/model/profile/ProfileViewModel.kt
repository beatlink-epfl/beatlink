package com.epfl.beatlink.model.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * A ViewModel for fetching and updating user profiles.
 *
 * @param repository The repository to use for fetching and updating user profiles.
 */
class ProfileViewModel(private val repository: ProfileRepositoryFirestore) : ViewModel() {
  private val _profileImageUrl = MutableLiveData<String?>()
  val profileImageUrl: LiveData<String?>
    get() = _profileImageUrl

  private val _profile = MutableStateFlow<ProfileData?>(null)
  val profile: StateFlow<ProfileData?>
    get() = _profile

  fun fetchProfile() {
    val userId = repository.getUserId() ?: return
    viewModelScope.launch {
      val userProfile = repository.fetchProfile(userId)
      _profile.value = userProfile
    }
  }

  /*fun uploadProfilePicture(imageUri: File) {
    viewModelScope.launch {
      val imageUrl = repository.uploadProfilePicture(imageUri)
      imageUrl?.let {
        _profileImageUrl.value = it // Set URL as String after upload
      }
    }
  }*/

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
            val firebaseStorage = FirebaseStorage.getInstance()
            return ProfileViewModel(
                ProfileRepositoryFirestore(Firebase.firestore, firebaseAuth, firebaseStorage))
                as T
          }
        }
  }
}
