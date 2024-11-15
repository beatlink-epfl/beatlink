package com.epfl.beatlink.model.map.user

import androidx.test.core.app.ApplicationProvider
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.anyString
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.robolectric.RobolectricTestRunner
import kotlin.math.abs

@RunWith(RobolectricTestRunner::class)
class MapUsersRepositoryFirestoreTest {

  @Mock private lateinit var mockFirestore: FirebaseFirestore
  @Mock private lateinit var mockFirebaseUser: FirebaseUser
  @Mock private lateinit var mockDocumentReference: DocumentReference
  @Mock private lateinit var mockCollectionReference: CollectionReference
  @Mock private lateinit var mockDocumentSnapshot: DocumentSnapshot
  @Mock private lateinit var documentTask: Task<Void>
  @Mock private lateinit var mockQuerySnapshot: QuerySnapshot
  @Mock private lateinit var querySnapshotTask: Task<QuerySnapshot>
  @Mock private lateinit var mockQuery: Query
  @Mock private lateinit var mockFirebaseAuth: FirebaseAuth

  private lateinit var mapUsersRepositoryFirestore: MapUsersRepositoryFirestore

  private val radiusInMeters = 1000.0
  private val sampleLocation = Location(latitude = 46.5196535, longitude = 6.6322734)
  private val mapUser =
      MapUser(
          username = "testUser",
          currentPlayingTrack =
              CurrentPlayingTrack(
                  songName = "Song",
                  artistName = "Artist",
                  albumName = "Album",
                  albumCover = "CoverURL"),
          location = sampleLocation)

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }

    // Mock FirebaseAuth and current user
    `when`(mockFirebaseAuth.currentUser).thenReturn(mockFirebaseUser)
    `when`(mockFirebaseUser.uid).thenReturn("testUserId")

    `when`(mockFirestore.collection(any())).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(any())).thenReturn(mockDocumentReference)
    mapUsersRepositoryFirestore = MapUsersRepositoryFirestore(mockFirestore, mockFirebaseAuth)
  }

  @Test
  fun init_callsOnSuccessWhenUserIsAuthenticated() {
    val mockAuthStateListener = argumentCaptor<FirebaseAuth.AuthStateListener>()
    var onSuccessCalled = false
    val onSuccess = { onSuccessCalled = true }

    mapUsersRepositoryFirestore.init(onSuccess)
    verify(mockFirebaseAuth).addAuthStateListener(mockAuthStateListener.capture())

    mockAuthStateListener.firstValue.onAuthStateChanged(mockFirebaseAuth)
    assertTrue("onSuccess should have been called when currentUser is not null", onSuccessCalled)
  }

  @Test
  fun getMapUsers_successful() {
      // Mock the Firestore query chain
      `when`(mockCollectionReference.whereGreaterThan(anyString(), any())).thenReturn(mockQuery)
      `when`(mockQuery.whereLessThan(anyString(), any())).thenReturn(mockQuery)
      `when`(mockQuery.whereGreaterThan(anyString(), any())).thenReturn(mockQuery)
      `when`(mockQuery.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))

      // Mock the query snapshot to return a list of mock document snapshots
      `when`(mockQuerySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))
      `when`(mockDocumentSnapshot.toObject(MapUser::class.java)).thenReturn(mapUser)

      mapUsersRepositoryFirestore.getMapUsers(
          currentUserLocation = sampleLocation,
          radiusInMeters = 1000.0,
          onSuccess = { mapUsers -> assertEquals(mapUser, mapUsers[0]) },
          onFailure = { fail("Failure callback should not be called") })
  }

  @Test
  fun getMapUsers_failure() {
      // Mock the Firestore query chain
      `when`(mockCollectionReference.whereGreaterThan(anyString(), any())).thenReturn(mockQuery)
      `when`(mockQuery.whereLessThan(anyString(), any())).thenReturn(mockQuery)
      `when`(mockQuery.whereGreaterThan(anyString(), any())).thenReturn(mockQuery)

      // Mock the documents returned by the query
      `when`(mapUsersRepositoryFirestore.documentToMapUser(mockDocumentSnapshot)).thenReturn(mapUser)

      // Create a mock Task for Firestore async operation
      val exception = Exception("Firestore query failed")
      `when`(mockQuery.get()).thenReturn(querySnapshotTask)
      `when`(querySnapshotTask.isSuccessful).thenReturn(false)
      `when`(querySnapshotTask.exception).thenReturn(exception)

      // Simulate adding the OnCompleteListener to mockTask
      `when`(querySnapshotTask.addOnCompleteListener(any())).thenAnswer { invocation ->
          val listener = invocation.getArgument<OnCompleteListener<QuerySnapshot>>(0)
          listener.onComplete(querySnapshotTask)  // Simulate task completion
          querySnapshotTask
      }

    // Call getMapUsers and test the failure callback
    mapUsersRepositoryFirestore.getMapUsers(
        currentUserLocation = sampleLocation,
        radiusInMeters = 1000.0,
        onSuccess = { mapUsers -> fail("Success callback should not be called") },
        onFailure = { e -> assertEquals("Firestore query failed", e.message) })
  }

  @Test
  fun addMapUser_shouldCallFirestoreCollection() {
    // Simulate success
    `when`(mockDocumentReference.set(any())).thenReturn(documentTask)
      `when`(documentTask.isSuccessful).thenReturn(true)

      // Invoke onCompleteListener for the mock task to simulate async completion
      `when`(documentTask.addOnCompleteListener(any())).thenAnswer { invocation ->
          val listener = invocation.getArgument<OnCompleteListener<Void>>(0)
          listener.onComplete(documentTask)
          documentTask
      }

    mapUsersRepositoryFirestore.addMapUser(
        mapUser = mapUser,
        onSuccess = {},
        onFailure = { fail("Failure callback should not be called") })

    verify(mockDocumentReference).set(any())
  }

  @Test
  fun addMapUser_failure_shouldCallFailureCallback() {
    // Simulate failure by returning a failed task
    val exception = Exception("Firestore set failed")
    `when`(mockDocumentReference.set(any())).thenReturn(documentTask)
      `when`(documentTask.isSuccessful).thenReturn(false)
        `when`(documentTask.exception).thenReturn(exception)

      // Invoke onCompleteListener for the mock task to simulate async completion
      `when`(documentTask.addOnCompleteListener(any())).thenAnswer { invocation ->
          val listener = invocation.getArgument<OnCompleteListener<Void>>(0)
          listener.onComplete(documentTask)
          documentTask
      }

    mapUsersRepositoryFirestore.addMapUser(
        mapUser = mapUser,
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { e -> assertEquals("Firestore set failed", e.message) })
  }

  @Test
  fun updateMapUser_shouldCallFirestoreCollection() {
      // Simulate success
      `when`(mockDocumentReference.set(any())).thenReturn(documentTask)
      `when`(documentTask.isSuccessful).thenReturn(true)

      // Invoke onCompleteListener for the mock task to simulate async completion
      `when`(documentTask.addOnCompleteListener(any())).thenAnswer { invocation ->
          val listener = invocation.getArgument<OnCompleteListener<Void>>(0)
          listener.onComplete(documentTask)
          documentTask
      }

      mapUsersRepositoryFirestore.updateMapUser(
          mapUser = mapUser,
          onSuccess = {},
          onFailure = { fail("Failure callback should not be called") })

      verify(mockDocumentReference).set(any())
  }

  @Test
  fun updateMapUser_failure_shouldCallFailureCallback() {
      // Simulate failure by returning a failed task
      val exception = Exception("Firestore set failed")
      `when`(mockDocumentReference.set(any())).thenReturn(documentTask)
      `when`(documentTask.isSuccessful).thenReturn(false)
      `when`(documentTask.exception).thenReturn(exception)

      // Invoke onCompleteListener for the mock task to simulate async completion
      `when`(documentTask.addOnCompleteListener(any())).thenAnswer { invocation ->
          val listener = invocation.getArgument<OnCompleteListener<Void>>(0)
          listener.onComplete(documentTask)
          documentTask
      }

      mapUsersRepositoryFirestore.updateMapUser(
          mapUser = mapUser,
          onSuccess = { fail("Success callback should not be called") },
          onFailure = { e -> assertEquals("Firestore set failed", e.message) })
  }

  @Test
  fun deleteMapUser_shouldCallFirestoreCollection() {
      // Simulate success
      `when`(mockDocumentReference.delete()).thenReturn(documentTask)
      `when`(documentTask.isSuccessful).thenReturn(true)

      // Invoke onCompleteListener for the mock task to simulate async completion
      `when`(documentTask.addOnCompleteListener(any())).thenAnswer { invocation ->
          val listener = invocation.getArgument<OnCompleteListener<Void>>(0)
          listener.onComplete(documentTask)
          documentTask
      }

      mapUsersRepositoryFirestore.deleteMapUser(
          onSuccess = {},
          onFailure = { fail("Failure callback should not be called") })

      verify(mockDocumentReference).delete()
  }

  @Test
  fun deleteMapUser_failure_shouldCallFailureCallback() {
      // Simulate failure by returning a failed task
      val exception = Exception("Firestore set failed")
      `when`(mockDocumentReference.delete()).thenReturn(documentTask)
      `when`(documentTask.isSuccessful).thenReturn(false)
      `when`(documentTask.exception).thenReturn(exception)

      // Invoke onCompleteListener for the mock task to simulate async completion
      `when`(documentTask.addOnCompleteListener(any())).thenAnswer { invocation ->
          val listener = invocation.getArgument<OnCompleteListener<Void>>(0)
          listener.onComplete(documentTask)
          documentTask
      }

      mapUsersRepositoryFirestore.deleteMapUser(
          onSuccess = { fail("Success callback should not be called") },
          onFailure = { e -> assertEquals("Firestore set failed", e.message) })
  }

    @Test
    fun mapUserToMap_convertsMapUserToMapCorrectly() {
        // Set up a MapUser object
        val location = Location(latitude = 46.5191, longitude = 6.5668)  // Example coordinates
        val track = CurrentPlayingTrack(
            songName = "Imagine",
            artistName = "John Lennon",
            albumName = "Imagine",
            albumCover = "some_url"
        )
        val mapUser = MapUser(username = "testUser", currentPlayingTrack = track, location = location)

        // Call mapUserToMap function
        val result = mapUsersRepositoryFirestore.mapUserToMap(mapUser)

        // Verify that the returned map matches the expected structure
        val expectedMap = mapOf(
            "username" to "testUser",
            "currentPlayingTrack" to mapOf(
                "songName" to "Imagine",
                "artistName" to "John Lennon",
                "albumName" to "Imagine"
            ),
            "location" to mapOf(
                "latitude" to 46.5191,
                "longitude" to 6.5668
            )
        )
        assertEquals(expectedMap, result)
    }

    @Test
    fun haversineDistance_calculatesCorrectDistanceBetweenTwoLocations() {
        // Arrange: Set up two known locations with coordinates
        val loc1 = Location(latitude = 46.5191, longitude = 6.5668)
        val loc2 = Location(latitude = 46.2044, longitude = 6.1432)

        // Act: Calculate the Haversine distance between loc1 and loc2
        val distance = mapUsersRepositoryFirestore.haversineDistance(loc1, loc2)

        // Assert: Check if the distance is within an acceptable range of the known distance
        val expectedDistance = 47800.0  // Approximate distance in meters
        val tolerance = 50.0           // Allowable tolerance in meters
        assertTrue("Distance was off by more than $tolerance meters", abs(distance - expectedDistance) <= tolerance)
    }


}
