package com.epfl.beatlink.model.map.user

import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQuery
import com.firebase.geofire.GeoQueryEventListener
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import junit.framework.Assert.assertTrue
import junit.framework.Assert.fail
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.Mockito.eq
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.argumentCaptor
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
  fun init_callsOnSuccessWhenUserIsAuthenticated() {
    val mockAuthStateListener = argumentCaptor<FirebaseAuth.AuthStateListener>()
    var onSuccessCalled = false
    val onSuccess = { onSuccessCalled = true }

    mapUsersRepository.init(onSuccess)
    verify(mockFirebaseAuth).addAuthStateListener(mockAuthStateListener.capture())

    mockAuthStateListener.firstValue.onAuthStateChanged(mockFirebaseAuth)
    assertTrue("onSuccess should have been called when currentUser is not null", onSuccessCalled)
  }

  @Test
  fun getMapUsers_successfulDatabaseTask() {
    val sampleLocation = Location(46.518, 6.567) // User's current location
    val radiusInMeters = 1000.0 // 1 km radius
    val radiusInKilometers = radiusInMeters / 1000.0 // Convert to kilometers

    // Mock GeoFire query
    `when`(mockGeoFire.queryAtLocation(any(GeoLocation::class.java), eq(radiusInKilometers)))
        .thenReturn(mockGeoQuery)

    // Mock MapUser data to return when the database is queried
    val sampleMapUser =
        MapUser("testUser", CurrentPlayingTrack("Song", "Artist", "Album", "Cover"), sampleLocation)
    val mockDataSnapshot = mock(DataSnapshot::class.java)
    `when`(mockDataSnapshot.getValue(MapUser::class.java)).thenReturn(sampleMapUser)

    // Mock a successful task
    val mockTask = mock(Task::class.java) as Task<DataSnapshot>
    `when`(mockTask.isSuccessful).thenReturn(true)
    `when`(mockTask.result).thenReturn(mockDataSnapshot)

    // Mock DatabaseReference to return the successful task
    val mockChildRef = mock(DatabaseReference::class.java)
    `when`(mockDatabaseReference.child("testUserId")).thenReturn(mockChildRef)
    `when`(mockChildRef.get()).thenReturn(mockTask)

    // Simulate GeoQuery event listener
    `when`(mockGeoQuery.addGeoQueryEventListener(any(GeoQueryEventListener::class.java)))
        .thenAnswer { invocation ->
          val listener = invocation.getArgument<GeoQueryEventListener>(0)
          listener.onKeyEntered(
              "testUserId", GeoLocation(sampleLocation.latitude, sampleLocation.longitude))
          listener.onKeyExited("testUserId")
          listener.onKeyMoved("testUserId", GeoLocation(0.0, 0.0))
          listener.onGeoQueryReady()
          null
        }

    // Invoke onCompleteListener for the mock task to simulate async completion
    `when`(mockTask.addOnCompleteListener(any())).thenAnswer { invocation ->
      val listener = invocation.getArgument<OnCompleteListener<DataSnapshot>>(0)
      listener.onComplete(mockTask)
      mockTask
    }

    val successMapUsers = mutableListOf<MapUser>()

    // Call getMapUsers and capture success callback
    mapUsersRepository.getMapUsers(
        currentUserLocation = sampleLocation,
        radiusInMeters = radiusInMeters,
        onSuccess = { users -> successMapUsers.addAll(users) },
        onFailure = { fail("Failure callback should not be called") })

    // Verify queryAtLocation was called with the correct parameters
    verify(mockGeoFire).queryAtLocation(any(GeoLocation::class.java), eq(radiusInKilometers))

    // Verify addGeoQueryEventListener was called
    verify(mockGeoQuery).addGeoQueryEventListener(any())

    // Verify that the correct child reference and get() method are called
    verify(mockDatabaseReference).child("testUserId")
    verify(mockChildRef).get()

    // Simulate completion of the task and verify the result
    mockTask.addOnCompleteListener {
      assert(it.isSuccessful)
      assert(it.result?.getValue(MapUser::class.java) == sampleMapUser)
    }

    // Verify that mapUsers contains the expected MapUser from the successful task
    assert(successMapUsers.contains(sampleMapUser))
  }

  @Test
  fun getMapUsers_unsuccessfulDatabaseTask() {
    val sampleLocation = Location(46.518, 6.567) // User's current location
    val radiusInMeters = 1000.0 // 1 km radius
    val radiusInKilometers = radiusInMeters / 1000.0 // Convert to kilometers

    // Mock GeoFire query
    `when`(mockGeoFire.queryAtLocation(any(GeoLocation::class.java), eq(radiusInKilometers)))
        .thenReturn(mockGeoQuery)

    // Mock an unsuccessful task with a specific exception for the database query
    val sampleException = Exception("Error fetching mapUser data")
    val mockFailureTask = mock(Task::class.java) as Task<DataSnapshot>
    `when`(mockFailureTask.isSuccessful).thenReturn(false)
    `when`(mockFailureTask.exception).thenReturn(sampleException)

    // Mock DatabaseReference to return the unsuccessful task
    val mockChildRef = mock(DatabaseReference::class.java)
    `when`(mockDatabaseReference.child("testUserId")).thenReturn(mockChildRef)
    `when`(mockChildRef.get()).thenReturn(mockFailureTask)

    // Invoke onCompleteListener for the mock task to simulate async completion
    `when`(mockFailureTask.addOnCompleteListener(any())).thenAnswer { invocation ->
      val listener = invocation.getArgument<OnCompleteListener<DataSnapshot>>(0)
      listener.onComplete(mockFailureTask)
      mockFailureTask
    }

    // Call getMapUsers and only expect a failure from the database task
    mapUsersRepository.getMapUsers(
        currentUserLocation = sampleLocation,
        radiusInMeters = radiusInMeters,
        onSuccess = { fail("Success callback should not be called on failure") },
        onFailure = { exception ->
          assert(exception.message?.contains("Error fetching mapUser data") == true)
        })
  }

  @Test
  fun getMapUsers_onGeoQueryError() {
    val sampleLocation = Location(46.518, 6.567)
    val radiusInMeters = 1000.0 // 1 km radius
    val radiusInKilometers = radiusInMeters / 1000.0 // Convert to kilometers

    // Mocking GeoFire query
    `when`(mockGeoFire.queryAtLocation(any(GeoLocation::class.java), eq(radiusInKilometers)))
        .thenReturn(mockGeoQuery)

    // Mock the Task response for the get() method to simulate a failure scenario
    val mockTask = mock(Task::class.java) as Task<DataSnapshot>
    `when`(mockTask.isSuccessful).thenReturn(false)
    `when`(mockTask.exception).thenReturn(Exception("GeoQuery Error: Something went wrong"))

    // Mock the database reference call chain for getting MapUser
    val mockChildRef = mock(DatabaseReference::class.java)
    `when`(mockDatabaseReference.child("testUserId")).thenReturn(mockChildRef)
    `when`(mockChildRef.get()).thenReturn(mockTask)

    // Mock addGeoQueryEventListener to simulate the onGeoQueryError callback
    `when`(mockGeoQuery.addGeoQueryEventListener(any(GeoQueryEventListener::class.java)))
        .thenAnswer { invocation ->
          val listener = invocation.getArgument<GeoQueryEventListener>(0)
          listener.onGeoQueryError(
              DatabaseError.fromException(Exception("GeoQuery Error: Something went wrong")))
          null
        }

    // Call the getMapUsers function
    mapUsersRepository.getMapUsers(
        currentUserLocation = sampleLocation,
        radiusInMeters = radiusInMeters,
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { exception ->
          // Verify that the onFailure callback is invoked with the correct exception message
          assert(exception.message?.contains("GeoQuery Error") == true)
        })

    // Verify queryAtLocation was called with the correct parameters
    verify(mockGeoFire).queryAtLocation(any(GeoLocation::class.java), eq(radiusInKilometers))

    // Verify addGeoQueryEventListener was called
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
  fun addMapUser_unsuccessfulTask() {
    val sampleLocation = Location(46.518, 6.567)
    val sampleMapUser =
        MapUser("testUser", CurrentPlayingTrack("Song", "Artist", "Album", "Cover"), sampleLocation)

    // Simulate failure of setValue call on DatabaseReference
    `when`(mockDatabaseReference.child("testUserId")).thenReturn(mockDatabaseReference)
    `when`(mockDatabaseReference.setValue(any(MapUser::class.java))).thenAnswer {
      `when`(mockDatabaseTask.isSuccessful).thenReturn(false)
      `when`(mockDatabaseTask.exception).thenReturn(Exception("Error adding MapUser"))
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
        onSuccess = { fail("Success callback should not be called on failure") },
        onFailure = { exception -> assert(exception.message == "Error adding MapUser") })

    // Verify that setValue was called with the sample MapUser data
    verify(mockDatabaseReference).setValue(sampleMapUser)
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
  fun updateMapUser_unsuccessfulTask() {
    val sampleLocation = Location(46.518, 6.567)
    val sampleMapUser =
        MapUser("testUser", CurrentPlayingTrack("Song", "Artist", "Album", "Cover"), sampleLocation)

    // Simulate failure of setValue call on DatabaseReference
    `when`(mockDatabaseReference.child("testUserId")).thenReturn(mockDatabaseReference)
    `when`(mockDatabaseReference.setValue(any(MapUser::class.java))).thenAnswer {
      `when`(mockDatabaseTask.isSuccessful).thenReturn(false)
      `when`(mockDatabaseTask.exception).thenReturn(Exception("Error updating MapUser"))
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
        onSuccess = { fail("Success callback should not be called on failure") },
        onFailure = { exception -> assert(exception.message == "Error updating MapUser") })

    // Verify that setValue was called with the sample MapUser data
    verify(mockDatabaseReference).setValue(sampleMapUser)
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

  @Test
  fun deleteMapUser_unsuccessfulTask() {
    // Simulate failure of removeValue call on DatabaseReference
    `when`(mockDatabaseReference.child("testUserId")).thenReturn(mockDatabaseReference)
    `when`(mockDatabaseReference.removeValue()).thenAnswer {
      `when`(mockDatabaseTask.isSuccessful).thenReturn(false)
      `when`(mockDatabaseTask.exception).thenReturn(Exception("Error deleting MapUser"))
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
        onSuccess = { fail("Success callback should not be called on failure") },
        onFailure = { exception -> assert(exception.message == "Error deleting MapUser") })

    // Verify that removeValue was called
    verify(mockDatabaseReference).removeValue()
  }
}
