package com.epfl.beatlink.repository.profile

import android.graphics.Bitmap
import com.epfl.beatlink.model.profile.ProfileData

interface ProfileRepository {

  /**
   * Retrieve the unique identifier (UID) of the currently authenticated user.
   *
   * @return The UID of the currently logged-in user, or `null` if no user is logged in.
   */
  fun getUserId(): String?

  /**
   * Retrieve the username of the user given the userId.
   *
   * @return The username of the user, or `null` if no username found.
   */
  suspend fun getUsername(userId: String): String?

  /**
   * Retrieve the userId of the user.
   *
   * @return The userId, or `null` if no userId found.
   */
  suspend fun getUserIdByUsername(username: String): String?

  /**
   * Fetch the profile data of a specific user.
   *
   * @param userId The unique identifier of the user.
   * @return A [ProfileData] object containing the user's profile information if found, or `null` if
   *   the profile does not exist.
   */
  suspend fun fetchProfile(userId: String): ProfileData?

  /**
   * Add a new user profile to Firestore, and add the corresponding username to the "usernames"
   * collection.
   *
   * @param userId The unique identifier of the user.
   * @param profileData A [ProfileData] object containing the profile information to be added.
   * @return `true` if the profile was successfully added, `false` otherwise.
   */
  suspend fun addProfile(userId: String, profileData: ProfileData): Boolean

  /**
   * Update a user's profile in Firestore, and update the corresponding username in the "usernames"
   * collection if it changes.
   *
   * @param userId The unique identifier of the user.
   * @param profileData A [ProfileData] object containing the updated profile information.
   * @return `true` if the profile was successfully updated, `false` otherwise.
   */
  suspend fun updateProfile(userId: String, profileData: ProfileData): Boolean

  /**
   * Delete a user's profile from Firestore, and remove the corresponding username from the
   * "usernames" collection.
   *
   * @param userId The unique identifier of the user.
   * @return `true` if the profile was successfully deleted, `false` otherwise.
   */
  suspend fun deleteProfile(userId: String): Boolean

  /**
   * Load the profile picture of a specific user.
   *
   * @param userId The unique identifier of the user.
   * @param onBitmapLoaded A callback function that is called when the profile picture is loaded.
   */
  fun loadProfilePicture(userId: String, onBitmapLoaded: (Bitmap?) -> Unit)

  /**
   * Check if a username is available (no duplicates) in the "usernames" collection.
   *
   * @param username The username to check for availability.
   * @return `true` if the username is available, `false` otherwise.
   */
  suspend fun isUsernameAvailable(username: String): Boolean

  /**
   * Search for users based on a query string.
   *
   * @param query The search query.
   * @return A list of [ProfileData] objects matching the search query.
   */
  suspend fun searchUsers(query: String): List<ProfileData>
}
