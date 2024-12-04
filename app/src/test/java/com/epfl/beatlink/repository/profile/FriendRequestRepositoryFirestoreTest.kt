package com.epfl.beatlink.repository.profile

import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class FriendRequestRepositoryFirestoreTest {
  private lateinit var mockDb: FirebaseFirestore
  private lateinit var mockAuth: FirebaseAuth
  private lateinit var mockUser: FirebaseUser

  private lateinit var repositoryFirestore: FriendRequestRepositoryFirestore
  private lateinit var mockCollectionReference: CollectionReference
  private lateinit var mockSenderDocument: DocumentReference
  private lateinit var mockReceiverDocument: DocumentReference
  private lateinit var mockUserDocument: DocumentReference
  private lateinit var mockFriendDocument: DocumentReference

  private lateinit var mockQuerySnapshot: QuerySnapshot
  private lateinit var mockDocumentSnapshot: DocumentSnapshot

  private val collectionPath = "friendRequests"

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    mockDb = mock(FirebaseFirestore::class.java)
    mockAuth = mock(FirebaseAuth::class.java)
    mockUser = mock(FirebaseUser::class.java)

    mockCollectionReference = mock(CollectionReference::class.java)
    mockSenderDocument = mock(DocumentReference::class.java)
    mockReceiverDocument = mock(DocumentReference::class.java)
    mockUserDocument = mock(DocumentReference::class.java)
    mockFriendDocument = mock(DocumentReference::class.java)

    mockQuerySnapshot = mock(QuerySnapshot::class.java)
    mockDocumentSnapshot = mock(DocumentSnapshot::class.java)

    `when`(mockDb.collection(collectionPath)).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document("testSender")).thenReturn(mockSenderDocument)
    `when`(mockCollectionReference.document("testReceiver")).thenReturn(mockReceiverDocument)
    `when`(mockCollectionReference.document("testUser")).thenReturn(mockUserDocument)
    `when`(mockCollectionReference.document("testFriend")).thenReturn(mockFriendDocument)

    repositoryFirestore = FriendRequestRepositoryFirestore(mockDb, mockAuth)
  }

  @Test
  fun `test getUserId returns user ID when user is authenticated`() {
    // Arrange
    `when`(mockAuth.currentUser).thenReturn(mockUser)
    `when`(mockUser.uid).thenReturn("testUserId")
    // Act
    val userId = repositoryFirestore.getUserId()
    // Assert
    assert(userId == "testUserId")
  }

  @Test
  fun `test getUserId returns null when user is not authenticated`() {
    // Arrange
    `when`(mockAuth.currentUser).thenReturn(null)
    // Act
    val userId = repositoryFirestore.getUserId()
    // Assert
    assert(userId == null)
  }

  @Test
  fun `sendFriendRequest updates ownRequests and friendRequests`(): Unit = runBlocking {
    `when`(mockSenderDocument.update(anyString(), any())).thenReturn(Tasks.forResult(null))
    `when`(mockReceiverDocument.update(anyString(), any())).thenReturn(Tasks.forResult(null))

    repositoryFirestore.sendFriendRequest("testSender", "testReceiver")

    verify(mockSenderDocument).update(eq("ownRequests.testReceiver"), eq(true))
    verify(mockReceiverDocument).update(eq("friendRequests.testSender"), eq(true))
  }

  @Test
  fun `sendFriendRequest logs error when updating ownRequests fails`(): Unit = runBlocking {
    // Mock sender update to fail
    val exception = Exception("Failed to update ownRequests")
    `when`(mockSenderDocument.update(anyString(), any())).thenReturn(Tasks.forException(exception))

    repositoryFirestore.sendFriendRequest("testSender", "testReceiver")

    // Verify sender update is attempted and receiver update is not
    verify(mockSenderDocument).update("ownRequests.testReceiver", true)
    verify(mockReceiverDocument, never()).update(anyString(), any())
  }

  @Test
  fun `sendFriendRequest logs error when updating friendRequests fails`(): Unit = runBlocking {
    // Mock sender update to succeed and receiver update to fail
    `when`(mockSenderDocument.update(anyString(), any())).thenReturn(Tasks.forResult(null))
    val exception = Exception("Failed to update friendRequests")
    `when`(mockReceiverDocument.update(anyString(), any()))
        .thenReturn(Tasks.forException(exception))

    repositoryFirestore.sendFriendRequest("testSender", "testReceiver")

    // Verify both updates are attempted
    verify(mockSenderDocument).update("ownRequests.testReceiver", true)
    verify(mockReceiverDocument).update("friendRequests.testSender", true)
  }

  @Test
  fun `acceptFriendRequest completes successfully`(): Unit = runBlocking {
    // Mock successful updates
    `when`(mockReceiverDocument.update(anyString(), any())).thenReturn(Tasks.forResult(null))
    `when`(mockSenderDocument.update(anyString(), any())).thenReturn(Tasks.forResult(null))

    repositoryFirestore.acceptFriendRequest("testReceiver", "testSender")

    // Verify all Firestore updates are called with correct arguments
    verify(mockReceiverDocument).update(eq("friendRequests.testSender"), eq(FieldValue.delete()))
    verify(mockSenderDocument).update(eq("ownRequests.testReceiver"), eq(FieldValue.delete()))
    verify(mockReceiverDocument)
        .update(eq("allFriends.testSender"), eq(mapOf("status" to "linked")))
    verify(mockSenderDocument)
        .update(eq("allFriends.testReceiver"), eq(mapOf("status" to "linked")))
  }

  @Test
  fun `acceptFriendRequest logs error when removing from friendRequests fails`(): Unit =
      runBlocking {
        // Mock failure on removing from friendRequests
        val exception = Exception("Failed to remove from friendRequests")
        `when`(mockReceiverDocument.update("friendRequests.testSender", FieldValue.delete()))
            .thenReturn(Tasks.forException(exception))

        repositoryFirestore.acceptFriendRequest("testReceiver", "testSender")

        // Verify only the first operation is attempted
        verify(mockReceiverDocument).update("friendRequests.testSender", FieldValue.delete())
        verify(mockSenderDocument, never()).update(anyString(), any())
      }

  @Test
  fun `acceptFriendRequest logs error when removing from ownRequests fails`(): Unit = runBlocking {
    // Mock success for removing from friendRequests
    `when`(mockReceiverDocument.update("friendRequests.testSender", FieldValue.delete()))
        .thenReturn(Tasks.forResult(null))

    // Mock failure for removing from ownRequests
    val exception = Exception("Failed to remove from ownRequests")
    `when`(mockSenderDocument.update("ownRequests.testReceiver", FieldValue.delete()))
        .thenReturn(Tasks.forException(exception))

    repositoryFirestore.acceptFriendRequest("testReceiver", "testSender")

    // Verify operations up to the point of failure
    verify(mockReceiverDocument).update("friendRequests.testSender", FieldValue.delete())
    verify(mockSenderDocument).update("ownRequests.testReceiver", FieldValue.delete())
    verify(mockReceiverDocument, never())
        .update("allFriends.testSender", mapOf("status" to "linked"))
  }

  @Test
  fun `acceptFriendRequest logs error when adding to receiver's allFriends fails`(): Unit =
      runBlocking {
        // Mock success for previous steps
        `when`(mockReceiverDocument.update("friendRequests.testSender", FieldValue.delete()))
            .thenReturn(Tasks.forResult(null))
        `when`(mockSenderDocument.update("ownRequests.testReceiver", FieldValue.delete()))
            .thenReturn(Tasks.forResult(null))

        // Mock failure for adding to receiver's allFriends
        val exception = Exception("Failed to add to receiver's allFriends")
        `when`(mockReceiverDocument.update("allFriends.testSender", mapOf("status" to "linked")))
            .thenReturn(Tasks.forException(exception))

        repositoryFirestore.acceptFriendRequest("testReceiver", "testSender")

        // Verify operations up to the point of failure
        verify(mockReceiverDocument).update("friendRequests.testSender", FieldValue.delete())
        verify(mockSenderDocument).update("ownRequests.testReceiver", FieldValue.delete())
        verify(mockReceiverDocument).update("allFriends.testSender", mapOf("status" to "linked"))
        verify(mockSenderDocument, never())
            .update("allFriends.testReceiver", mapOf("status" to "linked"))
      }

  @Test
  fun `acceptFriendRequest logs error when adding to sender's allFriends fails`(): Unit =
      runBlocking {
        // Mock success for previous steps
        `when`(mockReceiverDocument.update("friendRequests.testSender", FieldValue.delete()))
            .thenReturn(Tasks.forResult(null))
        `when`(mockSenderDocument.update("ownRequests.testReceiver", FieldValue.delete()))
            .thenReturn(Tasks.forResult(null))
        `when`(mockReceiverDocument.update("allFriends.testSender", mapOf("status" to "linked")))
            .thenReturn(Tasks.forResult(null))

        // Mock failure for adding to sender's allFriends
        val exception = Exception("Failed to add to sender's allFriends")
        `when`(mockSenderDocument.update("allFriends.testReceiver", mapOf("status" to "linked")))
            .thenReturn(Tasks.forException(exception))

        repositoryFirestore.acceptFriendRequest("testReceiver", "testSender")

        // Verify all previous operations are completed
        verify(mockReceiverDocument).update("friendRequests.testSender", FieldValue.delete())
        verify(mockSenderDocument).update("ownRequests.testReceiver", FieldValue.delete())
        verify(mockReceiverDocument).update("allFriends.testSender", mapOf("status" to "linked"))
        verify(mockSenderDocument).update("allFriends.testReceiver", mapOf("status" to "linked"))
      }

  @Test
  fun `rejectFriendRequest completes successfully`(): Unit = runBlocking {
    // Mock successful updates
    `when`(mockReceiverDocument.update(anyString(), any())).thenReturn(Tasks.forResult(null))
    `when`(mockSenderDocument.update(anyString(), any())).thenReturn(Tasks.forResult(null))

    repositoryFirestore.rejectFriendRequest("testReceiver", "testSender")

    // Verify Firestore updates are called with correct arguments
    verify(mockReceiverDocument).update("friendRequests.testSender", FieldValue.delete())
    verify(mockSenderDocument).update("ownRequests.testReceiver", FieldValue.delete())
  }

  @Test
  fun `rejectFriendRequest logs error when removing from friendRequests fails`(): Unit =
      runBlocking {
        // Mock failure on removing from friendRequests
        val exception = Exception("Failed to remove from friendRequests")
        `when`(mockReceiverDocument.update("friendRequests.testSender", FieldValue.delete()))
            .thenReturn(Tasks.forException(exception))

        repositoryFirestore.rejectFriendRequest("testReceiver", "testSender")

        // Verify only the first operation is attempted
        verify(mockReceiverDocument).update("friendRequests.testSender", FieldValue.delete())
        verify(mockSenderDocument, never()).update("ownRequests.testReceiver", FieldValue.delete())
      }

  @Test
  fun `rejectFriendRequest logs error when removing from ownRequests fails`(): Unit = runBlocking {
    // Mock success for removing from friendRequests
    `when`(mockReceiverDocument.update("friendRequests.testSender", FieldValue.delete()))
        .thenReturn(Tasks.forResult(null))

    // Mock failure for removing from ownRequests
    val exception = Exception("Failed to remove from ownRequests")
    `when`(mockSenderDocument.update("ownRequests.testReceiver", FieldValue.delete()))
        .thenReturn(Tasks.forException(exception))

    repositoryFirestore.rejectFriendRequest("testReceiver", "testSender")

    // Verify operations up to the point of failure
    verify(mockReceiverDocument).update("friendRequests.testSender", FieldValue.delete())
    verify(mockSenderDocument).update("ownRequests.testReceiver", FieldValue.delete())
  }

  @Test
  fun `cancelFriendRequest completes successfully`(): Unit = runBlocking {
    // Mock successful updates
    `when`(mockSenderDocument.update(anyString(), any())).thenReturn(Tasks.forResult(null))
    `when`(mockReceiverDocument.update(anyString(), any())).thenReturn(Tasks.forResult(null))

    repositoryFirestore.cancelFriendRequest("testSender", "testReceiver")

    // Verify Firestore updates are called with correct arguments
    verify(mockSenderDocument).update("ownRequests.testReceiver", FieldValue.delete())
    verify(mockReceiverDocument).update("friendRequests.testSender", FieldValue.delete())
  }

  @Test
  fun `cancelFriendRequest logs error when removing from ownRequests fails`(): Unit = runBlocking {
    // Mock failure on removing from ownRequests
    val exception = Exception("Failed to remove from ownRequests")
    `when`(mockSenderDocument.update("ownRequests.testReceiver", FieldValue.delete()))
        .thenReturn(Tasks.forException(exception))

    repositoryFirestore.cancelFriendRequest("testSender", "testReceiver")

    // Verify only the first operation is attempted
    verify(mockSenderDocument).update("ownRequests.testReceiver", FieldValue.delete())
    verify(mockReceiverDocument, never()).update("friendRequests.testSender", FieldValue.delete())
  }

  @Test
  fun `cancelFriendRequest logs error when removing from friendRequests fails`(): Unit =
      runBlocking {
        // Mock success for removing from ownRequests
        `when`(mockSenderDocument.update("ownRequests.testReceiver", FieldValue.delete()))
            .thenReturn(Tasks.forResult(null))

        // Mock failure for removing from friendRequests
        val exception = Exception("Failed to remove from friendRequests")
        `when`(mockReceiverDocument.update("friendRequests.testSender", FieldValue.delete()))
            .thenReturn(Tasks.forException(exception))

        repositoryFirestore.cancelFriendRequest("testSender", "testReceiver")

        // Verify operations up to the point of failure
        verify(mockSenderDocument).update("ownRequests.testReceiver", FieldValue.delete())
        verify(mockReceiverDocument).update("friendRequests.testSender", FieldValue.delete())
      }

  @Test
  fun `removeFriend completes successfully`(): Unit = runBlocking {
    // Mock successful updates
    `when`(mockUserDocument.update(anyString(), any())).thenReturn(Tasks.forResult(null))
    `when`(mockFriendDocument.update(anyString(), any())).thenReturn(Tasks.forResult(null))

    repositoryFirestore.removeFriend("testUser", "testFriend")

    // Verify Firestore updates are called with correct arguments
    verify(mockUserDocument).update("allFriends.testFriend", FieldValue.delete())
    verify(mockFriendDocument).update("allFriends.testUser", FieldValue.delete())
  }

  @Test
  fun `removeFriend logs error when removing from user allFriends fails`(): Unit = runBlocking {
    // Mock failure on removing from userId's allFriends
    val exception = Exception("Failed to remove from user's allFriends")
    `when`(mockUserDocument.update("allFriends.testFriend", FieldValue.delete()))
        .thenReturn(Tasks.forException(exception))

    repositoryFirestore.removeFriend("testUser", "testFriend")

    // Verify only the first operation is attempted
    verify(mockUserDocument).update("allFriends.testFriend", FieldValue.delete())
    verify(mockFriendDocument, never()).update("allFriends.testUser", FieldValue.delete())
  }

  @Test
  fun `removeFriend logs error when removing from friend allFriends fails`(): Unit = runBlocking {
    // Mock success for removing from userId's allFriends
    `when`(mockUserDocument.update("allFriends.testFriend", FieldValue.delete()))
        .thenReturn(Tasks.forResult(null))

    // Mock failure for removing from friendId's allFriends
    val exception = Exception("Failed to remove from friend's allFriends")
    `when`(mockFriendDocument.update("allFriends.testUser", FieldValue.delete()))
        .thenReturn(Tasks.forException(exception))

    repositoryFirestore.removeFriend("testUser", "testFriend")

    // Verify operations up to the point of failure
    verify(mockUserDocument).update("allFriends.testFriend", FieldValue.delete())
    verify(mockFriendDocument).update("allFriends.testUser", FieldValue.delete())
  }

  @Test
  fun `getOwnRequests returns list of user IDs successfully`() = runBlocking {
    // Mock document snapshot containing ownRequests
    val mockOwnRequests = mapOf("testReceiver1" to true, "testReceiver2" to true)
    `when`(mockDocumentSnapshot.get("ownRequests")).thenReturn(mockOwnRequests)
    `when`(mockUserDocument.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))

    val result = repositoryFirestore.getOwnRequests("testUser")

    // Verify Firestore document is fetched
    verify(mockUserDocument).get()

    // Assert result matches the keys of the mock data
    assertEquals(listOf("testReceiver1", "testReceiver2"), result)
  }

  @Test
  fun `getOwnRequests returns empty list when no requests exist`() = runBlocking {
    // Mock document snapshot with no ownRequests field
    `when`(mockDocumentSnapshot.get("ownRequests")).thenReturn(null)
    `when`(mockUserDocument.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))

    val result = repositoryFirestore.getOwnRequests("testUser")

    // Verify Firestore document is fetched
    verify(mockUserDocument).get()

    // Assert result is an empty list
    assertTrue(result.isEmpty())
  }

  @Test
  fun `getOwnRequests returns empty list on exception`() = runBlocking {
    // Mock an exception during the Firestore operation
    val exception = Exception("Failed to fetch ownRequests")
    `when`(mockUserDocument.get()).thenReturn(Tasks.forException(exception))

    val result = repositoryFirestore.getOwnRequests("testUser")

    // Verify Firestore document is fetched
    verify(mockUserDocument).get()

    // Assert result is an empty list
    assertTrue(result.isEmpty())
  }

  @Test
  fun `getFriendRequests returns list of user IDs successfully`() = runBlocking {
    // Mock document snapshot containing friendRequests
    val mockFriendRequests = mapOf("testSender1" to true, "testSender2" to true)
    `when`(mockDocumentSnapshot.get("friendRequests")).thenReturn(mockFriendRequests)
    `when`(mockUserDocument.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))

    val result = repositoryFirestore.getFriendRequests("testUser")

    // Verify Firestore document is fetched
    verify(mockUserDocument).get()

    // Assert result matches the keys of the mock data
    assertEquals(listOf("testSender1", "testSender2"), result)
  }

  @Test
  fun `getFriendRequests returns empty list when no requests exist`() = runBlocking {
    // Mock document snapshot with no friendRequests field
    `when`(mockDocumentSnapshot.get("friendRequests")).thenReturn(null)
    `when`(mockUserDocument.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))

    val result = repositoryFirestore.getFriendRequests("testUser")

    // Verify Firestore document is fetched
    verify(mockUserDocument).get()

    // Assert result is an empty list
    assertTrue(result.isEmpty())
  }

  @Test
  fun `getFriendRequests returns empty list on exception`() = runBlocking {
    // Mock an exception during the Firestore operation
    val exception = Exception("Failed to fetch friendRequests")
    `when`(mockUserDocument.get()).thenReturn(Tasks.forException(exception))

    val result = repositoryFirestore.getFriendRequests("testUser")

    // Verify Firestore document is fetched
    verify(mockUserDocument).get()

    // Assert result is an empty list
    assertTrue(result.isEmpty())
  }

  @Test
  fun `getAllFriends returns list of friend IDs successfully`() = runBlocking {
    // Mock document snapshot containing allFriends
    val mockAllFriends =
        mapOf("friend1" to mapOf("status" to "linked"), "friend2" to mapOf("status" to "linked"))
    `when`(mockDocumentSnapshot.get("allFriends")).thenReturn(mockAllFriends)
    `when`(mockUserDocument.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))

    val result = repositoryFirestore.getAllFriends("testUser")

    // Verify Firestore document is fetched
    verify(mockUserDocument).get()

    // Assert result matches the keys of the mock data
    assertEquals(listOf("friend1", "friend2"), result)
  }

  @Test
  fun `getAllFriends returns empty list when no friends exist`() = runBlocking {
    // Mock document snapshot with no allFriends field
    `when`(mockDocumentSnapshot.get("allFriends")).thenReturn(null)
    `when`(mockUserDocument.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))

    val result = repositoryFirestore.getAllFriends("testUser")

    // Verify Firestore document is fetched
    verify(mockUserDocument).get()

    // Assert result is an empty list
    assertTrue(result.isEmpty())
  }

  @Test
  fun `getAllFriends returns empty list on exception`() = runBlocking {
    // Mock an exception during the Firestore operation
    val exception = Exception("Failed to fetch allFriends")
    `when`(mockUserDocument.get()).thenReturn(Tasks.forException(exception))

    val result = repositoryFirestore.getAllFriends("testUser")

    // Verify Firestore document is fetched
    verify(mockUserDocument).get()

    // Assert result is an empty list
    assertTrue(result.isEmpty())
  }
}
