package com.epfl.beatlink.repository.map.user

import com.epfl.beatlink.model.map.user.Location
import com.epfl.beatlink.model.map.user.MapUser

interface MapUserRepository {
  /**
   * Initialize the repository.
   *
   * @param onSuccess Callback for successful initialization.
   */
  fun init(onSuccess: () -> Unit)

  /**
   * Retrieve all map users within a given radius.
   *
   * @param currentUserLocation The current location of the user.
   * @param radiusInMeters The radius within which users will be queried (in meters).
   * @param onSuccess Callback that is invoked with the list of map users.
   * @param onFailure Callback that is invoked if an error occurs.
   */
  fun getMapUsers(
      currentUserLocation: Location,
      radiusInMeters: Double,
      onSuccess: (List<MapUser>) -> Unit,
      onFailure: (Exception) -> Unit
  )

  /**
   * Add a new MapUser to Firestore.
   *
   * @param mapUser The MapUser object to add.
   * @param onSuccess Callback for successful addition.
   * @param onFailure Callback that is invoked if an error occurs.
   */
  fun addMapUser(mapUser: MapUser, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Update an existing MapUser.
   *
   * @param mapUser The MapUser object with updated data.
   * @param onSuccess Callback for successful update.
   * @param onFailure Callback that is invoked if an error occurs.
   */
  fun updateMapUser(mapUser: MapUser, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Delete the current user's MapUser data from Firestore.
   *
   * @param onSuccess Callback for successful deletion.
   * @param onFailure Callback that is invoked if an error occurs.
   */
  suspend fun deleteMapUser(): Boolean

  /**
   * Delete expired MapUsers from Firestore.
   *
   * This function identifies MapUsers whose `lastUpdated` timestamp is older than the defined TTL
   * (Time-To-Live) duration and deletes their records from the Firestore database.
   *
   * @return A Boolean indicating whether the deletion was successful.
   */
  suspend fun deleteExpiredUsers(): Boolean
}
