package com.epfl.beatlink.repository.library

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.epfl.beatlink.model.library.Playlist
import com.epfl.beatlink.model.spotify.objects.SpotifyTrack
import com.epfl.beatlink.model.spotify.objects.State
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
import org.mockito.Mockito.times
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
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

  private val song1 =
      SpotifyTrack(
          name = "thank god",
          artist = "travis",
          trackId = "1",
          cover = "",
          duration = 1,
          popularity = 2,
          state = State.PAUSE)
  private val song2 =
      SpotifyTrack(
          name = "my eyes",
          artist = "travis",
          trackId = "2",
          cover = "",
          duration = 1,
          popularity = 3,
          state = State.PAUSE)
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
          playlistTracks = listOf(song1),
          nbTracks = 1)
  private val track1 =
      SpotifyTrack(
          name = "Track 1",
          artist = "Artist 1",
          trackId = "trackId1",
          cover = "cover1.jpg",
          duration = 200,
          popularity = 80,
          state = State.PLAY,
          likes = 10)
  private val track2 =
      SpotifyTrack(
          name = "Track 2",
          artist = "Artist 2",
          trackId = "trackId2",
          cover = "cover2.jpg",
          duration = 220,
          popularity = 90,
          state = State.PAUSE,
          likes = 15)
  private val playlist =
      Playlist(
          playlistID = "playlist1",
          playlistCover = "cover.jpg",
          playlistName = "Test Playlist",
          playlistDescription = "Test Description",
          playlistPublic = true,
          userId = "user123",
          playlistOwner = "owner123",
          playlistCollaborators = listOf("collab1", "collab2"),
          playlistTracks = listOf(track1, track2),
          nbTracks = 2)

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

    playlistRepositoryFirestore = PlaylistRepositoryFirestore(mockFirestore, mockAuth)
  }

  @Test
  fun `test documentToPlaylist with valid data`() {
    // Mock the data returned by the DocumentSnapshot
    `when`(mockDocumentSnapshot.getString("playlistID")).thenReturn("playlist1")
    `when`(mockDocumentSnapshot.getString("playlistName")).thenReturn("Test Playlist")
    `when`(mockDocumentSnapshot.getString("playlistDescription")).thenReturn("Test Description")
    `when`(mockDocumentSnapshot.getString("playlistCover")).thenReturn("cover.jpg")
    `when`(mockDocumentSnapshot.getBoolean("playlistPublic")).thenReturn(true)
    `when`(mockDocumentSnapshot.getString("userId")).thenReturn("user123")
    `when`(mockDocumentSnapshot.getString("playlistOwner")).thenReturn("owner123")
    `when`(mockDocumentSnapshot.get("playlistCollaborators"))
        .thenReturn(listOf("collab1", "collab2"))
    `when`(mockDocumentSnapshot.get("playlistTracks"))
        .thenReturn(
            listOf(
                mapOf(
                    "name" to "Track 1",
                    "artist" to "Artist 1",
                    "trackId" to "trackId1",
                    "cover" to "cover1.jpg",
                    "duration" to 200L,
                    "popularity" to 80L,
                    "state" to "PLAY",
                    "likes" to 10L),
                mapOf(
                    "name" to "Track 2",
                    "artist" to "Artist 2",
                    "trackId" to "trackId2",
                    "cover" to "cover2.jpg",
                    "duration" to 220L,
                    "popularity" to 90L,
                    "state" to "PAUSE",
                    "likes" to 15L)))

    // Act: Call the function to convert DocumentSnapshot to Playlist
    val result = playlistRepositoryFirestore.documentToPlaylist(mockDocumentSnapshot)

    // Assert: Verify that the result matches the expected playlist
    assertEquals(playlist.playlistID, result.playlistID)
    assertEquals(playlist.playlistName, result.playlistName)
    assertEquals(playlist.playlistDescription, result.playlistDescription)
    assertEquals(playlist.playlistCover, result.playlistCover)
    assertTrue(result.playlistPublic)
    assertEquals(playlist.userId, result.userId)
    assertEquals(playlist.playlistOwner, result.playlistOwner)
    assertEquals(playlist.playlistCollaborators, result.playlistCollaborators)
    assertEquals(playlist.nbTracks, result.nbTracks)
  }

  @Test
  fun `test spotifyTrackToMap converts track correctly`() {
    // Act: Call the function to convert SpotifyTrack to Map
    val result = playlistRepositoryFirestore.spotifyTrackToMap(track1)

    // Assert: Verify the Map contains the correct data
    assertEquals("Track 1", result["name"])
    assertEquals("Artist 1", result["artist"])
    assertEquals("trackId1", result["trackId"])
    assertEquals("cover1.jpg", result["cover"])
    assertEquals(200, result["duration"])
    assertEquals(80, result["popularity"])
    assertEquals("PLAY", result["state"])
    assertEquals(10, result["likes"])
  }

  @Test
  fun `test playlistToMap converts playlist correctly`() {
    // Act: Call the function to convert Playlist to Map
    val result = playlistRepositoryFirestore.playlistToMap(playlist)

    // Assert: Verify the Map contains the correct data
    assertEquals(playlist.playlistID, result["playlistID"])
    assertEquals(playlist.playlistCover, result["playlistCover"])
    assertEquals(playlist.playlistName, result["playlistName"])
    assertEquals(playlist.playlistDescription, result["playlistDescription"])
    assertTrue(result["playlistPublic"] as Boolean)
    assertEquals(playlist.userId, result["userId"])
    assertEquals(playlist.playlistOwner, result["playlistOwner"])
    assertEquals(playlist.playlistCollaborators, result["playlistCollaborators"])
    assertEquals(playlist.nbTracks, result["nbTracks"])

    // Check if playlistTracks were converted to maps correctly
    val tracksMaps = result["playlistTracks"] as List<Map<String, Any>>
    assertEquals(2, tracksMaps.size)
    assertTrue(tracksMaps[0]["name"] == "Track 1")
    assertTrue(tracksMaps[1]["name"] == "Track 2")
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

  @Test
  fun `getUserId returns correct userID`() {
    // Arrange
    `when`(mockAuth.currentUser).thenReturn(mockFirebaseUser)
    `when`(mockFirebaseUser.uid).thenReturn("testUserId")

    // Act
    val userId = playlistRepositoryFirestore.getUserId()

    // Assert
    assert(userId == "testUserId")
  }

  /** getPlaylists */
  @Test
  fun `getPlaylists should retrieve one playlist owned by the current user`() {
    val mockDocumentSnapshot1 = mock(DocumentSnapshot::class.java)
    val mockDocumentSnapshot2 = mock(DocumentSnapshot::class.java)

    `when`(mockFirestore.collection(any())).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.whereEqualTo(eq("userId"), any())).thenReturn(mockQuery)
    `when`(mockQuery.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))

    val documents = listOf(mockDocumentSnapshot1, mockDocumentSnapshot2)
    `when`(mockQuerySnapshot.documents).thenReturn(documents)
    // Firestore returns a QuerySnapshot, which contains the list of
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
            playlistTracks = listOf(song1),
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

  @Test
  fun `getSharedPlaylists fetches playlists correctly`() {
    // Arrange
    val mockUserID = "testUserId"
    val mockPlaylistID = "playlist1"
    val mockPlaylist =
        Playlist(
            playlistID = mockPlaylistID,
            playlistName = "Test Playlist",
            playlistDescription = "A test description",
            playlistCover = "testCover.jpg",
            playlistPublic = true,
            userId = "otherUser",
            playlistOwner = "ownerUsername",
            playlistCollaborators = listOf("testUserId"),
            playlistTracks = listOf(),
            nbTracks = 0)
    whenever(mockFirestore.collection(any())).thenReturn(mockCollectionReference)
    whenever(mockCollectionReference.whereArrayContains(anyString(), anyString()))
        .thenReturn(mockQuery)
    whenever(mockQuery.whereNotEqualTo(eq("userId"), any())).thenReturn(mockQuery)
    whenever(mockQuery.whereNotEqualTo(eq("userId"), eq(mockUserID))).thenReturn(mockQuery)
    whenever(mockQuery.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))

    whenever(mockQuerySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))
    whenever(mockDocumentSnapshot.toObject(Playlist::class.java)).thenReturn(mockPlaylist)

    // Act
    playlistRepositoryFirestore.getSharedPlaylists(
        onSuccess = { playlists ->
          assertEquals(1, playlists.size)
          assertEquals(mockPlaylistID, playlists[0].playlistID)
          assertEquals(listOf(mockPlaylist), playlists)
        },
        onFailure = { assert(false) { "onFailure should not be called" } })
  }

  @Test
  fun `getSharedPlaylists should call onFailure on error`() {
    val exception = Exception("Test exception")
    val mockCollectionReference = mock(CollectionReference::class.java)

    `when`(mockFirestore.collection(any())).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.whereArrayContains(anyString(), any())).thenReturn(mockQuery)
    whenever(mockQuery.whereNotEqualTo(eq("userId"), any())).thenReturn(mockQuery)
    `when`(mockQuery.get()).thenReturn(Tasks.forException(exception))

    // Act
    playlistRepositoryFirestore.getSharedPlaylists(
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { error -> assert(error == exception) })
    shadowOf(Looper.getMainLooper()).idle()
  }

  @Test
  fun `getPublicPlaylists fetches playlists correctly`() {
    // Arrange
    val mockUserID = "testUserId"
    val mockPlaylistID = "playlist1"
    val mockPlaylist =
        Playlist(
            playlistID = mockPlaylistID,
            playlistName = "Test Playlist",
            playlistDescription = "A test description",
            playlistCover = "testCover.jpg",
            playlistPublic = true,
            userId = "otherUser",
            playlistOwner = "ownerUsername",
            playlistCollaborators = listOf(),
            playlistTracks = listOf(),
            nbTracks = 0)

    whenever(mockFirestore.collection(any())).thenReturn(mockCollectionReference)
    whenever(mockCollectionReference.whereEqualTo("playlistPublic", true)).thenReturn(mockQuery)
    whenever(mockQuery.whereNotEqualTo(eq("userId"), any())).thenReturn(mockQuery)
    whenever(mockQuery.whereNotEqualTo(eq("userId"), eq(mockUserID))).thenReturn(mockQuery)
    whenever(mockQuery.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))

    whenever(mockQuerySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))
    whenever(mockDocumentSnapshot.toObject(Playlist::class.java)).thenReturn(mockPlaylist)

    // Act
    playlistRepositoryFirestore.getPublicPlaylists(
        onSuccess = { playlists ->
          assertEquals(1, playlists.size)
          assertEquals(mockPlaylistID, playlists[0].playlistID)
          assertEquals(listOf(mockPlaylist), playlists)
        },
        onFailure = { assert(false) { "onFailure should not be called" } })
    verify(mockCollectionReference).whereEqualTo(eq("playlistPublic"), eq(true))
  }

  @Test
  fun `getPublicPlaylists should call onFailure on error`() {
    val exception = Exception("Test exception")
    val mockCollectionReference = mock(CollectionReference::class.java)

    `when`(mockFirestore.collection(any())).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.whereEqualTo("playlistPublic", true)).thenReturn(mockQuery)
    whenever(mockQuery.whereNotEqualTo(eq("userId"), any())).thenReturn(mockQuery)
    `when`(mockQuery.get()).thenReturn(Tasks.forException(exception))

    // Act
    playlistRepositoryFirestore.getPublicPlaylists(
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
    // Mock the conversion of the playlist to a Map
    val playlistMap = playlistRepositoryFirestore.playlistToMap(playlist)
    // Simulate a successful operation when saving playlist by mocking Firestore's set() method
    `when`(mockDocumentReference.set(playlistMap)).thenReturn(Tasks.forResult(null))

    playlistRepositoryFirestore.addPlaylist(playlist = playlist, onSuccess = {}, onFailure = {})
    verify(mockDocumentReference).set(playlistMap)
    assertTrue(playlistMap.containsKey("playlistID"))
    assertEquals("playlist1", playlistMap["playlistID"])
    assertEquals("user123", playlistMap["userId"])
    assertEquals("cover.jpg", playlistMap["playlistCover"])
    assertEquals("Test Playlist", playlistMap["playlistName"])
  }

  /** updatePlaylist */
  @Test
  fun updatePlaylist_shouldCallFirestoreCollection() {
    `when`(mockDocumentReference.set(any())).thenReturn(Tasks.forResult(null)) // Simulate success

    // This test verifies that when we add a new playlist, the Firestore `collection()` method is
    // called
    playlistRepositoryFirestore.updatePlaylist(playlist1, onSuccess = {}, onFailure = {})

    shadowOf(Looper.getMainLooper()).idle()

    // Ensure Firestore collection method was called to reference the "playlists" collection
    verify(mockDocumentReference).set(any())
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
    // Mock the conversion of the playlist to a Map
    val playlistMap = playlistRepositoryFirestore.playlistToMap(playlist)
    // Simulate a successful operation when saving playlist by mocking Firestore's set() method
    `when`(mockDocumentReference.set(playlistMap)).thenReturn(Tasks.forResult(null))

    playlistRepositoryFirestore.updatePlaylist(playlist = playlist, onSuccess = {}, onFailure = {})
    verify(mockDocumentReference).set(playlistMap)

    assertTrue(playlistMap.containsKey("playlistID"))
    assertEquals("playlist1", playlistMap["playlistID"])
    assertEquals("user123", playlistMap["userId"])
    assertEquals("cover.jpg", playlistMap["playlistCover"])
    assertEquals("Test Playlist", playlistMap["playlistName"])
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

  /** updatePlaylistCollaborators */
  @Test
  fun updatePlaylistCollaborators_shouldCallFirestoreUpdate() {
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
    `when`(mockDocumentReference.update("playlistCollaborators", listOf("C1")))
        .thenReturn(Tasks.forResult(null)) // Simulate success

    // Call the method to update the track count
    playlistRepositoryFirestore.updatePlaylistCollaborators(
        playlist = playlist,
        newCollabList = listOf("C1"),
        onSuccess = {
          // Verify that the update was successful and the callback was triggered
          verify(mockDocumentReference).update("playlistCollaborators", listOf("C1"))
        },
        onFailure = { _ ->
          // Fail the test if this callback is called
          assert(false) { "onFailure should not be called" }
        })
  }

  @Test
  fun updatePlaylistCollaborators_withFirestoreError() {
    val exception = Exception("Firestore update error")
    `when`(mockDocumentReference.update("playlistCollaborators", listOf("C1")))
        .thenReturn(Tasks.forException(exception))

    playlistRepositoryFirestore.updatePlaylistCollaborators(
        playlist1,
        listOf("C1"),
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { error -> assert(error == exception) })

    shadowOf(Looper.getMainLooper()).idle()
  }

  @Test
  fun updatePlaylistCollaborators_correctlyUpdatesCollaboratorsList() {
    val newCollabList = listOf("C1", "C2", "C3")

    // Mock the initial document retrieval behavior
    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.toObject(Playlist::class.java)).thenReturn(playlist1)

    `when`(mockDocumentReference.update("playlistCollaborators", newCollabList))
        .thenReturn(Tasks.forResult(null))
    playlistRepositoryFirestore.updatePlaylistCollaborators(
        playlist = playlist1,
        newCollabList = newCollabList,
        onSuccess = { assertEquals(newCollabList, playlist1.playlistCollaborators) },
        onFailure = { fail("Update failed") })

    // Verify that the `update()` method was called with the correct field and value
    verify(mockDocumentReference).update("playlistCollaborators", newCollabList)
  }

  /** updatePlaylistTracks */
  @Test
  fun updatePlaylistTracks_correctlyUpdatesTracks() {
    val newTrackList = listOf(track1, track2)
    val trackMapList = newTrackList.map { playlistRepositoryFirestore.spotifyTrackToMap(it) }

    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.toObject(Playlist::class.java)).thenReturn(playlist1)
    `when`(mockDocumentReference.update("playlistTracks", trackMapList))
        .thenReturn(Tasks.forResult(null))

    playlistRepositoryFirestore.updatePlaylistTracks(
        playlist = playlist1,
        newListTracks = newTrackList,
        onSuccess = {
          assertEquals(newTrackList, playlist1.playlistTracks)
          assert(true)
        },
        onFailure = {
          fail("Update failed")
          assert(false)
        })

    verify(mockDocumentReference).update("playlistTracks", trackMapList)
  }

  @Test
  fun updatePlaylistSongs_withFirestoreError() {
    val newTrackList = listOf(track1, track2)
    val trackMapList = newTrackList.map { playlistRepositoryFirestore.spotifyTrackToMap(it) }
    val exception = Exception("Firestore update error")
    `when`(mockDocumentReference.update("playlistTracks", trackMapList))
        .thenReturn(Tasks.forException(exception))

    playlistRepositoryFirestore.updatePlaylistTracks(
        playlist1,
        newTrackList,
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { error -> assert(error == exception) })

    shadowOf(Looper.getMainLooper()).idle()
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
