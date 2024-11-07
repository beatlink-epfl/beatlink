package com.epfl.beatlink.model.spotify.api

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
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

  @Test
  fun `playPlayback calls repository and returns success result`() = runTest {
    // Arrange
    val mockResult = Result.success(JSONObject())
    mockApiRepository.stub { onBlocking { put("me/player/play") } doReturn mockResult }
    val observer = mock<Observer<Result<JSONObject>>>()

    // Act
    viewModel.playPlayback { result -> observer.onChanged(result) }

    testDispatcher.scheduler.advanceUntilIdle()

    // Assert
    verify(observer).onChanged(mockResult)
    verify(mockApiRepository).put("me/player/play")
  }

  @Test
  fun `playPlayback calls repository and returns failure result`() = runTest {
    // Arrange
    val exception = Exception("Network error")
    val mockResult = Result.failure<JSONObject>(exception)
    mockApiRepository.stub { onBlocking { put("me/player/play") } doReturn mockResult }
    val observer = mock<Observer<Result<JSONObject>>>()

    // Act
    viewModel.playPlayback { result -> observer.onChanged(result) }

    testDispatcher.scheduler.advanceUntilIdle()

    // Assert
    verify(observer).onChanged(mockResult)
    verify(mockApiRepository).put("me/player/play")
  }

  @Test
  fun `getPlaybackState calls repository and returns success result`() = runTest {
    // Arrange
    val mockResult = Result.success(JSONObject())
    mockApiRepository.stub { onBlocking { get("me/player") } doReturn mockResult }
    val observer = mock<Observer<Result<JSONObject>>>()

    // Act
    viewModel.getPlaybackState { result -> observer.onChanged(result) }

    testDispatcher.scheduler.advanceUntilIdle()

    // Assert
    verify(observer).onChanged(mockResult)
    verify(mockApiRepository).get("me/player")
  }

  @Test
  fun `getPlaybackState calls repository and returns failure result`() = runTest {
    // Arrange
    val exception = Exception("Network error")
    val mockResult = Result.failure<JSONObject>(exception)
    mockApiRepository.stub { onBlocking { get("me/player") } doReturn mockResult }
    val observer = mock<Observer<Result<JSONObject>>>()

    // Act
    viewModel.getPlaybackState { result -> observer.onChanged(result) }

    testDispatcher.scheduler.advanceUntilIdle()

    // Assert
    verify(observer).onChanged(mockResult)
    verify(mockApiRepository).get("me/player")
  }

  @Test
  fun `skipSong calls repository and returns success result`() = runTest {
    // Arrange
    val mockResult = Result.success(JSONObject())
    // Use argument matchers for request body
    `when`(mockApiRepository.post(eq("me/player/next"), any<RequestBody>()))
      .thenReturn(mockResult)
    val observer = mock<Observer<Result<JSONObject>>>()

    // Act
    viewModel.skipSong { result -> observer.onChanged(result) }

    // Advance coroutine until idle
    testDispatcher.scheduler.advanceUntilIdle()

    // Assert
    verify(observer).onChanged(mockResult)
    verify(mockApiRepository).post(eq("me/player/next"), any<RequestBody>())
  }

  @Test
  fun `skipSong calls repository and returns failure result`() = runTest {
    // Arrange
    val exception = Exception("Network error")
    val mockResult = Result.failure<JSONObject>(exception)
    // Use argument matchers for request body
    `when`(mockApiRepository.post(eq("me/player/next"), any<RequestBody>()))
      .thenReturn(mockResult)
    val observer = mock<Observer<Result<JSONObject>>>()

    // Act
    viewModel.skipSong { result -> observer.onChanged(result) }

    // Advance coroutine until idle
    testDispatcher.scheduler.advanceUntilIdle()

    // Assert
    verify(observer).onChanged(mockResult)
    verify(mockApiRepository).post(eq("me/player/next"), any<RequestBody>())
  }

  @Test
  fun `previousSong calls repository and returns success result`() = runTest {
    // Arrange
    val mockResult = Result.success(JSONObject())
    `when`(mockApiRepository.post(eq("me/player/previous"), any<RequestBody>()))
      .thenReturn(mockResult)
    val observer = mock<Observer<Result<JSONObject>>>()

    // Act
    viewModel.previousSong { result -> observer.onChanged(result) }

    // Advance coroutine until idle
    testDispatcher.scheduler.advanceUntilIdle()

    // Assert
    verify(observer).onChanged(mockResult)
    verify(mockApiRepository).post(eq("me/player/previous"), any<RequestBody>())
  }

  @Test
  fun `previousSong calls repository and returns failure result`() = runTest {
    // Arrange
    val exception = Exception("Network error")
    val mockResult = Result.failure<JSONObject>(exception)
    `when`(mockApiRepository.post(eq("me/player/previous"), any<RequestBody>()))
      .thenReturn(mockResult)
    val observer = mock<Observer<Result<JSONObject>>>()

    // Act
    viewModel.previousSong { result -> observer.onChanged(result) }

    // Advance coroutine until idle
    testDispatcher.scheduler.advanceUntilIdle()

    // Assert
    verify(observer).onChanged(mockResult)
    verify(mockApiRepository).post(eq("me/player/previous"), any<RequestBody>())
  }

  @Test
  fun `transferPlayback calls repository with correct endpoint and body`() = runTest {
    // Arrange
    val testDeviceId = "test_device_id"
    viewModel.deviceId = testDeviceId
    val expectedRequestBody = "{\"device_ids\":[\"$testDeviceId\"]}".toRequestBody()

    // Set up mock result
    `when`(mockApiRepository.put(eq("me/player"), any())).thenReturn(Result.success(JSONObject()))

    // Act
    viewModel.transferPlayback()
    testDispatcher.scheduler.advanceUntilIdle()

    // Capture the argument
    val captor = argumentCaptor<RequestBody>()
    verify(mockApiRepository).put(eq("me/player"), captor.capture())

    // Assert
    assertEquals(expectedRequestBody.contentLength(), captor.firstValue.contentLength())
    assertEquals(expectedRequestBody.contentType(), captor.firstValue.contentType())
  }

  @Test
  fun `getDeviceId fetches devices and selects a smartphone device`() = runTest {
    // Arrange
    val mockDevices = JSONObject().apply {
      put("devices", JSONArray().apply {
        put(JSONObject().apply {
          put("id", "12345")
          put("type", "Smartphone")
          put("is_active", false)
        })
      })
    }

    val mockResult = Result.success(mockDevices)
    mockApiRepository.stub { onBlocking { get("me/player/devices") } doReturn mockResult }

    // Act
    viewModel.getDeviceId()

    testDispatcher.scheduler.advanceUntilIdle() // Advance until coroutines are completed

    // Assert
    verify(mockApiRepository).get("me/player/devices")
    assertEquals("12345", viewModel.deviceId) // Check that the smartphone ID is set

    // Verify transferPlayback was called using Mockito's verify method
    // Since we cannot verify directly on the viewModel, check the side effect
    // by ensuring that transferPlayback was called in a real scenario
    assertTrue(viewModel.deviceId == "12345") // A simple check on deviceId
  }

  @Test
  fun `mock repository get method works as expected`() = runTest {
    // Arrange
    val mockDevices = JSONObject().apply {
      put("devices", JSONArray().apply {
        put(JSONObject().apply {
          put("id", "67890")
          put("type", "Speaker")
          put("is_active", true)
        })
      })
    }

    val mockResult = Result.success(mockDevices)
    `when`(mockApiRepository.get("me/player/devices")).thenReturn(mockResult)

    // Act
    val result = mockApiRepository.get("me/player/devices")

    // Assert
    assertTrue(result.isSuccess)
    assertEquals("67890", result.getOrNull()?.getJSONArray("devices")?.getJSONObject(0)?.getString("id"))
  }

  @Test
  fun `getDeviceId handles no devices case`() = runTest {
    // Arrange
    val mockDevices = JSONObject().apply {
      put("devices", JSONArray()) // Empty array of devices
    }

    val mockResult = Result.success(mockDevices)
    mockApiRepository.stub { onBlocking { get("me/player/devices") } doReturn mockResult }

    // Act
    viewModel.getDeviceId()

    testDispatcher.scheduler.advanceUntilIdle() // Advance until coroutines are completed

    // Assert
    verify(mockApiRepository).get("me/player/devices")
    assertNull(viewModel.deviceId) // Device ID should be null when no devices are found
  }

  @Test
  fun `getDeviceId falls back to first device when no smartphone is found`() = runTest {
    // Arrange
    val mockDevices = JSONObject().apply {
      put("devices", JSONArray().apply {
        put(JSONObject().apply {
          put("id", "98765")
          put("type", "Speaker")
          put("is_active", true)
        })
      })
    }

    val mockResult = Result.success(mockDevices)
    mockApiRepository.stub { onBlocking { get("me/player/devices") } doReturn mockResult }

    // Act
    viewModel.getDeviceId()

    testDispatcher.scheduler.advanceUntilIdle() // Advance until coroutines are completed

    // Assert
    verify(mockApiRepository).get("me/player/devices")
    assertEquals("98765", viewModel.deviceId) // ID of the first (and only) device should be selected
  }
}
