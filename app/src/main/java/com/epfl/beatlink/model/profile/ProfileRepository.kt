package com.epfl.beatlink.model.profile

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri

interface ProfileRepository {

  /**
   * Retrieve the unique identifier (UID) of the currently authenticated user.
   *
   * @return The UID of the currently logged-in user, or `null` if no user is logged in.
   */
  fun getUserId(): String?

  /**
   * Retrieve the username of the userId.
   *
   * @return The username of the userId, or `null` if no username found.
   */
  suspend fun getUsername(userId: String): String?

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
   * Add a new user profile to Firestore.
   *
   * @param userId The unique identifier of the user.
   * @param profileData A [ProfileData] object containing the profile information to be added.
   * @return `true` if the profile was successfully added, `false` otherwise.
   */
  suspend fun addProfile(userId: String, profileData: ProfileData): Boolean

  /**
   * Update the profile of a specific user.
   *
   * @param userId The unique identifier of the user.
   * @param profileData A [ProfileData] object containing the updated profile information.
   * @return `true` if the profile was successfully updated, `false` otherwise.
   */
  suspend fun updateProfile(userId: String, profileData: ProfileData): Boolean

  /**
   * Delete the profile of a specific user.
   *
   * @param userId The unique identifier of the user.
   * @return `true` if the profile was successfully deleted, `false` otherwise.
   */
  suspend fun deleteProfile(userId: String): Boolean

  /**
   * Upload a new profile picture for a specific user.
   *
   * @param imageUri The URI of the image to upload.
   * @param context The application context.
   * @param userId The unique identifier of the user.
   */
  fun uploadProfilePicture(imageUri: Uri, context: Context, userId: String)

  /**
   * Load the profile picture of a specific user.
   *
   * @param userId The unique identifier of the user.
   * @param onBitmapLoaded A callback function that is called when the profile picture is loaded.
   */
  fun loadProfilePicture(userId: String, onBitmapLoaded: (Bitmap?) -> Unit)
}
