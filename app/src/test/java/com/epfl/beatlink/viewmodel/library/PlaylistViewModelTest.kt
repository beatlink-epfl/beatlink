package com.epfl.beatlink.viewmodel.library

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.epfl.beatlink.model.library.Playlist
import com.epfl.beatlink.model.library.PlaylistRepository
import com.epfl.beatlink.model.library.PlaylistTrack
import com.epfl.beatlink.model.spotify.objects.SpotifyTrack
import com.epfl.beatlink.model.spotify.objects.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class PlaylistViewModelTest {
  @get:Rule val instantExecutorRule = InstantTaskExecutorRule()

  private lateinit var playlistRepository: PlaylistRepository
  private lateinit var playlistViewModel: PlaylistViewModel
  private val testDispatcher = StandardTestDispatcher()

  private val track1 =
      PlaylistTrack(
          track =
              SpotifyTrack(
                  name = "thank god",
                  artist = "travis",
                  trackId = "1",
                  cover = "",
                  duration = 1,
                  popularity = 2,
                  state = State.PAUSE),
          likes = 1,
          likedBy = mutableListOf("user1"))

  private val track2 =
      PlaylistTrack(
          track =
              SpotifyTrack(
                  name = "my eyes",
                  artist = "travis",
                  trackId = "2",
                  cover = "",
                  duration = 1,
                  popularity = 3,
                  state = State.PAUSE),
          likes = 0,
          likedBy = mutableListOf())

  private val playlist =
      Playlist(
          playlistID = "1",
          playlistCover = "",
          playlistName = "playlist",
          playlistDescription = "testingggg",
          playlistPublic = false,
          userId = "testUserId",
          playlistOwner = "luna",
          playlistCollaborators = emptyList(),
          playlistTracks = listOf(track1, track2),
          nbTracks = 2)

  @OptIn(ExperimentalCoroutinesApi::class)
  @Before
  fun setUp() {
    Dispatchers.setMain(testDispatcher)
    playlistRepository = mock(PlaylistRepository::class.java)
    playlistViewModel = PlaylistViewModel(playlistRepository)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @After
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun getNewUid() {
    `when`(playlistRepository.getNewUid()).thenReturn("uid")
    assertThat(playlistViewModel.getNewUid(), `is`("uid"))
  }

  @Test
  fun getPlaylistsCallsRepository() {
    playlistViewModel.getOwnedPlaylists()
    verify(playlistRepository).getOwnedPlaylists(any(), any())
  }

  @Test
  fun getSharedPlaylistsCallsRepository() {
    playlistViewModel.getSharedPlaylists()
    verify(playlistRepository).getSharedPlaylists(any(), any())
  }

  @Test
  fun getPublicPlaylistsCallRepository() {
    playlistViewModel.getPublicPlaylists()
    verify(playlistRepository).getPublicPlaylists(any(), any())
  }

  @Test
  fun addPlaylistCallsRepository() {
    playlistViewModel.addPlaylist(playlist)
    verify(playlistRepository).addPlaylist(eq(playlist), any(), any())
  }

  @Test
  fun updateCollaboratorsCallsRepository() {
    playlistViewModel.updateCollaborators(playlist, listOf())
    verify(playlistRepository).updatePlaylistCollaborators(eq(playlist), any(), any(), any())
  }

  @Test
  fun deletePlaylistCallsRepository() {
    playlistViewModel.deletePlaylist("test")
    verify(playlistRepository).deletePlaylistById(eq("test"), any(), any())
  }

  @Test
  fun `selectPlaylist should update selectedPlaylist with the given playlist`() {
    playlistViewModel.selectPlaylist(playlist)
    Assert.assertEquals(playlist, playlistViewModel.selectedPlaylist.value)
  }

  @Test
  fun updateTrackLikes_shouldAddLike_whenUserHasNotLiked() = runTest {
    // Arrange
    playlistViewModel.selectPlaylist(playlist)
    val trackId = track2.track.trackId // Track initially not liked by the user
    val userId = "newUser"

    doAnswer { invocation ->
          val callback = invocation.arguments[1] as? () -> Unit
          callback?.invoke()
          null
        }
        .whenever(playlistRepository)
        .updatePlaylist(any(), any(), any())

    // Act
    playlistViewModel.updateTrackLikes(trackId, userId)

    // Assert
    verify(playlistRepository).updatePlaylist(any(), any(), any())
    val updatedTrack =
        playlistViewModel.selectedPlaylist.value?.playlistTracks?.find {
          it.track.trackId == trackId
        }
    Assert.assertNotNull(updatedTrack)
    Assert.assertTrue(updatedTrack?.likedBy?.contains(userId) == true)
    Assert.assertEquals(1, updatedTrack?.likes) // Incremented from 0 to 1
  }

  @Test
  fun updateTrackLikes_shouldRemoveLike_whenUserHasAlreadyLiked() = runTest {
    // Arrange
    playlistViewModel.selectPlaylist(playlist)
    val trackId = track1.track.trackId // Track initially liked by "user1"
    val userId = "user1"

    doAnswer { invocation ->
          val callback = invocation.arguments[1] as? () -> Unit
          callback?.invoke()
          null
        }
        .whenever(playlistRepository)
        .updatePlaylist(any(), any(), any())

    // Act
    playlistViewModel.updateTrackLikes(trackId, userId)

    // Assert
    verify(playlistRepository).updatePlaylist(any(), any(), any())
    val updatedTrack =
        playlistViewModel.selectedPlaylist.value?.playlistTracks?.find {
          it.track.trackId == trackId
        }
    Assert.assertNotNull(updatedTrack)
    Assert.assertFalse(updatedTrack?.likedBy?.contains(userId) == true)
    Assert.assertEquals(0, updatedTrack?.likes) // Decremented from 1 to 0
  }

  @Test
  fun updateTrackLikes_shouldDoNothing_whenNoPlaylistSelected() = runTest {
    // Arrange
    val trackId = track1.track.trackId
    val userId = "newUser"

    // Act
    playlistViewModel.updateTrackLikes(trackId, userId)

    // Assert
    verify(playlistRepository, never()).updatePlaylist(any(), any(), any())
  }

  @Test
  fun addPlaylist_shouldTriggerSuccessCallback_andRefreshPlaylists() = runTest {
    doAnswer { invocation ->
          (invocation.arguments[1] as? () -> Unit)?.invoke()
          null
        }
        .whenever(playlistRepository)
        .addPlaylist(eq(playlist), any(), any())

    playlistViewModel.addPlaylist(playlist)

    verify(playlistRepository).getOwnedPlaylists(any(), any())
  }

  @Test
  fun addPlaylist_shouldCallOnFailure_whenAddFails() = runTest {
    val exception = Exception("Failed to add playlist")
    doAnswer { invocation ->
          (invocation.arguments[2] as (Exception) -> Unit).invoke(
              exception) // invoke onFailure callback
          null
        }
        .`when`(playlistRepository)
        .addPlaylist(eq(playlist), any(), any())

    playlistViewModel.addPlaylist(playlist)
  }

  @Test
  fun deletePlaylist_shouldTriggerSuccessCallback_andRefreshPlaylists() = runTest {
    doAnswer { invocation ->
          (invocation.arguments[1] as () -> Unit).invoke()
          null
        }
        .whenever(playlistRepository)
        .deletePlaylistById(eq(playlist.playlistID), any(), any())

    playlistViewModel.deletePlaylist(playlist.playlistID)

    verify(playlistRepository).deletePlaylistById(eq(playlist.playlistID), any(), any())
    verify(playlistRepository).getOwnedPlaylists(any(), any())
  }

  @Test
  fun updatePlaylist_shouldCallOnFailure_whenUpdateFails() = runTest {
    val exception = Exception("Failed to update playlist")
    doAnswer { invocation ->
          (invocation.arguments[2] as (Exception) -> Unit).invoke(
              exception) // invoke onFailure callback
          null
        }
        .`when`(playlistRepository)
        .updatePlaylist(eq(playlist), any(), any())

    playlistViewModel.updatePlaylist(playlist)
  }

  @Test
  fun deletePlaylist_shouldCallOnFailure_whenDeleteFails() = runTest {
    val playlistUID = "test_playlist_id"
    val exception = Exception("Failed to delete playlist")
    doAnswer { invocation ->
          (invocation.arguments[2] as (Exception) -> Unit).invoke(
              exception) // invoke onFailure callback
          null
        }
        .`when`(playlistRepository)
        .deletePlaylistById(eq(playlistUID), any(), any())

    playlistViewModel.deletePlaylist(playlistUID)
  }

  @Test
  fun updateTrackCount_shouldTriggerSuccessCallback_andRefreshPlaylists() = runTest {
    val newTrackCount = 5
    doAnswer { invocation ->
          (invocation.arguments[2] as () -> Unit).invoke() // invoke onSuccess callback
          null
        }
        .`when`(playlistRepository)
        .updatePlaylistTrackCount(eq(playlist), eq(newTrackCount), any(), any())

    playlistViewModel.updateTrackCount(playlist, newTrackCount)

    verify(playlistRepository).getOwnedPlaylists(any(), any())
  }
}
