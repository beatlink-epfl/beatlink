package com.epfl.beatlink.model.profile

import com.epfl.beatlink.model.spotify.objects.SpotifyArtist
import com.epfl.beatlink.model.spotify.objects.SpotifyTrack

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
 * @param topSongs The user's top songs listened to, in Spotify
 * @param topArtists The user's top artists listened to, in Spotify
 * @param spotifyId The user's spotifyId
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
    var favoriteMusicGenres: List<String> = emptyList(),
    val topSongs: List<SpotifyTrack> = emptyList(),
    val topArtists: List<SpotifyArtist> = emptyList(),
    val spotifyId: String = ""
) {
  companion object {
    const val MAX_USERNAME_LENGTH = 20
    const val MAX_DESCRIPTION_LENGTH = 100
  }
}

enum class MusicGenre(val displayName: String) {
  CLASSICAL("Classical"),
  COUNTRY("Country"),
  DNB("Drum and Bass"),
  EDM("EDM"),
  ELECTRO("Electro"),
  HIP_HOP("Hip Hop"),
  HOUSE("House"),
  JAZZ("Jazz"),
  JPOP("J-Pop"),
  KPOP("K-Pop"),
  LOFI("Lo-Fi"),
  METAL("Metal"),
  POP("Pop"),
  PUNK("Punk"),
  RANDB("R&B"),
  RAP("Rap"),
  REGGAE("Reggae"),
  REGGAETON("Reggaeton"),
  ROCK("Rock"),
  SOUL("Soul"),
  TECHNO("Techno");

  companion object {
    const val MAX_SELECTABLE_GENRES = 4

    fun getAllGenres(): List<String> {
      return values().map { it.displayName }
    }
  }
}
