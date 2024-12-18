package com.epfl.beatlink.ui.profile

import androidx.lifecycle.MutableLiveData
import com.epfl.beatlink.repository.profile.FriendRequestRepository
import com.epfl.beatlink.viewmodel.profile.FriendRequestViewModel
import org.mockito.Mockito.mock

class FakeFriendRequestViewModel(
    friendRequestRepository: FriendRequestRepository = mock(FriendRequestRepository::class.java)
) : FriendRequestViewModel(friendRequestRepository) {

  // Helper methods to set LiveData values manually for testing
  fun setOwnRequests(requests: List<String>) {
    (ownRequests as MutableLiveData).postValue(requests)
  }

  fun setFriendRequests(requests: List<String>) {
    (friendRequests as MutableLiveData).postValue(requests)
  }

  fun setAllFriends(friends: List<String>) {
    (allFriends as MutableLiveData).postValue(friends)
  }
}
