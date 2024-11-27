package com.epfl.beatlink.viewmodel.library

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.epfl.beatlink.model.library.Playlist
import com.epfl.beatlink.model.library.PlaylistRepository
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

@OptIn(ExperimentalCoroutinesApi::class)
class PlaylistViewModelTest {
  @get:Rule val instantExecutorRule = InstantTaskExecutorRule()

  private lateinit var playlistRepository: PlaylistRepository
  private lateinit var playlistViewModel: PlaylistViewModel
  private val testDispatcher = StandardTestDispatcher()

  private val song1 =
      SpotifyTrack(
          name = "thank god",
          artist = "travis",
          trackId = "1",
          cover = "",
          duration = 1,
          popularity = 2,
          state = State.PAUSE)

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
          playlistTracks = emptyList(),
          nbTracks = 0)
  private val playlist1 =
      Playlist(
          playlistID = "1",
          playlistCover = "",
          playlistName = "playlist 1",
          playlistDescription = "testingggg",
          playlistPublic = false,
          userId = "testUserId",
          playlistOwner = "luna",
          playlistCollaborators = emptyList(),
          playlistTracks = emptyList(),
          nbTracks = 0)
  private val playlist2 =
      Playlist(
          playlistID = "2",
          playlistCover = "",
          playlistName = "playlist 2",
          playlistDescription = "testingggg 2",
          playlistPublic = false,
          userId = "testUserId2",
          playlistOwner = "luna2",
          playlistCollaborators = emptyList(),
          playlistTracks = listOf(song1),
          nbTracks = 1)

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
    val selectedPlaylist = playlist2

    playlistViewModel.selectPlaylist(selectedPlaylist)

    Assert.assertEquals(selectedPlaylist, playlistViewModel.selectedPlaylist.value)
  }

  @Test
  fun addPlaylist_shouldTriggerSuccessCallback_andRefreshPlaylists() = runTest {
    doAnswer { invocation ->
          // Safely cast and invoke the callback
          (invocation.arguments[1] as? () -> Unit)?.invoke()
              ?: throw IllegalArgumentException(
                  "Argument at index 1 is not a valid callback function")
          null
        }
        .`when`(playlistRepository)
        .addPlaylist(eq(playlist), any(), any())

    playlistViewModel.addPlaylist(playlist)

    // Check that getPlaylists() was called after successful addition
    verify(playlistRepository).getOwnedPlaylists(any(), any())
  }

  @Test
  fun updatePlaylist_shouldTriggerSuccessCallback_andUpdateSelectedPlaylist() = runTest {
    doAnswer { invocation ->
          val callback = invocation.arguments[1] as? () -> Unit
          callback?.invoke() // Invoke the callback if it was correctly casted
          null
        }
        .`when`(playlistRepository)
        .updatePlaylist(eq(playlist2), any(), any())

    playlistViewModel.updatePlaylist(playlist2)

    // Check that the selected playlist is updated
    Assert.assertEquals(playlist2, playlistViewModel.selectedPlaylist.value)
    // Verify that getPlaylists() was called after update
    verify(playlistRepository).getOwnedPlaylists(any(), any())
  }

  @Test
  fun updatePlaylistCollaborators_shouldTriggerSuccessCallback_andUpdateSelectedPlaylist() =
      runTest {
        val newCollabList = listOf("1", "2")
        doAnswer { invocation ->
              val callback = invocation.arguments[1] as? () -> Unit
              callback?.invoke() // Invoke the callback if it was correctly casted
              null
            }
            .`when`(playlistRepository)
            .updatePlaylistCollaborators(eq(playlist2), eq(newCollabList), any(), any())

        playlistViewModel.updateCollaborators(playlist2, newCollabList)

        verify(playlistRepository).getOwnedPlaylists(any(), any())
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
  fun updatePlaylist_shouldCallOnFailure_whenUpdateFails() = runTest {
    val exception = Exception("Failed to update playlist")
    doAnswer { invocation ->
          (invocation.arguments[2] as (Exception) -> Unit).invoke(
              exception) // invoke onFailure callback
          null
        }
        .`when`(playlistRepository)
        .updatePlaylist(eq(playlist2), any(), any())

    playlistViewModel.updatePlaylist(playlist2)
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
  fun deletePlaylist_shouldTriggerSuccessCallback_andRefreshPlaylists() = runTest {
    val playlistID = "test"
    doAnswer { invocation ->
          (invocation.arguments[1] as () -> Unit).invoke() // invoke onSuccess callback
          null
        }
        .`when`(playlistRepository)
        .deletePlaylistById(eq(playlistID), any(), any())

    playlistViewModel.deletePlaylist(playlistID)

    // Assert: Verify both delete and refresh actions
    verify(playlistRepository).deletePlaylistById(eq(playlistID), any(), any())
    verify(playlistRepository).getOwnedPlaylists(any(), any())
  }

  @Test
  fun addTrack_shouldAddTrackToSelectedPlaylist_andTriggerSuccessCallback() = runTest {
    // Arrange
    val selectedPlaylist = playlist2
    playlistViewModel.selectPlaylist(selectedPlaylist)

    val trackToAdd = song1.copy(trackId = "newTrack")
    doAnswer { invocation ->
          // Simulate successful update of playlist
          val callback = invocation.arguments[1] as? () -> Unit
          callback?.invoke()
          null
        }
        .`when`(playlistRepository)
        .updatePlaylist(any(), any(), any())

    doAnswer { invocation ->
          // Simulate successful track count update
          val callback = invocation.arguments[2] as? () -> Unit
          callback?.invoke()
          null
        }
        .`when`(playlistRepository)
        .updatePlaylistTrackCount(any(), any(), any(), any())

    // Act
    var successCallbackInvoked = false
    playlistViewModel.addTrack(
        trackToAdd, onSuccess = { successCallbackInvoked = true }, onFailure = {})

    // Assert
    verify(playlistRepository).updatePlaylist(any(), any(), any())
    verify(playlistRepository)
        .updatePlaylistTrackCount(any(), eq(2), any(), any()) // Total tracks = 2
    assert(successCallbackInvoked) // Ensure success callback is called
  }

  @Test
  fun addTrack_shouldNotAddTrack_whenNoPlaylistSelected_andTriggerFailureCallback() = runTest {
    // Arrange
    val trackToAdd = song1
    var failureCallbackInvoked = false

    // Act
    playlistViewModel.addTrack(
        trackToAdd, onSuccess = {}, onFailure = { failureCallbackInvoked = true })

    // Assert
    verify(playlistRepository, never()).updatePlaylist(any(), any(), any())
    assert(failureCallbackInvoked) // Ensure failure callback is called
  }

  @Test
  fun updateTrackLikes_shouldUpdateTrackLikes_andTriggerPlaylistUpdate() = runTest {
    // Arrange
    val selectedPlaylist = playlist2
    playlistViewModel.selectPlaylist(selectedPlaylist)

    val updatedTrack = song1.copy(likes = 10)
    doAnswer { invocation ->
          // Simulate successful update
          val callback = invocation.arguments[1] as? () -> Unit
          callback?.invoke()
          null
        }
        .`when`(playlistRepository)
        .updatePlaylist(any(), any(), any())

    // Act
    playlistViewModel.updateTrackLikes(updatedTrack)

    // Assert
    verify(playlistRepository).updatePlaylist(any(), any(), any())
    Assert.assertTrue(
        playlistViewModel.selectedPlaylist.value?.playlistTracks?.any { it.likes == 10 } ?: false)
  }

  @Test
  fun updateTrackLikes_shouldDoNothing_whenNoPlaylistSelected() = runTest {
    // Arrange
    val updatedTrack = song1.copy(likes = 5)

    // Act
    playlistViewModel.updateTrackLikes(updatedTrack)

    // Assert
    verify(playlistRepository, never()).updatePlaylist(any(), any(), any())
  }
}
