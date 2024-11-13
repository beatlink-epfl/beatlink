package com.epfl.beatlink.model.map.user

import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQuery
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import junit.framework.Assert.fail
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.Mockito.eq
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify

class MapUsersRepositoryFirebaseDatabaseTest {

  @Mock lateinit var mockFirebaseAuth: FirebaseAuth
  @Mock lateinit var mockFirebaseUser: FirebaseUser
  @Mock lateinit var mockDatabaseReference: DatabaseReference
  @Mock lateinit var mockDatabaseTask: Task<Void>
  @Mock lateinit var mockGeoFire: GeoFire
  @Mock lateinit var mockGeoQuery: GeoQuery

  private lateinit var mapUsersRepository: MapUsersRepositoryFirebaseDatabase

  @Before
  fun setup() {
    MockitoAnnotations.openMocks(this)

    // Set up mock FirebaseAuth to return a test user
    `when`(mockFirebaseAuth.currentUser).thenReturn(mockFirebaseUser)
    `when`(mockFirebaseUser.uid).thenReturn("testUserId")

    // Instantiate repository with mock dependencies
    mapUsersRepository =
        MapUsersRepositoryFirebaseDatabase(
            auth = mockFirebaseAuth,
            mapUsersReference = mockDatabaseReference,
            geoFire = mockGeoFire)
  }

  @Test
  fun getMapUsers_callsGeoFireMethods() {
    val sampleLocation = Location(46.518, 6.567) // User's current location
    val radiusInMeters = 1000.0 // 1 km radius
    val radiusInKilometers = radiusInMeters / 1000.0 // Convert to kilometers

    `when`(mockGeoFire.queryAtLocation(any(GeoLocation::class.java), eq(radiusInKilometers)))
        .thenReturn(mockGeoQuery)

    // Call the getMapUsers function
    mapUsersRepository.getMapUsers(
        currentUserLocation = sampleLocation,
        radiusInMeters = radiusInMeters,
        onSuccess = {},
        onFailure = { fail("Failure callback should not be called") })

    // Verify queryAtLocation was called with correct parameters
    verify(mockGeoFire).queryAtLocation(any(GeoLocation::class.java), eq(radiusInKilometers))

    // Verify addGeoQueryEventListener was called on the GeoQuery
    verify(mockGeoQuery).addGeoQueryEventListener(any())
  }

  @Test
  fun addMapUser_setsMapUserDataAndLocationInGeoFire() {
    val sampleLocation = Location(46.518, 6.567)
    val sampleMapUser =
        MapUser(
            username = "testUser",
            currentPlayingTrack =
                CurrentPlayingTrack(
                    "Sample Song", "Sample Artist", "Sample Album", "SampleAlbumCoverUrl"),
            location = sampleLocation)

    // Mock successful completion of setValue call on DatabaseReference
    `when`(mockDatabaseReference.child("testUserId")).thenReturn(mockDatabaseReference)
    `when`(mockDatabaseReference.setValue(any(MapUser::class.java))).thenAnswer {
      `when`(mockDatabaseTask.isSuccessful).thenReturn(true)
      mockDatabaseTask
    }

    // Invoke onCompleteListener for the mock task to simulate async completion
    `when`(mockDatabaseTask.addOnCompleteListener(any())).thenAnswer { invocation ->
      val listener = invocation.getArgument<OnCompleteListener<Void>>(0)
      listener.onComplete(mockDatabaseTask)
      mockDatabaseTask
    }

    // Call the addMapUser function
    mapUsersRepository.addMapUser(
        mapUser = sampleMapUser,
        onSuccess = {},
        onFailure = { fail("Failure callback should not be called") })

    // Verify that setValue was called with the sample MapUser data
    verify(mockDatabaseReference).setValue(sampleMapUser)

    // Verify that the MapUser's location was added to GeoFire with the correct coordinates
    verify(mockGeoFire)
        .setLocation("testUserId", GeoLocation(sampleLocation.latitude, sampleLocation.longitude))
  }

  @Test
  fun updateMapUser_updatesMapUserDataAndLocationInGeoFire() {
    val sampleLocation = Location(46.518, 6.567)
    val sampleMapUser =
        MapUser(
            username = "testUser",
            currentPlayingTrack =
                CurrentPlayingTrack(
                    "Sample Song", "Sample Artist", "Sample Album", "SampleAlbumCoverUrl"),
            location = sampleLocation)

    // Mock successful completion of setValue call on DatabaseReference
    `when`(mockDatabaseReference.child("testUserId")).thenReturn(mockDatabaseReference)
    `when`(mockDatabaseReference.setValue(any(MapUser::class.java))).thenAnswer {
      `when`(mockDatabaseTask.isSuccessful).thenReturn(true)
      mockDatabaseTask
    }

    // Invoke onCompleteListener for the mock task to simulate async completion
    `when`(mockDatabaseTask.addOnCompleteListener(any())).thenAnswer { invocation ->
      val listener = invocation.getArgument<OnCompleteListener<Void>>(0)
      listener.onComplete(mockDatabaseTask)
      mockDatabaseTask
    }

    // Call the updateMapUser function
    mapUsersRepository.updateMapUser(
        mapUser = sampleMapUser,
        onSuccess = {},
        onFailure = { fail("Failure callback should not be called") })

    // Verify that setValue was called with the sample MapUser data
    verify(mockDatabaseReference).setValue(sampleMapUser)

    // Verify that the MapUser's location was added to GeoFire with the correct coordinates
    verify(mockGeoFire)
        .setLocation("testUserId", GeoLocation(sampleLocation.latitude, sampleLocation.longitude))
  }

  @Test
  fun deleteMapUser_removesMapUserDataAndLocationFromGeoFire() {
    // Mock the behavior of the DatabaseReference to simulate successful remove operation
    `when`(mockDatabaseReference.child("testUserId")).thenReturn(mockDatabaseReference)
    `when`(mockDatabaseReference.removeValue()).thenAnswer {
      `when`(mockDatabaseTask.isSuccessful).thenReturn(true)
      mockDatabaseTask
    }

    // Invoke onCompleteListener for the mock task to simulate async completion
    `when`(mockDatabaseTask.addOnCompleteListener(any())).thenAnswer { invocation ->
      val listener = invocation.getArgument<OnCompleteListener<Void>>(0)
      listener.onComplete(mockDatabaseTask)
      mockDatabaseTask
    }

    // Call the deleteMapUser function
    mapUsersRepository.deleteMapUser(
        onSuccess = {}, onFailure = { fail("Failure callback should not be called") })

    // Verify that removeValue was called with the sample MapUser data
    verify(mockDatabaseReference).removeValue()

    // Verify that the MapUser's location was removed from GeoFire
    verify(mockGeoFire).removeLocation("testUserId")
  }
}
