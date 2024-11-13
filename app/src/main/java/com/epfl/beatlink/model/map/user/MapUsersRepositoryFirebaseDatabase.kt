package com.epfl.beatlink.model.map.user

import android.util.Log
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQueryEventListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

private const val METERS_IN_ONE_KM = 1000.0

class MapUsersRepositoryFirebaseDatabase(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val mapUsersReference: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("mapUsers"),
    private val geoFire: GeoFire = GeoFire(mapUsersReference)
) : MapUsersRepository {

  override fun init(onSuccess: () -> Unit) {
    auth.addAuthStateListener {
      if (it.currentUser != null) {
        onSuccess()
      }
    }
  }

  override fun getMapUsers(
      currentUserLocation: Location,
      radiusInMeters: Double,
      onSuccess: (List<MapUser>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    // Convert the radius to kilometers
    val radiusInKilometers = radiusInMeters / METERS_IN_ONE_KM

    // Define the GeoFire query for nearby users using a circular radius
    val geoQuery =
        geoFire.queryAtLocation(
            GeoLocation(currentUserLocation.latitude, currentUserLocation.longitude),
            radiusInKilometers)

    val mapUsers = mutableListOf<MapUser>()

    geoQuery.addGeoQueryEventListener(
        object : GeoQueryEventListener {
          override fun onKeyEntered(key: String, location: GeoLocation) {
            // Fetch the MapUser data based on the UID (key) and add it to the list of mapUsers
            mapUsersReference.child(key).get().addOnCompleteListener { task ->
              if (task.isSuccessful) {
                val mapUser = task.result?.getValue(MapUser::class.java)
                mapUser?.let { mapUsers.add(it) }
              } else {
                task.exception?.let { e ->
                  Log.e("MapUsersRepository", "Error fetching mapUser data", e)
                }
              }
            }
          }

          override fun onKeyExited(key: String) {}

          override fun onKeyMoved(key: String, location: GeoLocation) {}

          override fun onGeoQueryReady() {
            // Return the list of mapUsers within the given radius
            onSuccess(mapUsers)
          }

          override fun onGeoQueryError(error: DatabaseError) {
            Log.e("MapUsersRepository", "GeoQuery Error: ${error.message}")
            onFailure(Exception("GeoQuery Error: ${error.message}"))
          }
        })
  }

  override fun addMapUser(mapUser: MapUser, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    val uid = auth.currentUser?.uid ?: return
    val userRef = mapUsersReference.child(uid)

    // Store MapUser object
    userRef.setValue(mapUser).addOnCompleteListener { task ->
      if (task.isSuccessful) {
        // Add location data to GeoFire for spatial queries
        geoFire.setLocation(uid, GeoLocation(mapUser.location.latitude, mapUser.location.longitude))
        onSuccess()
      } else {
        task.exception?.let { e ->
          Log.e("MapUsersRepository", "Error adding mapUser", e)
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
    val userRef = mapUsersReference.child(uid)

    // Update MapUser object
    userRef.setValue(mapUser).addOnCompleteListener { task ->
      if (task.isSuccessful) {
        // Update location in GeoFire
        geoFire.setLocation(uid, GeoLocation(mapUser.location.latitude, mapUser.location.longitude))
        onSuccess()
      } else {
        task.exception?.let { e ->
          Log.e("MapUsersRepository", "Error updating mapUser", e)
          onFailure(e)
        }
      }
    }
  }

  override fun deleteMapUser(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    val uid = auth.currentUser?.uid ?: return
    val userRef = mapUsersReference.child(uid)

    // Remove MapUser object
    userRef.removeValue().addOnCompleteListener { task ->
      if (task.isSuccessful) {
        // Remove location data from GeoFire
        geoFire.removeLocation(uid)
        onSuccess()
      } else {
        task.exception?.let { e ->
          Log.e("MapUsersRepository", "Error deleting mapUser", e)
          onFailure(e)
        }
      }
    }
  }
}
