package com.epfl.beatlink.viewmodel.library

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.epfl.beatlink.model.library.Playlist
import com.epfl.beatlink.model.library.PlaylistRepository
import com.epfl.beatlink.model.library.PlaylistTrack
import com.epfl.beatlink.model.spotify.objects.SpotifyTrack
import com.epfl.beatlink.model.spotify.objects.State
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Assert
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@Suppress("UNCHECKED_CAST")
class PlaylistViewModelTest {
  @get:Rule val instantExecutorRule = InstantTaskExecutorRule()

  private lateinit var playlistRepository: PlaylistRepository
  private lateinit var playlistViewModel: PlaylistViewModel
  private val testDispatcher = StandardTestDispatcher()
  private val mockContext: Context = mock()
  private val mockUri: Uri = mock()

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
  private val playlist1 =
      Playlist(
          playlistID = "1",
          playlistCover = "",
          playlistName = "playlist 1",
          playlistDescription = "this is a description",
          playlistPublic = false,
          userId = "testUserId",
          playlistOwner = "luna",
          playlistCollaborators = emptyList(),
          playlistTracks = emptyList(),
          nbTracks = 0)
  private val playlist2 =
      Playlist(
          playlistID = "2",
          playlistCover = "iVBORw0KGgoAAAANSUhEUgAAAAUA",
          playlistName = "playlist 2",
          playlistDescription = "testingggg 2",
          playlistPublic = false,
          userId = "testUserId2",
          playlistOwner = "luna2",
          playlistCollaborators = emptyList(),
          playlistTracks = listOf(track1),
          nbTracks = 1)

  private val playlistEmpty =
      Playlist(
          playlistID = "",
          playlistCover = "",
          playlistName = "",
          playlistDescription = "",
          playlistPublic = false,
          userId = "",
          playlistOwner = "",
          playlistCollaborators = emptyList(),
          playlistTracks = emptyList(),
          nbTracks = 0)

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

  @Test
  fun addTrack_shouldAddTrackToPlaylist_andTriggerSuccessCallback() = runTest {
    // Arrange
    playlistViewModel.selectPlaylist(playlist)
    val newTrack =
        PlaylistTrack(
            track =
                SpotifyTrack(
                    name = "new song",
                    artist = "artist",
                    trackId = "3",
                    cover = "",
                    duration = 1,
                    popularity = 2,
                    state = State.PAUSE),
            likes = 0,
            likedBy = mutableListOf())

    doAnswer { invocation ->
          val onSuccess = invocation.arguments[1] as? () -> Unit
          onSuccess?.invoke()
          null
        }
        .whenever(playlistRepository)
        .updatePlaylist(any(), any(), any())

    // Act
    var successCallbackTriggered = false
    playlistViewModel.addTrack(
        newTrack, onSuccess = { successCallbackTriggered = true }, onFailure = {})

    // Assert
    val updatedPlaylist = playlistViewModel.selectedPlaylist.value
    Assert.assertTrue(successCallbackTriggered)
    Assert.assertNotNull(updatedPlaylist)
    Assert.assertEquals(3, updatedPlaylist?.playlistTracks?.size) // 2 original + 1 new
    Assert.assertTrue(updatedPlaylist?.playlistTracks?.contains(newTrack) == true)
    verify(playlistRepository).updatePlaylist(eq(updatedPlaylist!!), any(), any())
  }

  @Test
  fun addTrack_shouldCallOnFailure_whenNoPlaylistSelected() = runTest {
    // Arrange
    val newTrack =
        PlaylistTrack(
            track =
                SpotifyTrack(
                    name = "new track",
                    artist = "new artist",
                    trackId = "3",
                    cover = "",
                    duration = 180,
                    popularity = 50,
                    state = State.PAUSE),
            likes = 0,
            likedBy = mutableListOf())

    var failureCallbackCalled = false

    // Act
    playlistViewModel.addTrack(
        newTrack, onSuccess = {}, onFailure = { failureCallbackCalled = true })

    // Assert
    verify(playlistRepository, never()).updatePlaylist(any(), any(), any())
    Assert.assertTrue(failureCallbackCalled) // Ensure failure callback is triggered
  }

  @Test
  fun addTrack_shouldCallOnFailure_whenUpdateFails() = runTest {
    val exception = Exception("Failed to add playlist")
    doAnswer { invocation ->
          (invocation.arguments[2] as (Exception) -> Unit).invoke(
              exception) // invoke onFailure callback
          null
        }
        .`when`(playlistRepository)
        .updatePlaylist(eq(playlist), any(), any())

    playlistViewModel.addTrack(track1, onSuccess = {}, onFailure = {})
  }

