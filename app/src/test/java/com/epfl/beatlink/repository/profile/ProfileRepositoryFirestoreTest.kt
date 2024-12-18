package com.epfl.beatlink.repository.profile

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.epfl.beatlink.model.profile.ProfileData
import com.epfl.beatlink.model.spotify.objects.SpotifyArtist
import com.epfl.beatlink.model.spotify.objects.SpotifyTrack
import com.epfl.beatlink.model.spotify.objects.State
import com.epfl.beatlink.utils.ImageUtils.base64ToBitmap
import com.epfl.beatlink.utils.ImageUtils.resizeAndCompressImageFromUri
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Transaction
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import java.io.InputStream
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.never
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify

@Suppress("UNCHECKED_CAST")
class ProfileRepositoryFirestoreTest {

  private lateinit var repository: ProfileRepositoryFirestore
  private lateinit var mockDb: FirebaseFirestore
  private lateinit var mockAuth: FirebaseAuth
  private lateinit var mockDocumentSnapshot: DocumentSnapshot
  private lateinit var mockUser: FirebaseUser
  private lateinit var mockUploadTask: UploadTask
  private lateinit var mockUri: Uri
  private lateinit var mockContext: Context
  private lateinit var mockQuery: Query
  private lateinit var mockTransaction: Transaction
  private lateinit var mockProfileCollectionReference: CollectionReference
  private lateinit var mockProfileDocumentReference: DocumentReference
  private lateinit var mockUsernamesCollectionReference: CollectionReference
  private lateinit var mockUsernameDocumentReference: DocumentReference
  private lateinit var mockFriendRequestsCollectionReference: CollectionReference
  private lateinit var mockFriendRequestsDocumentReference: DocumentReference

  private lateinit var mockUsernameQuerySnapshot: QuerySnapshot
  private lateinit var mockUsernameDocumentSnapshot: DocumentSnapshot

  private lateinit var mockFriendRequestsSnapshot: QuerySnapshot
  private lateinit var mockFriendRequestDocumentSnapshot: DocumentSnapshot

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    mockDb = mock(FirebaseFirestore::class.java)
    mockAuth = mock(FirebaseAuth::class.java)
    mockDocumentSnapshot = mock(DocumentSnapshot::class.java)
    mockUser = mock(FirebaseUser::class.java)

    mockProfileCollectionReference = mock(CollectionReference::class.java)
    mockProfileDocumentReference = mock(DocumentReference::class.java)

    mockUsernamesCollectionReference = mock(CollectionReference::class.java)
    mockUsernameDocumentReference = mock(DocumentReference::class.java)

    mockFriendRequestsCollectionReference = mock(CollectionReference::class.java)
    mockFriendRequestsDocumentReference = mock(DocumentReference::class.java)

    mockUsernameQuerySnapshot = mock(QuerySnapshot::class.java)
    mockUsernameDocumentSnapshot = mock(DocumentSnapshot::class.java)

    mockFriendRequestsSnapshot = mock(QuerySnapshot::class.java)
    mockFriendRequestDocumentSnapshot = mock(DocumentSnapshot::class.java)

    mockUploadTask = mock(UploadTask::class.java)
    mockUri = mock(Uri::class.java)
    mockContext = mock(Context::class.java)
    mockQuery = mock(Query::class.java)
    mockTransaction = mock(Transaction::class.java)

    // Setup mock behavior for collection and document references
    `when`(mockDb.collection("userProfiles")).thenReturn(mockProfileCollectionReference)
    `when`(mockProfileCollectionReference.document("testUserId"))
        .thenReturn(mockProfileDocumentReference)

    `when`(mockDb.collection("usernames")).thenReturn(mockUsernamesCollectionReference)
    `when`(mockUsernamesCollectionReference.document("testUsername"))
        .thenReturn(mockUsernameDocumentReference)

    `when`(mockDb.collection("friendRequests")).thenReturn(mockFriendRequestsCollectionReference)
    `when`(mockFriendRequestsCollectionReference.document("testUserId"))
        .thenReturn(mockFriendRequestsDocumentReference)

