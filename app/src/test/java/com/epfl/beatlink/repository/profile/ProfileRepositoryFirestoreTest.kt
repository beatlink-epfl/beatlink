package com.epfl.beatlink.repository.profile

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.epfl.beatlink.model.profile.ProfileData
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import java.io.InputStream
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.anyMap
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.never
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
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

  @Mock private lateinit var mockQuerySnapshot: QuerySnapshot

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
  fun `getUsername returns username on success`(): Unit = runBlocking {
    // Arrange
    val userId = "testUserId"
    val expectedUsername = "TestUsername"

    // Mocking behavior for Firestore document reference and snapshot
    `when`(mockAuth.currentUser).thenReturn(mockUser)
    `when`(mockUser.uid).thenReturn("testUserId")
    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.getString("username")).thenReturn(expectedUsername)

    // Act
    val result = repository.getUsername(userId)

    // Assert
    assertEquals(expectedUsername, result)
    verify(mockDocumentReference).get()
    verify(mockDocumentSnapshot).getString("username")
  }

  @Test
  fun `getUsername should return null and log an error when an exception occurs`(): Unit =
      runBlocking {
        // Arrange
        val userId = "testUserId"
        val exception = RuntimeException("Firestore error")

        // Mock Firestore behavior to throw an exception
        `when`(mockDocumentReference.get()).thenThrow(exception)
        `when`(mockDb.collection("userProfiles")).thenReturn(mockCollectionReference)
        `when`(mockCollectionReference.document(userId)).thenReturn(mockDocumentReference)

        // Act
        val result = repository.getUsername(userId)

        // Assert
        assertNull(result)
        verify(mockDocumentReference).get()
      }

  @Test
  fun `getUserIdByUsername returns userId on success`(): Unit = runBlocking {
    // Arrange
    val username = "TestUsername"
    val userId = "testUserId"
    val mockQuerySnapshot: QuerySnapshot = mock(QuerySnapshot::class.java)

    // Mocking behavior for Firestore query
    `when`(mockCollectionReference.whereEqualTo("username", username))
        .thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))
    `when`(mockQuerySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.id).thenReturn(userId)

    // Act
    val result = repository.getUserIdByUsername(username)

    // Assert
    assertEquals(userId, result)
    verify(mockCollectionReference).whereEqualTo("username", username)
    verify(mockCollectionReference).get()
    verify(mockQuerySnapshot).documents
    verify(mockDocumentSnapshot).id
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

  @Test
  fun `saveProfilePictureBase64 saves base64 image successfully`() {
    // Arrange
    val mockDb = mock(FirebaseFirestore::class.java)
    val mockCollectionReference = mock(CollectionReference::class.java)
    val mockDocumentReference = mock(DocumentReference::class.java)
    val mockTask = mock(Task::class.java) as Task<Void>
    val mockSuccessListener =
        ArgumentCaptor.forClass(OnSuccessListener::class.java)
            as ArgumentCaptor<OnSuccessListener<Void>>

    // Mock Firestore behavior
    `when`(mockDb.collection("your_collection_name")).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document("user_id")).thenReturn(mockDocumentReference)
    `when`(mockDocumentReference.set(anyMap<String, String>(), eq(SetOptions.merge())))
        .thenReturn(mockTask)

    // Ensure that addOnSuccessListener and addOnFailureListener return mockTask for chaining
    `when`(mockTask.addOnSuccessListener(any())).thenReturn(mockTask)
    `when`(mockTask.addOnFailureListener(any())).thenReturn(mockTask)

    val testClass =
        object {
          val db: FirebaseFirestore = mockDb

          fun saveProfilePictureBase64(userId: String, base64Image: String) {
            val userDoc = db.collection("your_collection_name").document(userId)
            val profileData = mapOf("profilePicture" to base64Image)

            userDoc
                .set(profileData, SetOptions.merge())
                .addOnSuccessListener { Log.d("FIRESTORE", "Base64 image saved successfully!") }
                .addOnFailureListener { e ->
                  Log.e("FIRESTORE", "Error saving Base64 image: ${e.message}")
                }
          }
        }

    // Act
    testClass.saveProfilePictureBase64("user_id", "base64_image_data")

    // Simulate success
    verify(mockTask).addOnSuccessListener(mockSuccessListener.capture())
    mockSuccessListener.value.onSuccess(null)

    // Assert
    verify(mockDocumentReference)
        .set(mapOf("profilePicture" to "base64_image_data"), SetOptions.merge())
    verify(mockTask).addOnSuccessListener(any())
  }

  @Test
  fun `saveProfilePictureBase64 handles failure`() {
    // Arrange
    val mockDb = mock(FirebaseFirestore::class.java)
    val mockCollectionReference = mock(CollectionReference::class.java)
    val mockDocumentReference = mock(DocumentReference::class.java)
    val mockTask = mock(Task::class.java) as Task<Void>
    val mockFailureListener =
        ArgumentCaptor.forClass(OnFailureListener::class.java) as ArgumentCaptor<OnFailureListener>

    // Mock Firestore behavior
    `when`(mockDb.collection("your_collection_name")).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document("user_id")).thenReturn(mockDocumentReference)
    `when`(mockDocumentReference.set(anyMap<String, String>(), eq(SetOptions.merge())))
        .thenReturn(mockTask)

    // Ensure that addOnSuccessListener and addOnFailureListener return mockTask for chaining
    `when`(mockTask.addOnSuccessListener(any())).thenReturn(mockTask)
    `when`(mockTask.addOnFailureListener(any())).thenReturn(mockTask)

    val testClass =
        object {
          val db: FirebaseFirestore = mockDb

          fun saveProfilePictureBase64(userId: String, base64Image: String) {
            val userDoc = db.collection("your_collection_name").document(userId)
            val profileData = mapOf("profilePicture" to base64Image)

            userDoc
                .set(profileData, SetOptions.merge())
                .addOnSuccessListener { Log.d("FIRESTORE", "Base64 image saved successfully!") }
                .addOnFailureListener { e ->
                  Log.e("FIRESTORE", "Error saving Base64 image: ${e.message}")
                }
          }
        }

    // Act
    testClass.saveProfilePictureBase64("user_id", "base64_image_data")

    // Simulate failure
    verify(mockTask).addOnFailureListener(mockFailureListener.capture())
    mockFailureListener.value.onFailure(Exception("Test error"))

    // Assert
    verify(mockDocumentReference)
        .set(mapOf("profilePicture" to "base64_image_data"), SetOptions.merge())
    verify(mockTask).addOnFailureListener(any())
  }

  @Test
  fun `resizeAndCompressImageFromUri returns base64 string on success`() {
    // Arrange
    val mockContext = mock(Context::class.java)
    val mockContentResolver = mock(ContentResolver::class.java)
    val mockInputStream = mock(InputStream::class.java)
    val mockUri = mock(Uri::class.java)

    val originalBitmap = mock(Bitmap::class.java)
    val resizedBitmap = mock(Bitmap::class.java)

    val sampleCompressedBytes = "compressed_image".toByteArray()

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

    // Mock Bitmap.createScaledBitmap()
    val mockBitmapClass = mockStatic(Bitmap::class.java)
    mockBitmapClass
        .`when`<Bitmap> { Bitmap.createScaledBitmap(originalBitmap, 512, 256, true) }
        .thenReturn(resizedBitmap)

    // Mock Bitmap.compress() behavior using ArgumentCaptor
    val captor = ArgumentCaptor.forClass(ByteArrayOutputStream::class.java)
    `when`(resizedBitmap.compress(eq(Bitmap.CompressFormat.JPEG), eq(80), captor.capture()))
        .thenAnswer {
          captor.value.write(sampleCompressedBytes)
          true
        }

    // Act
    val result = repository.resizeAndCompressImageFromUri(mockUri, mockContext)

    // Assert
    val expectedBase64 = Base64.encodeToString(sampleCompressedBytes, Base64.DEFAULT)
    assertEquals(expectedBase64, result)

    // Verify interactions
    verify(mockInputStream).close()
    verify(resizedBitmap)
        .compress(
            eq(Bitmap.CompressFormat.JPEG), eq(80), Mockito.any(ByteArrayOutputStream::class.java))

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
    val result = repository.resizeAndCompressImageFromUri(mockUri, mockContext)

    // Assert
    assertNull(result)
  }
}
