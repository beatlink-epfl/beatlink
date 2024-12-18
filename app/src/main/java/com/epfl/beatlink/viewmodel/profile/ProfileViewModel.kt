package com.epfl.beatlink.viewmodel.profile

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.epfl.beatlink.model.profile.ProfileData
import com.epfl.beatlink.repository.profile.ProfileRepository
import com.epfl.beatlink.repository.profile.ProfileRepositoryFirestore
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
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
    initialProfile: ProfileData? = null
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

  val profilePicture = mutableStateOf<Bitmap?>(null)

  private val _profileReady = MutableStateFlow(false)
  val profileReady: StateFlow<Boolean>
    get() = _profileReady

  // Stack-like structure to hold user navigation history
  private val userStack = mutableListOf<String>()

  private val _selectedUserUserId = MutableStateFlow("")
  val selectedUserUserId: StateFlow<String>
    get() = _selectedUserUserId

  private val _selectedUserProfile = MutableStateFlow(initialProfile)
  val selectedUserProfile: StateFlow<ProfileData?>
    get() = _selectedUserProfile

  // selects a user profile
  fun selectSelectedUser(userId: String) {
    if (_selectedUserUserId.value.isNotEmpty()) {
      userStack.add(_selectedUserUserId.value) // Push current user to the stack
    }
    _selectedUserUserId.value = userId
  }

  /**
   * Pops the last user ID from the stack and sets it as the selected user. If the stack is empty,
   * clears the selected user.
   */
  fun goBackToPreviousUser() {
    if (userStack.isNotEmpty()) {
      val previousUserId = userStack.removeAt(userStack.lastIndex) // Pop the stack
      _selectedUserUserId.value = previousUserId
      Log.d("PROFILE", "selectedUser: $previousUserId")
    } else {
      unselectSelectedUser() // Clear selection if the stack is empty
    }
  }

  /** Clears the selected user and resets the stack. */
  fun unselectSelectedUser() {
    _selectedUserUserId.value = ""
    userStack.clear()
  }

  // Function to set the profileReady flag to false
  fun unreadyProfile() {
    _profileReady.value = false
  }

  /** Function that updates the profileUpdate flag to true */
  fun markProfileAsUpdated() {
    _isProfileUpdated.value = true
  }

  /** Function that resets the profileUpdate flag to false */
  fun markProfileAsNotUpdated() {
    _isProfileUpdated.value = false
  }

  /** Function that clears the selected user */
  fun clearSelectedUser() {
    _selectedUserUserId.value = ""
    _selectedUserProfile.value = null
  }

  // Gets the username of a user given their user ID
  open fun getUsername(userId: String, onResult: (String?) -> Unit) {
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

  // Gets the user ID of a user given their username
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

  // Fetches the profile data for the current user
  open fun fetchProfile() {
    val userId = repository.getUserId() ?: return
    viewModelScope.launch {
      val userProfile = repository.fetchProfile(userId)
      _profile.value = userProfile
    }
  }

  /**
   * Fetches the profile data for a user by their unique ID.
   *
   * @param userId The unique ID of the user whose profile is being fetched.
   * @param onResult A callback function that receives the fetched `ProfileData` if successful, or
   *   `null` if the operation fails or no data is found.
   */
  open fun fetchProfileById(userId: String, onResult: (ProfileData?) -> Unit) {
    viewModelScope.launch {
      try {
        val userProfileData = repository.fetchProfile(userId)
        onResult(userProfileData)
      } catch (e: Exception) {
        Log.e("ViewModel", "Error fetching user profile", e)
        onResult(null)
      }
    }
  }

  // Fetches the profile data for a user by their username
  open fun fetchUserProfile() {
    _profileReady.value = false
    viewModelScope.launch {
      val userProfile = repository.fetchProfile(_selectedUserUserId.value)
      _selectedUserProfile.value = userProfile
      _profileReady.value = true
    }
  }

  // Adds a new profile to the database
  open fun addProfile(profileData: ProfileData) {
    val userId = repository.getUserId() ?: return
    viewModelScope.launch {
      val success = repository.addProfile(userId, profileData)
      if (success) {
        _profile.value = profileData
      }
    }
  }

  // Updates the profile data for the current user
  open fun updateProfile(profileData: ProfileData) {
    val userId = repository.getUserId() ?: return
    viewModelScope.launch {
      val success = repository.updateProfile(userId, profileData)
      if (success) {
        _profile.value = profileData
      }
    }
  }

  // Deletes the profile of the current user
  open suspend fun deleteProfile(): Boolean {
    val userId = repository.getUserId() ?: return false
    return try {
      if (repository.deleteProfile(userId)) {
        _profile.value = null
        true
      } else {
        Log.e("DELETE_PROFILE", "Error deleting profile")
        false
      }
    } catch (e: Exception) {
      Log.e("DELETE_PROFILE", "Exception while deleting profile: ${e.message}")
      false
    }
  }

  /**
   * Updates the number of Links of the current user
   *
   * @param profileData The current user's profile data that will be updated.
   * @param nbLinks The new number of links to set for the current user.
   */
  open fun updateNbLinks(profileData: ProfileData, nbLinks: Int) {
    val userId = repository.getUserId() ?: return
    viewModelScope.launch {
      try {
        val updatedProfile = profileData.copy(links = nbLinks)
        val success = repository.updateProfile(userId, updatedProfile)
        if (success) {
          _profile.emit(updatedProfile)
          _profile.value = updatedProfile
        }
      } catch (e: Exception) {
        Log.e("ProfileViewModel", "Error updating links: ${e.message}")
      }
    }
  }

  /**
   * Updates the number of Links of the given user ProfileData and ID
   *
   * @param otherProfileData The profile data of the user to be updated.
   * @param otherProfileUserId The unique ID of the user whose profile is being updated.
   * @param nbLinks The new number of links to set for the specified user.
   */
  open fun updateOtherProfileNbLinks(
      otherProfileData: ProfileData,
      otherProfileUserId: String,
      nbLinks: Int
  ) {
    viewModelScope.launch {
      try {
        val updatedProfile = otherProfileData.copy(links = nbLinks)
        val success = repository.updateProfile(otherProfileUserId, updatedProfile)
        if (success) {
          _selectedUserProfile.emit(updatedProfile)
          _selectedUserProfile.value = updatedProfile
        }
      } catch (e: Exception) {
        Log.e("ProfileViewModel", "Error updating links: ${e.message}")
      }
    }
  }

  // Loads the profile picture of the current user
  open fun loadProfilePicture(
      userId: String? = repository.getUserId(),
      onBitmapLoaded: (Bitmap?) -> Unit
  ) {
    if (userId == null) {
      return
    }
    return repository.loadProfilePicture(userId, onBitmapLoaded)
  }

  // Loads the profile picture of the selected user
  open fun loadSelectedUserProfilePicture(
      userId: String? = _selectedUserUserId.value,
      onBitmapLoaded: (Bitmap?) -> Unit
  ) {
    if (userId == null) {
      return
    }
    return repository.loadProfilePicture(userId, onBitmapLoaded)
  }

  // Searches for users based on a query
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
