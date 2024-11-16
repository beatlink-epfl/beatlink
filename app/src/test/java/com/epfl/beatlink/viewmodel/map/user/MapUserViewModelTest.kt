package com.epfl.beatlink.viewmodel.map.user

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.epfl.beatlink.model.map.user.CurrentPlayingTrack
import com.epfl.beatlink.model.map.user.Location
import com.epfl.beatlink.model.map.user.MapUser
import com.epfl.beatlink.model.spotify.objects.SpotifyAlbum
import com.epfl.beatlink.model.spotify.objects.SpotifyArtist
import com.epfl.beatlink.model.spotify.objects.SpotifyTrack
import com.epfl.beatlink.model.spotify.objects.State
import com.epfl.beatlink.repository.map.user.MapUsersRepositoryFirestore
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MapUserViewModelTest {

  @get:Rule val instantTaskExecutorRule = InstantTaskExecutorRule()

  private lateinit var viewModel: MapUsersViewModel
  private lateinit var repository: MapUsersRepositoryFirestore
  private val testDispatcher = StandardTestDispatcher()

  @Before
  fun setup() {
    repository = mockk(relaxed = true)

    // Mock repository init to set authState to true
    every { repository.init(any()) } answers
        {
          firstArg<() -> Unit>().invoke() // Simulates callback that sets _authState to true
        }
    viewModel = MapUsersViewModel(repository)
    Dispatchers.setMain(testDispatcher)
  }

  @Test
  fun `initial authState is true`() = runTest {
    val authState = viewModel.authState.first()
    assertEquals(true, authState)
  }

  @Test
  fun `fetchMapUsers updates mapUsers on success`() = runTest {
    val fakeLocation = mockk<Location>()
    val currentTrack = CurrentPlayingTrack("Song", "Artist", "Album", "URL")
    val fakeUsers = listOf(MapUser("user1", currentTrack, fakeLocation))
    coEvery { repository.getMapUsers(any(), any(), any(), any()) } answers
        {
          thirdArg<(List<MapUser>) -> Unit>().invoke(fakeUsers)
        }

    viewModel.fetchMapUsers(fakeLocation, 1000.0)
    testDispatcher.scheduler.advanceUntilIdle()

    assertEquals(fakeUsers, viewModel.mapUsers.first())
  }

  @Test
  fun `updatePlayback correctly sets playbackState`() = runTest {
    val authState = viewModel.authState.first()
    assertEquals(true, authState)

    val album =
        SpotifyAlbum(
            name = "Album",
            cover = "URL",
            spotifyId = "spotifyId",
            artist = "artist",
            year = 2000,
            tracks = emptyList(),
            size = 120,
            genres = emptyList(),
            popularity = 80)
    val track =
        SpotifyTrack(
            name = "Song",
            trackId = "trackId",
            cover = "cover",
            duration = 120,
            state = State.PLAY,
            popularity = 80)
    val artist =
        SpotifyArtist(name = "Artist", image = "image", genres = listOf("genre"), popularity = 80)

    viewModel.updatePlayback(album, track, artist)
    testDispatcher.scheduler.advanceUntilIdle()

    // Verify playback state is updated
    val expectedTrack = CurrentPlayingTrack("Song", "Artist", "Album", "URL")
    assertEquals(expectedTrack, viewModel.playbackState.first())
  }

  @Test
  fun `updatePlayback with null track sets playbackState to null and deletes user`() = runTest {
    // Set _mapUser to a non-null user initially
    val fakeLocation = mockk<Location>()
    val currentTrack = CurrentPlayingTrack("Song", "Artist", "Album", "URL")

    val album =
        SpotifyAlbum(
            name = "Album",
            cover = "URL",
            spotifyId = "spotifyId",
            artist = "artist",
            year = 2000,
            tracks = emptyList(),
            size = 120,
            genres = emptyList(),
            popularity = 80)
    val track =
        SpotifyTrack(
            name = "Song",
            trackId = "trackId",
            cover = "cover",
            duration = 120,
            state = State.PLAY,
            popularity = 80)
    val artist =
        SpotifyArtist(name = "Artist", image = "image", genres = listOf("genre"), popularity = 80)

    viewModel.updatePlayback(album, track, artist)
    testDispatcher.scheduler.advanceUntilIdle()
    // Verify the playback state is set
    assertEquals(currentTrack, viewModel.playbackState.first())

    viewModel.addMapUser("user1", fakeLocation)
    testDispatcher.scheduler.advanceUntilIdle()

    val album2 =
        SpotifyAlbum(
            name = "",
            cover = "",
            spotifyId = "spotifyId",
            artist = "artist",
            year = 2000,
            tracks = emptyList(),
            size = 120,
            genres = emptyList(),
            popularity = 80)
    val track2 =
        SpotifyTrack(
            name = "",
            trackId = "trackId",
            cover = "cover",
            duration = 120,
            state = State.PLAY,
            popularity = 80)
    val artist2 =
        SpotifyArtist(name = "", image = "image", genres = listOf("genre"), popularity = 80)

    viewModel.updatePlayback(album2, track2, artist2)
    testDispatcher.scheduler.advanceUntilIdle()

    // Verify playbackState is null
    assertEquals(null, viewModel.playbackState.first())

    // Verify _mapUser is set to null
    assertEquals(null, viewModel.mapUser.first())

    // Verify deleteMapUser was called
    coVerify { repository.deleteMapUser(any(), any()) }
  }

  @Test
  fun `addMapUser calls repository and updates mapUser`() = runTest {
    val fakeLocation = mockk<Location>()
    val currentTrack = CurrentPlayingTrack("Song", "Artist", "Album", "URL")
    val album =
        SpotifyAlbum(
            name = "Album",
            cover = "URL",
            spotifyId = "spotifyId",
            artist = "artist",
            year = 2000,
            tracks = emptyList(),
            size = 120,
            genres = emptyList(),
            popularity = 80)
    val track =
        SpotifyTrack(
            name = "Song",
            trackId = "trackId",
            cover = "cover",
            duration = 120,
            state = State.PLAY,
            popularity = 80)
    val artist =
        SpotifyArtist(name = "Artist", image = "image", genres = listOf("genre"), popularity = 80)

    viewModel.updatePlayback(album, track, artist)
    testDispatcher.scheduler.advanceUntilIdle()

    // Verify the playback state is set
    assertEquals(currentTrack, viewModel.playbackState.first())

    viewModel.addMapUser("user1", fakeLocation)
    testDispatcher.scheduler.advanceUntilIdle()

    assertEquals("user1", viewModel.mapUser.first()?.username)
  }

  @Test
  fun `updateMapUser updates existing user in repository`() = runTest {
    // Step 1: Set up an initial location and add a user
    val initialLocation = mockk<Location>()
    val updatedLocation = mockk<Location>()
    val album =
        SpotifyAlbum(
            name = "Album",
            cover = "URL",
            spotifyId = "spotifyId",
            artist = "artist",
            year = 2000,
            tracks = emptyList(),
            size = 120,
            genres = emptyList(),
            popularity = 80)
    val track =
        SpotifyTrack(
            name = "Song",
            trackId = "trackId",
            cover = "cover",
            duration = 120,
            state = State.PLAY,
            popularity = 80)
    val artist =
        SpotifyArtist(name = "Artist", image = "image", genres = listOf("genre"), popularity = 80)

    viewModel.updatePlayback(album, track, artist)
    testDispatcher.scheduler.advanceUntilIdle()

    // Add the user with the initial location
    viewModel.addMapUser("user1", initialLocation)
    testDispatcher.scheduler.advanceUntilIdle()

    // Step 2: Update the user's location
    viewModel.updateMapUser(updatedLocation)
    testDispatcher.scheduler.advanceUntilIdle()

    // Step 3: Verify that updateMapUser was called in the repository with the updated location
    coVerify {
      repository.updateMapUser(
          match { it.username == "user1" && it.location == updatedLocation },
          any(), // onSuccess lambda
          any() // onFailure lambda
          )
    }
  }

  @Test
  fun `fetchMapUsers calls getMapUsers on repository`() = runTest {
    val fakeLocation = mockk<Location>()
    val radius = 1000.0

    viewModel.fetchMapUsers(fakeLocation, radius)
    testDispatcher.scheduler.advanceUntilIdle()

    // Verify that getMapUsers was called with correct parameters
    coVerify { repository.getMapUsers(fakeLocation, radius, any(), any()) }
  }

  @Test
  fun `addMapUser calls addMapUser on repository`() = runTest {
    val fakeLocation = mockk<Location>()
    val album =
        SpotifyAlbum(
            name = "Album",
            cover = "URL",
            spotifyId = "spotifyId",
            artist = "artist",
            year = 2000,
            tracks = emptyList(),
            size = 120,
            genres = emptyList(),
            popularity = 80)
    val track =
        SpotifyTrack(
            name = "Song",
            trackId = "trackId",
            cover = "cover",
            duration = 120,
            state = State.PLAY,
            popularity = 80)
    val artist =
        SpotifyArtist(name = "Artist", image = "image", genres = listOf("genre"), popularity = 80)

    viewModel.updatePlayback(album, track, artist)
    testDispatcher.scheduler.advanceUntilIdle()

    viewModel.addMapUser("user1", fakeLocation)
    testDispatcher.scheduler.advanceUntilIdle()

    // Verify that addMapUser was called with the correct user
    coVerify {
      repository.addMapUser(
          match { it.username == "user1" && it.location == fakeLocation }, any(), any())
    }
  }

  @Test
  fun `updateMapUser calls updateMapUser on repository`() = runTest {
    val fakeLocation = mockk<Location>()
    val album =
        SpotifyAlbum(
            name = "Album",
            cover = "URL",
            spotifyId = "spotifyId",
            artist = "artist",
            year = 2000,
            tracks = emptyList(),
            size = 120,
            genres = emptyList(),
            popularity = 80)
    val track =
        SpotifyTrack(
            name = "Song",
            trackId = "trackId",
            cover = "cover",
            duration = 120,
            state = State.PLAY,
            popularity = 80)
    val artist =
        SpotifyArtist(name = "Artist", image = "image", genres = listOf("genre"), popularity = 80)

    viewModel.updatePlayback(album, track, artist)
    testDispatcher.scheduler.advanceUntilIdle()

    viewModel.addMapUser("user1", fakeLocation)
    testDispatcher.scheduler.advanceUntilIdle()

    // Now call updateMapUser
    viewModel.updateMapUser(fakeLocation)
    testDispatcher.scheduler.advanceUntilIdle()

    // Verify that updateMapUser was called with the correct user
    coVerify {
      repository.updateMapUser(
          match { it.username == "user1" && it.location == fakeLocation }, any(), any())
    }
  }

  @Test
  fun `deleteMapUser calls deleteMapUser on repository`() = runTest {
    viewModel.deleteMapUser()
    testDispatcher.scheduler.advanceUntilIdle()

    // Verify that deleteMapUser was called on the repository
    coVerify { repository.deleteMapUser(any(), any()) }
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
  }
}
