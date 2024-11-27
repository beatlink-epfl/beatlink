package com.epfl.beatlink.viewmodel.profile

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
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

  fun getUsername(userId: String, onResult: (String?) -> Unit) {
    viewModelScope.launch {
      try {
        val username = repository.getUsername(userId)
        onResult(username)
      } catch (e: Exception) {
        Log.e("ERROR", "Error fetching username", e)
        onResult(null)
      }
    }
  }

  fun getUserIdByUsername(username: String, onResult: (String?) -> Unit) {
    viewModelScope.launch {
      try {
        val userId = repository.getUserIdByUsername(username)
        onResult(userId)
      } catch (e: Exception) {
        Log.e("ERROR", "Error fetching user id", e)
        onResult(null)
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

  fun handlePermissionResult(
      isGranted: Boolean,
      galleryLauncher: ManagedActivityResultLauncher<String, Uri?>,
      context: Context
  ) {
    if (isGranted) {
      galleryLauncher.launch("image/*")
    } else {
      Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
    }
  }

  @Composable
  fun permissionLauncher(
      context: Context,
      onResult: (Uri?) -> Unit
  ): ManagedActivityResultLauncher<String, Boolean> {
    val galleryLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(), onResult = onResult)
    val permissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {
            isGranted: Boolean ->
          handlePermissionResult(isGranted, galleryLauncher, context)
        }
    return permissionLauncher
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