    repository = ProfileRepositoryFirestore(mockDb, mockAuth)
  }

  @Test
  fun `test getUserId returns user ID when user is authenticated`() {
    // Arrange
    `when`(mockAuth.currentUser).thenReturn(mockUser)
    `when`(mockUser.uid).thenReturn("testUserId")

    // Act
    val userId = repository.getUserId()

    // Assert
    assert(userId == "testUserId")
  }

  @Test
  fun `test getUserId returns null when user is not authenticated`() {
    // Arrange
    `when`(mockAuth.currentUser).thenReturn(null)

    // Act
    val userId = repository.getUserId()

    // Assert
    assert(userId == null)
  }

  @Test
  fun `test fetchProfile returns data when fetch is successful`() = runBlocking {
    // Arrange
    val userId = "testUserId"
    val profileData =
        ProfileData(
            bio = "Sample bio",
            links = 5,
            name = "John Doe",
            profilePicture = null,
            username = "johndoe",
            email = "example@gmail.com",
            favoriteMusicGenres = listOf("Rock", "Pop"))

    // Simulate the behavior of the document get() call
    `when`(mockProfileDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.getString("bio")).thenReturn(profileData.bio)
    `when`(mockDocumentSnapshot.getLong("links")).thenReturn(profileData.links.toLong())
    `when`(mockDocumentSnapshot.getString("name")).thenReturn(profileData.name)
    `when`(mockDocumentSnapshot.getString("profilePicture")).thenReturn(null)
    `when`(mockDocumentSnapshot.getString("username")).thenReturn(profileData.username)
    `when`(mockDocumentSnapshot.getString("email")).thenReturn(profileData.email)
    `when`(mockDocumentSnapshot.get("favoriteMusicGenres"))
        .thenReturn(profileData.favoriteMusicGenres)

    // Act
    val result = repository.fetchProfile(userId)

    // Assert
    assert(result == profileData)
  }

  @Test
  fun `test fetchProfile fails`() = runBlocking {
    // Arrange
    val userId = "testUserId"

    `when`(mockProfileDocumentReference.get())
        .thenReturn(Tasks.forException(Exception("Fetch profile failed")))

    // Act
    val result = repository.fetchProfile(userId)

    // Assert
    assert(result == null)
  }

  @Test
  fun `getUsername returns username on success`(): Unit = runBlocking {
    // Arrange
    val userId = "testUserId"
    val expectedUsername = "TestUsername"

    // Mocking behavior for Firestore document reference and snapshot
    `when`(mockAuth.currentUser).thenReturn(mockUser)
    `when`(mockUser.uid).thenReturn("testUserId")
    `when`(mockProfileDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.getString("username")).thenReturn(expectedUsername)

    // Act
    val result = repository.getUsername(userId)

    // Assert
    assertEquals(expectedUsername, result)
    verify(mockProfileDocumentReference).get()
    verify(mockDocumentSnapshot).getString("username")
  }

  @Test
  fun `getUsername should return null and log an error when an exception occurs`(): Unit =
      runBlocking {
        // Arrange
        val userId = "testUserId"
        val exception = RuntimeException("Firestore error")

        // Mock Firestore behavior to throw an exception
        `when`(mockProfileDocumentReference.get()).thenThrow(exception)
        `when`(mockDb.collection("userProfiles")).thenReturn(mockProfileCollectionReference)
        `when`(mockProfileCollectionReference.document(userId))
            .thenReturn(mockProfileDocumentReference)

        // Act
        val result = repository.getUsername(userId)

        // Assert
        assertNull(result)
        verify(mockProfileDocumentReference).get()
      }

  @Test
  fun `getUserIdByUsername returns userId on success`(): Unit = runBlocking {
    // Arrange
    val username = "TestUsername"
    val userId = "testUserId"
    val mockQuerySnapshot: QuerySnapshot = mock(QuerySnapshot::class.java)

    // Mocking behavior for Firestore query
    `when`(mockProfileCollectionReference.whereEqualTo("username", username))
        .thenReturn(mockProfileCollectionReference)
    `when`(mockProfileCollectionReference.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))
    `when`(mockQuerySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.id).thenReturn(userId)

    // Act
    val result = repository.getUserIdByUsername(username)

    // Assert
    assertEquals(userId, result)
    verify(mockProfileCollectionReference).whereEqualTo("username", username)
    verify(mockProfileCollectionReference).get()
    verify(mockQuerySnapshot).documents
    verify(mockDocumentSnapshot).id
  }

  @Test
  fun `getUserIdByUsername fails`(): Unit = runBlocking {
    // Arrange
    val username = "testUsername"

    // Mocking behavior for Firestore query
    `when`(mockProfileCollectionReference.whereEqualTo("username", username))
        .thenReturn(mockProfileCollectionReference)
    `when`(mockProfileCollectionReference.get())
        .thenReturn(Tasks.forException(Exception("getUserIdByUsername failed")))

    // Act
    val result = repository.getUserIdByUsername(username)

    // Assert
    assertEquals(null, result)
    verify(mockProfileCollectionReference).whereEqualTo("username", username)
    verify(mockProfileCollectionReference).get()
  }

  @Test
  fun `addProfile succeeds and adds username in usernames collection and adds uid in friendRequests collection`():
      Unit = runBlocking {
    // Arrange
    val profileData = ProfileData(username = "testUsername")
    val userId = "testUserId"

    // Mock transaction behavior
    `when`(mockTransaction.get(mockUsernameDocumentReference)).thenReturn(mockDocumentSnapshot)
    `when`(mockDocumentSnapshot.exists()).thenReturn(false)
    `when`(mockTransaction.set(mockProfileDocumentReference, profileData))
        .thenReturn(mockTransaction)
    `when`(mockTransaction.set(mockUsernameDocumentReference, mapOf<String, Any>()))
        .thenReturn(mockTransaction)
    `when`(
            mockTransaction.set(
                mockFriendRequestsDocumentReference,
                mapOf(
                    "ownRequests" to mapOf<String, Boolean>(),
                    "friendRequests" to mapOf<String, Boolean>(),
                    "allFriends" to mapOf<String, String>())))
        .thenReturn(mockTransaction)

    // Mock runTransaction to execute the transaction block
    `when`(mockDb.runTransaction<Transaction>(any())).thenAnswer { invocation ->
      val transactionFunction = invocation.arguments[0] as Transaction.Function<*>
      transactionFunction.apply(mockTransaction)
      Tasks.forResult(null)
    }

    // Act
    val success = repository.addProfile(userId, profileData)

    // Assert
    assertTrue(success)

    // Verify that the `set` method was called with the correct arguments
    verify(mockTransaction).get(mockUsernameDocumentReference)
    verify(mockDocumentSnapshot).exists()
    verify(mockTransaction).set(mockProfileDocumentReference, profileData)
    verify(mockTransaction).set(mockUsernameDocumentReference, mapOf<String, Any>())
    verify(mockTransaction)
        .set(
            mockFriendRequestsDocumentReference,
            mapOf(
                "ownRequests" to mapOf<String, Boolean>(),
                "friendRequests" to mapOf<String, Boolean>(),
                "allFriends" to mapOf<String, String>()))
  }

  @Test
  fun `addProfile fails because username is taken`() = runBlocking {
    // Arrange
    val profileData = ProfileData(username = "testUsername")

    // Mock transaction behavior
    `when`(mockTransaction.get(mockUsernameDocumentReference)).thenReturn(mockDocumentSnapshot)
    `when`(mockDocumentSnapshot.exists()).thenReturn(true)

    // Mock runTransaction to execute the transaction block
    `when`(mockDb.runTransaction<Transaction>(any())).thenAnswer { invocation ->
      val transactionFunction = invocation.arguments[0] as Transaction.Function<*>
      transactionFunction.apply(mockTransaction)
      Tasks.forResult(null)
    }

    // Act
    val success = repository.addProfile("testUserId", profileData)

    // Assert
    assertFalse(success)
  }

  @Test
  fun `addProfile fails`() = runBlocking {
    // Arrange
    val profileData = ProfileData(username = "testUsername")
    `when`(mockDb.runTransaction<Transaction>(any()))
        .thenReturn(Tasks.forException(Exception("Add profile failed")))

    // Act
    val success = repository.addProfile("testUserId", profileData)

    // Assert
    assertFalse(success)
  }

  @Test
  fun `updateProfile succeeds and doesn't update usernames collection`(): Unit = runBlocking {
    // Arrange
    val profileData = ProfileData(username = "testUsername")

    `when`(mockTransaction.get(mockProfileDocumentReference)).thenReturn(mockDocumentSnapshot)
    `when`(mockDocumentSnapshot.getString("username")).thenReturn("testUsername")
    `when`(
            mockTransaction.set(
                eq(mockProfileDocumentReference), any<ProfileData>(), eq(SetOptions.merge())))
        .thenReturn(mockTransaction)
    `when`(
            mockTransaction.update(
                eq(mockProfileDocumentReference), eq("topSongs"), eq(emptyList<Any>())))
        .thenReturn(mockTransaction)
    `when`(
            mockTransaction.update(
                eq(mockProfileDocumentReference), eq("topArtists"), eq(emptyList<Any>())))
        .thenReturn(mockTransaction)

    // Mock runTransaction to execute the transaction block
    `when`(mockDb.runTransaction<Transaction>(any())).thenAnswer { invocation ->
      val transactionFunction = invocation.arguments[0] as Transaction.Function<*>
      transactionFunction.apply(mockTransaction)
      Tasks.forResult(null)
    }

    // Act
    val success = repository.updateProfile("testUserId", profileData)

    // Assert
    assertTrue(success)
    verify(mockTransaction).get(mockProfileDocumentReference)
    verify(mockDocumentSnapshot).getString("username")
    verify(mockTransaction)
        .set(eq(mockProfileDocumentReference), any<ProfileData>(), eq(SetOptions.merge()))
    verify(mockTransaction)
        .update(eq(mockProfileDocumentReference), eq("topSongs"), eq(emptyList<Any>()))
    verify(mockTransaction)
        .update(eq(mockProfileDocumentReference), eq("topArtists"), eq(emptyList<Any>()))
    verify(mockTransaction, never()).delete(any())
    verify(mockTransaction, never())
        .set(eq(mockUsernamesCollectionReference.document("testUsername")), any())
  }

  @Test
  fun `updateProfile succeeds and updates usernames collection`(): Unit = runBlocking {
    // Arrange
    val profileData = ProfileData(username = "testUsername")
    val mockUsernameSnapshot = mock(DocumentSnapshot::class.java)

    `when`(mockTransaction.get(mockProfileDocumentReference)).thenReturn(mockDocumentSnapshot)
    `when`(mockDocumentSnapshot.getString("username")).thenReturn("testOtherUsername")
    `when`(mockTransaction.get(mockUsernameDocumentReference)).thenReturn(mockUsernameSnapshot)
    `when`(mockUsernameSnapshot.exists()).thenReturn(false)
    `when`(
            mockTransaction.delete(
                eq(mockUsernamesCollectionReference.document("testOtherUsername"))))
        .thenReturn(mockTransaction)
    `when`(
            mockTransaction.set(
                eq(mockUsernamesCollectionReference.document("testUsername")),
                eq(mapOf<String, Any>())))
        .thenReturn(mockTransaction)

    `when`(
            mockTransaction.set(
                eq(mockProfileDocumentReference), any<ProfileData>(), eq(SetOptions.merge())))
        .thenReturn(mockTransaction)
    `when`(
            mockTransaction.update(
                eq(mockProfileDocumentReference), eq("topSongs"), eq(emptyList<Any>())))
        .thenReturn(mockTransaction)
    `when`(
            mockTransaction.update(
                eq(mockProfileDocumentReference), eq("topArtists"), eq(emptyList<Any>())))
        .thenReturn(mockTransaction)

    // Mock runTransaction to execute the transaction block
    `when`(mockDb.runTransaction<Transaction>(any())).thenAnswer { invocation ->
      val transactionFunction = invocation.arguments[0] as Transaction.Function<*>
      transactionFunction.apply(mockTransaction)
      Tasks.forResult(null)
    }

    // Act
    val success = repository.updateProfile("testUserId", profileData)

    // Assert
    assertTrue(success)
    verify(mockTransaction).get(mockProfileDocumentReference)
    verify(mockDocumentSnapshot).getString("username")
    verify(mockTransaction).get(mockUsernameDocumentReference)
    verify(mockUsernameSnapshot).exists()
    verify(mockTransaction)
        .set(eq(mockProfileDocumentReference), any<ProfileData>(), eq(SetOptions.merge()))
    verify(mockTransaction)
        .update(eq(mockProfileDocumentReference), eq("topSongs"), eq(emptyList<Any>()))
    verify(mockTransaction)
        .update(eq(mockProfileDocumentReference), eq("topArtists"), eq(emptyList<Any>()))
    verify(mockTransaction)
        .delete(eq(mockUsernamesCollectionReference.document("testOtherUsername")))
    verify(mockTransaction)
        .set(
            eq(mockUsernamesCollectionReference.document("testUsername")), eq(mapOf<String, Any>()))
  }

  @Test
  fun `updateProfile fails because username is taken`() = runBlocking {
    // Arrange
    val profileData = ProfileData(username = "testUsername")
    val mockUsernameSnapshot = mock(DocumentSnapshot::class.java)

    `when`(mockTransaction.get(mockProfileDocumentReference)).thenReturn(mockDocumentSnapshot)
    `when`(mockDocumentSnapshot.getString("username")).thenReturn("testOtherUsername")
    `when`(mockTransaction.get(mockUsernameDocumentReference)).thenReturn(mockUsernameSnapshot)
    `when`(mockUsernameSnapshot.exists()).thenReturn(true)

    // Mock runTransaction to execute the transaction block
    `when`(mockDb.runTransaction<Transaction>(any())).thenAnswer { invocation ->
      val transactionFunction = invocation.arguments[0] as Transaction.Function<*>
      transactionFunction.apply(mockTransaction)
      Tasks.forResult(null)
    }

    // Act
    val success = repository.updateProfile("testUserId", profileData)

    // Assert
    assertFalse(success)
  }

  @Test
  fun `updateProfile fails`() = runBlocking {
    // Arrange
    val profileData = ProfileData(username = "testUsername")

    `when`(mockDb.runTransaction<Transaction>(any()))
        .thenReturn(Tasks.forException(Exception("Update profile failed")))

    // Act
    val success = repository.updateProfile("testUserId", profileData)

    // Assert
    assertFalse(success)
  }

  @Test
  fun `deleteProfile succeeds and deletes username in usernames collection`(): Unit = runBlocking {
    // Arrange
    `when`(mockTransaction.get(mockProfileDocumentReference)).thenReturn(mockDocumentSnapshot)
    `when`(mockDocumentSnapshot.getString("username")).thenReturn("testUsername")
    `when`(mockTransaction.delete(mockProfileDocumentReference)).thenReturn(mockTransaction)
    `when`(mockTransaction.delete(mockUsernameDocumentReference)).thenReturn(mockTransaction)

    `when`(mockTransaction.delete(mockFriendRequestsDocumentReference)).thenReturn(mockTransaction)
    `when`(mockDb.collection("friendRequests").get())
        .thenReturn(Tasks.forResult(mockFriendRequestsSnapshot))
    `when`(mockFriendRequestsSnapshot.documents)
        .thenReturn(listOf(mockFriendRequestDocumentSnapshot))
    `when`(mockFriendRequestDocumentSnapshot.reference)
        .thenReturn(mockFriendRequestsDocumentReference)

    val ownRequests = mapOf("testUserId" to true)
    val friendRequests = mapOf("testUserId" to true)
    `when`(mockFriendRequestDocumentSnapshot.get("ownRequests")).thenReturn(ownRequests)
    `when`(mockFriendRequestDocumentSnapshot.get("friendRequests")).thenReturn(friendRequests)

    // Mock runTransaction to execute the transaction block
    `when`(mockDb.runTransaction<Transaction>(any())).thenAnswer { invocation ->
      val transactionFunction = invocation.arguments[0] as Transaction.Function<*>
      transactionFunction.apply(mockTransaction)
      Tasks.forResult(null)
    }

    // Act
    val success = repository.deleteProfile("testUserId")

    // Assert
    assertTrue(success)
    verify(mockTransaction).get(mockProfileDocumentReference)
    verify(mockDocumentSnapshot).getString("username")
    verify(mockTransaction).delete(mockProfileDocumentReference)
    verify(mockTransaction).delete(mockUsernameDocumentReference)
    verify(mockTransaction).delete(mockFriendRequestsDocumentReference)

    verify(mockTransaction)
        .update(mockFriendRequestsDocumentReference, "ownRequests.testUserId", FieldValue.delete())
    verify(mockTransaction)
        .update(
            mockFriendRequestsDocumentReference, "friendRequests.testUserId", FieldValue.delete())
  }

  @Test
  fun `deleteProfile fails`() = runBlocking {
    // Arrange
    `when`(mockDb.runTransaction<Transaction>(any()))
        .thenReturn(Tasks.forException(Exception("Delete profile failed")))

    // Act
    val success = repository.deleteProfile("testUserId")

    // Assert
    assertFalse(success)
  }

  @Test
  fun `base64ToBitmap returns valid Bitmap for valid Base64`() {
    // Arrange
    val validBase64 = "iVBORw0KGgoAAAANSUhEUgAAAAUA" // Replace with a valid Base64 string
    val byteArray = byteArrayOf(1, 2, 3) // Mock decoded byte array
    val mockBitmap = mock(Bitmap::class.java)

    mockStatic(Base64::class.java).use { base64Mock ->
      mockStatic(BitmapFactory::class.java).use { bitmapFactoryMock ->
        base64Mock
            .`when`<ByteArray> { Base64.decode(validBase64, Base64.DEFAULT) }
            .thenReturn(byteArray)

        bitmapFactoryMock
            .`when`<Bitmap?> { BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size) }
            .thenReturn(mockBitmap)

        // Act
        val result = base64ToBitmap(validBase64)

        // Assert
        assertNotNull(result)
        assertEquals(mockBitmap, result)
      }
    }
  }

  @Test
  fun `test base64ToBitmap returns null for invalid Base64 string`() {
    // Arrange
    val invalidBase64 = "InvalidString"

    // Act
    val bitmap = base64ToBitmap(invalidBase64)

    // Assert
    assertNull(bitmap)
  }

  @Test
  fun `test resizeAndCompressImageFromUri returns null on failure`() {
    // Arrange
    val mockContentResolver = mock(ContentResolver::class.java) // Mock ContentResolver
    `when`(mockContext.contentResolver).thenReturn(mockContentResolver) // Inject mock
    `when`(mockContentResolver.openInputStream(mockUri)).thenThrow(RuntimeException("File error"))

    // Act
    val result = resizeAndCompressImageFromUri(mockUri, mockContext)

    // Assert
    assertNull(result)
  }

  @Test
  fun `test loadProfilePicture handles missing profile picture`() {
    // Arrange
    val userId = "testUserId"
    val mockDocumentReference = mock(DocumentReference::class.java)
    val mockSnapshot = mock(DocumentSnapshot::class.java)
    `when`(mockProfileCollectionReference.document(userId)).thenReturn(mockDocumentReference)
    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockSnapshot))
    `when`(mockSnapshot.getString("profilePicture")).thenReturn(null)

    // Act
    repository.loadProfilePicture(userId) { bitmap ->
      // Assert
      assertNull(bitmap)
    }
  }

  @Test
  fun `resizeAndCompressImageFromUri returns base64 string on success`() {
    // Arrange
    val mockContext = mock(Context::class.java)
    val mockContentResolver = mock(ContentResolver::class.java)
    val mockInputStream = mock(InputStream::class.java)
    val mockUri = mock(Uri::class.java)

    val originalBitmap = mock(Bitmap::class.java)
    val croppedBitmap = mock(Bitmap::class.java)
    val resizedBitmap = mock(Bitmap::class.java)

    val sampleCompressedBytes = "compressed_image".toByteArray()
    val sideLength = 600
    val quality = 10

    // Mock ContentResolver behavior
    `when`(mockContext.contentResolver).thenReturn(mockContentResolver)
    `when`(mockContentResolver.openInputStream(mockUri)).thenReturn(mockInputStream)

    // Mock BitmapFactory.decodeStream() as a static method
    val mockBitmapFactory = mockStatic(BitmapFactory::class.java)
    mockBitmapFactory
        .`when`<Bitmap?> { BitmapFactory.decodeStream(mockInputStream) }
        .thenReturn(originalBitmap)

    // Mock properties of original Bitmap
    `when`(originalBitmap.width).thenReturn(1024)
    `when`(originalBitmap.height).thenReturn(512)

    // Mock Bitmap.createBitmap() for cropping
    val cropSize = 512 // Min of width and height
    val cropX = (1024 - cropSize) / 2
    val cropY = (512 - cropSize) / 2
    val mockBitmapClass = mockStatic(Bitmap::class.java)
    mockBitmapClass
        .`when`<Bitmap> { Bitmap.createBitmap(originalBitmap, cropX, cropY, cropSize, cropSize) }
        .thenReturn(croppedBitmap)

    // Mock Bitmap.createScaledBitmap() for resizing
    mockBitmapClass
        .`when`<Bitmap> { Bitmap.createScaledBitmap(croppedBitmap, sideLength, sideLength, true) }
        .thenReturn(resizedBitmap)

    // Mock Bitmap.compress() behavior using ArgumentCaptor
    val captor = ArgumentCaptor.forClass(ByteArrayOutputStream::class.java)
    `when`(resizedBitmap.compress(eq(Bitmap.CompressFormat.JPEG), eq(quality), captor.capture()))
        .thenAnswer {
          captor.value.write(sampleCompressedBytes)
          true
        }

    // Act
    val result = resizeAndCompressImageFromUri(mockUri, mockContext, sideLength, quality)

    // Assert
    val expectedBase64 = Base64.encodeToString(sampleCompressedBytes, Base64.DEFAULT)
    assertEquals(expectedBase64, result)

    // Verify interactions
    verify(mockInputStream).close()
    verify(resizedBitmap)
        .compress(
            eq(Bitmap.CompressFormat.JPEG),
            eq(quality),
            Mockito.any(ByteArrayOutputStream::class.java))

    // Cleanup
    mockBitmapFactory.close()
    mockBitmapClass.close()
  }

  @Test
  fun `resizeAndCompressImageFromUri returns null on failure`() {
    // Arrange
    val mockContext = mock(Context::class.java)
    val mockContentResolver = mock(ContentResolver::class.java)
    val mockUri = mock(Uri::class.java)

    // Mock ContentResolver behavior to throw a RuntimeException
    `when`(mockContext.contentResolver).thenReturn(mockContentResolver)
    `when`(mockContentResolver.openInputStream(mockUri))
        .thenThrow(RuntimeException("Test Exception"))

    // Act
    val result = resizeAndCompressImageFromUri(mockUri, mockContext)

    // Assert
    assertNull(result)
  }

  @Test
  fun `isUsernameAvailable returns true when username does not exist`() = runBlocking {
    // Arrange
    val username = "testUsername"
    val mockQuery: Query = mock(Query::class.java)
    `when`(mockUsernamesCollectionReference.limit(1)).thenReturn(mockQuery)
    `when`(mockQuery.get()).thenReturn(Tasks.forResult(mockUsernameQuerySnapshot))
    `when`(mockUsernameQuerySnapshot.isEmpty).thenReturn(false)
    `when`(mockUsernameDocumentReference.get())
        .thenReturn(Tasks.forResult(mockUsernameDocumentSnapshot))
    `when`(mockUsernameDocumentSnapshot.exists()).thenReturn(false)

    // Act
    val result = repository.isUsernameAvailable(username)

    // Assert
    assertTrue(result)
  }

  @Test
  fun `isUsernameAvailable returns true when usernames collection does not exist`() = runBlocking {
    // Arrange
    val username = "testUsername"
    val mockQuery: Query = mock(Query::class.java)
    `when`(mockUsernamesCollectionReference.limit(1)).thenReturn(mockQuery)
    `when`(mockQuery.get()).thenReturn(Tasks.forResult(mockUsernameQuerySnapshot))
    `when`(mockUsernameQuerySnapshot.isEmpty).thenReturn(true)

    // Act
    val result = repository.isUsernameAvailable(username)

    // Assert
    assertTrue(result)
  }

  @Test
  fun `isUsernameAvailable returns false when username exists`() = runBlocking {
    // Arrange
    val username = "testUsername"
    val mockQuery: Query = mock(Query::class.java)
    `when`(mockUsernamesCollectionReference.limit(1)).thenReturn(mockQuery)
    `when`(mockQuery.get()).thenReturn(Tasks.forResult(mockUsernameQuerySnapshot))
    `when`(mockUsernameQuerySnapshot.isEmpty).thenReturn(false)
    `when`(mockUsernameDocumentReference.get())
        .thenReturn(Tasks.forResult(mockUsernameDocumentSnapshot))
    `when`(mockUsernameDocumentSnapshot.exists()).thenReturn(true)

    // Act
    val result = repository.isUsernameAvailable(username)

    // Assert
    assertFalse(result)
  }

  @Test
  fun `isUsernameAvailable returns false when error occurs`() = runBlocking {
    // Arrange
    val username = "testUsername"
    val mockQuery: Query = mock(Query::class.java)
    `when`(mockUsernamesCollectionReference.limit(1)).thenReturn(mockQuery)
    `when`(mockQuery.get()).thenReturn(Tasks.forResult(mockUsernameQuerySnapshot))
    `when`(mockUsernameQuerySnapshot.isEmpty).thenReturn(false)
    `when`(mockUsernameDocumentReference.get())
        .thenReturn(Tasks.forException(Exception("Get document failed")))

    // Act
    val result = repository.isUsernameAvailable(username)

    // Assert
    assertFalse(result)
  }

  @Test
  fun `test searchUsers returns list of matching users`() = runBlocking {
    // Arrange
    val query = "john"
    val mockSnapshot = mock(QuerySnapshot::class.java)
    val mockDocument1 = mock(DocumentSnapshot::class.java)
    val mockDocument2 = mock(DocumentSnapshot::class.java)

    val user1 =
        ProfileData(
            bio = "Bio 1",
            links = 2,
            name = "John Doe",
            profilePicture = null,
            username = "john_doe",
            favoriteMusicGenres = listOf("Rock"))
    val user2 =
        ProfileData(
            bio = "Bio 2",
            links = 3,
            name = "John Smith",
            profilePicture = null,
            username = "john_smith",
            favoriteMusicGenres = listOf("Pop"))

    `when`(mockDocument1.toObject(ProfileData::class.java)).thenReturn(user1)
    `when`(mockDocument2.toObject(ProfileData::class.java)).thenReturn(user2)
    `when`(mockSnapshot.documents).thenReturn(listOf(mockDocument1, mockDocument2))
    `when`(mockProfileCollectionReference.whereGreaterThanOrEqualTo("username", query))
        .thenReturn(mockQuery)
    `when`(mockQuery.whereLessThanOrEqualTo("username", "$query\uf8ff")).thenReturn(mockQuery)
    `when`(mockQuery.get()).thenReturn(Tasks.forResult(mockSnapshot))

    // Act
    val result = repository.searchUsers(query)

    // Assert
    assertNotNull(result)
    assertEquals(2, result.size)
    assert(result.contains(user1))
    assert(result.contains(user2))
  }

  @Test
  fun `test searchUsers returns empty list when no matches found`() = runBlocking {
    // Arrange
    val query = "nonexistent"
    val mockSnapshot = mock(QuerySnapshot::class.java)

    `when`(mockSnapshot.documents).thenReturn(emptyList())
    `when`(mockProfileCollectionReference.whereGreaterThanOrEqualTo("username", query))
        .thenReturn(mockQuery)
    `when`(mockQuery.whereLessThanOrEqualTo("username", "$query\uf8ff")).thenReturn(mockQuery)
    `when`(mockQuery.get()).thenReturn(Tasks.forResult(mockSnapshot))

    // Act
    val result = repository.searchUsers(query)

    // Assert
    assertNotNull(result)
    assert(result.isEmpty())
  }

  @Test
  fun `test searchUsers returns empty list on error`() = runBlocking {
    // Arrange
    val query = "john"
    `when`(mockProfileCollectionReference.whereGreaterThanOrEqualTo("username", query))
        .thenReturn(mockQuery)
    `when`(mockQuery.whereLessThanOrEqualTo("username", "$query\uf8ff")).thenReturn(mockQuery)
    `when`(mockQuery.get()).thenReturn(Tasks.forException(Exception("Firestore error")))

    // Act
    val result = repository.searchUsers(query)

    // Assert
    assertNotNull(result)
    assert(result.isEmpty())
  }

  @Test
  fun `addProfile transforms Spotify tracks and artists to Firestore format`() = runBlocking {
    // Arrange
    val userId = "testUserId"
    val profileData =
        ProfileData(
            username = "testUsername",
            topSongs =
                listOf(
                    SpotifyTrack(
                        name = "Test Track",
                        artist = "Test Artist",
                        trackId = "track123",
                        cover = "http://example.com/cover.jpg",
                        duration = 200,
                        popularity = 80,
                        state = State.PLAY)),
            topArtists =
                listOf(
                    SpotifyArtist(
                        name = "Test Artist",
                        image = "http://example.com/artist.jpg",
                        genres = listOf("Pop", "Rock"),
                        popularity = 90)))

    // Mock transaction behavior
    `when`(mockTransaction.get(mockUsernameDocumentReference)).thenReturn(mockDocumentSnapshot)
    `when`(mockDocumentSnapshot.exists()).thenReturn(false)

    `when`(mockDb.runTransaction<Transaction>(any())).thenAnswer { invocation ->
      val transactionFunction = invocation.arguments[0] as Transaction.Function<*>
      transactionFunction.apply(mockTransaction)
      Tasks.forResult(null)
    }

    // Act
    val success = repository.addProfile(userId, profileData)

    // Assert
    assertTrue(success)

    // Verify the transformation of topSongs
    val topSongsCaptor = ArgumentCaptor.forClass(List::class.java)
    verify(mockTransaction)
        .update(eq(mockProfileDocumentReference), eq("topSongs"), topSongsCaptor.capture())
    val transformedTopSongs = topSongsCaptor.value as List<Map<String, Any>>
    assertEquals(1, transformedTopSongs.size)
    assertEquals("Test Track", transformedTopSongs[0]["name"])
    assertEquals("Test Artist", transformedTopSongs[0]["artist"])
    assertEquals("track123", transformedTopSongs[0]["trackId"])
    assertEquals("http://example.com/cover.jpg", transformedTopSongs[0]["cover"])
    assertEquals(200, transformedTopSongs[0]["duration"])
    assertEquals(80, transformedTopSongs[0]["popularity"])
    assertEquals("PLAY", transformedTopSongs[0]["state"])

    // Verify the transformation of topArtists
    val topArtistsCaptor = ArgumentCaptor.forClass(List::class.java)
    verify(mockTransaction)
        .update(eq(mockProfileDocumentReference), eq("topArtists"), topArtistsCaptor.capture())
    val transformedTopArtists = topArtistsCaptor.value as List<Map<String, Any>>
    assertEquals(1, transformedTopArtists.size)
    assertEquals("Test Artist", transformedTopArtists[0]["name"])
    assertEquals("http://example.com/artist.jpg", transformedTopArtists[0]["image"])
    assertEquals(listOf("Pop", "Rock"), transformedTopArtists[0]["genres"])
    assertEquals(90, transformedTopArtists[0]["popularity"])
  }

  @Test
  fun `spotifyTrackToMap correctly transforms SpotifyTrack to map`() {
    // Arrange
    val privateMethod =
        ProfileRepositoryFirestore::class
            .java
            .getDeclaredMethod("spotifyTrackToMap", ProfileData::class.java)
    privateMethod.isAccessible = true

    val profileData =
        ProfileData(
            topSongs =
                listOf(
                    SpotifyTrack(
                        name = "Test Track",
                        artist = "Test Artist",
                        trackId = "track123",
                        cover = "http://example.com/cover.jpg",
                        duration = 200,
                        popularity = 80,
                        state = State.PLAY)))

    // Act
    val result = privateMethod.invoke(repository, profileData) as List<Map<String, Any>>

    // Assert
    assertEquals(1, result.size)
    assertEquals("Test Track", result[0]["name"])
    assertEquals("Test Artist", result[0]["artist"])
    assertEquals("track123", result[0]["trackId"])
    assertEquals("http://example.com/cover.jpg", result[0]["cover"])
    assertEquals(200, result[0]["duration"])
    assertEquals(80, result[0]["popularity"])
    assertEquals("PLAY", result[0]["state"])
  }

  @Test
  fun `spotifyArtistToMap correctly transforms SpotifyArtist to map`() {
    // Arrange
    val privateMethod =
        ProfileRepositoryFirestore::class
            .java
            .getDeclaredMethod("spotifyArtistToMap", ProfileData::class.java)
    privateMethod.isAccessible = true

    val profileData =
        ProfileData(
            topArtists =
                listOf(
                    SpotifyArtist(
                        name = "Test Artist",
                        image = "http://example.com/artist.jpg",
                        genres = listOf("Pop", "Rock"),
                        popularity = 90)))

    // Act
    val result = privateMethod.invoke(repository, profileData) as List<Map<String, Any>>

    // Assert
    assertEquals(1, result.size)
    assertEquals("Test Artist", result[0]["name"])
    assertEquals("http://example.com/artist.jpg", result[0]["image"])
    assertEquals(listOf("Pop", "Rock"), result[0]["genres"])
    assertEquals(90, result[0]["popularity"])
  }
}