  @Test
  fun `preloadTemporaryState sets correct temporary values when uninitialized`() = runTest {
    playlistViewModel.preloadTemporaryState(playlist1)
    // Assert
    assertEquals("playlist 1", playlistViewModel.tempPlaylistTitle.first())
    assertEquals("this is a description", playlistViewModel.tempPlaylistDescription.first())
    assertEquals(false, playlistViewModel.tempPlaylistIsPublic.first())
    assertEquals(emptyList<String>(), playlistViewModel.tempPlaylistCollaborators.first())
    assertEquals(true, playlistViewModel.isTempStateInitialized.first())
  }

  @Test
  fun `resetTemporaryState clears all temporary values`() = runTest {
    playlistViewModel.preloadTemporaryState(playlist1)
    playlistViewModel.resetTemporaryState()
    // Assert
    assertEquals("", playlistViewModel.tempPlaylistTitle.first())
    assertEquals("", playlistViewModel.tempPlaylistDescription.first())
    assertEquals(false, playlistViewModel.tempPlaylistIsPublic.first())
    assertEquals(emptyList<String>(), playlistViewModel.tempPlaylistCollaborators.first())
    assertEquals(false, playlistViewModel.isTempStateInitialized.first())
  }

  @Test
  fun `updateTemporallyTitle updates the temporary title`() = runTest {
    // Act
    playlistViewModel.updateTemporallyTitle("New Title")
    // Assert
    assertEquals("New Title", playlistViewModel.tempPlaylistTitle.first())
  }

  @Test
  fun `updateTemporallyDescription updates the temporary description`() = runTest {
    // Act
    playlistViewModel.updateTemporallyDescription("New Description")
    // Assert
    assertEquals("New Description", playlistViewModel.tempPlaylistDescription.first())
  }

  @Test
  fun `updateTemporallyIsPublic updates the temporary public state`() = runTest {
    // Act
    playlistViewModel.updateTemporallyIsPublic(true)
    // Assert
    assertEquals(true, playlistViewModel.tempPlaylistIsPublic.first())
  }

  @Test
  fun `updateTemporallyCollaborators updates the temporary collaborators list`() = runTest {
    // Act
    playlistViewModel.updateTemporallyCollaborators(listOf("user1", "user2"))
    // Assert
    assertEquals(listOf("user1", "user2"), playlistViewModel.tempPlaylistCollaborators.first())
  }

  @Test
  fun `uploadPlaylistCover should log error if playlist ID is empty`() {
    // Call the method
    playlistViewModel.uploadPlaylistCover(mockUri, mockContext, playlistEmpty)

    // Verify that the repository is never called
    verify(playlistRepository, never()).uploadPlaylistCover(any(), any(), any())
  }

  @Test
  fun `uploadPlaylistCover should invoke repository if playlist ID is valid`() {
    val testDispatcher = StandardTestDispatcher()

    // Inject the test dispatcher into the ViewModel
    playlistViewModel = PlaylistViewModel(playlistRepository, testDispatcher)

    // Mock the repository method
    doNothing().`when`(playlistRepository).uploadPlaylistCover(any(), any(), any())

    // Call the method
    playlistViewModel.uploadPlaylistCover(mockUri, mockContext, playlist2)

    // Advance the dispatcher to execute the coroutine
    testDispatcher.scheduler.advanceUntilIdle()

    // Verify that the repository method is invoked
    verify(playlistRepository).uploadPlaylistCover(mockUri, mockContext, playlist2)
  }

  @Test
  fun `loadPlaylistCover should invoke repository and callback if playlist ID is valid`() {
    val mockBitmap: Bitmap = mock()
    val callbackSlot = argumentCaptor<(Bitmap?) -> Unit>()

    // Stub the repository to invoke the callback with a mock bitmap
    doAnswer {
          val callback = it.getArgument<(Bitmap?) -> Unit>(1)
          callback(mockBitmap)
        }
        .whenever(playlistRepository)
        .loadPlaylistCover(eq(playlist2), callbackSlot.capture())

    var resultBitmap: Bitmap? = null

    // Call the method
    playlistViewModel.loadPlaylistCover(playlist2) { resultBitmap = it }

    // Verify that the repository method is invoked
    verify(playlistRepository).loadPlaylistCover(eq(playlist2), any())
    // Verify the callback is invoked with the correct bitmap
    assertEquals(mockBitmap, resultBitmap)
  }

  @Test
  fun `loadPlaylistCover should log error if playlist ID is empty`() {
    var resultBitmap: Bitmap? = null

    // Call the method
    playlistViewModel.loadPlaylistCover(playlistEmpty) { resultBitmap = it }

    // Verify that the repository is never called
    verify(playlistRepository, never()).loadPlaylistCover(any(), any())
    // Verify the callback is not invoked
    assertNull(resultBitmap)
  }

