package com.epfl.beatlink.viewmodel.profile

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.epfl.beatlink.model.profile.ProfileData
import com.epfl.beatlink.model.profile.ProfileRepository
import com.epfl.beatlink.repository.profile.ProfileRepositoryFirestore
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * A ViewModel for fetching and updating user profiles.
 *
 * @param repository The repository to use for fetching and updating user profiles.
 */
open class ProfileViewModel(
    private val repository: ProfileRepository,
    initialProfile: ProfileData? = null,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

  private val _profile = MutableStateFlow(initialProfile)
  val profile: StateFlow<ProfileData?>
    get() = _profile

  private val _searchResult = MutableLiveData<List<ProfileData>>(emptyList())
  val searchResult: LiveData<List<ProfileData>>
    get() = _searchResult

  fun fetchProfile() {
    val userId = repository.getUserId() ?: return
    viewModelScope.launch {
      val userProfile = repository.fetchProfile(userId)
      _profile.value = userProfile
    }
  }

  fun addProfile(profileData: ProfileData) {
    val userId = repository.getUserId() ?: return
    viewModelScope.launch {
      val success = repository.addProfile(userId, profileData)
      if (success) {
        _profile.value = profileData
      }
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

  fun uploadProfilePicture(context: Context, uri: Uri) {
    val userId = repository.getUserId() ?: return
    viewModelScope.launch(dispatcher) { repository.uploadProfilePicture(uri, context, userId) }
  }

  fun loadProfilePicture(onBitmapLoaded: (Bitmap?) -> Unit) {
    val userId = repository.getUserId() ?: return
    return repository.loadProfilePicture(userId, onBitmapLoaded)
  }

  fun deleteProfile() {
    val userId = repository.getUserId() ?: return
    viewModelScope.launch {
      if (repository.deleteProfile(userId)) {
        _profile.value = null
      } else {
        Log.e("DELETE_PROFILE", "Error deleting profile")
      }
    }
  }

  fun searchUsers(query: String) {
    viewModelScope.launch {
      try {
        val profiles = repository.searchUsers(query)
        _searchResult.value = profiles
      } catch (e: Exception) {
        Log.e("SEARCH_PROFILES", "Error searching profiles: ${e.message}")
        _searchResult.value = emptyList()
      }
    }
  }

  // Create factory
  companion object {
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val firebaseAuth = FirebaseAuth.getInstance()
            return ProfileViewModel(ProfileRepositoryFirestore(Firebase.firestore, firebaseAuth))
                as T
          }
        }
  }
}
