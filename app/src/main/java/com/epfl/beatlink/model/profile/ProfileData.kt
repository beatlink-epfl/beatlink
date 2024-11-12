package com.epfl.beatlink.model.profile

data class ProfileData(
    // The description of the user's profile with a default value of null
    val bio: String? = null,
    // The number of social media links in the user's profile with a default value of 0
    val links: Int = 0,
    // The name of the user with a default value of null
    val name: String? = null,
    // The profile picture of the user with a default value of null
    val profilePicture: Int? = null,
    // The username of the user with a default value of an empty string
    val username: String = "",
)
