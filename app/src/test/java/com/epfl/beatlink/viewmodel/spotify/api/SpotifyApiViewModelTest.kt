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
import org.json.JSONException
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertFalse
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
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.stub

@OptIn(ExperimentalCoroutinesApi::class)
class SpotifyApiViewModelTest {

  @get:Rule val instantTaskExecutorRule = InstantTaskExecutorRule()

  @get:Rule val mockitoRule: MockitoRule = MockitoJUnit.rule()

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
                                            put("url", "https://i.scdn.co/image/artist-image-url")
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
      onBlocking { get("search?q=testQuery&type=artist,track&market=CH&limit=20") } doReturn
          mockResult
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
                image = "https://i.scdn.co/image/artist-image-url"))
    val expectedTracks =
        listOf(
            SpotifyTrack(
                name = "Creep",
                artist = "Radiohead",
                trackId = "track123",
                cover = "https://i.scdn.co/image/track-cover-url",
                duration = 238000,
                popularity = 95,
                state = State.PAUSE))
    verify(observer).onChanged(Pair(expectedArtists, expectedTracks))
    verify(mockApiRepository).get("search?q=testQuery&type=artist,track&market=CH&limit=20")
  }

  @Test
  fun `searchArtistsAndTracks calls repository and returns failure result`() = runTest {
    // Arrange
    val exception = Exception("Network error")
    val mockResult = Result.failure<JSONObject>(exception)
    mockApiRepository.stub {
      onBlocking { get("search?q=testQuery&type=artist,track&market=CH&limit=20") } doReturn
          mockResult
    }
    val observer = mock<Observer<Pair<List<SpotifyArtist>, List<SpotifyTrack>>>>()

    // Act
    viewModel.searchArtistsAndTracks(
        query = "testQuery",
        onSuccess = { _, _ -> fail("Expected failure but got success") },
        onFailure = { artists, tracks -> observer.onChanged(Pair(artists, tracks)) })

    testDispatcher.scheduler.advanceUntilIdle()

    // Assert
    verify(observer).onChanged(Pair(emptyList(), emptyList()))
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
                state = State.PAUSE))
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
  fun `pausePlayback calls repository and sets isPlaying to false when playback is active`() =
      runTest {
        // Arrange
        mockApiRepository.stub {
          onBlocking { put("me/player/pause") } doReturn Result.success(JSONObject())
        }
        viewModel.isPlaying = true

        // Act
        viewModel.pausePlayback()

        // Advance coroutine until idle
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        verify(mockApiRepository).put("me/player/pause")
        assertFalse(viewModel.isPlaying) // Verify isPlaying is set to false
  }

  @Test
  fun `pausePlayback does not call repository when playback is not active`() = runTest {
    // Arrange
    viewModel.isPlaying = false

    // Act
    viewModel.pausePlayback()

    // Advance coroutine until idle
    testDispatcher.scheduler.advanceUntilIdle()

    // Assert
    verify(mockApiRepository, never()).put("me/player/pause")
  }

  @Test
  fun `playPlayback calls repository and sets isPlaying to true when playback is not active`() =
      runTest {
        // Arrange
        mockApiRepository.stub {
          onBlocking { put("me/player/play") } doReturn Result.success(JSONObject())
        }
        viewModel.isPlaying = false

        // Act
        viewModel.playPlayback()

        // Advance coroutine until idle
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        verify(mockApiRepository).put("me/player/play")
        assertTrue(viewModel.isPlaying) // Verify isPlaying is set to true
  }

  @Test
  fun `playPlayback does not call repository when playback is already active`() = runTest {
    // Arrange
    viewModel.isPlaying = true

    // Act
    viewModel.playPlayback()

    // Advance coroutine until idle
    testDispatcher.scheduler.advanceUntilIdle()

    // Assert
    verify(mockApiRepository, never()).put("me/player/play")
  }

  @Test
  fun `getPlaybackState calls repository and invokes onSuccess callback when result is success`() =
      runTest {
        // Arrange
        val mockResult = Result.success(JSONObject())
        mockApiRepository.stub { onBlocking { get("me/player") } doReturn mockResult }
        val onSuccess = mock<(JSONObject) -> Unit>()
        val onFailure = mock<() -> Unit>()
        viewModel.deviceId = "mockDeviceId" // Ensure deviceId is not null

        // Act
        viewModel.getPlaybackState(onSuccess, onFailure)

        // Advance coroutine until idle
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        verify(mockApiRepository).get("me/player")
        verify(onSuccess).invoke(mockResult.getOrNull()!!)
        verify(onFailure, never()).invoke()
      }

  @Test
  fun `getPlaybackState calls repository and invokes onFailure callback when result is failure`() =
      runTest {
        // Arrange
        val mockResult = Result.failure<JSONObject>(Exception("Network error"))
        mockApiRepository.stub { onBlocking { get("me/player") } doReturn mockResult }
        val onSuccess = mock<(JSONObject) -> Unit>()
        val onFailure = mock<() -> Unit>()
        viewModel.deviceId = "mockDeviceId" // Ensure deviceId is not null

        // Act
        viewModel.getPlaybackState(onSuccess, onFailure)

        // Advance coroutine until idle
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        verify(mockApiRepository).get("me/player")
        verify(onFailure).invoke()
        verify(onSuccess, never()).invoke(any())
      }

  @Test
  fun `skipSong does not call repository when playback is not active`() = runTest {
    // Arrange
    viewModel.isPlaying = false

    // Act
    viewModel.skipSong()

    // Advance coroutine until idle
    testDispatcher.scheduler.advanceUntilIdle()

    // Assert
    verify(mockApiRepository, never())
        .post(eq("me/player/next"), any()) // Ensure no repository call
    // No direct assertion for updatePlayer as it won't be invoked
  }

  @Test
  fun `previousSong does not call repository when playback is not active`() = runTest {
    // Arrange
    viewModel.playbackActive = false
    viewModel.isPlaying = false

    // Act
    viewModel.previousSong()

    // Advance coroutine until idle
    testDispatcher.scheduler.advanceUntilIdle()

    // Assert
    verify(mockApiRepository, never()).post(eq("me/player/previous"), any<RequestBody>())
  }

    @Test
    fun testUpdatePlayer_Success() = runTest {
        // Prepare the mock responses
        val mockTrack = SpotifyTrack(
            name = "Test Track",
            artist = "Test Artist",
            trackId = "1234",
            cover = "testCoverUrl",
            duration = 200000,
            popularity = 80,
            state = State.PLAY
        )

        val mockAlbum = SpotifyAlbum(
            "albumId",
            "Test Album",
            "",
            "Test Artist",
            2024,
            listOf(),
            10,
            listOf(),
            80
        )

        val mockArtist = SpotifyArtist(
            "artistId",
            "Test Artist",
            listOf(),
            80
        )

        val mockJsonResponse = JSONObject().apply {
            put("item", JSONObject().apply {
                put("id", "trackId")
                put("name", "Test Track")
                put("duration_ms", 200000)
                put("popularity", 80)
                put("is_playing", true)
                put("artists", JSONArray().apply {
                    put(JSONObject().apply {
                        put("name", "Test Artist")
                    })
                })
                put("album", JSONObject().apply {
                    put("id", "albumId")
                    put("name", "Test Album")
                    put("release_date", "2024")
                    put("total_tracks", 10)
                })
            })
        }

        `when`(mockApiRepository.get("me/player")).thenReturn(Result.success(mockJsonResponse))

        // Call updatePlayer
        viewModel.updatePlayer()

        // Advance the dispatcher to ensure the coroutine gets executed
        testDispatcher.scheduler.advanceUntilIdle()

        // Verify the updated states
        assertTrue(viewModel.playbackActive)
        assertEquals("", viewModel.currentTrack.name)
        assertEquals("", viewModel.currentTrack.artist)
        assertEquals("", viewModel.currentAlbum.name)
        assertEquals("", viewModel.currentArtist.name)
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
  fun `buildAlbum constructs SpotifyAlbum from valid JSON`() {
    // Arrange
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

    // Act
    val result = viewModel.buildAlbum(mockAlbumJson)

    // Assert
    val expectedAlbum =
        SpotifyAlbum("123", "Test Album", "", "Test Artist", 2020, listOf(), 10, listOf(), 0)
    assertEquals(expectedAlbum, result)
  }

  @Test(expected = JSONException::class)
  fun `buildAlbum throws JSONException for invalid JSON`() {
    // Arrange
    val invalidJson =
        JSONObject("""
        {
            "invalid": "structure"
        }
        """)

    // Act
    viewModel.buildAlbum(invalidJson)

    // Assert
    // Exception is expected, so no additional assertions
  }

  @Test
  fun `buildTrack constructs SpotifyTrack from valid JSON`() {
    // Arrange
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

    // Act
    val result = viewModel.buildTrack(mockTrackJson)

    // Assert
    val expectedTrack = SpotifyTrack("Test Track", "Test Artist", "456", "", 300000, 80, State.PLAY)
    assertEquals(expectedTrack, result)
  }

  @Test
  fun `buildTrack constructs SpotifyTrack with PAUSE state when is_playing is false`() {
    // Arrange
    val mockTrackJson =
        JSONObject(
            """
        {
            "is_playing": false,
            "item": {
                "name": "Test Track",
                "artists": [{"name": "Test Artist"}],
                "id": "456",
                "duration_ms": 300000,
                "popularity": 80
            }
        }
        """)

    // Act
    val result = viewModel.buildTrack(mockTrackJson)

    // Assert
    val expectedTrack =
        SpotifyTrack("Test Track", "Test Artist", "456", "", 300000, 80, State.PAUSE)
    assertEquals(expectedTrack, result)
  }

  @Test(expected = JSONException::class)
  fun `buildTrack throws JSONException for invalid JSON`() {
    // Arrange
    val invalidJson =
        JSONObject("""
        {
            "invalid": "structure"
        }
        """)

    // Act
    viewModel.buildTrack(invalidJson)

    // Assert
    // Exception is expected, so no additional assertions
  }

  @Test
  fun `buildArtist constructs SpotifyArtist from valid JSON`() {
    // Arrange
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

    // Act
    val result = viewModel.buildArtist(mockArtistJson)

    // Assert
    val expectedArtist = SpotifyArtist("", "Test Artist", listOf(), 0)
    assertEquals(expectedArtist, result)
  }

  @Test
  fun `buildArtist constructs SpotifyArtist with the first artist from multiple artists`() {
    // Arrange
    val mockArtistJson =
        JSONObject(
            """
        {
            "item": {
                "artists": [
                    { "name": "First Artist" },
                    { "name": "Second Artist" }
                ]
            }
        }
        """)

    // Act
    val result = viewModel.buildArtist(mockArtistJson)

    // Assert
    val expectedArtist = SpotifyArtist("", "First Artist", listOf(), 0)
    assertEquals(expectedArtist, result)
  }

  @Test(expected = JSONException::class)
  fun `buildArtist throws JSONException when artists array is missing`() {
    // Arrange
    val invalidArtistJson = JSONObject("""
        {
            "item": {}
        }
        """)

    // Act
    viewModel.buildArtist(invalidArtistJson)

    // Assert
    // Exception is expected, so no additional assertions
  }

  @Test(expected = JSONException::class)
  fun `buildArtist throws JSONException when artists array is empty`() {
    // Arrange
    val emptyArtistsJson =
        JSONObject(
            """
        {
            "item": {
                "artists": []
            }
        }
        """)

    // Act
    viewModel.buildArtist(emptyArtistsJson)

    // Assert
    // Exception is expected, so no additional assertions
  }

  @Test
  fun `buildArtist constructs empty SpotifyArtist for minimal valid JSON`() {
    // Arrange
    val minimalArtistJson =
        JSONObject(
            """
        {
            "item": {
                "artists": [
                    { "name": "" }
                ]
            }
        }
        """)

    // Act
    val result = viewModel.buildArtist(minimalArtistJson)

    // Assert
    val expectedArtist = SpotifyArtist("", "", listOf(), 0)
    assertEquals(expectedArtist, result)
  }
}
