package com.epfl.beatlink.model.profile

import android.net.Uri

/**
 * A data class representing the user's profile.
 *
 * @param bio The description of the user's profile with a default value of null
 * @param links The number of social media links in the user's profile with a default value of 0
 * @param name The name of the user with a default value of null
 * @param profilePicture The profile picture of the user with a default value of null
 * @param username The username of the user with a default value of an empty string
 * @param favoriteMusicGenres The user's favorite music genres with a default value of an empty list
 * @property MAX_USERNAME_LENGTH The maximum length of the username
 * @property MAX_DESCRIPTION_LENGTH The maximum length of the description
 */
data class ProfileData(
    val bio: String? = null,
    val links: Int = 0,
    val name: String? = null,
    val profilePicture: Uri? = null,
    val username: String = "",
    val favoriteMusicGenres: List<String> = emptyList()
) {
  companion object {
    const val MAX_USERNAME_LENGTH = 20
    const val MAX_DESCRIPTION_LENGTH = 100
  }
}

enum class MusicGenre(val displayName: String) {
  POP("Pop"),
  RAP("Rap"),
  ROCK("Rock"),
  JAZZ("Jazz"),
  ELECTRO("Electro"),
  CLASSICAL("Classical"),
  HIP_HOP("Hip Hop"),
  EDM("EDM"),
  REGGAE("Reggae"),
  METAL("Metal");

  companion object {
    fun fromString(genre: String): MusicGenre? {
      return values().find { it.displayName.equals(genre, ignoreCase = true) }
    }
  }
}
