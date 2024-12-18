package com.epfl.beatlink.repository.profile

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * A Firestore implementation of the FriendRequestRepository interface.
 *
 * @param db The Firestore database instance.
 * @param auth The Firebase authentication instance.
 */
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
      db.runTransaction { transaction ->
            // Reference to sender's document
            val senderRef = db.collection(collectionPath).document(senderId)

            // Reference to receiver's document
            val receiverRef = db.collection(collectionPath).document(receiverId)

            // Update sender's ownRequests
            transaction.update(senderRef, "ownRequests.$receiverId", true)

            // Update receiver's friendRequests
            transaction.update(receiverRef, "friendRequests.$senderId", true)
          }
          .await()
      Log.d("SEND_FRIEND_REQUEST", "Friend request sent successfully!")
    } catch (e: Exception) {
      Log.e("SEND_FRIEND_REQUEST", "Error sending friend request: ${e.message}")
    }
  }

  // Accept a friend request by moving it from "friendRequests" to "allFriends"
  override suspend fun acceptFriendRequest(receiverId: String, senderId: String) {
    try {
      db.runTransaction { transaction ->
            // References to the relevant documents
            val receiverRef = db.collection(collectionPath).document(receiverId)
            val senderRef = db.collection(collectionPath).document(senderId)

            // Remove from receiver's friendRequests
            transaction.update(receiverRef, "friendRequests.$senderId", FieldValue.delete())

            // Remove from sender's ownRequests
            transaction.update(senderRef, "ownRequests.$receiverId", FieldValue.delete())

            // Add to both users' allFriends
            transaction.update(receiverRef, "allFriends.$senderId", mapOf("status" to "linked"))
            transaction.update(senderRef, "allFriends.$receiverId", mapOf("status" to "linked"))
          }
          .await()
      Log.d("ACCEPT_FRIEND_REQUEST", "Friend request accepted successfully!")
    } catch (e: Exception) {
      Log.e("ACCEPT_FRIEND_REQUEST", "Error accepting friend request: ${e.message}")
    }
  }

  // Reject a friend request by removing it from "friendRequests"
  override suspend fun rejectFriendRequest(receiverId: String, senderId: String) {
    try {
      db.runTransaction { transaction ->
            // References to the relevant documents
            val receiverRef = db.collection(collectionPath).document(receiverId)
            val senderRef = db.collection(collectionPath).document(senderId)

            // Remove from receiver's friendRequests
            transaction.update(receiverRef, "friendRequests.$senderId", FieldValue.delete())

            // Remove from sender's ownRequests
            transaction.update(senderRef, "ownRequests.$receiverId", FieldValue.delete())
          }
          .await()
      Log.d("REJECT_FRIEND_REQUEST", "Friend request rejected successfully!")
    } catch (e: Exception) {
      Log.e("REJECT_FRIEND_REQUEST", "Error rejecting friend request: ${e.message}")
    }
  }

  // Cancel a sent friend request by removing it from "ownRequests"
  override suspend fun cancelFriendRequest(senderId: String, receiverId: String) {
    try {
      db.runTransaction { transaction ->
            // References to the relevant documents
            val senderRef = db.collection(collectionPath).document(senderId)
            val receiverRef = db.collection(collectionPath).document(receiverId)

            // Remove from sender's ownRequests
            transaction.update(senderRef, "ownRequests.$receiverId", FieldValue.delete())

            // Remove from receiver's friendRequests
            transaction.update(receiverRef, "friendRequests.$senderId", FieldValue.delete())
          }
          .await()
      Log.d("CANCEL_FRIEND_REQUEST", "Friend request canceled successfully!")
    } catch (e: Exception) {
      Log.e("CANCEL_FRIEND_REQUEST", "Error canceling friend request: ${e.message}")
    }
  }

  // Remove a friend by deleting from "allFriends"
  override suspend fun removeFriend(userId: String, friendId: String) {
    try {
      db.runTransaction { transaction ->
            // References to the relevant documents
            val userRef = db.collection(collectionPath).document(userId)
            val friendRef = db.collection(collectionPath).document(friendId)

            // Remove the friend from the user's allFriends
            transaction.update(userRef, "allFriends.$friendId", FieldValue.delete())

            // Remove the user from the friend's allFriends
            transaction.update(friendRef, "allFriends.$userId", FieldValue.delete())
          }
          .await()
      Log.d("REMOVE_FRIEND", "Friend removed successfully!")
    } catch (e: Exception) {
      Log.e("REMOVE_FRIEND", "Error removing friend: ${e.message}")
    }
  }

  // Get the list of sent requests (ownRequests)
  override suspend fun getOwnRequests(userId: String): List<String> {
    return try {
      val document = db.collection(collectionPath).document(userId).get().await()
      val ownRequests = document.data?.get("ownRequests") as? Map<*, *>

      // Safely process the map
      ownRequests
          ?.filterKeys { it is String }
          ?.filterValues { it is Boolean && it }
          ?.keys
          ?.map { it as String } ?: emptyList()
    } catch (e: Exception) {
      Log.e("GET_OWN_REQUESTS", "Error fetching own requests: ${e.message}")
      emptyList()
    }
  }

  // Get the list of received friend requests (friendRequests)
  override suspend fun getFriendRequests(userId: String): List<String> {
    return try {
      val document = db.collection(collectionPath).document(userId).get().await()
      val friendRequests = document.data?.get("friendRequests") as? Map<*, *>

      // Safely process the map
      friendRequests
          ?.filterKeys { it is String }
          ?.filterValues { it is Boolean && it }
          ?.keys
          ?.map { it as String } ?: emptyList()
    } catch (e: Exception) {
      Log.e("GET_FRIEND_REQUESTS", "Error fetching friend requests: ${e.message}")
      emptyList()
    }
  }

  // Get the list of friends (allFriends)
  override suspend fun getAllFriends(userId: String): List<String> {
    return try {
      val document = db.collection(collectionPath).document(userId).get().await()
      val allFriends = document.data?.get("allFriends") as? Map<*, *>

      // Safely process the map
      allFriends
          ?.filterKeys { it is String }
          ?.filterValues { it is Map<*, *> }
          ?.keys
          ?.map { it as String } ?: emptyList()
    } catch (e: Exception) {
      Log.e("GET_ALL_FRIENDS", "Error fetching all friends: ${e.message}")
      emptyList()
    }
  }
}
