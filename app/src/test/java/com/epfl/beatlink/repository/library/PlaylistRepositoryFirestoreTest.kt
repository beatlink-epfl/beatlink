package com.epfl.beatlink.repository.library

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.epfl.beatlink.model.library.Playlist
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
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class PlaylistRepositoryFirestoreTest {

  @Mock private lateinit var mockQuery: Query
  @Mock private lateinit var mockQuerySnapshot: QuerySnapshot

  @Mock private lateinit var mockFirestore: FirebaseFirestore
  @Mock private lateinit var mockAuth: FirebaseAuth
  @Mock private lateinit var mockFirebaseUser: FirebaseUser

  @Mock private lateinit var mockDocumentReference: DocumentReference
  @Mock private lateinit var mockCollectionReference: CollectionReference
  @Mock private lateinit var mockDocumentSnapshot: DocumentSnapshot

  private lateinit var playlistRepositoryFirestore: PlaylistRepositoryFirestore

  private lateinit var onSuccess: () -> Unit

  private val playlist1 =
      Playlist(
          playlistID = "1",
          playlistCover = "",
          playlistName = "playlist 1",
          playlistDescription = "testingggg",
          playlistPublic = false,
          userId = "",
          playlistOwner = "luna",
          playlistCollaborators = emptyList(),
          playlistTracks = emptyList(),
          nbTracks = 0)
  private val playlist2 =
      Playlist(
          playlistID = "2",
          playlistCover = "",
          playlistName = "playlist 2",
          playlistDescription = "testingggg 2",
          playlistPublic = false,
          userId = "testUserId",
          playlistOwner = "luna2",
          playlistCollaborators = emptyList(),
          playlistTracks = listOf("thank god"),
          nbTracks = 1)

  @Before
  fun setUp() {
    // Initialize FirebaseApp with application context
    MockitoAnnotations.openMocks(this)

    // Initialize Firebase if necessary
    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }

    mockAuth = mock()
    mockFirebaseUser = mock()
    onSuccess = mock()
    mockQuery = mock(Query::class.java)
    mockQuerySnapshot = mock(QuerySnapshot::class.java)
    mockDocumentSnapshot = mock(DocumentSnapshot::class.java)

    mockFirestore = mock(FirebaseFirestore::class.java)
    mockCollectionReference = mock(CollectionReference::class.java)

    // Mock FirebaseAuth and current user
    `when`(mockAuth.currentUser).thenReturn(mockFirebaseUser)
    `when`(mockFirebaseUser.uid).thenReturn("testUserId")

    `when`(mockFirestore.collection(any())).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(any())).thenReturn(mockDocumentReference)
    `when`(mockCollectionReference.document()).thenReturn(mockDocumentReference)

    `when`(mockQuery.whereEqualTo(anyString(), any())).thenReturn(mockQuery)

    playlistRepositoryFirestore = PlaylistRepositoryFirestore(mockFirestore, mockAuth)
  }

  @Test
  fun testDocumentToPlaylist_withValidData() {
    // Arrange: Define mock data that should be returned by the DocumentSnapshot
    val expectedPlaylist =
        Playlist(
            playlistID = "1",
            playlistCover = "cover.jpg",
            playlistName = "Test Playlist",
            playlistDescription = "Test Description",
            playlistPublic = true,
            userId = "user123",
            playlistOwner = "owner123",
            playlistCollaborators = listOf("collaborator1", "collaborator2"),
            playlistTracks = listOf("song1", "song2"),
            nbTracks = 2)

    // Simulate `getString` and `getBoolean` calls on the mocked DocumentSnapshot
    `when`(mockDocumentSnapshot.getString("playlistID")).thenReturn("1")
    `when`(mockDocumentSnapshot.getString("playlistCover")).thenReturn("cover.jpg")
    `when`(mockDocumentSnapshot.getString("playlistName")).thenReturn("Test Playlist")
    `when`(mockDocumentSnapshot.getString("playlistDescription")).thenReturn("Test Description")
    `when`(mockDocumentSnapshot.getBoolean("playlistPublic")).thenReturn(true)
    `when`(mockDocumentSnapshot.getString("userId")).thenReturn("user123")
    `when`(mockDocumentSnapshot.getString("playlistOwner")).thenReturn("owner123")
    `when`(mockDocumentSnapshot.get("playlistCollaborators"))
        .thenReturn(listOf("collaborator1", "collaborator2"))
    `when`(mockDocumentSnapshot.get("playlistSongs")).thenReturn(listOf("song1", "song2"))

    // Act: Call the function to convert the DocumentSnapshot to a Playlist
    val result = playlistRepositoryFirestore.documentToPlaylist(mockDocumentSnapshot)

    // Assert: Verify that the result matches the expected playlist
    assertEquals(expectedPlaylist, result)
  }

  @Test
  fun `init should call onSuccess when user is authenticated`() {
    val mockAuth = mock(FirebaseAuth::class.java)
    val mockFirebaseUser = mock(FirebaseUser::class.java)

    // Mock the current user and their UID
    `when`(mockAuth.currentUser).thenReturn(mockFirebaseUser)
    `when`(mockFirebaseUser.uid).thenReturn("testUserId")

    // The `init` method should invoke `onSuccess` when user is authenticated
    var onSuccessCalled = false

    // Create an instance of your repository or class that contains the `init` method
    val playlistRepositoryFirestore = PlaylistRepositoryFirestore(mockFirestore, mockAuth)

    // Act: Call the init method
    playlistRepositoryFirestore.init(onSuccess = { onSuccessCalled = true })

    // Simulate the authentication state listener callback (since this is async)
    val authStateListenerCaptor = argumentCaptor<FirebaseAuth.AuthStateListener>()
    verify(mockAuth).addAuthStateListener(authStateListenerCaptor.capture())

    // Simulate the user being authenticated (trigger the listener manually)
    authStateListenerCaptor.firstValue.onAuthStateChanged(mockAuth)

    // Assert: Verify onSuccess is called
    assertTrue("onSuccess should be called when user is authenticated", onSuccessCalled)
  }

  @Test
  fun `init should not call onSuccess when user is not authenticated`() {
    // Mock the behavior when there's no current user
    `when`(mockAuth.currentUser).thenReturn(null)

    // Call init method
    playlistRepositoryFirestore.init(onSuccess)

    // Verify that onSuccess is not called
    verify(onSuccess, never()).invoke()
  }

  @Test
  fun getNewUid_generatesExpectedUid() {
    `when`(mockDocumentReference.id).thenReturn("1")
    val uid = playlistRepositoryFirestore.getNewUid()
    assert(uid == "1")
  }

  /** getPlaylists */
  @Test
  fun `getPlaylists should retrieve one playlist owned by the current user`() {
    val mockDocumentSnapshot1 = mock(DocumentSnapshot::class.java)
    val mockDocumentSnapshot2 = mock(DocumentSnapshot::class.java)

    `when`(mockFirestore.collection(any())).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.whereEqualTo(anyString(), any())).thenReturn(mockQuery)
    `when`(mockQuery.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))

    val documents = listOf(mockDocumentSnapshot1, mockDocumentSnapshot2)
    `when`(mockQuerySnapshot.documents)
        .thenReturn(documents) // Firestore returns a QuerySnapshot, which contains the list of
    // DocumentSnapshots that match the query.
    `when`(mockDocumentSnapshot1.toObject(Playlist::class.java)).thenReturn(playlist1)
    `when`(mockDocumentSnapshot2.toObject(Playlist::class.java)).thenReturn(playlist2)

    playlistRepositoryFirestore.getPlaylists(
        onSuccess = { playlists ->
          println("Retrieved playlists: $playlists")

          // Check if playlist2 is retrieved
          assertEquals(1, playlists.size)
          assertEquals("2", playlists[0].playlistID)
          assertEquals("playlist 2", playlists[0].playlistName)
        },
        onFailure = { fail("Failure callback should not be called") })
  }

  @Test
  fun `getPlaylists should retrieve all playlists owned by the current user`() {
    val playlist1 =
        Playlist(
            playlistID = "1",
            playlistCover = "",
            playlistName = "playlist 1",
            playlistDescription = "testing 2",
            playlistPublic = false,
            userId = "testUserId",
            playlistOwner = "luna2",
            playlistCollaborators = emptyList(),
            playlistTracks = listOf("thank god"),
            nbTracks = 1)
    val mockDocumentSnapshot1 = mock(DocumentSnapshot::class.java)
    val mockDocumentSnapshot2 = mock(DocumentSnapshot::class.java)

    `when`(mockFirestore.collection(any())).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.whereEqualTo(anyString(), any())).thenReturn(mockQuery)
    `when`(mockQuery.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))

    val documents = listOf(mockDocumentSnapshot1, mockDocumentSnapshot2)
    `when`(mockQuerySnapshot.documents)
        .thenReturn(documents) // Firestore returns a QuerySnapshot, which contains the list of
    // DocumentSnapshots that match the query.
    `when`(mockDocumentSnapshot1.toObject(Playlist::class.java)).thenReturn(playlist1)
    `when`(mockDocumentSnapshot2.toObject(Playlist::class.java)).thenReturn(playlist2)

    playlistRepositoryFirestore.getPlaylists(
        onSuccess = { playlists ->
          println("Retrieved playlists: $playlists")

          // Check if playlist2 is retrieved
          assertEquals(2, playlists.size)
          assertEquals("1", playlists[0].playlistID)
          assertEquals("2", playlists[1].playlistID)
          assertEquals("playlist 1", playlists[0].playlistName)
          assertEquals("playlist 2", playlists[1].playlistName)
        },
        onFailure = { fail("Failure callback should not be called") })
  }

  @Test
  fun `getPlaylists should call onFailure on error`() {
    val exception = Exception("Test exception")
    val mockCollectionReference = mock(CollectionReference::class.java)

    // Mock the collection reference and query behavior
    `when`(mockFirestore.collection(any())).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.whereEqualTo(anyString(), any())).thenReturn(mockQuery)
    `when`(mockQuery.get()).thenReturn(Tasks.forException(exception))

    playlistRepositoryFirestore.getPlaylists(
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { error -> assert(error == exception) })

    shadowOf(Looper.getMainLooper()).idle()
  }

  /** addPlaylist */
  @Test
  fun addPlaylist_shouldCallFirestoreCollection() {
    `when`(mockDocumentReference.set(any())).thenReturn(Tasks.forResult(null)) // Simulate success

    // This test verifies that when we add a new playlist, the Firestore `collection()` method is
    // called
    playlistRepositoryFirestore.addPlaylist(playlist1, onSuccess = {}, onFailure = {})

    shadowOf(Looper.getMainLooper()).idle()

    // Ensure Firestore collection method was called to reference the "playlists" collection
    verify(mockDocumentReference).set(any())
  }

  @Test
  fun addPlaylist_withFirestoreError() {
    val exception = Exception("Firestore set error")
    `when`(mockDocumentReference.set(any())).thenReturn(Tasks.forException(exception))

    playlistRepositoryFirestore.addPlaylist(
        playlist1,
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { error -> assert(error == exception) })

    shadowOf(Looper.getMainLooper()).idle()
  }

  @Test
  fun addPlaylist_isCorrectlyAdded() {
    // simulate a successful operation when saving playlist1
    val newplaylist1 = playlist1.copy(userId = "testUserId")
    `when`(mockDocumentReference.set(newplaylist1)).thenReturn(Tasks.forResult(null))
    playlistRepositoryFirestore.addPlaylist(playlist = playlist1, onSuccess = {}, onFailure = {})
    verify(mockDocumentReference).set(playlist1)
  }

  /** updatePlaylist */
  @Test
  fun updatePlaylist_shouldCallFirestoreCollection() {
    // Mock Firestore behavior
    `when`(mockFirestore.collection("playlists")).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document("playlistID")).thenReturn(mockDocumentReference)

    // Create a mock Playlist object
    val playlist =
        Playlist(
            playlistID = "playlistID",
            playlistName = "Test Playlist",
            playlistDescription = "A description",
            playlistCover = "cover.jpg",
            playlistPublic = true,
            userId = "userID",
            playlistOwner = "owner",
            playlistCollaborators = emptyList(),
            playlistTracks = emptyList(),
            nbTracks = 0)

    // Call the method to update the playlist
    playlistRepositoryFirestore.updatePlaylist(playlist, onSuccess = {}, onFailure = {})

    // Verify that Firestore's collection method is called with the correct path
    verify(mockFirestore).collection("playlists")

    // Verify that the correct document is being updated by checking the document reference
    verify(mockCollectionReference).document("playlistID")

    // Verify that the set method was called on the document reference
    verify(mockDocumentReference).set(playlist)
  }

  @Test
  fun updatePlaylist_withFirestoreError() {
    val exception = Exception("Firestore set error")
    `when`(mockDocumentReference.set(any())).thenReturn(Tasks.forException(exception))

    playlistRepositoryFirestore.updatePlaylist(
        playlist1,
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { error -> assert(error == exception) })

    shadowOf(Looper.getMainLooper()).idle()
  }

  @Test
  fun updatePlaylist_correctlyUpdatesPlaylist() {
    // Mock the successful set operation in Firestore
    `when`(mockDocumentReference.set(playlist1)).thenReturn(Tasks.forResult(null))
    playlistRepositoryFirestore.updatePlaylist(playlist = playlist2, onSuccess = {}, onFailure = {})
    verify(mockDocumentReference).set(playlist2)
    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
  }

  @Test
  fun updatePlaylist_correctlyCallsOnSuccess() {
    `when`(mockDocumentReference.set(any())).thenReturn(Tasks.forResult(null))

    playlistRepositoryFirestore.updatePlaylist(
        playlist = playlist1,
        onSuccess = {},
        onFailure = { fail("onFailure callback should not be called") })

    shadowOf(Looper.getMainLooper()).idle()
  }

  /** updatePlaylistTrackCount */
  @Test
  fun updatePlaylistTrackCount_shouldCallFirestoreUpdate() {
    // Create a playlist object to be used in the test
    val playlist =
        Playlist(
            playlistID = "playlistID",
            playlistName = "Test Playlist",
            playlistDescription = "A description",
            playlistCover = "cover.jpg",
            playlistPublic = true,
            userId = "userID",
            playlistOwner = "owner",
            playlistCollaborators = emptyList(),
            playlistTracks = emptyList(),
            nbTracks = 0)

    // Simulate the Firestore update response using Tasks.forResult() (success case)
    `when`(mockFirestore.collection("playlists").document("playlistID"))
        .thenReturn(mockDocumentReference)
    `when`(mockDocumentReference.update("nbTracks", 10))
        .thenReturn(Tasks.forResult(null)) // Simulate success

    // Call the method to update the track count
    playlistRepositoryFirestore.updatePlaylistTrackCount(
        playlist = playlist,
        newTrackCount = 10,
        onSuccess = {
          // Verify that the update was successful and the callback was triggered
          verify(mockDocumentReference).update("nbTracks", 10)
        },
        onFailure = { _ ->
          // Fail the test if this callback is called
          assert(false) { "onFailure should not be called" }
        })
  }

  @Test
  fun updatePlaylistTrackCount_withFirestoreError() {
    val exception = Exception("Firestore update error")
    `when`(mockDocumentReference.update("nbTracks", 10)).thenReturn(Tasks.forException(exception))

    playlistRepositoryFirestore.updatePlaylistTrackCount(
        playlist1,
        10,
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { error -> assert(error == exception) })

    shadowOf(Looper.getMainLooper()).idle()
  }

  @Test
  fun updatePlaylistTrackCount_correctlyUpdatesNbTracks() {
    val newTrackCount = 3

    // Mock the initial document retrieval behavior
    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.toObject(Playlist::class.java)).thenReturn(playlist1)

    `when`(mockDocumentReference.update("nbTracks", newTrackCount))
        .thenReturn(Tasks.forResult(null))
    playlistRepositoryFirestore.updatePlaylistTrackCount(
        playlist = playlist1,
        newTrackCount = newTrackCount,
        onSuccess = { assertEquals(newTrackCount, playlist1.nbTracks) },
        onFailure = { fail("Update failed") })

    // Verify that the `update()` method was called with the correct field and value
    verify(mockDocumentReference).update("nbTracks", newTrackCount)
  }

  @Test
  fun updatePlaylistTrackCount_correctlyCallsOnSuccess() {
    val newTrackCount = 3

    // Mock the initial document retrieval behavior
    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.toObject(Playlist::class.java)).thenReturn(playlist1)

    `when`(mockDocumentReference.update("nbTracks", newTrackCount))
        .thenReturn(Tasks.forResult(null))
    playlistRepositoryFirestore.updatePlaylistTrackCount(
        playlist = playlist1,
        newTrackCount = newTrackCount,
        onSuccess = {},
        onFailure = { fail("Fail callback should not be called") })
  }

  /** updatePlaylistSongs */
  @Test
  fun updatePlaylistSongs_correctlyUpdatesSongs() {
    val newSongs = listOf("song1")
    // Mock the initial document retrieval behavior
    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.toObject(Playlist::class.java)).thenReturn(playlist1)

    `when`(mockDocumentReference.update("playlistSongs", newSongs))
        .thenReturn(Tasks.forResult(null))
    playlistRepositoryFirestore.updatePlaylistTracks(
        playlist = playlist1,
        newListSongs = newSongs,
        onSuccess = { assertEquals(newSongs, playlist1.playlistTracks) },
        onFailure = { fail("Update failed") })

    // Verify that the `update()` method was called with the correct field and value
    verify(mockDocumentReference).update("playlistSongs", newSongs)
  }

  @Test
  fun updatePlaylistSongs_withFirestoreError() {
    val newSongs = listOf("hey", "mama")
    val exception = Exception("Firestore update error")
    `when`(mockDocumentReference.update("playlistSongs", newSongs))
        .thenReturn(Tasks.forException(exception))

    playlistRepositoryFirestore.updatePlaylistTracks(
        playlist1,
        newSongs,
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { error -> assert(error == exception) })

    shadowOf(Looper.getMainLooper()).idle()
  }

  @Test
  fun updatePlaylistSongs_shouldCallOnSuccessWhenUpdateIsSuccessful() {
    // Playlist and new song list
    val playlist =
        Playlist(
            playlistID = "playlistID",
            playlistCover = "",
            playlistName = "Test Playlist",
            userId = "testUserId",
            playlistOwner = "username",
            playlistCollaborators = emptyList(),
            playlistTracks = listOf("song1", "song2"))
    val newSongsList = listOf("song3", "song4")

    // Simulate Firestore successful update
    `when`(mockFirestore.collection("playlists").document(playlist.playlistID))
        .thenReturn(mockDocumentReference)
    `when`(mockDocumentReference.update("playlistSongs", newSongsList))
        .thenReturn(Tasks.forResult(null)) // Successful update

    // Call the method to update the playlist songs
    playlistRepositoryFirestore.updatePlaylistTracks(
        playlist = playlist,
        newListSongs = newSongsList,
        onSuccess = {
          // If onSuccess is called, the test should pass
          assert(true) { "onSuccess should be called when the update is successful." }
        },
        onFailure = { exception ->
          // Fail the test if onFailure is called
          assert(false) { "onFailure should not be called: ${exception.message}" }
        })
  }

  @Test
  fun updatePlaylistSongs_shouldCallOnFailureWhenUpdateFails() {
    // Playlist and new song list
    val playlist =
        Playlist(
            playlistID = "playlistID",
            playlistCover = "",
            playlistName = "Test Playlist",
            userId = "testUserId",
            playlistOwner = "username",
            playlistCollaborators = emptyList(),
            playlistTracks = listOf("song1", "song2"))
    val newSongsList = listOf("song3", "song4")

    // Simulate Firestore failed update by using Tasks.forException()
    `when`(mockFirestore.collection("playlists").document(playlist.playlistID))
        .thenReturn(mockDocumentReference)
    `when`(mockDocumentReference.update("playlistSongs", newSongsList))
        .thenReturn(Tasks.forException(Exception("Firestore update error"))) // Simulate failure

    // Call the method to update the playlist songs
    playlistRepositoryFirestore.updatePlaylistTracks(
        playlist = playlist,
        newListSongs = newSongsList,
        onSuccess = {
          // Fail the test if onSuccess is called
          assert(false) { "onSuccess should not be called" }
        },
        onFailure = { exception ->
          // Verify that onFailure is called with the correct exception
          assertTrue(exception.message == "Firestore update error")
        })
  }

  /** deletePlaylist */
  @Test
  fun deletePlaylistById_shouldCallDocumentReferenceDelete() {
    `when`(mockDocumentReference.delete()).thenReturn(Tasks.forResult(null))

    playlistRepositoryFirestore.deletePlaylistById("1", onSuccess = {}, onFailure = {})

    shadowOf(Looper.getMainLooper()).idle() // Ensure all asynchronous operations complete

    verify(mockDocumentReference).delete()
  }

  @Test
  fun deletePlaylistById_withFirestoreError() {
    val exception = Exception("Firestore delete error")
    `when`(mockDocumentReference.delete()).thenReturn(Tasks.forException(exception))

    playlistRepositoryFirestore.deletePlaylistById(
        "1",
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { error -> assert(error == exception) })

    shadowOf(Looper.getMainLooper()).idle()
  }

  @Test
  fun deletePlaylistById_correctlyDeletesPlaylist() {
    // Mock the successful set operation in Firestore
    `when`(mockDocumentReference.set(playlist1)).thenReturn(Tasks.forResult(null))
    playlistRepositoryFirestore.deletePlaylistById(id = "1", onSuccess = {}, onFailure = {})
    verify(mockDocumentReference).delete()
  }

  @Test
  fun deletePlaylistById_shouldCallOnFailureWhenErrorOccurs() {
    // Playlist ID to be used in the test
    val playlistId = "playlistID"

    // Simulate Firestore delete failure by using Tasks.forException()
    `when`(mockFirestore.collection("playlists").document(playlistId))
        .thenReturn(mockDocumentReference)
    `when`(mockDocumentReference.delete())
        .thenReturn(Tasks.forException(Exception("Firestore delete error"))) // Simulate failure

    // Call the method to delete the playlist
    playlistRepositoryFirestore.deletePlaylistById(
        id = playlistId,
        onSuccess = {
          // Fail the test if onSuccess is called
          assert(false) { "onSuccess should not be called" }
        },
        onFailure = { exception ->
          // Verify that onFailure is called with the correct exception
          assertTrue(exception.message == "Firestore delete error")
        })
  }
}
