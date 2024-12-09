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
import com.epfl.beatlink.model.profile.ProfileRepository
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

    private val _selectedUserUserId = MutableStateFlow("")
    val selectedUserUserId: StateFlow<String>
        get() = _selectedUserUserId

    private val _selectedUserProfile = MutableStateFlow(initialProfile)
    val selectedUserProfile: StateFlow<ProfileData?>
        get() = _selectedUserProfile

    fun selectSelectedUser(userId: String) {
        _selectedUserUserId.value = userId
    }

    fun unselectSelectedUser() {
        _selectedUserUserId.value = ""
    }

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

    fun clearSelectedUser() {
        _selectedUserUserId.value = ""
        _selectedUserProfile.value = null
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

    open fun fetchUserProfile() {
        _profileReady.value = false
        viewModelScope.launch {
            val userProfile = repository.fetchProfile(_selectedUserUserId.value)
            _selectedUserProfile.value = userProfile
            _profileReady.value = true
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

    open fun loadProfilePicture(
        userId: String? = repository.getUserId(),
        onBitmapLoaded: (Bitmap?) -> Unit
    ) {
        if (userId == null) {
            return
        }
        return repository.loadProfilePicture(userId, onBitmapLoaded)
    }

    open fun loadSelectedUserProfilePicture(
        userId: String? = _selectedUserUserId.value,
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
                            "Username can only contain letters, numbers, dots and underscores"
                        )

                    username.startsWith(".") || username.endsWith(".") ->
                        UsernameValidationResult.Invalid("Username cannot start or end with a dot")

                    username.contains("..") ->
                        UsernameValidationResult.Invalid("Username cannot have consecutive dots")

                    username.contains("___") ->
                        UsernameValidationResult.Invalid(
                            "Username cannot have more than two consecutive underscores"
                        )

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
                    return ProfileViewModel(
                        ProfileRepositoryFirestore(
                            Firebase.firestore,
                            firebaseAuth
                        )
                    )
                            as T
                }
            }
    }
}
