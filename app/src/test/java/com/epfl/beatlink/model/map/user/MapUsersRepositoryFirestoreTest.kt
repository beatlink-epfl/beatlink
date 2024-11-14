package com.epfl.beatlink.model.map.user

import androidx.test.core.app.ApplicationProvider
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

@RunWith(RobolectricTestRunner::class)
class MapUsersRepositoryFirestoreTest {

  @Mock private lateinit var mockFirestore: FirebaseFirestore
  @Mock private lateinit var mockFirebaseUser: FirebaseUser
  @Mock private lateinit var mockDocumentReference: DocumentReference
  @Mock private lateinit var mockCollectionReference: CollectionReference
  @Mock private lateinit var mockDocumentSnapshot: DocumentSnapshot
  @Mock private lateinit var mockQuerySnapshot: QuerySnapshot
  @Mock private lateinit var mockQuery: Query
  @Mock private lateinit var mockFirebaseAuth: FirebaseAuth

  private lateinit var mapUsersRepositoryFirestore: MapUsersRepositoryFirestore

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

    // Mock the query to return a failed task
    val exception = Exception("Firestore query failed")
    `when`(mockQuery.get()).thenReturn(Tasks.forException(exception))

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
    `when`(mockDocumentReference.set(any())).thenReturn(Tasks.forResult(null))

    mapUsersRepositoryFirestore.addMapUser(
        mapUser = mapUser,
        onSuccess = {},
        onFailure = { fail("Failure callback should not be called") })

    // Ensure Firestore collection method was called to reference the "mapUsers" collection
    verify(mockDocumentReference).set(any())
  }

  @Test
  fun addMapUser_failure_shouldCallFailureCallback() {
    // Simulate failure by returning a failed task
    val exception = Exception("Firestore set failed")
    `when`(mockDocumentReference.set(any())).thenReturn(Tasks.forException(exception))

    // Call addMapUser and test the failure callback
    mapUsersRepositoryFirestore.addMapUser(
        mapUser = mapUser,
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { e -> assertEquals("Firestore set failed", e.message) })
  }

  @Test
  fun updateMapUser_shouldCallFirestoreCollection() {
    // Simulate success
    `when`(mockDocumentReference.set(any())).thenReturn(Tasks.forResult(null))

    mapUsersRepositoryFirestore.updateMapUser(
        mapUser = mapUser,
        onSuccess = {},
        onFailure = { fail("Failure callback should not be called") })

    // Ensure Firestore collection method was called to update the map user document
    verify(mockDocumentReference).set(any())
  }

  @Test
  fun updateMapUser_failure_shouldCallFailureCallback() {
    // Simulate failure by returning a failed task
    val exception = Exception("Firestore update failed")
    `when`(mockDocumentReference.set(any())).thenReturn(Tasks.forException(exception))

    // Call updateMapUser and test the failure callback
    mapUsersRepositoryFirestore.updateMapUser(
        mapUser = mapUser,
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { e -> assertEquals("Firestore update failed", e.message) })
  }

  @Test
  fun deleteMapUser_shouldCallFirestoreCollection() {
    // Simulate success
    `when`(mockDocumentReference.delete()).thenReturn(Tasks.forResult(null))

    mapUsersRepositoryFirestore.deleteMapUser(
        onSuccess = {}, onFailure = { fail("Failure callback should not be called") })

    // Ensure Firestore collection method was called to delete the map user document
    verify(mockDocumentReference).delete()
  }

  @Test
  fun deleteMapUser_failure_shouldCallFailureCallback() {
    // Simulate failure by returning a failed task
    val exception = Exception("Firestore delete failed")
    `when`(mockDocumentReference.delete()).thenReturn(Tasks.forException(exception))

    // Call deleteMapUser and test the failure callback
    mapUsersRepositoryFirestore.deleteMapUser(
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { e -> assertEquals("Firestore delete failed", e.message) })
  }
}