  @Test
  fun `getFinalListTracks should return tracks sorted by likes in descending order`() = runTest {
    // Arrange
    val track3 =
        PlaylistTrack(
            track =
                SpotifyTrack(
                    name = "new hit",
                    artist = "artist",
                    trackId = "3",
                    cover = "",
                    duration = 1,
                    popularity = 10,
                    state = State.PAUSE),
            likes = 5,
            likedBy = mutableListOf("user1", "user2"))

    val playlistWithTracks =
        playlist.copy(playlistTracks = listOf(track1, track2, track3), nbTracks = 3)

    playlistViewModel.selectPlaylist(playlistWithTracks)

    // Act
    val finalTracks = playlistViewModel.getFinalListTracks()

    // Assert
    assertEquals(3, finalTracks.size)
    assertEquals(listOf("3", "1", "2"), finalTracks.map { it.trackId }) // Most liked first
  }

  @Test
  fun `getFinalListTracks should limit the list to 50 tracks`() = runTest {
    // Arrange
    val manyTracks =
        (1..100).map { index ->
          PlaylistTrack(
              track =
                  SpotifyTrack(
                      name = "Track $index",
                      artist = "Artist $index",
                      trackId = "$index",
                      cover = "",
                      duration = index,
                      popularity = index,
                      state = State.PAUSE),
              likes = index,
              likedBy = mutableListOf("user${index}"))
        }

    val largePlaylist = playlist.copy(playlistTracks = manyTracks, nbTracks = manyTracks.size)

    playlistViewModel.selectPlaylist(largePlaylist)

    // Act
    val finalTracks = playlistViewModel.getFinalListTracks()

    // Assert
    assertEquals(50, finalTracks.size) // Limited to 50
    assertEquals((100 downTo 51).map { it.toString() }, finalTracks.map { it.trackId })
  }

  @Test
  fun `getFinalListTracks should return an empty list when no playlist is selected`() = runTest {
    // Act
    val finalTracks = playlistViewModel.getFinalListTracks()

    // Assert
    assertTrue(finalTracks.isEmpty())
  }

  @Test
  fun `getFinalListTracks should return an empty list for a playlist with no tracks`() = runTest {
    // Arrange
    playlistViewModel.selectPlaylist(playlist1) // Empty playlist

    // Act
    val finalTracks = playlistViewModel.getFinalListTracks()

    // Assert
    assertTrue(finalTracks.isEmpty())
  }

  @Test
  fun `preparePlaylistCoverForSpotify should return null for invalid Base64 string`() {
    // Arrange
    val invalidBase64String = "InvalidBase64Data"
    val playlistWithInvalidCover = playlist.copy(playlistCover = invalidBase64String)

    playlistViewModel.selectPlaylist(playlistWithInvalidCover)

    // Act
    val result = playlistViewModel.preparePlaylistCoverForSpotify()

    // Assert
    assertNull(result) // Ensure the result is null due to invalid input
  }

  @Test
  fun `preparePlaylistCoverForSpotify should return null if no playlist is selected`() {
    // Act
    val result = playlistViewModel.preparePlaylistCoverForSpotify()

    // Assert
    assertNull(result) // Ensure the result is null when no playlist is selected
  }

  @Test
  fun `preparePlaylistCoverForSpotify should return null if playlistCover is empty`() {
    // Arrange
    val playlistWithoutCover = playlist.copy(playlistCover = "")
    playlistViewModel.selectPlaylist(playlistWithoutCover)

    // Act
    val result = playlistViewModel.preparePlaylistCoverForSpotify()

    // Assert
    assertNull(result) // Ensure the result is null due to empty cover
  }

  @Test
  fun `resetTemporaryState should reset coverImage`() = runTest {
    // Arrange
    playlistViewModel.coverImage.value = mock(Bitmap::class.java) // Simulate a cover image

    // Act
    playlistViewModel.resetTemporaryState()

    // Assert
    assertNull(playlistViewModel.coverImage.value) // Ensure the cover image is reset
  }

  @Test
  fun `deletePlaylist should reset coverImage on success`() = runTest {
    // Arrange
    playlistViewModel.selectPlaylist(playlist)
    playlistViewModel.coverImage.value = mock(Bitmap::class.java) // Simulate a cover image

    doAnswer { invocation ->
          val onSuccess = invocation.arguments[1] as? () -> Unit
          onSuccess?.invoke() // Call the success callback
          null
        }
        .whenever(playlistRepository)
        .deletePlaylistById(eq(playlist.playlistID), any(), any())

    // Act
    playlistViewModel.deletePlaylist(playlist.playlistID)

    // Assert
    assertNull(playlistViewModel.coverImage.value) // Ensure the cover image is reset
  }
}
