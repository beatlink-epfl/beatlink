package com.epfl.beatlink.ui.profile

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import com.epfl.beatlink.model.profile.ProfileData
import com.epfl.beatlink.repository.profile.ProfileRepository
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.mockito.Mockito.mock

class FakeProfileViewModel(
    profileRepository: ProfileRepository = mock(ProfileRepository::class.java)
) : ProfileViewModel(profileRepository) {

  private val fakeProfiles = mutableListOf<ProfileData>()
  private val fakeProfilePictures = mutableMapOf<String, Bitmap>()
  private val fakeUserIdByUsername = mutableMapOf<String, String>()
  private val fakeUsernameById = mutableMapOf<String, String>()
  private val fakeProfileDataById = mutableMapOf<String, ProfileData>()

  override fun searchUsers(query: String, callback: (List<ProfileData>) -> Unit) {
    val result = fakeProfiles.filter { it.username.contains(query, ignoreCase = true) }
    setSearchResult(result) // Use the custom setter
    callback(result)
  }

  private fun setSearchResult(profiles: List<ProfileData>) {
    (searchResult as MutableLiveData).postValue(profiles)
  }

  fun setFakeUserIdByUsername(map: Map<String, String>) {
    fakeUserIdByUsername.clear()
    fakeUserIdByUsername.putAll(map)
  }

  override fun getUserIdByUsername(username: String, onResult: (String?) -> Unit) {
    onResult(fakeUserIdByUsername[username])
  }

  fun setFakeUsernameById(map: Map<String, String>) {
    fakeUsernameById.clear()
    fakeUsernameById.putAll(map)
  }

  override fun getUsername(userId: String, onResult: (String?) -> Unit) {
    val username = fakeUsernameById[userId]
    onResult(username)
  }

  fun setFakeProfileDataById(map: Map<String, ProfileData>) {
    fakeProfileDataById.clear() // Clear existing data to avoid duplication
    fakeProfileDataById.putAll(map)
  }

  override fun fetchProfileById(userId: String, onResult: (ProfileData?) -> Unit) {
    // Return the fake profile if the userId exists in the map
    val profile = fakeProfileDataById[userId]
    onResult(profile)
  }

  override fun loadProfilePicture(userId: String?, onBitmapLoaded: (Bitmap?) -> Unit) {
    val bitmap = fakeProfilePictures[userId]
    onBitmapLoaded(bitmap)
  }

  override fun addProfile(profileData: ProfileData) {
    fakeProfiles.add(profileData)
  }

  override fun updateProfile(profileData: ProfileData) {
    fakeProfiles.replaceAll { if (it.username == profileData.username) profileData else it }
  }

  fun setFakeProfile(profileData: ProfileData) {
    (profile as MutableStateFlow).value = profileData
  }

  fun setFakeSelectedProfile(selectedProfileData: ProfileData) {
    (selectedUserProfile as MutableStateFlow).value = selectedProfileData
  }

  fun setFakeSelectedId(id: String) {
    (selectedUserUserId as MutableStateFlow).value = id
  }

  fun setFakeProfiles(profiles: List<ProfileData>) {
    fakeProfiles.clear()
    fakeProfiles.addAll(profiles)
  }

  override fun fetchProfile() {
    // Mocked fetch profile
  }

  override suspend fun deleteProfile(): Boolean {
    // Mocked delete profile
    return true
  }
}
