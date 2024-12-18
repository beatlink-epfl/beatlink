package com.epfl.beatlink.ui.library

import android.app.Application
import android.widget.Toast
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.viewmodel.compose.viewModel
import com.epfl.beatlink.model.library.Playlist
import com.epfl.beatlink.model.library.PlaylistTrack
import com.epfl.beatlink.model.spotify.objects.SpotifyTrack
import com.epfl.beatlink.model.spotify.objects.State
import com.epfl.beatlink.repository.library.PlaylistRepository
import com.epfl.beatlink.repository.spotify.auth.SpotifyAuthRepository
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.ui.profile.FakeSpotifyApiViewModel
import com.epfl.beatlink.ui.spotify.FakeSpotifyAuthViewModel
import com.epfl.beatlink.viewmodel.library.PlaylistViewModel
import com.epfl.beatlink.viewmodel.map.user.MapUsersViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any

@Suppress("Unchecked_cast")
class SearchTracksScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions
  private lateinit var fakeSpotifyApiViewModel: FakeSpotifyApiViewModel
  private lateinit var playlistRepository: PlaylistRepository
  private lateinit var playlistViewModel: PlaylistViewModel

  private lateinit var application: Application
  private lateinit var spotifyAuthRepository: SpotifyAuthRepository

  private lateinit var spotifyAuthViewModel: FakeSpotifyAuthViewModel

  private val topSongs =
      listOf(
          SpotifyTrack(
              name = "Track 1",
              artist = "Artist A",
              trackId = "1",
              cover = "",
              duration = 4,
              popularity = 1,
              state = State.PAUSE),
          SpotifyTrack(
              name = "Track 2",
              artist = "Artist B",
              trackId = "2",
              cover = "",
              duration = 3,
              popularity = 2,
              state = State.PAUSE))

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    `when`(navigationActions.currentRoute()).thenReturn(Screen.ADD_TRACK_TO_PLAYLIST)

    fakeSpotifyApiViewModel = FakeSpotifyApiViewModel()
    fakeSpotifyApiViewModel.setTopTracks(topSongs)

    application = mockk<Application>(relaxed = true)
    spotifyAuthRepository = mockk<SpotifyAuthRepository>(relaxed = true)

    spotifyAuthViewModel =
        FakeSpotifyAuthViewModel(application = application, repository = spotifyAuthRepository)

    playlistRepository = mock(PlaylistRepository::class.java)
    playlistViewModel = PlaylistViewModel(playlistRepository)

    composeTestRule.setContent {
      SearchTracksScreen(
          navigationActions = navigationActions,
          spotifyApiViewModel = fakeSpotifyApiViewModel,
          spotifyAuthViewModel = spotifyAuthViewModel,
          viewModel(factory = MapUsersViewModel.Factory),
          playlistViewModel = playlistViewModel)
    }
  }

  @Test
  fun everythingIsDisplayed() {
    composeTestRule.onNodeWithTag("searchScaffold").assertIsDisplayed()
    composeTestRule.onNodeWithTag("shortSearchBarRow").assertIsDisplayed()
    composeTestRule.onNodeWithTag("goBackButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("writableSearchBar").assertIsDisplayed()
  }

  @Test
  fun searchResultsDisplayTracks() {
    // Perform a search
    composeTestRule.onNodeWithTag("writableSearchBar").performTextInput("Track")

    // Ensure all tracks are displayed
    topSongs.forEach { track ->
      // Check each track's name and artist are displayed
      composeTestRule.onNodeWithText(track.name).assertExists()
      composeTestRule.onNodeWithText(track.artist).assertExists()

      // Verify that each track row (with unique testTag) is displayed
      composeTestRule.onNodeWithTag("trackItem-${track.trackId}").assertExists()
    }
  }

  @Test
  fun addTrackToPlaylistSuccess() {
    // Select a playlist
    val testPlaylist =
        Playlist(
            playlistID = "testPlaylistId",
            playlistName = "Test Playlist",
            playlistDescription = "A test playlist",
            playlistCover = "",
            playlistPublic = false,
            userId = "userId",
            playlistOwner = "owner",
            playlistCollaborators = emptyList(),
            playlistTracks = emptyList(),
            nbTracks = 0)

    `when`(playlistRepository.getOwnedPlaylists(any(), any())).thenAnswer {
      (it.arguments[0] as (List<Playlist>) -> Unit).invoke(listOf(testPlaylist))
    }

    playlistViewModel.selectPlaylist(testPlaylist)

    // Perform a search
    composeTestRule.onNodeWithTag("writableSearchBar").performTextInput("Track 1")

    // Simulate the `updatePlaylist` call
    `when`(playlistRepository.updatePlaylist(any(), any(), any())).thenAnswer {
      (it.arguments[1] as () -> Unit).invoke()
    }

    // Click the track item
    composeTestRule.onNodeWithTag("trackItem-1").performClick()

    // Verify that updatePlaylist was called
    verify(playlistRepository).updatePlaylist(any(), any(), any())
  }

  @Test
  fun trackAlreadyInPlaylistAndAddNewTrack() {
    mockkStatic(Toast::class)
    val mockToast = mockk<Toast>(relaxed = true)
    every { Toast.makeText(any(), any<String>(), any()) } returns mockToast
    // Define a test playlist with an existing track
    val testPlaylist =
        Playlist(
            playlistID = "testPlaylistId",
            playlistName = "Test Playlist",
            playlistDescription = "A test playlist",
            playlistCover = "",
            playlistPublic = false,
            userId = "userId",
            playlistOwner = "owner",
            playlistCollaborators = emptyList(),
            playlistTracks =
                listOf(
                    PlaylistTrack(
                        track = topSongs[0], // Track 1 already in the playlist
                        likes = 0,
                        likedBy = mutableListOf())),
            nbTracks = 1)

    `when`(playlistRepository.getOwnedPlaylists(any(), any())).thenAnswer {
      (it.arguments[0] as (List<Playlist>) -> Unit).invoke(listOf(testPlaylist))
    }

    playlistViewModel.selectPlaylist(testPlaylist)

    // Perform a search
    composeTestRule.onNodeWithTag("writableSearchBar").performTextInput("Track")

    // Case 1: Click the track already in the playlist (Track 1)
    composeTestRule.onNodeWithTag("trackItem-1").performClick()

    // Verify that a toast message for "Track already in playlist!" would be shown
    io.mockk.verify { Toast.makeText(any(), "Track already in playlist!", Toast.LENGTH_SHORT) }
    io.mockk.verify { mockToast.show() }

    // Case 2: Add a new track to the playlist (Track 2)
    `when`(playlistRepository.updatePlaylist(any(), any(), any())).thenAnswer {
      (it.arguments[1] as () -> Unit).invoke()
    }
    composeTestRule.onNodeWithTag("writableSearchBar").performTextInput("Track")
    composeTestRule.onNodeWithTag("trackItem-2").performClick()

    // Verify that updatePlaylist was called for adding Track 2
    verify(playlistRepository).updatePlaylist(any(), any(), any())

    io.mockk.verify { Toast.makeText(any(), "Track added to playlist!", Toast.LENGTH_SHORT) }
    io.mockk.verify { mockToast.show() }

    // Verify the track list now includes both tracks
    val updatedTracks =
        playlistViewModel.selectedPlaylist.value?.playlistTracks?.map { it.track.trackId }
    assert(updatedTracks?.contains("1") == true) // Track 1 should remain
    assert(updatedTracks?.contains("2") == true) // Track 2 should be added
  }

  @Test
  fun testBackNavigation() {
    composeTestRule.onNodeWithTag("goBackButton").performClick()
    verify(navigationActions).goBack()
  }
}
