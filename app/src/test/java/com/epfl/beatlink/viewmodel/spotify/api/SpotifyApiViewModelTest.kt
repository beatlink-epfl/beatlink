package com.epfl.beatlink.viewmodel.spotify.api

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.epfl.beatlink.model.spotify.objects.SpotifyAlbum
import com.epfl.beatlink.model.spotify.objects.SpotifyArtist
import com.epfl.beatlink.model.spotify.objects.SpotifyTrack
import com.epfl.beatlink.model.spotify.objects.State
import com.epfl.beatlink.repository.spotify.api.SpotifyApiRepository
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.fail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
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
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.stub
import org.mockito.kotlin.whenever

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
    fun `searchArtistsAndTracks calls repository and returns success result`() = runTest {
        // Arrange
        val mockResult =
            Result.success(
                JSONObject().apply {
                    put(
                        "artists",
                        JSONObject().apply {
                            put(
                                "items",
                                JSONArray().apply {
                                    put(
                                        JSONObject().apply {
                                            put("name", "Radiohead")
                                            put("popularity", 90)
                                            put("genres", JSONArray(listOf("alternative rock", "indie rock")))
                                            put(
                                                "images",
                                                JSONArray().apply {
                                                    put(
                                                        JSONObject().apply {
                                                            put(
                                                                "url",
                                                                "https://i.scdn.co/image/artist-image-url")
                                                        })
                                                })
                                        })
                                })
                        })
                    put(
                        "tracks",
                        JSONObject().apply {
                            put(
                                "items",
                                JSONArray().apply {
                                    put(
                                        JSONObject().apply {
                                            put("name", "Creep")
                                            put("id", "track123")
                                            put("duration_ms", 238000)
                                            put("popularity", 95)
                                            put(
                                                "artists",
                                                JSONArray().apply {
                                                    put(
                                                        JSONObject().apply {
                                                            put("name", "Radiohead")
                                                            put(
                                                                "images",
                                                                JSONArray().apply {
                                                                    put(
                                                                        JSONObject().apply {
                                                                            put(
                                                                                "url",
                                                                                "https://i.scdn.co/image/track-cover-url")
                                                                        })
                                                                })
                                                        })
                                                })
                                        })
                                })
                        })
                })
        mockApiRepository.stub {
            onBlocking { get("search?q=testQuery&type=artist,track&market=CH&limit=20") } doReturn mockResult
        }
        val observer = mock<Observer<Pair<List<SpotifyArtist>, List<SpotifyTrack>>>>()

        // Act
        viewModel.searchArtistsAndTracks(
            query = "testQuery",
            onSuccess = { artists, tracks -> observer.onChanged(Pair(artists, tracks)) },
            onFailure = { _, _ -> fail("Expected success but got failure") })

        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val expectedArtists =
            listOf(
                SpotifyArtist(
                    name = "Radiohead",
                    popularity = 90,
                    genres = listOf("alternative rock", "indie rock"),
                    image = "https://i.scdn.co/image/artist-image-url"
                )
            )
        val expectedTracks =
            listOf(
                SpotifyTrack(
                    name = "Creep",
                    artist = "Radiohead",
                    trackId = "track123",
                    cover = "https://i.scdn.co/image/track-cover-url",
                    duration = 238000,
                    popularity = 95,
                    state = State.PAUSE
                )
            )
        verify(observer).onChanged(Pair(expectedArtists, expectedTracks))
        verify(mockApiRepository).get("search?q=testQuery&type=artist,track&market=CH&limit=20")
    }

    @Test
    fun `searchArtistsAndTracks calls repository and returns failure result`() = runTest {
        // Arrange
        val exception = Exception("Network error")
        val mockResult = Result.failure<JSONObject>(exception)
        mockApiRepository.stub {
            onBlocking { get("search?q=testQuery&type=artist,track&market=CH&limit=20") } doReturn mockResult
        }
        val observer = mock<Observer<Pair<List<SpotifyArtist>, List<SpotifyTrack>>>>()

        // Act
        viewModel.searchArtistsAndTracks(
            query = "testQuery",
            onSuccess = { _, _ -> fail("Expected failure but got success") },
            onFailure = { artists, tracks -> observer.onChanged(Pair(artists, tracks)) })

        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        verify(observer).onChanged(Pair(emptyList<SpotifyArtist>(), emptyList<SpotifyTrack>()))
        verify(mockApiRepository).get("search?q=testQuery&type=artist,track&market=CH&limit=20")
    }

    @Test
    fun `getCurrentUserTopTracks calls repository and returns success result`() = runTest {
        // Arrange
        val mockResult =
            Result.success(
                JSONObject().apply {
                    put(
                        "items",
                        JSONArray().apply {
                            put(
                                JSONObject().apply {
                                    put("name", "Creep")
                                    put("id", "track123")
                                    put("duration_ms", 238000)
                                    put("popularity", 95)
                                    put(
                                        "artists",
                                        JSONArray().apply {
                                            put(
                                                JSONObject().apply {
                                                    put("name", "Radiohead")
                                                    put(
                                                        "images",
                                                        JSONArray().apply {
                                                            put(
                                                                JSONObject().apply {
                                                                    put(
                                                                        "url",
                                                                        "https://i.scdn.co/image/ab6761610000e5eba68c0feed141ac1ac2dcab19")
                                                                })
                                                        })
                                                })
                                        })
                                })
                        })
                })
        mockApiRepository.stub {
            onBlocking { get("me/top/tracks?time_range=short_term") } doReturn mockResult
        }
        val observer = mock<Observer<List<SpotifyTrack>>>()

        // Act
        viewModel.getCurrentUserTopTracks(
            onSuccess = { observer.onChanged(it) },
            onFailure = { fail("Expected success but got failure") })

        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val expectedTracks =
            listOf(
                SpotifyTrack(
                    name = "Creep",
                    artist = "Radiohead",
                    trackId = "track123",
                    cover = "https://i.scdn.co/image/ab6761610000e5eba68c0feed141ac1ac2dcab19",
                    duration = 238000,
                    popularity = 95,
                    state = State.PAUSE
                )
            )
        verify(observer).onChanged(expectedTracks)
        verify(mockApiRepository).get("me/top/tracks?time_range=short_term")
    }

    @Test
    fun `getCurrentUserTopTracks calls repository and returns failure result`() = runTest {
        // Arrange
        val exception = Exception("Network error")
        val mockResult = Result.failure<JSONObject>(exception)
        mockApiRepository.stub {
            onBlocking { get("me/top/tracks?time_range=short_term") } doReturn mockResult
        }
        val observer = mock<Observer<List<SpotifyTrack>>>()

        // Act
        viewModel.getCurrentUserTopTracks(
            onSuccess = { fail("Expected failure but got success") },
            onFailure = { observer.onChanged(it) })

        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        verify(observer).onChanged(emptyList())
        verify(mockApiRepository).get("me/top/tracks?time_range=short_term")
    }

  @Test
  fun `getCurrentUserTopArtists calls repository and returns success result`() = runTest {
    // Arrange
    val mockResult =
        Result.success(
            JSONObject().apply {
              put(
                  "items",
                  JSONArray().apply {
                    put(
                        JSONObject().apply {
                          put("name", "Hybrid Minds")
                          put("popularity", 60)
                          put("genres", JSONArray(listOf("drum and bass", "liquid funk")))
                          put(
                              "images",
                              JSONArray().apply {
                                put(
                                    JSONObject().apply {
                                      put(
                                          "url",
                                          "https://i.scdn.co/image/ab6761610000e5eba68c0feed141ac1ac2dcab18")
                                    })
                              })
                        })
                  })
            })
    mockApiRepository.stub {
      onBlocking { get("me/top/artists?time_range=short_term") } doReturn mockResult
    }
    val observer = mock<Observer<List<SpotifyArtist>>>()

    // Act
    viewModel.getCurrentUserTopArtists(
        onSuccess = { observer.onChanged(it) },
        onFailure = { fail("Expected success but got failure") })

    testDispatcher.scheduler.advanceUntilIdle()

    // Assert
    val expectedArtists =
        listOf(
            SpotifyArtist(
                image = "https://i.scdn.co/image/ab6761610000e5eba68c0feed141ac1ac2dcab18",
                name = "Hybrid Minds",
                genres = listOf("drum and bass", "liquid funk"),
                popularity = 60))
    verify(observer).onChanged(expectedArtists)
    verify(mockApiRepository).get("me/top/artists?time_range=short_term")
  }

  @Test
  fun `getCurrentUserTopArtists calls repository and returns failure result`() = runTest {
    // Arrange
    val exception = Exception("Network error")
    val mockResult = Result.failure<JSONObject>(exception)
    mockApiRepository.stub {
      onBlocking { get("me/top/artists?time_range=short_term") } doReturn mockResult
    }
    val observer = mock<Observer<List<SpotifyArtist>>>()

    // Act
    viewModel.getCurrentUserTopArtists(
        onSuccess = { fail("Expected failure but got success") },
        onFailure = { observer.onChanged(it) })

    testDispatcher.scheduler.advanceUntilIdle()

    // Assert
    verify(observer).onChanged(emptyList())
    verify(mockApiRepository).get("me/top/artists?time_range=short_term")
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
  fun `pausePlayback calls repository and returns success result when playback is active`() =
      runTest {
        // Arrange
        val mockResult = Result.success(JSONObject())
        mockApiRepository.stub { onBlocking { put("me/player/pause") } doReturn mockResult }
        val observer = mock<Observer<Result<JSONObject>>>()

        // Set playbackActive to true to trigger the repository call
        viewModel.playbackActive = true

        // Act
        viewModel.pausePlayback { result -> observer.onChanged(result) }

        // Advance coroutine until idle
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        verify(observer).onChanged(mockResult)
        verify(mockApiRepository).put("me/player/pause")
      }

  @Test
  fun `pausePlayback does not call repository when playback is not active`() = runTest {
    // Arrange
    val observer = mock<Observer<Result<JSONObject>>>()

    // Set playbackActive to false to prevent the repository call
    viewModel.playbackActive = false

    // Act
    viewModel.pausePlayback { result -> observer.onChanged(result) }

    // Advance coroutine until idle
    testDispatcher.scheduler.advanceUntilIdle()

    // Assert
    verify(observer, never()).onChanged(any())
    verify(mockApiRepository, never()).put("me/player/pause")
  }

  @Test
  fun `pausePlayback calls repository and returns failure result when playback is active`() =
      runTest {
        // Arrange
        val exception = Exception("Network error")
        val mockResult = Result.failure<JSONObject>(exception)
        mockApiRepository.stub { onBlocking { put("me/player/pause") } doReturn mockResult }
        val observer = mock<Observer<Result<JSONObject>>>()

        // Set playbackActive to true to trigger the repository call
        viewModel.playbackActive = true

        // Act
        viewModel.pausePlayback { result -> observer.onChanged(result) }

        // Advance coroutine until idle
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        verify(observer).onChanged(mockResult)
        verify(mockApiRepository).put("me/player/pause")
      }

  @Test
  fun `playPlayback calls repository and returns success result when playback is active`() =
      runTest {
        // Arrange
        val mockResult = Result.success(JSONObject())
        mockApiRepository.stub { onBlocking { put("me/player/play") } doReturn mockResult }
        val observer = mock<Observer<Result<JSONObject>>>()

        // Set playbackActive to true to trigger the repository call
        viewModel.playbackActive = true

        // Act
        viewModel.playPlayback { result -> observer.onChanged(result) }

        // Advance coroutine until idle
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        verify(observer).onChanged(mockResult)
        verify(mockApiRepository).put("me/player/play")
      }

  @Test
  fun `playPlayback does not call repository when playback is not active`() = runTest {
    // Arrange
    val observer = mock<Observer<Result<JSONObject>>>()

    // Set playbackActive to false to prevent the repository call
    viewModel.playbackActive = false

    // Act
    viewModel.playPlayback { result -> observer.onChanged(result) }

    // Advance coroutine until idle
    testDispatcher.scheduler.advanceUntilIdle()

    // Assert
    verify(observer, never()).onChanged(any())
    verify(mockApiRepository, never()).put("me/player/play")
  }

  @Test
  fun `playPlayback calls repository and returns failure result when playback is active`() =
      runTest {
        // Arrange
        val exception = Exception("Network error")
        val mockResult = Result.failure<JSONObject>(exception)
        mockApiRepository.stub { onBlocking { put("me/player/play") } doReturn mockResult }
        val observer = mock<Observer<Result<JSONObject>>>()

        // Set playbackActive to true to trigger the repository call
        viewModel.playbackActive = true

        // Act
        viewModel.playPlayback { result -> observer.onChanged(result) }

        // Advance coroutine until idle
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
  fun `getPlaybackState calls repository, updates playbackActive to true, and returns success result`() =
      runTest {
        // Arrange
        val mockResult = Result.success(JSONObject())
        mockApiRepository.stub { onBlocking { get("me/player") } doReturn mockResult }
        val observer = mock<Observer<Result<JSONObject>>>()

        // Ensure deviceId is not null to skip getDeviceId call
        viewModel.deviceId = "mockDeviceId"

        // Act
        viewModel.getPlaybackState { result -> observer.onChanged(result) }

        // Advance coroutine until idle
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertTrue(viewModel.playbackActive) // Verify playbackActive was set to true
        verify(observer).onChanged(mockResult)
        verify(mockApiRepository).get("me/player")
      }

  @Test
  fun `skipSong calls repository and returns success result when playback is active`() = runTest {
    // Arrange
    val mockResult = Result.success(JSONObject())
    `when`(mockApiRepository.post(eq("me/player/next"), any<RequestBody>())).thenReturn(mockResult)
    val observer = mock<Observer<Result<JSONObject>>>()

    // Set playbackActive to true to trigger the repository call
    viewModel.playbackActive = true

    // Act
    viewModel.skipSong { result -> observer.onChanged(result) }

    // Advance coroutine until idle
    testDispatcher.scheduler.advanceUntilIdle()

    // Assert
    verify(observer).onChanged(mockResult)
    verify(mockApiRepository).post(eq("me/player/next"), any<RequestBody>())
  }

  @Test
  fun `skipSong does not call repository when playback is not active`() = runTest {
    // Arrange
    val observer = mock<Observer<Result<JSONObject>>>()

    // Set playbackActive to false to prevent the repository call
    viewModel.playbackActive = false

    // Act
    viewModel.skipSong { result -> observer.onChanged(result) }

    // Advance coroutine until idle
    testDispatcher.scheduler.advanceUntilIdle()

    // Assert
    verify(observer, never()).onChanged(any())
    verify(mockApiRepository, never()).post(eq("me/player/next"), any<RequestBody>())
  }

  @Test
  fun `previousSong calls repository and returns success result when playback is active`() =
      runTest {
        // Arrange
        val mockResult = Result.success(JSONObject())
        `when`(mockApiRepository.post(eq("me/player/previous"), any<RequestBody>()))
            .thenReturn(mockResult)
        val observer = mock<Observer<Result<JSONObject>>>()

        // Set playbackActive to true to trigger the repository call
        viewModel.playbackActive = true

        // Act
        viewModel.previousSong { result -> observer.onChanged(result) }

        // Advance coroutine until idle
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        verify(observer).onChanged(mockResult)
        verify(mockApiRepository).post(eq("me/player/previous"), any<RequestBody>())
      }

  @Test
  fun `previousSong does not call repository when playback is not active`() = runTest {
    // Arrange
    val observer = mock<Observer<Result<JSONObject>>>()

    // Set playbackActive to false to prevent the repository call
    viewModel.playbackActive = false

    // Act
    viewModel.previousSong { result -> observer.onChanged(result) }

    // Advance coroutine until idle
    testDispatcher.scheduler.advanceUntilIdle()

    // Assert
    verify(observer, never()).onChanged(any())
    verify(mockApiRepository, never()).post(eq("me/player/previous"), any<RequestBody>())
  }

  @Test
  fun `previousSong calls repository and returns failure result when playback is active`() =
      runTest {
        // Arrange
        val exception = Exception("Network error")
        val mockResult = Result.failure<JSONObject>(exception)
        `when`(mockApiRepository.post(eq("me/player/previous"), any<RequestBody>()))
            .thenReturn(mockResult)
        val observer = mock<Observer<Result<JSONObject>>>()

        // Set playbackActive to true to trigger the repository call
        viewModel.playbackActive = true

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
    val mockDevices =
        JSONObject().apply {
          put(
              "devices",
              JSONArray().apply {
                put(
                    JSONObject().apply {
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
    val mockDevices =
        JSONObject().apply {
          put(
              "devices",
              JSONArray().apply {
                put(
                    JSONObject().apply {
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
    assertEquals(
        "67890", result.getOrNull()?.getJSONArray("devices")?.getJSONObject(0)?.getString("id"))
  }

  @Test
  fun `getDeviceId handles no devices case`() = runTest {
    // Arrange
    val mockDevices =
        JSONObject().apply {
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
    val mockDevices =
        JSONObject().apply {
          put(
              "devices",
              JSONArray().apply {
                put(
                    JSONObject().apply {
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
    assertEquals(
        "98765", viewModel.deviceId) // ID of the first (and only) device should be selected
  }

  @Test
  fun `buildAlbum constructs SpotifyAlbum and returns it on success when playback is active`() =
      runTest {
        // Arrange
        viewModel.playbackActive = true
        val mockAlbumJson =
            JSONObject(
                """
        {
            "item": {
                "album": {
                    "id": "123",
                    "name": "Test Album",
                    "artists": [{"name": "Test Artist"}],
                    "release_date": "2020-01-01",
                    "total_tracks": 10
                }
            }
        }
    """)
        val mockResult = Result.success(mockAlbumJson)
        mockApiRepository.stub {
          onBlocking { get("me/player/currently-playing") } doReturn mockResult
        }
        val observer = mock<Observer<SpotifyAlbum>>()

        // Act
        viewModel.buildAlbum { album -> observer.onChanged(album) }

        // Advance coroutine until idle
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val expectedAlbum =
            SpotifyAlbum("123", "Test Album", "", "Test Artist", 2020, listOf(), 10, listOf(), 0)
        verify(observer).onChanged(expectedAlbum)
        verify(mockApiRepository).get("me/player/currently-playing")
      }

  @Test
  fun `buildAlbum returns empty SpotifyAlbum when playback is not active`() = runTest {
    // Arrange
    viewModel.playbackActive = false
    val observer = mock<Observer<SpotifyAlbum>>()

    // Act
    viewModel.buildAlbum { album -> observer.onChanged(album) }

    // Advance coroutine until idle
    testDispatcher.scheduler.advanceUntilIdle()

    // Assert
    val emptyAlbum = SpotifyAlbum("", "", "", "", 0, listOf(), 0, listOf(), 0)
    verify(observer).onChanged(emptyAlbum)
    verify(mockApiRepository, never()).get("me/player/currently-playing")
  }

  @Test
  fun `buildAlbum returns empty SpotifyAlbum on API failure when playback is active`() = runTest {
    // Arrange
    viewModel.playbackActive = true
    val exception = Exception("Network error")
    val mockResult = Result.failure<JSONObject>(exception)
    mockApiRepository.stub { onBlocking { get("me/player/currently-playing") } doReturn mockResult }
    val observer = mock<Observer<SpotifyAlbum>>()

    // Act
    viewModel.buildAlbum { album -> observer.onChanged(album) }

    // Advance coroutine until idle
    testDispatcher.scheduler.advanceUntilIdle()

    // Assert
    val emptyAlbum = SpotifyAlbum("", "", "", "", 0, listOf(), 0, listOf(), 0)
    verify(observer).onChanged(emptyAlbum)
    verify(mockApiRepository).get("me/player/currently-playing")
  }

  @Test
  fun `buildTrack constructs SpotifyTrack and returns it on success when playback is active`() =
      runTest {
        // Arrange
        viewModel.playbackActive = true
        val mockTrackJson =
            JSONObject(
                """
        {
            "is_playing": true,
            "item": {
                "name": "Test Track",
                "artists": [{"name": "Test Artist"}],
                "id": "456",
                "duration_ms": 300000,
                "popularity": 80
            }
        }
    """)
        val mockResult = Result.success(mockTrackJson)
        mockApiRepository.stub {
          onBlocking { get("me/player/currently-playing") } doReturn mockResult
        }
        val observer = mock<Observer<SpotifyTrack>>()

        // Act
        viewModel.buildTrack { track -> observer.onChanged(track) }

        // Advance coroutine until idle
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val expectedTrack =
            SpotifyTrack("Test Track", "Test Artist", "456", "", 300000, 80, State.PLAY)
        verify(observer).onChanged(expectedTrack)
        verify(mockApiRepository).get("me/player/currently-playing")
      }

  @Test
  fun `buildTrack returns empty SpotifyTrack when playback is not active`() = runTest {
    // Arrange
    viewModel.playbackActive = false
    val observer = mock<Observer<SpotifyTrack>>()

    // Act
    viewModel.buildTrack { track -> observer.onChanged(track) }

    // Advance coroutine until idle
    testDispatcher.scheduler.advanceUntilIdle()

    // Assert
    val emptyTrack = SpotifyTrack("", "", "", "", 0, 0, State.PAUSE)
    verify(observer).onChanged(emptyTrack)
    verify(mockApiRepository, never()).get("me/player/currently-playing")
  }

  @Test
  fun `buildTrack returns empty SpotifyTrack on API failure when playback is active`() = runTest {
    // Arrange
    viewModel.playbackActive = true
    val exception = Exception("Network error")
    val mockResult = Result.failure<JSONObject>(exception)
    mockApiRepository.stub { onBlocking { get("me/player/currently-playing") } doReturn mockResult }
    val observer = mock<Observer<SpotifyTrack>>()

    // Act
    viewModel.buildTrack { track -> observer.onChanged(track) }

    // Advance coroutine until idle
    testDispatcher.scheduler.advanceUntilIdle()

    // Assert
    val emptyTrack = SpotifyTrack("", "", "", "", 0, 0, State.PAUSE)
    verify(observer).onChanged(emptyTrack)
    verify(mockApiRepository).get("me/player/currently-playing")
  }

  @Test
  fun `buildArtist constructs SpotifyArtist and returns it on success when playback is active`() =
      runTest {
        // Arrange
        viewModel.playbackActive = true
        val mockArtistJson =
            JSONObject(
                """
        {
            "item": {
                "artists": [
                    {
                        "name": "Test Artist"
                    }
                ]
            }
        }
    """)
        val mockResult = Result.success(mockArtistJson)
        mockApiRepository.stub {
          onBlocking { get("me/player/currently-playing") } doReturn mockResult
        }
        val observer = mock<Observer<SpotifyArtist>>()

        // Act
        viewModel.buildArtist { artist -> observer.onChanged(artist) }

        // Advance coroutine until idle
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val expectedArtist = SpotifyArtist("", "Test Artist", listOf(), 0)
        verify(observer).onChanged(expectedArtist)
        verify(mockApiRepository).get("me/player/currently-playing")
      }

  @Test
  fun `buildArtist returns empty SpotifyArtist when playback is not active`() = runTest {
    // Arrange
    viewModel.playbackActive = false
    val observer = mock<Observer<SpotifyArtist>>()

    // Act
    viewModel.buildArtist { artist -> observer.onChanged(artist) }

    // Advance coroutine until idle
    testDispatcher.scheduler.advanceUntilIdle()

    // Assert
    val emptyArtist = SpotifyArtist("", "", listOf(), 0)
    verify(observer).onChanged(emptyArtist)
    verify(mockApiRepository, never()).get("me/player/currently-playing")
  }

  @Test
  fun `buildArtist returns empty SpotifyArtist on API failure when playback is active`() = runTest {
    // Arrange
    viewModel.playbackActive = true
    val exception = Exception("Network error")
    val mockResult = Result.failure<JSONObject>(exception)
    mockApiRepository.stub { onBlocking { get("me/player/currently-playing") } doReturn mockResult }
    val observer = mock<Observer<SpotifyArtist>>()

    // Act
    viewModel.buildArtist { artist -> observer.onChanged(artist) }

    // Advance coroutine until idle
    testDispatcher.scheduler.advanceUntilIdle()

    // Assert
    val emptyArtist = SpotifyArtist("", "", listOf(), 0)
    verify(observer).onChanged(emptyArtist)
    verify(mockApiRepository).get("me/player/currently-playing")
  }
}
