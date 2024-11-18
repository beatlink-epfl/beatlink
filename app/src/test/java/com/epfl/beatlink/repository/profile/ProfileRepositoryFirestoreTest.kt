package com.epfl.beatlink.repository.profile

import android.net.Uri
import com.epfl.beatlink.model.profile.ProfileData
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class ProfileRepositoryFirestoreTest {

  private lateinit var repository: ProfileRepositoryFirestore
  private lateinit var mockDb: FirebaseFirestore
  private lateinit var mockAuth: FirebaseAuth
  private lateinit var mockDocumentSnapshot: DocumentSnapshot
  private lateinit var mockUser: FirebaseUser
  private lateinit var mockStorage: FirebaseStorage
  private lateinit var mockStorageRef: StorageReference
  private lateinit var mockUploadTask: UploadTask
  private lateinit var mockUri: Uri

  // Additional mock objects
  private lateinit var mockCollectionReference: CollectionReference
  private lateinit var mockDocumentReference: DocumentReference

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    mockDb = mock(FirebaseFirestore::class.java)
    mockAuth = mock(FirebaseAuth::class.java)
    mockStorage = mock(FirebaseStorage::class.java)
    mockDocumentSnapshot = mock(DocumentSnapshot::class.java)
    mockUser = mock(FirebaseUser::class.java)
    mockCollectionReference = mock(CollectionReference::class.java)
    mockDocumentReference = mock(DocumentReference::class.java)
    mockStorageRef = mock(StorageReference::class.java)
    mockUploadTask = mock(UploadTask::class.java)
    mockUri = mock(Uri::class.java)

    // Setup mock behavior for collection and document references
    `when`(mockDb.collection("userProfiles")).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document("testUserId")).thenReturn(mockDocumentReference)
    `when`(mockStorage.reference).thenReturn(mockStorageRef)

    repository = ProfileRepositoryFirestore(mockDb, mockAuth, mockStorage)
  }

  @Test
  fun `test getProfile returns data when fetch is successful`() = runBlocking {
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
    println(result)

    // Assert
    assert(result == profileData)
  }

  // Test for successful profile update
  @Test
  fun `test updateProfile returns true when update is successful`() = runBlocking {
    val userId = "testUserId"
    val profileData =
        ProfileData(
            bio = "Updated bio",
            links = 5,
            name = "Jane Doe",
            profilePicture = null,
            username = "janedoe")

    `when`(mockDocumentReference.set(profileData)).thenReturn(Tasks.forResult(null))

    val result = repository.updateProfile(userId, profileData)

    assert(result)
  }

  // Test for failed profile update
  @Test
  fun `test updateProfile returns false when update fails`() = runBlocking {
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

    val result = repository.updateProfile(userId, profileData)

    assert(!result)
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
}
