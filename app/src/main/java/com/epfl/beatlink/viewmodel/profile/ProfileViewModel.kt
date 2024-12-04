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

  /** Represents the result of username validation. */
  sealed class UsernameValidationResult {
    /** Indicates that the username is valid. */
    object Valid : UsernameValidationResult()

    /**
     * Indicates that the username is invalid.
     *
     * @param errorMessage A message describing why the username is invalid.
     */
    data class Invalid(val errorMessage: String) : UsernameValidationResult()
  }

  private val _profile = MutableStateFlow(initialProfile)
  val profile: StateFlow<ProfileData?>
    get() = _profile

  private val _searchResult = MutableLiveData<List<ProfileData>>(emptyList())
  val searchResult: LiveData<List<ProfileData>>
    get() = _searchResult

  private val _isProfileUpdated = MutableStateFlow(false)
  val isProfileUpdated: StateFlow<Boolean>
    get() = _isProfileUpdated

  /** Function that updates the profileUpdate flag to true */
  fun markProfileAsUpdated() {
    _isProfileUpdated.value = true
  }

  /** Function that resets the profileUpdate flag to false */
  fun markProfileAsNotUpdated() {
    _isProfileUpdated.value = false
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

  open fun getUserIdByUsername(username: String, onResult: (String?) -> Unit) {
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

  open fun fetchProfile() {
    val userId = repository.getUserId() ?: return
    viewModelScope.launch {
      val userProfile = repository.fetchProfile(userId)
      _profile.value = userProfile
    }
  }

  open fun addProfile(profileData: ProfileData) {
    val userId = repository.getUserId() ?: return
    viewModelScope.launch {
      val success = repository.addProfile(userId, profileData)
      if (success) {
        _profile.value = profileData
      }
    }
  }

  open fun updateProfile(profileData: ProfileData) {
    val userId = repository.getUserId() ?: return
    viewModelScope.launch {
      val success = repository.updateProfile(userId, profileData)
      if (success) {
        _profile.value = profileData
      }
    }
  }

  open fun deleteProfile() {
    val userId = repository.getUserId() ?: return
    viewModelScope.launch {
      if (repository.deleteProfile(userId)) {
        _profile.value = null
      } else {
        Log.e("DELETE_PROFILE", "Error deleting profile")
      }
    }
  }

  open fun uploadProfilePicture(context: Context, uri: Uri) {
    val userId = repository.getUserId() ?: return
    viewModelScope.launch(dispatcher) { repository.uploadProfilePicture(uri, context, userId) }
  }

  open fun loadProfilePicture(
      userId: String? = repository.getUserId(),
      onBitmapLoaded: (Bitmap?) -> Unit
  ) {
    if (userId == null) {
      return
    }
    return repository.loadProfilePicture(userId, onBitmapLoaded)
  }

  open fun searchUsers(query: String, callback: (List<ProfileData>) -> Unit = {}) {
    viewModelScope.launch {
      try {
        val profiles = repository.searchUsers(query)
        _searchResult.value = profiles
        callback(profiles)
      } catch (e: Exception) {
        Log.e("SEARCH_PROFILES", "Error searching profiles: ${e.message}")
        _searchResult.value = emptyList()
      }
    }
  }

  /**
   * Handle the result of a permission request.
   *
   * @param isGranted `true` if the permission was granted, `false` otherwise.
   * @param galleryLauncher The launcher to open the gallery if permission is granted.
   * @param context The application context.
   */
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
  /**
   * Create a permission launcher for requesting storage permissions and opening the gallery.
   *
   * @param context The application context.
   * @param onResult A callback function that is called with the URI of the selected image.
   * @return A [ManagedActivityResultLauncher] for requesting permissions.
   */
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

  /**
   * Verify the validity of a username based on specific criteria.
   *
   * @param username The username to be verified.
   * @param onResult A callback function that is called with the result of the validation.
   */
  fun verifyUsername(username: String, onResult: (UsernameValidationResult) -> Unit) {
    viewModelScope.launch {
      val result =
          when {
            username.length !in 1..30 ->
                UsernameValidationResult.Invalid("Username must be between 1 and 30 characters")
            !username.matches("^[a-zA-Z0-9._]+$".toRegex()) ->
                UsernameValidationResult.Invalid(
                    "Username can only contain letters, numbers, dots and underscores")
            username.startsWith(".") || username.endsWith(".") ->
                UsernameValidationResult.Invalid("Username cannot start or end with a dot")
            username.contains("..") ->
                UsernameValidationResult.Invalid("Username cannot have consecutive dots")
            username.contains("___") ->
                UsernameValidationResult.Invalid(
                    "Username cannot have more than two consecutive underscores")
            !repository.isUsernameAvailable(username) ->
                UsernameValidationResult.Invalid("Username is already taken")
            else -> UsernameValidationResult.Valid
          }
      onResult(result)
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
