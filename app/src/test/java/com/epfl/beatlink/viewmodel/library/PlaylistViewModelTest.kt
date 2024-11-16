package com.epfl.beatlink.viewmodel.library

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.epfl.beatlink.model.library.Playlist
import com.epfl.beatlink.model.library.PlaylistRepository
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
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.eq

class PlaylistViewModelTest {
  @get:Rule val instantExecutorRule = InstantTaskExecutorRule()

  private lateinit var playlistRepository: PlaylistRepository
  private lateinit var playlistViewModel: PlaylistViewModel
  private val testDispatcher = StandardTestDispatcher()

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
          playlistSongs = emptyList(),
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
          playlistSongs = emptyList(),
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
          playlistSongs = listOf("thank god"),
          nbTracks = 1)

  @OptIn(ExperimentalCoroutinesApi::class)
  @Before
  fun setUp() {
    Dispatchers.setMain(testDispatcher)
    playlistRepository = mock(PlaylistRepository::class.java)
    playlistViewModel = PlaylistViewModel(playlistRepository)
  }

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
    playlistViewModel.getPlaylists()
    verify(playlistRepository).getPlaylists(any(), any())
  }

  @Test
  fun addToDoCallsRepository() {
    playlistViewModel.addPlaylist(playlist)
    verify(playlistRepository).addPlaylist(eq(playlist), any(), any())
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
          (invocation.arguments[1] as () -> Unit).invoke() // invoke onSuccess callback
          null
        }
        .`when`(playlistRepository)
        .addPlaylist(eq(playlist), any(), any())

    playlistViewModel.addPlaylist(playlist)

    // Check that getPlaylists() was called after successful addition
    verify(playlistRepository).getPlaylists(any(), any())
  }

  @Test
  fun updatePlaylist_shouldTriggerSuccessCallback_andUpdateSelectedPlaylist() = runTest {
    doAnswer { invocation ->
          (invocation.arguments[1] as () -> Unit).invoke() // invoke onSuccess callback
          null
        }
        .`when`(playlistRepository)
        .updatePlaylist(eq(playlist2), any(), any())

    playlistViewModel.updatePlaylist(playlist2)

    // Check that the selected playlist is updated
    Assert.assertEquals(playlist2, playlistViewModel.selectedPlaylist.value)
    // Verify that getPlaylists() was called after update
    verify(playlistRepository).getPlaylists(any(), any())
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

    // Verify that getPlaylists() was called to refresh the playlist list
    verify(playlistRepository).getPlaylists(any(), any())
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

    // Ensure that onFailure callback was handled by logging or other means (log check could be here
    // if needed)
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

    // Ensure that onFailure callback was handled by logging or other means
  }
}
