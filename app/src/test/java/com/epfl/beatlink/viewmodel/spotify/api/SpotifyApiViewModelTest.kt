package com.epfl.beatlink.viewmodel.spotify.api

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.epfl.beatlink.model.library.UserPlaylist
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.startsWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.any
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
  fun `createBeatLinkPlaylist creates playlist and adds tracks successfully`() = runTest {
    // Arrange: Mock responses for getCurrentUserId, createEmptySpotifyPlaylist, and
    // addTracksToPlaylist
    val userId = "user123"
    val playlistId = "playlist123"
    val playlistName = "Test Playlist"
    val playlistDescription = "A test playlist description"
    val tracks =
        listOf(
            SpotifyTrack("Track1", "Artist1", "track1_id", "", 0, 0, State.PAUSE),
            SpotifyTrack("Track2", "Artist2", "track2_id", "", 0, 0, State.PAUSE))
    val createPlaylistResponse = JSONObject().apply { put("id", playlistId) }
    val mockAddTracksResult = Result.success(JSONObject())

    mockApiRepository.stub {
      // Mock getCurrentUserId response
      onBlocking { get("me") } doReturn Result.success(JSONObject().apply { put("id", userId) })
      // Mock createEmptySpotifyPlaylist response
      onBlocking { post(eq("users/$userId/playlists"), any()) } doReturn
          Result.success(createPlaylistResponse)
      // Mock addTracksToPlaylist response
      onBlocking { post(eq("playlists/$playlistId/tracks"), any()) } doReturn mockAddTracksResult
    }

    // Act
    viewModel.createBeatLinkPlaylist(playlistName, playlistDescription, tracks)

    testDispatcher.scheduler.advanceUntilIdle()

    // Assert
    verify(mockApiRepository).post(eq("users/$userId/playlists"), any())
    verify(mockApiRepository).post(eq("playlists/$playlistId/tracks"), any())
  }

  @Test
  fun `createBeatLinkPlaylist fails when user ID cannot be fetched`() = runTest {
    // Arrange: Mock getCurrentUserId to return failure
    val playlistName = "Test Playlist"
    val playlistDescription = "A test playlist description"
    val tracks =
        listOf(
            SpotifyTrack("Track1", "Artist1", "track1_id", "", 0, 0, State.PAUSE),
            SpotifyTrack("Track2", "Artist2", "track2_id", "", 0, 0, State.PAUSE))

    val exception = Exception("Failed to fetch user ID")
    mockApiRepository.stub { onBlocking { get("me") } doReturn Result.failure(exception) }

    // Act
    viewModel.createBeatLinkPlaylist(playlistName, playlistDescription, tracks)

    testDispatcher.scheduler.advanceUntilIdle()

    // Assert: Verify no further API calls were made
    verify(mockApiRepository, never()).post(startsWith("users/"), any())
    verify(mockApiRepository, never()).post(startsWith("playlists/"), any())
  }

  @Test
  fun `createBeatLinkPlaylist fails when playlist creation fails`() = runTest {
    // Arrange: Mock responses for getCurrentUserId and createEmptySpotifyPlaylist
    val userId = "user123"
    val playlistName = "Test Playlist"
    val playlistDescription = "A test playlist description"
    val tracks =
        listOf(
            SpotifyTrack("Track1", "Artist1", "track1_id", "", 0, 0, State.PAUSE),
            SpotifyTrack("Track2", "Artist2", "track2_id", "", 0, 0, State.PAUSE))

    mockApiRepository.stub {
      // Mock getCurrentUserId response
      onBlocking { get("me") } doReturn Result.success(JSONObject().apply { put("id", userId) })
      // Mock createEmptySpotifyPlaylist response to fail
      onBlocking { post(eq("users/$userId/playlists"), any()) } doReturn
          Result.failure(Exception("Playlist creation failed"))
    }

    // Act
    viewModel.createBeatLinkPlaylist(playlistName, playlistDescription, tracks)

    testDispatcher.scheduler.advanceUntilIdle()

    // Assert: Verify addTracksToPlaylist is not called
    verify(mockApiRepository).post(eq("users/$userId/playlists"), any())
    verify(mockApiRepository, never()).post(startsWith("playlists/"), any())
  }

  @Test
  fun `getPlaylistTracks fetches tracks successfully`() = runTest {
    // Arrange
    val mockResult =
        Result.success(
            JSONObject().apply {
              put(
                  "items",
                  JSONArray().apply {
                    put(
                        JSONObject().apply {
                          put(
                              "track",
                              JSONObject().apply {
                                put("name", "Track1")
                                put("id", "track123")
                                put("duration_ms", 240000)
                                put("popularity", 85)
                                put(
                                    "album",
                                    JSONObject().apply {
                                      put(
                                          "images",
                                          JSONArray().apply {
                                            put(
                                                JSONObject().apply {
                                                  put("url", "https://example.com/track1.jpg")
                                                })
                                          })
                                    })
                                put(
                                    "artists",
                                    JSONArray().apply {
                                      put(JSONObject().apply { put("name", "Artist1") })
                                    })
                              })
                        })
                  })
            })
    mockApiRepository.stub {
      onBlocking { get("playlists/playlist123/tracks?limit=50") } doReturn mockResult
    }

    val onSuccessMock = mock<(List<SpotifyTrack>) -> Unit>()

    // Act
    viewModel.getPlaylistTracks(
        playlistID = "playlist123",
        onSuccess = { tracks -> onSuccessMock(tracks) },
        onFailure = { fail("Expected success but got failure") })

    testDispatcher.scheduler.advanceUntilIdle()

    // Assert
    val expectedTracks =
        listOf(
            SpotifyTrack(
                name = "Track1",
                artist = "Artist1",
                trackId = "track123",
                cover = "https://example.com/track1.jpg",
                duration = 240000,
                popularity = 85,
                state = State.PAUSE))
    verify(onSuccessMock).invoke(expectedTracks)
    verify(mockApiRepository).get("playlists/playlist123/tracks?limit=50")
  }

  @Test
  fun `getPlaylistTracks handles failure gracefully`() = runTest {
    // Arrange
    val exception = Exception("Network error")
    val mockResult = Result.failure<JSONObject>(exception)
    mockApiRepository.stub {
      onBlocking { get("playlists/playlist123/tracks?limit=50") } doReturn mockResult
    }

    val onFailureMock = mock<(List<SpotifyTrack>) -> Unit>()

    // Act
    viewModel.getPlaylistTracks(
        playlistID = "playlist123",
        onSuccess = { fail("Expected failure but got success") },
        onFailure = { tracks -> onFailureMock(tracks) })

    testDispatcher.scheduler.advanceUntilIdle()

    // Assert
    verify(onFailureMock).invoke(emptyList())
    verify(mockApiRepository).get("playlists/playlist123/tracks?limit=50")
  }

  @Test
  fun testCreateSpotifyTrack() {
    // Mock JSON response for a track
    val trackJson =
        """
            {
                "name": "Song Title",
                "id": "12345",
                "artists": [{"name": "Artist Name"}],
                "album": {
                    "images": [{"url": "https://example.com/cover.jpg"}]
                },
                "duration_ms": 240000,
                "popularity": 90
            }
        """
            .trimIndent()

    // Create a JSONObject from the string
    val trackObject = JSONObject(trackJson)

    // Call the createSpotifyTrack method with the mocked JSON
    val spotifyTrack = viewModel.createSpotifyTrack(trackObject)

    // Assert that the values are correctly mapped
    assertEquals("Song Title", spotifyTrack.name)
    assertEquals("Artist Name", spotifyTrack.artist)
    assertEquals("12345", spotifyTrack.trackId)
    assertEquals("https://example.com/cover.jpg", spotifyTrack.cover)
    assertEquals(240000, spotifyTrack.duration)
    assertEquals(90, spotifyTrack.popularity)
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
                                put("name", "Artist1")
                                put("popularity", 90)
                                put("genres", JSONArray().apply { put("Rock") })
                                put(
                                    "images",
                                    JSONArray().apply {
                                      put(
                                          JSONObject().apply {
                                            put("url", "https://example.com/artist1.jpg")
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
                                put("name", "Track1")
                                put("id", "track123")
                                put("duration_ms", 240000)
                                put("popularity", 85)
                                put(
                                    "album",
                                    JSONObject().apply {
                                      put(
                                          "images",
                                          JSONArray().apply {
                                            put(
                                                JSONObject().apply {
                                                  put("url", "https://example.com/track1.jpg")
                                                })
                                          })
                                    })
                                put(
                                    "artists",
                                    JSONArray().apply {
                                      put(JSONObject().apply { put("name", "Artist1") })
                                    })
                              })
                        })
                  })
            })
    mockApiRepository.stub {
      onBlocking { get("search?q=query&type=artist,track&market=CH&limit=20") } doReturn mockResult
    }

    val onSuccessMock = mock<(List<SpotifyArtist>, List<SpotifyTrack>) -> Unit>()

    // Act
    viewModel.searchArtistsAndTracks(
        query = "query",
        onSuccess = { artists, tracks -> onSuccessMock(artists, tracks) },
        onFailure = { _, _ -> fail("Expected success but got failure") })

    testDispatcher.scheduler.advanceUntilIdle()

    // Assert
    val expectedArtists =
        listOf(
            SpotifyArtist(
                image = "https://example.com/artist1.jpg",
                name = "Artist1",
                genres = listOf("Rock"),
                popularity = 90))
    val expectedTracks =
        listOf(
            SpotifyTrack(
                name = "Track1",
                artist = "Artist1",
                trackId = "track123",
                cover = "https://example.com/track1.jpg",
                duration = 240000,
                popularity = 85,
                state = State.PAUSE))
    verify(onSuccessMock).invoke(expectedArtists, expectedTracks)
    verify(mockApiRepository).get("search?q=query&type=artist,track&market=CH&limit=20")
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
    // Arrange: Mock the JSON response to match the structure expected by createSpotifyTrack
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
                          put(
                              "album",
                              JSONObject().apply {
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

    // Stubbing the repository call to return mockResult
    mockApiRepository.stub {
      onBlocking { get("me/top/tracks?time_range=short_term") } doReturn mockResult
    }

    // Observer to verify the returned data
    val observer = mock<Observer<List<SpotifyTrack>>>()

    // Act: Call the function that fetches the top tracks
    viewModel.getCurrentUserTopTracks(
        onSuccess = { observer.onChanged(it) },
        onFailure = { fail("Expected success but got failure") })

    testDispatcher.scheduler.advanceUntilIdle()

    // Assert: Create expected SpotifyTrack object
    val expectedTracks =
        listOf(
            SpotifyTrack(
                name = "Creep",
                artist = "Radiohead",
                trackId = "track123",
                cover = "https://i.scdn.co/image/ab6761610000e5eba68c0feed141ac1ac2dcab19",
                duration = 238000,
                popularity = 95,
                state = State.PAUSE // Assuming the default state is PAUSE
                ))

    // Verify that the observer is called with the expected list of tracks
    verify(observer).onChanged(expectedTracks)

    // Verify the repository call with the expected endpoint
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
    val mockJsonResponse =
        JSONObject().apply {
          put(
              "item",
              JSONObject().apply {
                put("id", "trackId")
                put("name", "Test Track")
                put("duration_ms", 200000)
                put("popularity", 80)
                put("is_playing", true)
                put(
                    "artists",
                    JSONArray().apply { put(JSONObject().apply { put("name", "Test Artist") }) })
                put(
                    "album",
                    JSONObject().apply {
                      put("id", "albumId")
                      put("name", "Test Album")
                      put("release_date", "2024")
                      put("total_tracks", 10)
                      put(
                          "artists",
                          JSONArray().apply {
                            put(JSONObject().apply { put("name", "Test Artist") })
                          })
                      put(
                          "images",
                          JSONArray().apply {
                            put(JSONObject().apply { put("url", "testCoverUrl") })
                          })
                    })
              })
          put("is_playing", true)
        }

    `when`(mockApiRepository.get("me/player")).thenReturn(Result.success(mockJsonResponse))
    `when`(mockApiRepository.get("me/player/currently-playing"))
        .thenReturn(Result.success(mockJsonResponse))

    // Call updatePlayer
    viewModel.updatePlayer()

    // Advance the dispatcher to ensure the coroutine gets executed
    testDispatcher.scheduler.advanceUntilIdle()

    delay(5000) // Delay to ensure the coroutine gets executed
    // Verify the updated states
    assertTrue(viewModel.playbackActive)
    assertEquals("Test Track", viewModel.currentTrack.name)
    assertEquals("Test Artist", viewModel.currentTrack.artist)
    assertEquals("Test Album", viewModel.currentAlbum.name)
    assertEquals("Test Artist", viewModel.currentArtist.name)
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
                    "total_tracks": 10,
                    "images": [{"url": "image-url"}]
                }
            }
        }
        """)

    // Act
    val result = viewModel.buildAlbum(mockAlbumJson)

    // Assert
    val expectedAlbum =
        SpotifyAlbum(
            "123", "Test Album", "image-url", "Test Artist", 2020, listOf(), 10, listOf(), 0)
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
                "popularity": 80,
                "album": {
                    "images": [{"url": "image-url"}]
                    }
            }
        }
        """)

    // Act
    val result = viewModel.buildTrack(mockTrackJson)

    // Assert
    val expectedTrack =
        SpotifyTrack("Test Track", "Test Artist", "456", "image-url", 300000, 80, State.PLAY)
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
                "popularity": 80,
                "album": {
                    "images": [{"url": "image-url"}]
                    }
            }
        }
        """)

    // Act
    val result = viewModel.buildTrack(mockTrackJson)

    // Assert
    val expectedTrack =
        SpotifyTrack("Test Track", "Test Artist", "456", "image-url", 300000, 80, State.PAUSE)
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

  @Test
  fun `getCurrentUserPlaylists calls repository and returns success result`() = runTest {
    // Arrange
    val mockResult =
        Result.success(
            JSONObject().apply {
              put(
                  "items",
                  JSONArray().apply {
                    put(
                        JSONObject().apply {
                          put("name", "Chill Vibes")
                          put("id", "playlist_123")
                          put("public", true)
                          put("owner", JSONObject().put("id", "owner_123"))
                          put("tracks", JSONObject().put("total", 10))
                          put(
                              "images",
                              JSONArray().apply {
                                put(JSONObject().put("url", "https://example.com/cover.jpg"))
                              })
                        })
                    put(
                        JSONObject().apply {
                          put("name", "Private Mix")
                          put("id", "playlist_456")
                          put("public", false)
                          put("owner", JSONObject().put("id", "owner_456"))
                          put("tracks", JSONObject().put("total", 5))
                          put(
                              "images",
                              JSONArray().apply {
                                put(JSONObject().put("url", "https://example.com/private.jpg"))
                              })
                        })
                  })
            })
    mockApiRepository.stub { onBlocking { get("me/playlists?limit=50") } doReturn mockResult }
    val observer = mock<Observer<List<UserPlaylist>>>()

    // Act
    viewModel.getCurrentUserPlaylists(
        onSuccess = { observer.onChanged(it) },
        onFailure = { fail("Expected success but got failure") })

    testDispatcher.scheduler.advanceUntilIdle()

    // Assert
    val expectedPlaylists =
        listOf(
            UserPlaylist(
                playlistID = "playlist_123",
                ownerID = "owner_123",
                playlistCover = "https://example.com/cover.jpg",
                playlistName = "Chill Vibes",
                playlistPublic = true,
                playlistSongs = emptyList(),
                nbTracks = 10))
    verify(observer).onChanged(expectedPlaylists)
    verify(mockApiRepository).get("me/playlists?limit=50")
  }

  @Test
  fun `getCurrentUserPlaylists calls repository and filters out private playlists`() = runTest {
    // Arrange
    val mockResult =
        Result.success(
            JSONObject().apply {
              put(
                  "items",
                  JSONArray().apply {
                    put(
                        JSONObject().apply {
                          put("name", "Private Mix")
                          put("id", "playlist_456")
                          put("public", false)
                          put("owner", JSONObject().put("id", "owner_456"))
                          put("tracks", JSONObject().put("total", 5))
                          put(
                              "images",
                              JSONArray().apply {
                                put(JSONObject().put("url", "https://example.com/private.jpg"))
                              })
                        })
                  })
            })
    mockApiRepository.stub { onBlocking { get("me/playlists?limit=50") } doReturn mockResult }
    val observer = mock<Observer<List<UserPlaylist>>>()

    // Act
    viewModel.getCurrentUserPlaylists(
        onSuccess = { observer.onChanged(it) },
        onFailure = { fail("Expected success but got failure") })

    testDispatcher.scheduler.advanceUntilIdle()

    // Assert
    verify(observer).onChanged(emptyList()) // No public playlists
    verify(mockApiRepository).get("me/playlists?limit=50")
  }

  @Test
  fun `getCurrentUserPlaylists calls repository and returns failure result`() = runTest {
    // Arrange
    val exception = Exception("Network error")
    val mockResult = Result.failure<JSONObject>(exception)
    mockApiRepository.stub { onBlocking { get("me/playlists?limit=50") } doReturn mockResult }
    val observer = mock<Observer<List<UserPlaylist>>>()

    // Act
    viewModel.getCurrentUserPlaylists(
        onSuccess = { fail("Expected failure but got success") },
        onFailure = { observer.onChanged(it) })

    testDispatcher.scheduler.advanceUntilIdle()

    // Assert
    verify(observer).onChanged(emptyList())
    verify(mockApiRepository).get("me/playlists?limit=50")
  }
}
