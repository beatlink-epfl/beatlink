package com.android.sample.model.profile

import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class ProfileRepositoryTest {

    private lateinit var repository: ProfileRepository
    private lateinit var mockDb: FirebaseFirestore
    private lateinit var mockAuth: FirebaseAuth
    private lateinit var mockDocumentSnapshot: DocumentSnapshot
    private lateinit var mockUser: FirebaseUser

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

        // Setup mock behavior for collection and document references
        `when`(mockDb.collection("userProfiles")).thenReturn(mockCollectionReference)
        `when`(mockCollectionReference.document("testUserId")).thenReturn(mockDocumentReference)

        repository = ProfileRepository(mockDb, mockAuth)
    }


    @Test
    fun `test getProfile returns data when fetch is successful`() = runBlocking {
        // Arrange
        val userId = "testUserId"
        val profileData = ProfileData(
            bio = "Sample bio",
            links = 5,
            name = "John Doe",
            profilePicture = null,
            username = "johndoe"
        )

        // Simulate the behavior of the document get() call
        `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
        `when`(mockDocumentSnapshot.toObject(ProfileData::class.java)).thenReturn(profileData)

        // Act
        val result = repository.getProfile(userId)

        // Assert
        assert(result == profileData)
    }


    @Test
    fun `test getProfile returns null when an exception occurs`() = runBlocking {
        // Arrange
        val userId = "testUserId"
        `when`(mockDb.collection("userProfiles").document(userId).get())
            .thenReturn(Tasks.forException(Exception("Fetch failed")))

        // Act
        val result = repository.getProfile(userId)

        // Assert
        assert(result == null)
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
