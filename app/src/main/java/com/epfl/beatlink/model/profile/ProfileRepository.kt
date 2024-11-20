package com.epfl.beatlink.model.profile

interface ProfileRepository {

  /**
   * Retrieve the unique identifier (UID) of the currently authenticated user.
   *
   * @return The UID of the currently logged-in user, or `null` if no user is logged in.
   */
  fun getUserId(): String?

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

  /*
  /**
   * Uploads a profile picture for a specific user.
   *
   * @param imageFile A [File] object representing the image to be uploaded as the user's profile picture.
   * @return The URL of the uploaded profile picture if successful.
   */
  suspend fun uploadProfilePicture(imageUri: File): String?
  */
}
