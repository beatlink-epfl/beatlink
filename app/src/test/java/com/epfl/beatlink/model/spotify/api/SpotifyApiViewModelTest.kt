package com.epfl.beatlink.model.spotify.api

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.json.JSONObject
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.stub

@OptIn(ExperimentalCoroutinesApi::class)
class SpotifyApiViewModelTest {

  @get:Rule val instantTaskExecutorRule = InstantTaskExecutorRule()

  private val testDispatcher = StandardTestDispatcher()

  @Mock private lateinit var mockApplication: Application

  @Mock private lateinit var mockApiRepository: SpotifyApiRepository

  private lateinit var viewModel: SpotifyApiViewModel

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    Dispatchers.setMain(testDispatcher)
    viewModel = SpotifyApiViewModel(mockApplication, mockApiRepository)
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain() // Reset the Main dispatcher after tests
  }

  @Test
  fun `fetchCurrentUserProfile calls repository and returns success result`() = runTest {
    // Arrange
    val mockResult = Result.success(JSONObject().apply { put("id", "12345") })
    mockApiRepository.stub { onBlocking { get("me") } doReturn mockResult }
    val observer = mock<Observer<Result<JSONObject>>>()

    // Act
    viewModel.fetchCurrentUserProfile { result -> observer.onChanged(result) }

    testDispatcher.scheduler.advanceUntilIdle() // Advance the dispatcher to process coroutines

    // Assert
    verify(observer).onChanged(mockResult)
    verify(mockApiRepository).get("me")
  }

  @Test
  fun `fetchCurrentUserProfile calls repository and returns failure result`() = runTest {
    // Arrange
    val exception = Exception("Network error")
    val mockResult = Result.failure<JSONObject>(exception)
    mockApiRepository.stub { onBlocking { get("me") } doReturn mockResult }
    val observer = mock<Observer<Result<JSONObject>>>()

    // Act
    viewModel.fetchCurrentUserProfile { result -> observer.onChanged(result) }

    testDispatcher.scheduler.advanceUntilIdle()

    // Assert
    verify(observer).onChanged(mockResult)
    verify(mockApiRepository).get("me")
  }

  @Test
  fun `pausePlayback calls repository and returns success result`() = runTest {
    // Arrange
    val mockResult = Result.success(JSONObject())
    mockApiRepository.stub { onBlocking { put("me/player/pause") } doReturn mockResult }
    val observer = mock<Observer<Result<JSONObject>>>()

    // Act
    viewModel.pausePlayback { result -> observer.onChanged(result) }

    testDispatcher.scheduler.advanceUntilIdle()

    // Assert
    verify(observer).onChanged(mockResult)
    verify(mockApiRepository).put("me/player/pause")
  }

  @Test
  fun `pausePlayback calls repository and returns failure result`() = runTest {
    // Arrange
    val exception = Exception("Network error")
    val mockResult = Result.failure<JSONObject>(exception)
    mockApiRepository.stub { onBlocking { put("me/player/pause") } doReturn mockResult }
    val observer = mock<Observer<Result<JSONObject>>>()

    // Act
    viewModel.pausePlayback { result -> observer.onChanged(result) }

    testDispatcher.scheduler.advanceUntilIdle()

    // Assert
    verify(observer).onChanged(mockResult)
    verify(mockApiRepository).put("me/player/pause")
  }
}
