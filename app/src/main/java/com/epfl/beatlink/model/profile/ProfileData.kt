package com.epfl.beatlink.model.profile

/**
 * A data class representing the user's profile.
 *
 * @param bio The description of the user's profile with a default value of null
 * @param links The number of social media links in the user's profile with a default value of 0
 * @param name The name of the user with a default value of null
 * @param profilePicture The profile picture of the user with a default value of null
 * @param username The username of the user with a default value of an empty string
 * @param favoriteMusicGenres The user's favorite music genres with a default value of an empty list
 * @param email The user's email with a default value of an empty string
 * @property MAX_USERNAME_LENGTH The maximum length of the username
 * @property MAX_DESCRIPTION_LENGTH The maximum length of the description
 */
data class ProfileData(
    val bio: String? = null,
    val links: Int = 0,
    val name: String? = null,
    val profilePicture: String? = null,
    val username: String = "",
    val email: String = "",
    var favoriteMusicGenres: List<String> = emptyList()
) {
  companion object {
    const val MAX_USERNAME_LENGTH = 20
    const val MAX_DESCRIPTION_LENGTH = 100
  }
}

enum class MusicGenre(val displayName: String) {
  POP("Pop"),
  RAP("Rap"),
  RANDB("R&B"),
  ROCK("Rock"),
  COUNTRY("Country"),
  SOUL("Soul"),
  PUNK("Punk"),
  JAZZ("Jazz"),
  ELECTRO("Electro"),
  CLASSICAL("Classical"),
  HIP_HOP("Hip Hop"),
  EDM("EDM"),
  REGGAE("Reggae"),
  REGGAETON("Reggaeton"),
  METAL("Metal"),
  KPOP("K-Pop"),
  JPOP("J-Pop"),
  HOUSE("House"),
  TECHNO("Techno"),
  DNB("Drum and Bass"),
  LOFI("Lo-Fi");

  companion object {
    const val MAX_SELECTABLE_GENRES = 4

    fun getAllGenres(): List<String> {
      return values().map { it.displayName }
    }
  }
}
