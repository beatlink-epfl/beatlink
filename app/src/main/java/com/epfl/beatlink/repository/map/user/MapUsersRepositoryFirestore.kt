package com.epfl.beatlink.repository.map.user

import android.util.Log
import com.epfl.beatlink.model.map.user.CurrentPlayingTrack
import com.epfl.beatlink.model.map.user.Location
import com.epfl.beatlink.model.map.user.MapUser
import com.epfl.beatlink.model.map.user.MapUserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

// Approximate value for meters per degree of latitude/longitude at the equator
private const val APPROX_METERS_PER_DEGREE = 111000

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
                      // Precise filtering using Haversine formula
                      haversineDistance(currentUserLocation, userLocation) <= radiusInMeters
                    } ?: emptyList()

            onSuccess(mapUsers)
          } else {
            task.exception?.let { e ->
              Log.e("MapUsersRepository", "Error getting users", e)
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
          Log.e("MapUsersRepository", "Error adding map user", e)
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
          Log.e("MapUsersRepository", "Error updating map user", e)
          onFailure(e)
        }
      }
    }
  }

  override fun deleteMapUser(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    val uid = auth.currentUser?.uid ?: return
    db.collection(collectionPath).document(uid).delete().addOnCompleteListener { task ->
      if (task.isSuccessful) {
        onSuccess()
      } else {
        task.exception?.let { e ->
          Log.e("MapUsersRepository", "Error deleting map user", e)
          onFailure(e)
        }
      }
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
      val locationData = document.get("location") as Map<String, Any>
      val location =
          Location(
              latitude = locationData["latitude"] as Double,
              longitude = locationData["longitude"] as Double)

      // Retrieve and validate `currentPlayingTrack` data, making sure it's non-nullable.
      val currentPlayingTrackData =
          document.get("currentPlayingTrack") as? Map<String, Any>
              ?: throw IllegalArgumentException("currentPlayingTrack data is missing")

      // Create a non-null `CurrentPlayingTrack` instance
      val currentPlayingTrack =
          CurrentPlayingTrack(
              songName = currentPlayingTrackData["songName"] as String,
              artistName = currentPlayingTrackData["artistName"] as String,
              albumName = currentPlayingTrackData["albumName"] as String,
              albumCover = currentPlayingTrackData["albumPicture"] as String)

      // Return a new instance of `MapUser` with non-null `currentPlayingTrack`
      MapUser(
          username = document.getString("username") ?: "",
          currentPlayingTrack = currentPlayingTrack,
          location = location)
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
                "songName" to (mapUser.currentPlayingTrack?.songName ?: ""),
                "artistName" to (mapUser.currentPlayingTrack?.artistName ?: ""),
                "albumName" to (mapUser.currentPlayingTrack?.albumName ?: "")),
        "location" to
            mapOf(
                "latitude" to mapUser.location.latitude, "longitude" to mapUser.location.longitude))
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
