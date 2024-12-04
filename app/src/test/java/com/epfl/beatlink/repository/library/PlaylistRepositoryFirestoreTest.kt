package com.epfl.beatlink.repository.library

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.epfl.beatlink.model.library.Playlist
import com.epfl.beatlink.model.library.PlaylistTrack
import com.epfl.beatlink.model.spotify.objects.SpotifyTrack
import com.epfl.beatlink.model.spotify.objects.State
import com.epfl.beatlink.utils.ImageUtils.resizeAndCompressImageFromUri
import com.google.android.gms.tasks.OnFailureListener
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
import com.google.firebase.firestore.SetOptions
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.fail
import org.junit.Assert.assertNull
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
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@Suppress("UNCHECKED_CAST")
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
  private lateinit var imageUri: Uri
  private lateinit var context: Context
  private lateinit var mockTask: Task<DocumentSnapshot>
  private lateinit var mockBitmap: Bitmap

  private val collectionPath = "playlists"

  private lateinit var onSuccess: () -> Unit

  private val track1 =
      PlaylistTrack(
          track =
              SpotifyTrack(
                  name = "thank god",
                  artist = "travis",
                  trackId = "1",
                  cover = "",
                  duration = 1,
                  popularity = 2,
                  state = State.PAUSE),
          likes = 0,
          likedBy = mutableListOf())

  private val track2 =
      PlaylistTrack(
          track =
              SpotifyTrack(
                  name = "my eyes",
                  artist = "travis",
                  trackId = "2",
                  cover = "",
                  duration = 1,
                  popularity = 3,
                  state = State.PAUSE),
          likes = 0,
          likedBy = mutableListOf())

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
          playlistTracks = listOf(track1),
          nbTracks = 1)

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
    imageUri = mock(Uri::class.java)
    context = mock(Context::class.java)
    mockTask = mock(Task::class.java) as Task<DocumentSnapshot>
    mockBitmap = mock(Bitmap::class.java)

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
  fun `documentToPlaylist handles invalid playlistTracks gracefully`() {
    // Mock document snapshot with invalid playlistTracks
    val mockData =
        mapOf(
            "playlistID" to "playlist123",
            "playlistName" to "Test Playlist",
            "playlistPublic" to true,
            "userId" to "user123",
            "playlistTracks" to
                listOf(
                    mapOf(
                        "name" to "Song 1",
                        "artist" to null, // Invalid field
                        "trackId" to "track123"),
                    mapOf(
                        "invalidKey" to "invalidValue" // Completely invalid track data
                        )))
    `when`(mockDocumentSnapshot.data).thenReturn(mockData)

    val playlist = playlistRepositoryFirestore.documentToPlaylist(mockDocumentSnapshot)

    // Assertions
    assertEquals(0, playlist.playlistTracks.size) // Invalid tracks should be filtered out
    assertEquals(0, playlist.nbTracks)
  }

  @Test
  fun `playlistTrackToMap converts PlaylistTrack to Map successfully`() {
    // Create a PlaylistTrack object
    val track =
        PlaylistTrack(
            track =
                SpotifyTrack(
                    name = "Test Song",
                    artist = "Test Artist",
                    trackId = "track123",
                    cover = "cover.jpg",
                    duration = 240,
                    popularity = 85,
                    state = State.PLAY),
            likes = 5,
            likedBy = mutableListOf("user1", "user2"))

    // Convert PlaylistTrack to Map
    val resultMap = playlistRepositoryFirestore.playlistTrackToMap(track)

    // Assertions
    assertEquals("Test Song", resultMap["name"])
    assertEquals("Test Artist", resultMap["artist"])
    assertEquals("track123", resultMap["trackId"])
    assertEquals("cover.jpg", resultMap["cover"])
    assertEquals(240, resultMap["duration"])
    assertEquals(85, resultMap["popularity"])
    assertEquals("PLAY", resultMap["state"]) // Enum is converted to string
    assertEquals(5, resultMap["likes"])
    assertEquals(listOf("user1", "user2"), resultMap["likedBy"])
  }

  @Test
  fun `playlistTrackToMap handles empty likedBy list`() {
    // Create a PlaylistTrack object with an empty likedBy list
    val track =
        PlaylistTrack(
            track =
                SpotifyTrack(
                    name = "Test Song",
                    artist = "Test Artist",
                    trackId = "track123",
                    cover = "cover.jpg",
                    duration = 240,
                    popularity = 85,
                    state = State.PLAY),
            likes = 0,
            likedBy = mutableListOf())

    // Convert PlaylistTrack to Map
    val resultMap = playlistRepositoryFirestore.playlistTrackToMap(track)

    // Assertions
    assertEquals("Test Song", resultMap["name"])
    assertEquals("Test Artist", resultMap["artist"])
    assertEquals("track123", resultMap["trackId"])
    assertEquals("cover.jpg", resultMap["cover"])
    assertEquals(240, resultMap["duration"])
    assertEquals(85, resultMap["popularity"])
    assertEquals("PLAY", resultMap["state"]) // Enum is converted to string
    assertEquals(0, resultMap["likes"])
    assertEquals(emptyList<String>(), resultMap["likedBy"]) // Ensure likedBy is an empty list
  }

  @Test
  fun `playlistToMap converts Playlist to Map successfully`() {
    // Create a Playlist object
    val track1 =
        PlaylistTrack(
            track =
                SpotifyTrack(
                    name = "Track 1",
                    artist = "Artist 1",
                    trackId = "track123",
                    cover = "cover1.jpg",
                    duration = 240,
                    popularity = 85,
                    state = State.PLAY),
            likes = 10,
            likedBy = mutableListOf("user1", "user2"))

    val track2 =
        PlaylistTrack(
            track =
                SpotifyTrack(
                    name = "Track 2",
                    artist = "Artist 2",
                    trackId = "track456",
                    cover = "cover2.jpg",
                    duration = 200,
                    popularity = 75,
                    state = State.PAUSE),
            likes = 5,
            likedBy = mutableListOf("user3"))

    val playlist =
        Playlist(
            playlistID = "playlist123",
            playlistCover = "cover.jpg",
            playlistName = "Test Playlist",
            playlistDescription = "This is a test playlist.",
            playlistPublic = true,
            userId = "user123",
            playlistOwner = "owner123",
            playlistCollaborators = listOf("collab1", "collab2"),
            playlistTracks = listOf(track1, track2),
            nbTracks = 2)

    // Convert Playlist to Map
    val resultMap = playlistRepositoryFirestore.playlistToMap(playlist)

    // Assertions
    assertEquals("playlist123", resultMap["playlistID"])
    assertEquals("cover.jpg", resultMap["playlistCover"])
    assertEquals("Test Playlist", resultMap["playlistName"])
    assertEquals("This is a test playlist.", resultMap["playlistDescription"])
    assertEquals(true, resultMap["playlistPublic"])
    assertEquals("user123", resultMap["userId"])
    assertEquals("owner123", resultMap["playlistOwner"])
    assertEquals(listOf("collab1", "collab2"), resultMap["playlistCollaborators"])
    assertEquals(2, resultMap["nbTracks"])

    // Verify playlistTracks conversion
    val tracks = resultMap["playlistTracks"] as List<Map<String, Any>>
    assertEquals(2, tracks.size)

    val trackMap1 = tracks[0]
    assertEquals("Track 1", trackMap1["name"])
    assertEquals("Artist 1", trackMap1["artist"])
    assertEquals("track123", trackMap1["trackId"])
    assertEquals("cover1.jpg", trackMap1["cover"])
    assertEquals(240, trackMap1["duration"])
    assertEquals(85, trackMap1["popularity"])
    assertEquals("PLAY", trackMap1["state"])
    assertEquals(10, trackMap1["likes"])
    assertEquals(listOf("user1", "user2"), trackMap1["likedBy"])

    val trackMap2 = tracks[1]
    assertEquals("Track 2", trackMap2["name"])
    assertEquals("Artist 2", trackMap2["artist"])
    assertEquals("track456", trackMap2["trackId"])
    assertEquals("cover2.jpg", trackMap2["cover"])
    assertEquals(200, trackMap2["duration"])
    assertEquals(75, trackMap2["popularity"])
    assertEquals("PAUSE", trackMap2["state"])
    assertEquals(5, trackMap2["likes"])
    assertEquals(listOf("user3"), trackMap2["likedBy"])
  }

  @Test
  fun `playlistToMap handles empty playlistTracks`() {
    // Create a Playlist object with no tracks
    val playlist =
        Playlist(
            playlistID = "playlist123",
            playlistCover = "cover.jpg",
            playlistName = "Empty Playlist",
            playlistDescription = "This is an empty playlist.",
            playlistPublic = false,
            userId = "user123",
            playlistOwner = "owner123",
            playlistCollaborators = emptyList(),
            playlistTracks = emptyList(),
            nbTracks = 0)

    // Convert Playlist to Map
    val resultMap = playlistRepositoryFirestore.playlistToMap(playlist)

    // Assertions
    assertEquals("playlist123", resultMap["playlistID"])
    assertEquals("cover.jpg", resultMap["playlistCover"])
    assertEquals("Empty Playlist", resultMap["playlistName"])
    assertEquals("This is an empty playlist.", resultMap["playlistDescription"])
    assertEquals(false, resultMap["playlistPublic"])
    assertEquals("user123", resultMap["userId"])
    assertEquals("owner123", resultMap["playlistOwner"])
    assertEquals(emptyList<String>(), resultMap["playlistCollaborators"])
    assertEquals(0, resultMap["nbTracks"])
    assertEquals(emptyList<Map<String, Any>>(), resultMap["playlistTracks"])
  }

  @Test
  fun getNewUid_generatesExpectedUid() {
    `when`(mockDocumentReference.id).thenReturn("1")
    val uid = playlistRepositoryFirestore.getNewUid()
    assert(uid == "1")
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
  fun `getUserId returns correct userID`() {
    // Arrange
    `when`(mockAuth.currentUser).thenReturn(mockFirebaseUser)
    `when`(mockFirebaseUser.uid).thenReturn("testUserId")

    // Act
    val userId = playlistRepositoryFirestore.getUserId()

    // Assert
    assert(userId == "testUserId")
  }

  @Test
  fun `getOwnedPlaylists should retrieve playlists owned by the current user`() {
    // Mocking Firestore and DocumentSnapshots
    val mockDocumentSnapshot1 = mock(DocumentSnapshot::class.java)
    val mockDocumentSnapshot2 = mock(DocumentSnapshot::class.java)
    val mockQuerySnapshot = mock(QuerySnapshot::class.java)

    // Mock Firestore behavior
    `when`(mockFirestore.collection(any())).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.whereEqualTo(eq("userId"), anyString())).thenReturn(mockQuery)
    `when`(mockQuery.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))

    // Mock documents returned by Firestore
    val documents = listOf(mockDocumentSnapshot1, mockDocumentSnapshot2)
    `when`(mockQuerySnapshot.documents).thenReturn(documents)

    // Mocking playlist objects
    `when`(mockDocumentSnapshot1.toObject(Playlist::class.java)).thenReturn(playlist1)
    `when`(mockDocumentSnapshot2.toObject(Playlist::class.java)).thenReturn(playlist2)

    // Test function
    playlistRepositoryFirestore.getOwnedPlaylists(
        onSuccess = { playlists ->
          // Assertions
          assertEquals(2, playlists.size)
          assertEquals("1", playlists[0].playlistID)
          assertEquals("2", playlists[1].playlistID)
        },
        onFailure = { fail("Failure callback should not be called") })
  }

  @Test
  fun `getOwnedPlaylists should return an empty list if no playlists are owned`() {
    // Mocking Firestore behavior with no documents
    val mockQuerySnapshot = mock(QuerySnapshot::class.java)
    `when`(mockFirestore.collection(any())).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.whereEqualTo(eq("userId"), anyString())).thenReturn(mockQuery)
    `when`(mockQuery.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))

    // No documents
    `when`(mockQuerySnapshot.documents).thenReturn(emptyList())

    // Test function
    playlistRepositoryFirestore.getOwnedPlaylists(
        onSuccess = { playlists ->
          // Assertions
          assertNotNull(playlists)
          assertEquals(0, playlists.size)
        },
        onFailure = { fail("Failure callback should not be called") })
  }

  @Test
  fun `getOwnedPlaylists should call onFailure when Firestore query fails`() {
    // Mock Firestore query failure
    val exception = Exception("Firestore error")
    `when`(mockFirestore.collection(any())).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.whereEqualTo(eq("userId"), anyString())).thenReturn(mockQuery)
    `when`(mockQuery.get()).thenReturn(Tasks.forException(exception))

    // Test function
    playlistRepositoryFirestore.getOwnedPlaylists(
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { error ->
          // Assertions
          assertNotNull(error)
          assertEquals("Firestore error", error.message)
        })
  }

  @Test
  fun `getSharedPlaylists should retrieve playlists shared with the current user`() {
    // Mocking Firestore and DocumentSnapshots
    val mockDocumentSnapshot1 = mock(DocumentSnapshot::class.java)
    val mockDocumentSnapshot2 = mock(DocumentSnapshot::class.java)
    val mockQuerySnapshot = mock(QuerySnapshot::class.java)

    // Mock Firestore behavior
    `when`(mockFirestore.collection(any())).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.whereArrayContains(eq("playlistCollaborators"), anyString()))
        .thenReturn(mockQuery)
    `when`(mockQuery.whereNotEqualTo(eq("userId"), anyString())).thenReturn(mockQuery)
    `when`(mockQuery.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))

    // Mock documents returned by Firestore
    val documents = listOf(mockDocumentSnapshot1, mockDocumentSnapshot2)
    `when`(mockQuerySnapshot.documents).thenReturn(documents)

    // Mocking playlist objects
    `when`(mockDocumentSnapshot1.toObject(Playlist::class.java)).thenReturn(playlist1)
    `when`(mockDocumentSnapshot2.toObject(Playlist::class.java)).thenReturn(playlist2)

    // Test function
    playlistRepositoryFirestore.getSharedPlaylists(
        onSuccess = { playlists ->
          // Assertions
          assertEquals(2, playlists.size)
          assertEquals("1", playlists[0].playlistID)
          assertEquals("2", playlists[1].playlistID)
        },
        onFailure = { fail("Failure callback should not be called") })
  }

  @Test
  fun `getSharedPlaylists should return an empty list if no playlists are shared`() {
    // Mocking Firestore behavior with no documents
    val mockQuerySnapshot = mock(QuerySnapshot::class.java)
    `when`(mockFirestore.collection(any())).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.whereArrayContains(eq("playlistCollaborators"), anyString()))
        .thenReturn(mockQuery)
    `when`(mockQuery.whereNotEqualTo(eq("userId"), anyString())).thenReturn(mockQuery)
    `when`(mockQuery.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))

    // No documents
    `when`(mockQuerySnapshot.documents).thenReturn(emptyList())

    // Test function
    playlistRepositoryFirestore.getSharedPlaylists(
        onSuccess = { playlists ->
          // Assertions
          assertNotNull(playlists)
          assertEquals(0, playlists.size)
        },
        onFailure = { fail("Failure callback should not be called") })
  }

  @Test
  fun `getSharedPlaylists should call onFailure when Firestore query fails`() {
    // Mock Firestore query failure
    val exception = Exception("Firestore error")
    `when`(mockFirestore.collection(any())).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.whereArrayContains(eq("playlistCollaborators"), anyString()))
        .thenReturn(mockQuery)
    `when`(mockQuery.whereNotEqualTo(eq("userId"), anyString())).thenReturn(mockQuery)
    `when`(mockQuery.get()).thenReturn(Tasks.forException(exception))

    // Test function
    playlistRepositoryFirestore.getSharedPlaylists(
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { error ->
          // Assertions
          assertNotNull(error)
          assertEquals("Firestore error", error.message)
        })
  }

  @Test
  fun `getPublicPlaylists should retrieve public playlists`() {
    // Mocking Firestore and DocumentSnapshots
    val mockDocumentSnapshot1 = mock(DocumentSnapshot::class.java)
    val mockDocumentSnapshot2 = mock(DocumentSnapshot::class.java)
    val mockQuerySnapshot = mock(QuerySnapshot::class.java)

    // Mock Firestore behavior
    `when`(mockFirestore.collection(any())).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.whereEqualTo(eq("playlistPublic"), eq(true)))
        .thenReturn(mockQuery)
    `when`(mockQuery.whereNotEqualTo(eq("userId"), anyString())).thenReturn(mockQuery)
    `when`(mockQuery.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))

    // Mock documents returned by Firestore
    val documents = listOf(mockDocumentSnapshot1, mockDocumentSnapshot2)
    `when`(mockQuerySnapshot.documents).thenReturn(documents)

    // Mocking playlist objects
    `when`(mockDocumentSnapshot1.toObject(Playlist::class.java)).thenReturn(playlist1)
    `when`(mockDocumentSnapshot2.toObject(Playlist::class.java)).thenReturn(playlist2)

    // Test function
    playlistRepositoryFirestore.getPublicPlaylists(
        onSuccess = { playlists ->
          // Assertions
          assertEquals(2, playlists.size)
          assertEquals("1", playlists[0].playlistID)
          assertEquals("2", playlists[1].playlistID)
        },
        onFailure = { fail("Failure callback should not be called") })
  }

  @Test
  fun `getPublicPlaylists should return an empty list if no public playlists are available`() {
    // Mocking Firestore behavior with no documents
    val mockQuerySnapshot = mock(QuerySnapshot::class.java)
    `when`(mockFirestore.collection(any())).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.whereEqualTo(eq("playlistPublic"), eq(true)))
        .thenReturn(mockQuery)
    `when`(mockQuery.whereNotEqualTo(eq("userId"), anyString())).thenReturn(mockQuery)
    `when`(mockQuery.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))

    // No documents
    `when`(mockQuerySnapshot.documents).thenReturn(emptyList())

    // Test function
    playlistRepositoryFirestore.getPublicPlaylists(
        onSuccess = { playlists ->
          // Assertions
          assertNotNull(playlists)
          assertEquals(0, playlists.size)
        },
        onFailure = { fail("Failure callback should not be called") })
  }

  @Test
  fun `getPublicPlaylists should call onFailure when Firestore query fails`() {
    // Mock Firestore query failure
    val exception = Exception("Firestore error")
    `when`(mockFirestore.collection(any())).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.whereEqualTo(eq("playlistPublic"), eq(true)))
        .thenReturn(mockQuery)
    `when`(mockQuery.whereNotEqualTo(eq("userId"), anyString())).thenReturn(mockQuery)
    `when`(mockQuery.get()).thenReturn(Tasks.forException(exception))

    // Test function
    playlistRepositoryFirestore.getPublicPlaylists(
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { error ->
          // Assertions
          assertNotNull(error)
          assertEquals("Firestore error", error.message)
        })
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
  fun `addPlaylist should call onFailure on unexpected exception`() {
    // Mock Firestore behavior to throw an unexpected exception
    val unexpectedException = RuntimeException("Unexpected error")
    `when`(mockFirestore.collection(any())).thenThrow(unexpectedException)

    // Test function
    var failureCalled = false
    playlistRepositoryFirestore.addPlaylist(
        playlist = playlist1,
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { error ->
          failureCalled = true
          assertEquals("Unexpected error", error.message)
        })

    // Assertions
    assertTrue(failureCalled)
    verify(mockFirestore).collection(collectionPath)
  }

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

  @Test
  fun updatePlaylistTracks_correctlyUpdatesTracks() {
    val newTrackList = listOf(track1, track2)
    val trackMapList = newTrackList.map { playlistRepositoryFirestore.playlistTrackToMap(it) }

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
    val trackMapList = newTrackList.map { playlistRepositoryFirestore.playlistTrackToMap(it) }
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

  @Test
  fun `uploadPlaylistCover should log error when image processing fails`() {
    // Mock the resizeAndCompressImageFromUri to return null (image processing failure)
    whenever(resizeAndCompressImageFromUri(imageUri, context)).thenReturn(null)

    // Call the function under test
    playlistRepositoryFirestore.uploadPlaylistCover(imageUri, context, playlist)

    // Verify that savePlaylistCoverBase64 was NOT called
    verify(mockCollectionReference, never()).document(anyString())

    // Check that the error was logged
    // Assuming there's a logger in place that can be verified
  }

  @Test
  fun `savePlaylistCoverBase64 should save Base64 string to Firestore`() {
    val base64Image = "mockBase64Image"

    whenever(mockFirestore.collection("playlists")).thenReturn(mockCollectionReference)
    whenever(mockCollectionReference.document(playlist.playlistID))
        .thenReturn(mockDocumentReference)

    // Call the function under test
    playlistRepositoryFirestore.savePlaylistCoverBase64(playlist, base64Image)

    // Verify that Firestore's set method was called with correct arguments
    val expectedData = mapOf("playlistCover" to base64Image)
    verify(mockDocumentReference).set(expectedData, SetOptions.merge())
  }

  @Test
  fun `loadPlaylistCover should handle failure gracefully`() {
    // Mock Firestore components
    whenever(mockFirestore.collection(collectionPath)).thenReturn(mockCollectionReference)
    whenever(mockCollectionReference.document(playlist.playlistID))
        .thenReturn(mockDocumentReference)
    whenever(mockDocumentReference.get()).thenReturn(mockTask)

    // Mock behavior for addOnSuccessListener and addOnFailureListener
    whenever(mockTask.addOnSuccessListener(any())).thenReturn(mockTask)
    whenever(mockTask.addOnFailureListener(any())).thenAnswer { invocation ->
      val callback = invocation.getArgument<OnFailureListener>(0)
      callback.onFailure(Exception("Firestore error"))
      mockTask // Return mockTask for chainability
    }

    // Set up the callback to verify
    var resultBitmap: Bitmap? = null
    playlistRepositoryFirestore.loadPlaylistCover(playlist) { bitmap -> resultBitmap = bitmap }

    // Assert the callback was invoked with null
    assertNull(resultBitmap)
  }
}
