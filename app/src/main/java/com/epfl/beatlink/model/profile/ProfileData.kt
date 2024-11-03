package com.epfl.beatlink.model.profile

data class ProfileData(
    val username: String,
    val name: String?,
    val bio: String?,
    val links: Int,
    val profilePicture: Int?,
)
