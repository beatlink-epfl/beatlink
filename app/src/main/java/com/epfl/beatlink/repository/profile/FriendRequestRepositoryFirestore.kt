package com.epfl.beatlink.repository.profile

import android.content.ContentValues.TAG
import android.util.Log
import com.epfl.beatlink.model.profile.FriendRequestRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

open class FriendRequestRepositoryFirestore(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : FriendRequestRepository {
  private val collectionPath = "friendRequests"

  override fun init(onSuccess: () -> Unit) {
    auth.addAuthStateListener { firebaseAuth ->
      val currentUser = firebaseAuth.currentUser
      if (currentUser != null) {
        // User is authenticated, proceed with onSuccess
        onSuccess()
      } else {
        Log.d(TAG, "User not authenticated")
      }
    }
  }

  override fun getUserId(): String? {
    val userId = auth.currentUser?.uid
    Log.d("AUTH", "Current user ID: $userId")
    return userId
  }

  // Add a request to the "ownRequests" of the sender and "friendRequests" of the receiver
  override suspend fun sendFriendRequest(senderId: String, receiverId: String) {
    try {
      // Add to sender's ownRequests
      db.collection(collectionPath)
          .document(senderId)
          .update("ownRequests.$receiverId", true) // Use `true` to denote the UID exists
          .await()

      // Add to receiver's friendRequests
      db.collection(collectionPath)
          .document(receiverId)
          .update("friendRequests.$senderId", true)
          .await()

      Log.d("SEND_FRIEND_REQUEST", "Friend request sent successfully!")
    } catch (e: Exception) {
      Log.e("SEND_FRIEND_REQUEST", "Error sending friend request: ${e.message}")
    }
  }

  // Accept a friend request by moving it from "friendRequests" to "allFriends"
  override suspend fun acceptFriendRequest(receiverId: String, senderId: String) {
    try {
      // Remove from receiver's friendRequests
      db.collection(collectionPath)
          .document(receiverId)
          .update("friendRequests.$senderId", FieldValue.delete())
          .await()

      // Remove from sender's ownRequests
      db.collection(collectionPath)
          .document(senderId)
          .update("ownRequests.$receiverId", FieldValue.delete())
          .await()

      // Add to both users' allFriends
      db.collection(collectionPath)
          .document(receiverId)
          .update("allFriends.$senderId", mapOf("status" to "linked"))
          .await()

      db.collection(collectionPath)
          .document(senderId)
          .update("allFriends.$receiverId", mapOf("status" to "linked"))
          .await()

      Log.d("ACCEPT_FRIEND_REQUEST", "Friend request accepted successfully!")
    } catch (e: Exception) {
      Log.e("ACCEPT_FRIEND_REQUEST", "Error accepting friend request: ${e.message}")
    }
  }

  // Reject a friend request by removing it from "friendRequests"
  override suspend fun rejectFriendRequest(receiverId: String, senderId: String) {
    try {
      // Remove from receiver's friendRequests
      db.collection(collectionPath)
          .document(receiverId)
          .update("friendRequests.$senderId", FieldValue.delete())
          .await()

      // Remove from sender's ownRequests
      db.collection(collectionPath)
          .document(senderId)
          .update("ownRequests.$receiverId", FieldValue.delete())
          .await()

      println("Friend request rejected successfully!")
    } catch (e: Exception) {
      println("Error rejecting friend request: ${e.message}")
    }
  }

  // Cancel a sent friend request by removing it from "ownRequests"
  override suspend fun cancelFriendRequest(senderId: String, receiverId: String) {
    try {
      // Remove from sender's ownRequests
      db.collection(collectionPath)
          .document(senderId)
          .update("ownRequests.$receiverId", FieldValue.delete())
          .await()

      // Remove from receiver's friendRequests
      db.collection(collectionPath)
          .document(receiverId)
          .update("friendRequests.$senderId", FieldValue.delete())
          .await()

      println("Friend request canceled successfully!")
    } catch (e: Exception) {
      println("Error canceling friend request: ${e.message}")
    }
  }

  // Remove a friend by deleting from "allFriends"
  override suspend fun removeFriend(userId: String, friendId: String) {
    try {
      // Remove from user's allFriends
      db.collection(collectionPath)
          .document(userId)
          .update("allFriends.$friendId", FieldValue.delete())
          .await()

      // Remove from friend's allFriends
      db.collection(collectionPath)
          .document(friendId)
          .update("allFriends.$userId", FieldValue.delete())
          .await()

      println("Friend removed successfully!")
    } catch (e: Exception) {
      println("Error removing friend: ${e.message}")
    }
  }

  // Get the list of sent requests (ownRequests)
  override suspend fun getOwnRequests(userId: String): List<String> {
    return try {
      val document = db.collection(collectionPath).document(userId).get().await()
      val ownRequests = document.get("ownRequests") as? Map<String, Boolean>
      ownRequests?.keys?.toList() ?: emptyList()
    } catch (e: Exception) {
      println("Error fetching own requests: ${e.message}")
      emptyList()
    }
  }

  // Get the list of received friend requests (friendRequests)
  override suspend fun getFriendRequests(userId: String): List<String> {
    return try {
      val document = db.collection(collectionPath).document(userId).get().await()
      val friendRequests = document.get("friendRequests") as? Map<String, Boolean>
      friendRequests?.keys?.toList() ?: emptyList()
    } catch (e: Exception) {
      println("Error fetching friend requests: ${e.message}")
      emptyList()
    }
  }

  // Get the list of friends (allFriends)
  override suspend fun getAllFriends(userId: String): List<String> {
    return try {
      val document = db.collection(collectionPath).document(userId).get().await()
      val allFriends = document.get("allFriends") as? Map<String, Map<String, String>>
      allFriends?.keys?.toList() ?: emptyList()
    } catch (e: Exception) {
      println("Error fetching friends: ${e.message}")
      emptyList()
    }
  }
}
