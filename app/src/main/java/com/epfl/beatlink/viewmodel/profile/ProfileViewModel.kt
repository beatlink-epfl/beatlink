package com.epfl.beatlink.viewmodel.profile

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
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

  /**
   * Represents the result of username validation.
   */
  sealed class UsernameValidationResult {
    /**
     * Indicates that the username is valid.
     */
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

  fun deleteProfile() {
    val userId = repository.getUserId() ?: return
    viewModelScope.launch {
      val success = repository.deleteProfile(userId)
      if (success) {
        _profile.value = null
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

  fun verifyUsername(username: String, onResult: (UsernameValidationResult) -> Unit) {
    viewModelScope.launch {
      val result = when {
        username.length !in 1..30 ->
          UsernameValidationResult.Invalid("Username must be between 1 and 30 characters")

        !username.matches("^[a-zA-Z0-9._]+$".toRegex()) ->
          UsernameValidationResult.Invalid("Username can only contain letters, numbers, dots and underscores")

        username.startsWith(".") || username.endsWith(".") ->
          UsernameValidationResult.Invalid("Username cannot start or end with a dot")

        username.contains("..") ->
          UsernameValidationResult.Invalid("Username cannot have consecutive dots")

        username.contains("___") ->
          UsernameValidationResult.Invalid("Username cannot have more than two consecutive underscores")

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
