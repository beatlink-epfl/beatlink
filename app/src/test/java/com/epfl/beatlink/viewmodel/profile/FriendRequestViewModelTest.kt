package com.epfl.beatlink.viewmodel.profile

import android.content.Context
import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.epfl.beatlink.repository.profile.FriendRequestRepositoryFirestore
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.eq


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

    
}