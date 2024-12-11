package com.epfl.beatlink.viewmodel.network

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.epfl.beatlink.repository.network.NetworkStatusTracker
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class NetworkViewModelTest {

  // Rule to execute tasks synchronously
  @get:Rule val instantTaskExecutorRule = InstantTaskExecutorRule()

  @Test
  fun `isConnected reflects the value from NetworkStatusTracker`() {
    // Arrange
    val mockNetworkStatusTracker = mock(NetworkStatusTracker::class.java)
    val mockLiveData = MutableLiveData<Boolean>()
    `when`(mockNetworkStatusTracker.isConnected).thenReturn(mockLiveData)

    val viewModel = NetworkViewModel(mockNetworkStatusTracker)

    // Act
    mockLiveData.postValue(true)

    // Assert
    assertEquals(true, viewModel.isConnected.value)

    // Act again
    mockLiveData.postValue(false)

    // Assert
    assertEquals(false, viewModel.isConnected.value)

    // Verify interactions with the mock
    verify(mockNetworkStatusTracker, times(1)).isConnected
  }
}
