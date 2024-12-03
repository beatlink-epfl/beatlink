package com.epfl.beatlink.model.profile

data class FriendRequest(
    val ownRequests: Map<String, Boolean> = emptyMap(),
    val friendRequests: Map<String, Boolean> = emptyMap(),
    val allFriends: Map<String, FriendDetails> = emptyMap()
)

data class FriendDetails(val status: String)
