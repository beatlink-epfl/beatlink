package com.epfl.beatlink.repository.profile

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.epfl.beatlink.model.profile.ProfileData
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.never
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify

class ProfileRepositoryFirestoreTest {

  private lateinit var repository: ProfileRepositoryFirestore
  private lateinit var mockDb: FirebaseFirestore
  private lateinit var mockAuth: FirebaseAuth
  private lateinit var mockDocumentSnapshot: DocumentSnapshot
  private lateinit var mockUser: FirebaseUser
  private lateinit var mockUploadTask: UploadTask
  private lateinit var mockUri: Uri
  private lateinit var mockContext: Context

  // Additional mock objects
  private lateinit var mockCollectionReference: CollectionReference
  private lateinit var mockDocumentReference: DocumentReference

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    mockDb = mock(FirebaseFirestore::class.java)
    mockAuth = mock(FirebaseAuth::class.java)
    mockDocumentSnapshot = mock(DocumentSnapshot::class.java)
    mockUser = mock(FirebaseUser::class.java)
    mockCollectionReference = mock(CollectionReference::class.java)
    mockDocumentReference = mock(DocumentReference::class.java)
    mockUploadTask = mock(UploadTask::class.java)
    mockUri = mock(Uri::class.java)
    mockContext = mock(Context::class.java)

    // Setup mock behavior for collection and document references
    `when`(mockDb.collection("userProfiles")).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document("testUserId")).thenReturn(mockDocumentReference)

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
            favoriteMusicGenres = listOf("Rock", "Pop"))

    // Simulate the behavior of the document get() call
    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.getString("bio")).thenReturn(profileData.bio)
    `when`(mockDocumentSnapshot.getLong("links")).thenReturn(profileData.links.toLong())
    `when`(mockDocumentSnapshot.getString("name")).thenReturn(profileData.name)
    `when`(mockDocumentSnapshot.getString("profilePicture")).thenReturn(null)
    `when`(mockDocumentSnapshot.getString("username")).thenReturn(profileData.username)
    `when`(mockDocumentSnapshot.get("favoriteMusicGenres"))
        .thenReturn(profileData.favoriteMusicGenres)

    // Act
    val result = repository.fetchProfile(userId)

    // Assert
    assert(result == profileData)
  }

  @Test
  fun `test addProfile returns true when profile is added successfully`() = runBlocking {
    // Arrange
    val userId = "testUserId"
    val profileData =
        ProfileData(
            bio = "Sample bio",
            links = 5,
            name = "John Doe",
            profilePicture = null,
            username = "johndoe",
            favoriteMusicGenres = listOf("Rock", "Pop"))

    `when`(mockDocumentReference.set(profileData)).thenReturn(Tasks.forResult(null))

    // Act
    val result = repository.addProfile(userId, profileData)

    // Assert
    assert(result)
  }

  @Test
  fun `test addProfile returns false when profile addition fails`() = runBlocking {
    // Arrange
    val userId = "testUserId"
    val profileData =
        ProfileData(
            bio = "Sample bio",
            links = 5,
            name = "John Doe",
            profilePicture = null,
            username = "johndoe",
            favoriteMusicGenres = listOf("Rock", "Pop"))

    `when`(mockDocumentReference.set(profileData))
        .thenReturn(Tasks.forException(Exception("Add failed")))

    // Act
    val result = repository.addProfile(userId, profileData)

    // Assert
    assert(!result)
  }

  @Test
  fun `test updateProfile returns true when update is successful`() = runBlocking {
    // Arrange
    val userId = "testUserId"
    val profileData =
        ProfileData(
            bio = "Updated bio",
            links = 5,
            name = "Jane Doe",
            profilePicture = null,
            username = "janedoe")

    `when`(mockDocumentReference.set(profileData)).thenReturn(Tasks.forResult(null))

    // Act
    val result = repository.updateProfile(userId, profileData)

    // Assert
    assert(result)
  }

  @Test
  fun `test updateProfile returns false when update fails`() = runBlocking {
    // Arrange
    val userId = "testUserId"
    val profileData =
        ProfileData(
            bio = "Updated bio",
            links = 5,
            name = "Jane Doe",
            profilePicture = null,
            username = "janedoe")

    `when`(mockDocumentReference.set(profileData))
        .thenReturn(Tasks.forException(Exception("Update failed")))

    // Act
    val result = repository.updateProfile(userId, profileData)

    // Assert
    assert(!result)
  }

  @Test
  fun `test deleteProfile returns true when profile is deleted successfully`() = runBlocking {
    // Arrange
    val userId = "testUserId"

    `when`(mockDocumentReference.delete())
        .thenReturn(Tasks.forResult(null)) // Simulate successful deletion

    // Act
    val result = repository.deleteProfile(userId)

    // Assert
    assert(result)
  }

  @Test
  fun `test deleteProfile returns false when profile deletion fails`() = runBlocking {
    // Arrange
    val userId = "testUserId"

    `when`(mockDocumentReference.delete())
        .thenReturn(Tasks.forException(Exception("Delete failed")))

    // Act
    val result = repository.deleteProfile(userId)

    // Assert
    assert(!result)
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
        val result = repository.base64ToBitmap(validBase64)

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
    val bitmap = repository.base64ToBitmap(invalidBase64)

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
    val result = repository.resizeAndCompressImageFromUri(mockUri, mockContext)

    // Assert
    assertNull(result)
  }

  @Test
  fun `test uploadProfilePicture logs error on failure`() {
    // Arrange
    val userId = "testUserId"
    `when`(repository.resizeAndCompressImageFromUri(mockUri, mockContext)).thenReturn(null)

    // Act
    repository.uploadProfilePicture(mockUri, mockContext, userId)

    // Assert
    // No save operation should occur since Base64 conversion failed
    verify(mockCollectionReference, never()).document(anyString())
  }

  @Test
  fun `test loadProfilePicture handles missing profile picture`() {
    // Arrange
    val userId = "testUserId"
    val mockDocumentReference = mock(DocumentReference::class.java)
    val mockSnapshot = mock(DocumentSnapshot::class.java)
    `when`(mockCollectionReference.document(userId)).thenReturn(mockDocumentReference)
    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockSnapshot))
    `when`(mockSnapshot.getString("profilePicture")).thenReturn(null)

    // Act
    repository.loadProfilePicture(userId) { bitmap ->
      // Assert
      assertNull(bitmap)
    }
  }
}
