package com.epfl.beatlink.model.map.user

interface MapUsersRepository {
  /**
   * Initialize the repository.
   *
   * @param onSuccess Callback for successful initialization.
   */
  fun init(onSuccess: () -> Unit)

  /**
   * Retrieve the map users within a given radius using GeoFire.
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
   * Add a new MapUser to Firebase Realtime Database.
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
   * Delete the current user's MapUser data from Firebase Realtime Database.
   *
   * @param onSuccess Callback for successful deletion.
   * @param onFailure Callback that is invoked if an error occurs.
   */
  fun deleteMapUser(onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
}
