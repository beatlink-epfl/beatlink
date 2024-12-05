package com.epfl.beatlink.viewmodel.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.epfl.beatlink.model.profile.FriendRequestRepository
import com.epfl.beatlink.repository.profile.FriendRequestRepositoryFirestore
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class FriendRequestViewModel(
    private val repository: FriendRequestRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

  private val _ownRequests = MutableLiveData<List<String>>(emptyList())
  val ownRequests: LiveData<List<String>>
    get() = _ownRequests

  private val _friendRequests = MutableLiveData<List<String>>(emptyList())
  val friendRequests: LiveData<List<String>>
    get() = _friendRequests

  private val _allFriends = MutableLiveData<List<String>>(emptyList())
  val allFriends: LiveData<List<String>>
    get() = _allFriends

  // Create factory
  companion object {
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val firebaseAuth = FirebaseAuth.getInstance()
            return FriendRequestViewModel(
                FriendRequestRepositoryFirestore(Firebase.firestore, firebaseAuth))
                as T
          }
        }
  }

  init {
    repository.init(onSuccess = { fetchInitialData() })
  }

  private fun fetchInitialData() {
    getOwnRequests()
    getFriendRequests()
    getAllFriends()
  }

  open fun sendFriendRequestTo(receiverId: String) {
    val senderId = repository.getUserId() ?: return
    viewModelScope.launch(dispatcher) {
      try {
        repository.sendFriendRequest(senderId, receiverId)
        _ownRequests.postValue(_ownRequests.value.orEmpty() + receiverId)
      } catch (e: Exception) {
        Log.e("FriendRequestViewModel", "Error sending friend request: ${e.message}")
      }
    }
  }

  open fun acceptFriendRequestFrom(senderId: String) {
    val receiverId = repository.getUserId() ?: return
    viewModelScope.launch(dispatcher) {
      try {
        repository.acceptFriendRequest(receiverId, senderId)
        _friendRequests.postValue(_friendRequests.value.orEmpty().filter { it != senderId })
        _allFriends.postValue(_allFriends.value.orEmpty() + senderId)
      } catch (e: Exception) {
        Log.e("FriendRequestViewModel", "Error accepting friend request: ${e.message}")
      }
    }
  }

  open fun rejectFriendRequestFrom(senderId: String) {
    val receiverId = repository.getUserId() ?: return
    viewModelScope.launch(dispatcher) {
      try {
        repository.rejectFriendRequest(receiverId, senderId)
        _friendRequests.postValue(_friendRequests.value.orEmpty().filter { it != senderId })
      } catch (e: Exception) {
        Log.e("FriendRequestViewModel", "Error rejecting friend request: ${e.message}")
      }
    }
  }

  open fun cancelFriendRequestTo(receiverId: String) {
    val senderId = repository.getUserId() ?: return
    viewModelScope.launch(dispatcher) {
      try {
        repository.cancelFriendRequest(senderId, receiverId)
        _ownRequests.postValue(_ownRequests.value.orEmpty().filter { it != receiverId })
      } catch (e: Exception) {
        Log.e("FriendRequestViewModel", "Error canceling friend request: ${e.message}")
      }
    }
  }

  open fun removeFriend(friendToRemove: String) {
    val userId = repository.getUserId() ?: return
    viewModelScope.launch(dispatcher) {
      try {
        repository.removeFriend(userId, friendToRemove)
        _allFriends.postValue(_allFriends.value.orEmpty().filter { it != friendToRemove })
      } catch (e: Exception) {
        Log.e("FriendRequestViewModel", "Error removing friend: ${e.message}")
      }
    }
  }

  /** Fetch the list of sent friend requests (ownRequests) for the current user. */
  fun getOwnRequests() {
    val userId = repository.getUserId() ?: return
    viewModelScope.launch(dispatcher) {
      try {
        val requests = repository.getOwnRequests(userId)
        _ownRequests.postValue(requests)
      } catch (e: Exception) {
        Log.e("FriendRequestViewModel", "Error fetching own requests: ${e.message}")
        _ownRequests.postValue(emptyList())
      }
    }
  }

  /** Fetch the list of received friend requests (friendRequests) for the current user. */
  fun getFriendRequests() {
    val userId = repository.getUserId() ?: return
    viewModelScope.launch(dispatcher) {
      try {
        val requests = repository.getFriendRequests(userId)
        _friendRequests.postValue(requests)
      } catch (e: Exception) {
        Log.e("FriendRequestViewModel", "Error fetching friend requests: ${e.message}")
        _friendRequests.postValue(emptyList())
      }
    }
  }

  /** Fetch the list of all friends for the current user. */
  fun getAllFriends() {
    val userId = repository.getUserId() ?: return
    viewModelScope.launch(dispatcher) {
      try {
        val friends = repository.getAllFriends(userId)
        _allFriends.postValue(friends)
      } catch (e: Exception) {
        Log.e("FriendRequestViewModel", "Error fetching friends: ${e.message}")
        _allFriends.postValue(emptyList())
      }
    }
  }
}
