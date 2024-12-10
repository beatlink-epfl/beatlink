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
import com.google.firebase.firestore.Transaction
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

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
  private lateinit var mockTransaction: Transaction

  private lateinit var mockQuerySnapshot: QuerySnapshot
  private lateinit var mockDocumentSnapshot: DocumentSnapshot

  private val collectionPath = "friendRequests"

  @Before
  fun setUp() {
    // Initialize mocks
    mockDb = mock(FirebaseFirestore::class.java)
    mockAuth = mock(FirebaseAuth::class.java)
    mockUser = mock(FirebaseUser::class.java)

    mockCollectionReference = mock(CollectionReference::class.java)
    mockSenderDocument = mock(DocumentReference::class.java)
    mockReceiverDocument = mock(DocumentReference::class.java)
    mockUserDocument = mock(DocumentReference::class.java)
    mockFriendDocument = mock(DocumentReference::class.java)
    mockTransaction = mock(Transaction::class.java)

    mockQuerySnapshot = mock(QuerySnapshot::class.java)
    mockDocumentSnapshot = mock(DocumentSnapshot::class.java)

    // Mock Firestore collection
    `when`(mockDb.collection(collectionPath)).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document("testSender")).thenReturn(mockSenderDocument)
    `when`(mockCollectionReference.document("testReceiver")).thenReturn(mockReceiverDocument)
    `when`(mockCollectionReference.document("testUser")).thenReturn(mockUserDocument)
    `when`(mockCollectionReference.document("testFriend")).thenReturn(mockFriendDocument)
    `when`(mockDb.runTransaction<Transaction>(any())).thenAnswer { invocation ->
      val transactionFunction = invocation.arguments[0] as Transaction.Function<*>
      transactionFunction.apply(mockTransaction)
      Tasks.forResult(null)
    }

    repositoryFirestore = FriendRequestRepositoryFirestore(mockDb, mockAuth)
  }

  // User authentication tests
  @Test
  fun `getUserId returns user ID when user is authenticated`() {
    `when`(mockAuth.currentUser).thenReturn(mockUser)
    `when`(mockUser.uid).thenReturn("testUserId")

    val userId = repositoryFirestore.getUserId()

    assertEquals("testUserId", userId)
  }

  @Test
  fun `getUserId returns null when user is not authenticated`() {
    `when`(mockAuth.currentUser).thenReturn(null)

    val userId = repositoryFirestore.getUserId()

    assertEquals(null, userId)
  }

  @Test
  fun `sendFriendRequest successfully sends request and updates fields`(): Unit = runBlocking {
    // Call the method under test
    repositoryFirestore.sendFriendRequest("testSender", "testReceiver")

    // Verify the updates were made on the sender and receiver documents
    verify(mockTransaction).update(mockSenderDocument, "ownRequests.testReceiver", true)
    verify(mockTransaction).update(mockReceiverDocument, "friendRequests.testSender", true)
  }

  @Test
  fun `acceptFriendRequest completes successfully`(): Unit = runBlocking {
    // Mock successful updates
    `when`(mockReceiverDocument.update(anyString(), any())).thenReturn(Tasks.forResult(null))
    `when`(mockSenderDocument.update(anyString(), any())).thenReturn(Tasks.forResult(null))

    repositoryFirestore.acceptFriendRequest("testReceiver", "testSender")

    // Verify all Firestore updates are called with correct arguments
    verify(mockTransaction).update(mockReceiverDocument, "friendRequests.testSender", FieldValue.delete())
    verify(mockTransaction).update(mockSenderDocument, "ownRequests.testReceiver", FieldValue.delete())
    verify(mockTransaction).update(mockReceiverDocument, "allFriends.testSender", mapOf("status" to "linked"))
    verify(mockTransaction).update(mockSenderDocument, "allFriends.testReceiver", mapOf("status" to "linked"))
  }

  @Test
  fun `rejectFriendRequest completes successfully`(): Unit = runBlocking {
    // Mock successful updates
    `when`(mockReceiverDocument.update(anyString(), any())).thenReturn(Tasks.forResult(null))
    `when`(mockSenderDocument.update(anyString(), any())).thenReturn(Tasks.forResult(null))

    repositoryFirestore.rejectFriendRequest("testReceiver", "testSender")

    // Verify Firestore updates are called with correct arguments

    verify(mockTransaction).update(mockReceiverDocument, "friendRequests.testSender", FieldValue.delete())
    verify(mockTransaction).update(mockSenderDocument, "ownRequests.testReceiver", FieldValue.delete())
  }

  @Test
  fun `cancelFriendRequest completes successfully`(): Unit = runBlocking {
    // Mock successful updates
    `when`(mockSenderDocument.update(anyString(), any())).thenReturn(Tasks.forResult(null))
    `when`(mockReceiverDocument.update(anyString(), any())).thenReturn(Tasks.forResult(null))

    repositoryFirestore.cancelFriendRequest("testSender", "testReceiver")

    // Verify Firestore updates are called with correct arguments
    verify(mockTransaction).update(mockSenderDocument, "ownRequests.testReceiver", FieldValue.delete())
    verify(mockTransaction).update(mockReceiverDocument, "friendRequests.testSender", FieldValue.delete())
  }

  @Test
  fun `removeFriend completes successfully`(): Unit = runBlocking {
    // Mock successful updates
    `when`(mockUserDocument.update(anyString(), any())).thenReturn(Tasks.forResult(null))
    `when`(mockFriendDocument.update(anyString(), any())).thenReturn(Tasks.forResult(null))

    repositoryFirestore.removeFriend("testUser", "testFriend")

    // Verify Firestore updates are called with correct arguments
    verify(mockTransaction).update(mockUserDocument, "allFriends.testFriend", FieldValue.delete())
    verify(mockTransaction).update(mockFriendDocument, "allFriends.testUser", FieldValue.delete())
  }

  // getOwnRequests tests
  @Test
  fun `getOwnRequests returns list of user IDs successfully`() = runBlocking {
    val mockOwnRequests = mapOf("testReceiver1" to true, "testReceiver2" to true)
    `when`(mockDocumentSnapshot.data).thenReturn(mapOf("ownRequests" to mockOwnRequests))
    `when`(mockUserDocument.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))

    val result = repositoryFirestore.getOwnRequests("testUser")

    verify(mockUserDocument).get()
    assertEquals(listOf("testReceiver1", "testReceiver2"), result)
  }

  @Test
  fun `getOwnRequests returns empty list when no requests exist`() = runBlocking {
    `when`(mockDocumentSnapshot.get("ownRequests")).thenReturn(null)
    `when`(mockUserDocument.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))

    val result = repositoryFirestore.getOwnRequests("testUser")

    verify(mockUserDocument).get()
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
    `when`(mockDocumentSnapshot.data).thenReturn(mapOf("friendRequests" to mockFriendRequests))
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
    `when`(mockDocumentSnapshot.data).thenReturn(mapOf("allFriends" to mockAllFriends))
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
