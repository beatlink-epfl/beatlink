package com.epfl.beatlink.model.profile

interface ProfileRepository {
  fun getUserId(): String?

  suspend fun fetchProfile(userId: String): ProfileData?

  suspend fun updateProfile(userId: String, profileData: ProfileData): Boolean

  // suspend fun uploadProfilePicture(imageUri: File): String?
}
