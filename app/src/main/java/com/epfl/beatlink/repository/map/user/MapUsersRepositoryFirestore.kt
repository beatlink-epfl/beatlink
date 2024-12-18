package com.epfl.beatlink.repository.map.user

import android.util.Log
import com.epfl.beatlink.model.map.user.CurrentPlayingTrack
import com.epfl.beatlink.model.map.user.Location
import com.epfl.beatlink.model.map.user.MapUser
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlinx.coroutines.tasks.await

// Approximate value for meters per degree of latitude/longitude at the equator
private const val APPROX_METERS_PER_DEGREE = 111000

// Constants for Time-To-Live (TTL) duration
private const val TTL_DURATION_MINUTES = 30
private const val MINUTE_IN_MILLIS = 60 * 1000L
private const val TTL_MILLIS = TTL_DURATION_MINUTES * MINUTE_IN_MILLIS

class MapUsersRepositoryFirestore(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : MapUserRepository {

  private val collectionPath = "mapUsers"

  override fun init(onSuccess: () -> Unit) {
    auth.addAuthStateListener { firebaseAuth ->
      if (firebaseAuth.currentUser != null) {
        onSuccess()
      } else {
        Log.d("MapUsersRepository", "User is not logged in")
      }
    }
  }

  override fun getMapUsers(
      currentUserLocation: Location,
      radiusInMeters: Double,
      onSuccess: (List<MapUser>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    // Convert radius from meters to degrees for latitude
    val radiusInDegreesLatitude = radiusInMeters / APPROX_METERS_PER_DEGREE

    // Adjust longitude conversion based on the latitude
    val metersPerDegreeLongitude =
        APPROX_METERS_PER_DEGREE * cos(Math.toRadians(currentUserLocation.latitude))
    val radiusInDegreesLongitude = radiusInMeters / metersPerDegreeLongitude

    // Firestore query to retrieve MapUsers within a bounding box defined by the radius in degrees
    db.collection(collectionPath)
        .whereGreaterThan(
            "location.latitude", currentUserLocation.latitude - radiusInDegreesLatitude)
        .whereLessThan("location.latitude", currentUserLocation.latitude + radiusInDegreesLatitude)
        .whereGreaterThan(
            "location.longitude", currentUserLocation.longitude - radiusInDegreesLongitude)
        .whereLessThan(
            "location.longitude", currentUserLocation.longitude + radiusInDegreesLongitude)
        .get()
        .addOnCompleteListener { task ->
          if (task.isSuccessful) {
            val mapUsers =
                task.result
                    ?.documents
                    ?.mapNotNull { document -> documentToMapUser(document) }
                    ?.filter { mapUser ->
                      val userLocation = mapUser.location
                      // Exclude the user at the current location
                      userLocation != currentUserLocation &&
                          // Precise filtering using Haversine formula
                          haversineDistance(currentUserLocation, userLocation) <= radiusInMeters
                    } ?: emptyList()

            onSuccess(mapUsers)
          } else {
            task.exception?.let { e ->
              Log.e("MapUsersRepository", "Error getting MapUsers", e)
              onFailure(e)
            }
          }
        }
  }

  override fun addMapUser(mapUser: MapUser, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    val uid = auth.currentUser?.uid ?: return
    db.collection(collectionPath).document(uid).set(mapUserToMap(mapUser)).addOnCompleteListener {
        task ->
      if (task.isSuccessful) {
        onSuccess()
      } else {
        task.exception?.let { e ->
          Log.e("MapUsersRepository", "Error adding MapUser", e)
          onFailure(e)
        }
      }
    }
  }

  override fun updateMapUser(
      mapUser: MapUser,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val uid = auth.currentUser?.uid ?: return
    db.collection(collectionPath).document(uid).set(mapUserToMap(mapUser)).addOnCompleteListener {
        task ->
      if (task.isSuccessful) {
        onSuccess()
      } else {
        task.exception?.let { e ->
          Log.e("MapUsersRepository", "Error updating MapUser", e)
          onFailure(e)
        }
      }
    }
  }

  override suspend fun deleteMapUser(): Boolean {
    val uid = auth.currentUser?.uid ?: return false
    return try {
      db.collection(collectionPath).document(uid).delete().await()
      Log.d("MapUsersRepository", "MapUser deleted successfully")
      true
    } catch (e: Exception) {
      Log.e("MapUsersRepository", "Error deleting MapUser", e)
      false
    }
  }

  override suspend fun deleteExpiredUsers(): Boolean {
    return try {
      val expirationTime = Timestamp(Date(System.currentTimeMillis() - TTL_MILLIS))

      // Check if the user is logged in
      if (auth.currentUser == null) {
        Log.d("MapUsersRepository", "User is not logged in, cannot delete expired MapUsers.")
        return false
      }

      val querySnapshot =
          db.collection(collectionPath).whereLessThan("lastUpdated", expirationTime).get().await()

      // Check for expired MapUsers
      if (querySnapshot.documents.isEmpty()) {
        Log.d("MapUsersRepository", "No expired MapUser found.")
        return false
      }
      // Delete all expired MapUsers
      querySnapshot.documents.map { document ->
        db.collection(collectionPath).document(document.id).delete().await()
      }

      Log.d("MapUsersRepository", "All expired MapUsers have been deleted.")
      true
    } catch (e: Exception) {
      Log.e("MapUsersRepository", "Error deleting expired MapUsers", e)
      false
    }
  }

  /**
   * Converts a Firestore DocumentSnapshot into a MapUser object.
   *
   * @param document The Firestore DocumentSnapshot containing the MapUser data.
   * @return A MapUser object populated with data from the document, or null if the conversion
   *   fails.
   */
  fun documentToMapUser(document: DocumentSnapshot): MapUser? {
    return try {
      // Get `currentPlayingTrack` data
      val currentPlayingTrackData = document["currentPlayingTrack"] as Map<*, *>

      val currentPlayingTrack =
          CurrentPlayingTrack(
              trackId = currentPlayingTrackData["trackId"] as String,
              songName = currentPlayingTrackData["songName"] as String,
              artistName = currentPlayingTrackData["artistName"] as String,
              albumName = currentPlayingTrackData["albumName"] as String,
              albumCover = currentPlayingTrackData["albumCover"] as String)

      // Get `location` data
      val locationData = document["location"] as Map<*, *>
      val location =
          Location(
              latitude = locationData["latitude"] as Double,
              longitude = locationData["longitude"] as Double)

      // Return a new instance of `MapUser`
      MapUser(
          username = document.getString("username") ?: "",
          currentPlayingTrack = currentPlayingTrack,
          location = location,
          lastUpdated = document["lastUpdated"] as Timestamp)
    } catch (e: Exception) {
      Log.e("MapUsersRepository", "Error converting document to MapUser", e)
      null
    }
  }

  /**
   * Converts a MapUser object into a Map<String, Any> suitable for Firestore storage.
   *
   * @param mapUser The MapUser object to be converted.
   * @return A Map<String, Any> representation of the MapUser object suitable for Firestore storage.
   */
  fun mapUserToMap(mapUser: MapUser): Map<String, Any> {
    return mapOf(
        "username" to mapUser.username,
        "currentPlayingTrack" to
            mapOf(
                "trackId" to mapUser.currentPlayingTrack.trackId,
                "songName" to mapUser.currentPlayingTrack.songName,
                "artistName" to mapUser.currentPlayingTrack.artistName,
                "albumName" to mapUser.currentPlayingTrack.albumName,
                "albumCover" to mapUser.currentPlayingTrack.albumCover),
        "location" to
            mapOf(
                "latitude" to mapUser.location.latitude, "longitude" to mapUser.location.longitude),
        "lastUpdated" to mapUser.lastUpdated)
  }

  /**
   * Calculates the Haversine distance between two locations in meters.
   *
   * @param loc1 The first location with latitude and longitude.
   * @param loc2 The second location with latitude and longitude.
   * @return The distance between loc1 and loc2 in meters.
   */
  fun haversineDistance(loc1: Location, loc2: Location): Double {
    val earthRadiusMeters = 6371000.0 // Earth's radius in meters

    val latDiff = Math.toRadians(loc2.latitude - loc1.latitude)
    val lonDiff = Math.toRadians(loc2.longitude - loc1.longitude)

    val a =
        sin(latDiff / 2).pow(2) +
            cos(Math.toRadians(loc1.latitude)) *
                cos(Math.toRadians(loc2.latitude)) *
                sin(lonDiff / 2).pow(2)

    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return earthRadiusMeters * c
  }
}
