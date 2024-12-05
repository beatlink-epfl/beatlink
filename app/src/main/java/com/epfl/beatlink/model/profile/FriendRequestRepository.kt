package com.epfl.beatlink.model.profile

interface FriendRequestRepository {


  fun init(onSuccess: () -> Unit)

  /**
   * Retrieves the user ID of the currently logged-in user.
   *
   * @return The user ID of the current user, or null if no user is logged in.
   */
  fun getUserId(): String?

  /**
   * Sends a friend request from the sender to the receiver.
   *
   * @param senderId The ID of the user initiating the friend request.
   * @param receiverId The ID of the user receiving the friend request.
   * @throws Exception If the operation fails (e.g., Firestore errors).
   */
  suspend fun sendFriendRequest(senderId: String, receiverId: String)

  /**
   * Accepts a friend request by moving the relationship from "friendRequests" to "allFriends".
   *
   * @param receiverId The ID of the user accepting the friend request (the receiver).
   * @param senderId The ID of the user who sent the friend request (the sender).
   * @throws Exception If the operation fails (e.g., Firestore errors).
   */
  suspend fun acceptFriendRequest(receiverId: String, senderId: String)

  /**
   * Rejects a friend request by removing it from the "friendRequests" of the receiver and the
   * "ownRequests" of the sender.
   *
   * @param receiverId The ID of the user rejecting the friend request (the receiver).
   * @param senderId The ID of the user who sent the friend request (the sender).
   * @throws Exception If the operation fails (e.g., Firestore errors).
   */
  suspend fun rejectFriendRequest(receiverId: String, senderId: String)

  /**
   * Cancels a friend request that the sender has sent, removing it from the "ownRequests" of the
   * sender and the "friendRequests" of the receiver.
   *
   * @param senderId The ID of the user canceling the friend request (the sender).
   * @param receiverId The ID of the user who was intended to receive the friend request.
   * @throws Exception If the operation fails (e.g., Firestore errors).
   */
  suspend fun cancelFriendRequest(senderId: String, receiverId: String)

  /**
   * Removes a friendship between two users by deleting the relationship from the "allFriends" map
   * of both users.
   *
   * @param userId The ID of the user initiating the removal (either friend can remove).
   * @param friendId The ID of the friend being removed.
   * @throws Exception If the operation fails (e.g., Firestore errors).
   */
  suspend fun removeFriend(userId: String, friendId: String)

  /**
   * Fetches the list of user IDs to whom the specified user has sent friend requests (ownRequests).
   *
   * @param userId The ID of the user whose sent requests need to be fetched.
   * @return A list of user IDs to whom the user has sent friend requests. Returns an empty list if
   *   no sent requests exist or if an error occurs.
   * @throws Exception If there is an issue retrieving data from Firestore.
   */
  suspend fun getOwnRequests(userId: String): List<String>

  /**
   * Fetches the list of user IDs from whom the specified user has received friend requests
   * (friendRequests).
   *
   * @param userId The ID of the user whose received friend requests need to be fetched.
   * @return A list of user IDs who have sent friend requests to the user. Returns an empty list if
   *   no received requests exist or if an error occurs.
   * @throws Exception If there is an issue retrieving data from Firestore.
   */
  suspend fun getFriendRequests(userId: String): List<String>

  /**
   * Fetches the list of user IDs who are friends with the specified user (allFriends).
   *
   * @param userId The ID of the user whose friends need to be fetched.
   * @return A list of user IDs who are friends with the user. Returns an empty list if no friends
   *   exist or if an error occurs.
   * @throws Exception If there is an issue retrieving data from Firestore.
   */
  suspend fun getAllFriends(userId: String): List<String>
}
