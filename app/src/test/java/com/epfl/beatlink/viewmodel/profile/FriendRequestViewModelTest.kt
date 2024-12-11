package com.epfl.beatlink.viewmodel.profile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.epfl.beatlink.repository.profile.FriendRequestRepositoryFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@OptIn(ExperimentalCoroutinesApi::class)
class FriendRequestViewModelTest {

  @get:Rule
  val instantTaskExecutorRule = InstantTaskExecutorRule() // Rule to allow LiveData testing

  private lateinit var friendRequestViewModel: FriendRequestViewModel
  private lateinit var mockRepository: FriendRequestRepositoryFirestore

  private lateinit var mockObserverOwnRequests: Observer<List<String>>
  private lateinit var mockObserverFriendRequests: Observer<List<String>>
  private lateinit var mockObserverAllFriends: Observer<List<String>>

  @OptIn(ExperimentalCoroutinesApi::class)
  @Before
  fun setUp() {
    // Setup the test coroutine dispatcher
    Dispatchers.setMain(StandardTestDispatcher())

    // Create mock repository
    mockRepository = mock(FriendRequestRepositoryFirestore::class.java)

    // Create the ViewModel with the mocked repository
    friendRequestViewModel = FriendRequestViewModel(mockRepository)

    mockObserverOwnRequests = mock()
    mockObserverFriendRequests = mock()
    mockObserverAllFriends = mock()

    friendRequestViewModel.ownRequests.observeForever(mockObserverOwnRequests)
    friendRequestViewModel.friendRequests.observeForever(mockObserverFriendRequests)
    friendRequestViewModel.allFriends.observeForever(mockObserverAllFriends)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @After
  fun tearDown() {
    Dispatchers.resetMain() // Reset dispatcher to avoid affecting other tests
    friendRequestViewModel.ownRequests.removeObserver(mockObserverOwnRequests)
    friendRequestViewModel.friendRequests.removeObserver(mockObserverFriendRequests)
    friendRequestViewModel.allFriends.removeObserver(mockObserverAllFriends)
  }

  @Test
  fun sendFriendRequestToIsSuccessful(): Unit = runTest {
    val userId = "testUserId"
    val receiverId = "testReceiver"
    val newOwnRequests = listOf(receiverId)

    `when`(mockRepository.getUserId()).thenReturn(userId)
    `when`(mockRepository.sendFriendRequest(userId, receiverId)).thenReturn(null)

    friendRequestViewModel.sendFriendRequestTo(receiverId)
    advanceUntilIdle()

    val actualOwnRequests = friendRequestViewModel.ownRequests.value
    assertEquals(newOwnRequests, actualOwnRequests)
  }

  @Test
  fun sendFriendRequestToFails(): Unit = runTest {
    val userId = "testUserId"
    val receiverId = "testReceiver"

    `when`(mockRepository.getUserId()).thenReturn(userId)
    `when`(mockRepository.sendFriendRequest(userId, receiverId))
        .thenThrow(RuntimeException("Failed to send friend request"))

    friendRequestViewModel.sendFriendRequestTo(receiverId)
    advanceUntilIdle()

    val actualOwnRequests = friendRequestViewModel.ownRequests.value
    assertEquals(emptyList<String>(), actualOwnRequests)
  }

  @Test
  fun acceptFriendRequestFromIsSuccessful(): Unit = runTest {
    val userId = "testUserId"
    val senderId = "testSender"
    val updatedFriendRequests = emptyList<String>()
    val updatedAllFriends = listOf(senderId)

    `when`(mockRepository.getUserId()).thenReturn(userId)
    `when`(mockRepository.acceptFriendRequest(userId, senderId)).thenReturn(null)

    friendRequestViewModel.getFriendRequests()
    friendRequestViewModel.getAllFriends()

    friendRequestViewModel.acceptFriendRequestFrom(senderId)
    advanceUntilIdle()

    assertEquals(updatedFriendRequests, friendRequestViewModel.friendRequests.value)
    assertEquals(updatedAllFriends, friendRequestViewModel.allFriends.value)
  }

  @Test
  fun acceptFriendRequestFromFails(): Unit = runTest {
    val userId = "testUserId"
    val senderId = "testSender"
    val initialFriendRequests = listOf(senderId)
    val expectedFriendRequests = listOf(senderId) // Should remain unchanged on failure

    `when`(mockRepository.getUserId()).thenReturn(userId)
    `when`(mockRepository.acceptFriendRequest(userId, senderId))
        .thenThrow(RuntimeException("Failed to reject friend request"))

    (friendRequestViewModel.friendRequests as MutableLiveData).value = initialFriendRequests

    friendRequestViewModel.acceptFriendRequestFrom(senderId)
    advanceUntilIdle() // Wait for coroutine completion

    val actualFriendRequests = friendRequestViewModel.friendRequests.value
    assertEquals(expectedFriendRequests, actualFriendRequests)
  }

  @Test
  fun rejectFriendRequestToIsSuccessful(): Unit = runTest {
    val userId = "testUserId"
    val senderId = "testSender"
    val expectedFriendRequests = emptyList<String>()

    `when`(mockRepository.getUserId()).thenReturn(userId)
    `when`(mockRepository.rejectFriendRequest(userId, senderId)).thenReturn(null)

    friendRequestViewModel.rejectFriendRequestFrom(senderId)
    advanceUntilIdle() // Wait for coroutine completion

    val actualFriendRequests = friendRequestViewModel.friendRequests.value
    assertEquals(expectedFriendRequests, actualFriendRequests)
  }

  @Test
  fun rejectFriendRequestFromFails(): Unit = runTest {
    val userId = "testUserId"
    val senderId = "testSender"
    val initialFriendRequests = listOf(senderId)
    val expectedFriendRequests = listOf(senderId) // Should remain unchanged on failure

    `when`(mockRepository.getUserId()).thenReturn(userId)
    `when`(mockRepository.rejectFriendRequest(userId, senderId))
        .thenThrow(RuntimeException("Failed to reject friend request"))

    (friendRequestViewModel.friendRequests as MutableLiveData).value = initialFriendRequests

    friendRequestViewModel.rejectFriendRequestFrom(senderId)
    advanceUntilIdle() // Wait for coroutine completion

    val actualFriendRequests = friendRequestViewModel.friendRequests.value
    assertEquals(expectedFriendRequests, actualFriendRequests)
  }

  @Test
  fun cancelFriendRequestToIsSuccessful(): Unit = runTest {
    val userId = "testUserId"
    val receiverId = "testReceiver"
    val expectedOwnRequests = emptyList<String>()

    `when`(mockRepository.getUserId()).thenReturn(userId)
    `when`(mockRepository.cancelFriendRequest(userId, receiverId)).thenReturn(null)

    friendRequestViewModel.cancelFriendRequestTo(receiverId)
    advanceUntilIdle() // Wait for coroutine completion

    val actualOwnRequests = friendRequestViewModel.ownRequests.value
    assertEquals(expectedOwnRequests, actualOwnRequests)
  }

  @Test
  fun cancelFriendRequestToFails(): Unit = runTest {
    val userId = "testUserId"
    val receiverId = "testReceiver"
    val initialOwnRequests = listOf(receiverId)
    val expectedOwnRequests = listOf(receiverId) // Should remain unchanged on failure

    `when`(mockRepository.getUserId()).thenReturn(userId)
    `when`(mockRepository.cancelFriendRequest(userId, receiverId))
        .thenThrow(RuntimeException("Failed to cancel friend request"))

    (friendRequestViewModel.ownRequests as MutableLiveData).value = initialOwnRequests

    friendRequestViewModel.cancelFriendRequestTo(receiverId)
    advanceUntilIdle() // Wait for coroutine completion

    // Assert
    val actualOwnRequests = friendRequestViewModel.ownRequests.value
    assertEquals(expectedOwnRequests, actualOwnRequests)
  }

  @Test
  fun removeFriendIsSuccessful(): Unit = runTest {
    val userId = "testUserId"
    val friendToRemove = "testFriend"
    val initialFriends = listOf("testFriend", "otherFriend")
    val expectedFriends = listOf("otherFriend")

    `when`(mockRepository.getUserId()).thenReturn(userId)
    `when`(mockRepository.removeFriend(userId, friendToRemove)).thenReturn(null)

    (friendRequestViewModel.allFriends as MutableLiveData).value = initialFriends

    friendRequestViewModel.removeFriend(friendToRemove)
    advanceUntilIdle() // Wait for coroutine completion

    val actualFriends = friendRequestViewModel.allFriends.value
    assertEquals(expectedFriends, actualFriends)
  }

  @Test
  fun removeFriendFails(): Unit = runTest {
    val userId = "testUserId"
    val friendToRemove = "testFriend"
    val initialFriends = listOf("testFriend", "otherFriend")
    val expectedFriends = initialFriends // Should remain unchanged on failure

    `when`(mockRepository.getUserId()).thenReturn(userId)
    `when`(mockRepository.removeFriend(userId, friendToRemove))
        .thenThrow(RuntimeException("Failed to remove friend"))

    (friendRequestViewModel.allFriends as MutableLiveData).value = initialFriends

    friendRequestViewModel.removeFriend(friendToRemove)
    advanceUntilIdle() // Wait for coroutine completion

    val actualFriends = friendRequestViewModel.allFriends.value
    assertEquals(expectedFriends, actualFriends)
  }

  @Test
  fun getOwnRequestsIsSuccessful(): Unit = runTest {
    val userId = "testUserId"
    val expectedRequests = listOf("request1", "request2")

    `when`(mockRepository.getUserId()).thenReturn(userId)
    `when`(mockRepository.getOwnRequests(userId)).thenReturn(expectedRequests)

    friendRequestViewModel.getOwnRequests()
    advanceUntilIdle() // Wait for coroutine completion

    val actualRequests = friendRequestViewModel.ownRequests.value
    assertEquals(expectedRequests, actualRequests)
  }

  @Test
  fun getOwnRequestsFails(): Unit = runTest {
    val userId = "testUserId"

    `when`(mockRepository.getUserId()).thenReturn(userId)
    `when`(mockRepository.getOwnRequests(userId))
        .thenThrow(RuntimeException("Failed to fetch own requests"))

    friendRequestViewModel.getOwnRequests()
    advanceUntilIdle() // Wait for coroutine completion

    val actualRequests = friendRequestViewModel.ownRequests.value
    assertEquals(emptyList<String>(), actualRequests)
  }

  @Test
  fun getFriendRequestsIsSuccessful(): Unit = runTest {
    val userId = "testUserId"
    val expectedRequests = listOf("request1", "request2")

    `when`(mockRepository.getUserId()).thenReturn(userId)
    `when`(mockRepository.getFriendRequests(userId)).thenReturn(expectedRequests)

    friendRequestViewModel.getFriendRequests()
    advanceUntilIdle() // Wait for coroutine completion

    val actualRequests = friendRequestViewModel.friendRequests.value
    assertEquals(expectedRequests, actualRequests)
  }

  @Test
  fun getFriendRequestsFails(): Unit = runTest {
    val userId = "testUserId"

    `when`(mockRepository.getUserId()).thenReturn(userId)
    `when`(mockRepository.getFriendRequests(userId))
        .thenThrow(RuntimeException("Failed to fetch friend requests"))

    friendRequestViewModel.getFriendRequests()
    advanceUntilIdle() // Wait for coroutine completion

    val actualRequests = friendRequestViewModel.friendRequests.value
    assertEquals(emptyList<String>(), actualRequests)
  }

  @Test
  fun getAllFriendsIsSuccessful(): Unit = runTest {
    val userId = "testUserId"
    val expectedFriends = listOf("friend1", "friend2", "friend3")

    `when`(mockRepository.getUserId()).thenReturn(userId)
    `when`(mockRepository.getAllFriends(userId)).thenReturn(expectedFriends)

    friendRequestViewModel.getAllFriends()
    advanceUntilIdle() // Wait for coroutine completion

    val actualFriends = friendRequestViewModel.allFriends.value
    assertEquals(expectedFriends, actualFriends)
  }

  @Test
  fun getAllFriendsFails(): Unit = runTest {
    val userId = "testUserId"

    `when`(mockRepository.getUserId()).thenReturn(userId)
    `when`(mockRepository.getAllFriends(userId))
        .thenThrow(RuntimeException("Failed to fetch friends"))

    friendRequestViewModel.getAllFriends()
    advanceUntilIdle() // Wait for coroutine completion

    val actualFriends = friendRequestViewModel.allFriends.value
    assertEquals(emptyList<String>(), actualFriends)
  }

  @Test
  fun getOtherProfileAllFriendsIsSuccessful(): Unit = runTest {
    // Arrange
    val otherProfileId = "otherProfileId"
    val expectedFriends = listOf("friend1", "friend2", "friend3")

    `when`(mockRepository.getAllFriends(otherProfileId)).thenReturn(expectedFriends)

    var onCompleteCalled = false
    val onComplete = { onCompleteCalled = true }

    // Act
    friendRequestViewModel.getOtherProfileAllFriends(otherProfileId, onComplete)
    advanceUntilIdle() // Wait for coroutine completion

    // Assert
    val actualFriends = friendRequestViewModel.otherProfileAllFriends.value
    assertEquals(expectedFriends, actualFriends)
    assertTrue(onCompleteCalled)
  }

  @Test
  fun getOtherProfileAllFriendsReturnsEmptyList(): Unit = runTest {
    // Arrange
    val otherProfileId = "otherProfileId"
    val expectedFriends = emptyList<String>()

    `when`(mockRepository.getAllFriends(otherProfileId)).thenReturn(expectedFriends)

    var onCompleteCalled = false
    val onComplete = { onCompleteCalled = true }

    // Act
    friendRequestViewModel.getOtherProfileAllFriends(otherProfileId, onComplete)
    advanceUntilIdle() // Wait for coroutine completion

    // Assert
    val actualFriends = friendRequestViewModel.otherProfileAllFriends.value
    assertEquals(expectedFriends, actualFriends)
    assertTrue(onCompleteCalled)
  }

}
