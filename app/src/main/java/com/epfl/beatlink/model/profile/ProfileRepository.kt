package com.epfl.beatlink.model.profile

interface ProfileRepository {
    fun getUserId(): String?

    suspend fun fetchProfile(userId: String): ProfileData?

    fun updateProfile(userId: String, profileData: ProfileData): Boolean
}
