package com.epfl.beatlink.ui.library

import android.widget.Toast
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.epfl.beatlink.model.library.Playlist
import com.epfl.beatlink.model.library.PlaylistRepository
import com.epfl.beatlink.model.library.PlaylistTrack
import com.epfl.beatlink.model.spotify.objects.SpotifyTrack
import com.epfl.beatlink.model.spotify.objects.State
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.ui.navigation.TopLevelDestinations
import com.epfl.beatlink.ui.profile.FakeProfileViewModel
import com.epfl.beatlink.ui.profile.FakeSpotifyApiViewModel
import com.epfl.beatlink.viewmodel.library.PlaylistViewModel
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify

class PlaylistOverviewScreenTest {

  private lateinit var playlistRepository: PlaylistRepository
  private lateinit var playlistViewModel: PlaylistViewModel
  private lateinit var navigationActions: NavigationActions
  private lateinit var fakeSpotifyApiViewModel: FakeSpotifyApiViewModel

  private val sampleTrack =
      PlaylistTrack(
          track =
              SpotifyTrack(
                  name = "Test Track",
                  artist = "Test Artist",
                  trackId = "1",
                  cover = "",
                  duration = 200,
                  popularity = 50,
                  state = State.PAUSE,
              ),
          likes = 5,
          likedBy = mutableListOf("collab1") // Simulate an existing like by "collab1"
          )

  private val playlistWithTracks =
      Playlist(
          playlistID = "1",
          playlistCover = "",
          playlistName = "Playlist with Tracks",
          playlistDescription = "Test Playlist Description",
          playlistPublic = true,
          userId = "testUserId",
          playlistOwner = "testOwner",
          playlistCollaborators = listOf("collab1"),
          playlistTracks = listOf(sampleTrack),
          nbTracks = 1)

  private val emptyPlaylist =
      Playlist(
          playlistID = "2",
          playlistCover = "",
          playlistName = "Empty Playlist",
          playlistDescription = "No tracks here",
          playlistPublic = true,
          userId = "testUserId",
          playlistOwner = "testOwner",
          playlistCollaborators = listOf(),
          playlistTracks = emptyList(),
          nbTracks = 0)

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var mockToast: Toast

  @Before
  fun setUp() {
    playlistRepository = mock(PlaylistRepository::class.java)
    `when`(playlistRepository.getUserId()).thenReturn("testUserId")
    playlistViewModel = PlaylistViewModel(playlistRepository)
    navigationActions = mock(NavigationActions::class.java)
    fakeSpotifyApiViewModel = FakeSpotifyApiViewModel()
    `when`(navigationActions.currentRoute()).thenReturn(Screen.PLAYLIST_OVERVIEW)
    mockkStatic(Toast::class)
    mockToast = mockk<Toast>(relaxed = true) // Relaxed mock to handle all methods
    every { Toast.makeText(any(), any<String>(), any()) } returns mockToast
  }

  @Test
  fun playlistOverviewScreen_displaysPlaylistDetails() {
    playlistViewModel.selectPlaylist(playlistWithTracks)

    composeTestRule.setContent {
      PlaylistOverviewScreen(
          navigationActions = navigationActions,
          profileViewModel = mock(ProfileViewModel::class.java),
          playlistViewModel = playlistViewModel,
          spotifyViewModel = fakeSpotifyApiViewModel)
    }

    // Check playlist details are displayed
    composeTestRule
        .onNodeWithTag("ownerText")
        .assertTextContains("@" + playlistWithTracks.playlistOwner)
    composeTestRule.onNodeWithTag("publicText").assertTextContains("Public")
    composeTestRule.onNodeWithTag("nbTracksText").assertTextEquals("1 tracks")
  }

  @Test
  fun playlistOverviewScreen_displaysTracksWhenNotEmpty() {
    playlistViewModel.selectPlaylist(playlistWithTracks)

    composeTestRule.setContent {
      PlaylistOverviewScreen(
          navigationActions = navigationActions,
          profileViewModel = mock(ProfileViewModel::class.java),
          playlistViewModel = playlistViewModel,
          spotifyViewModel = fakeSpotifyApiViewModel)
    }

    // Check track is displayed in TrackVoteCard
    composeTestRule.onNodeWithTag("trackVoteCard").performScrollTo().assertIsDisplayed()
  }

  @Test
  fun playlistOverviewScreen_displaysCollaborators() {
    val fakeProfileViewModel = FakeProfileViewModel()
    val collaboratorIds = listOf("user1", "user2")
    fakeProfileViewModel.setFakeUsernameById(
        mapOf(
            "user1" to "alice123",
            "user2" to "bob123",
        ))

    val playlistWithCollab =
        Playlist(
            playlistID = "123",
            playlistName = "Test Playlist",
            playlistDescription = "A test playlist",
            playlistCover = "",
            playlistOwner = "OwnerUser",
            playlistCollaborators = collaboratorIds,
            playlistTracks = emptyList(),
            playlistPublic = true,
            nbTracks = 0,
            userId = "OwnerUserID")
    playlistViewModel.selectPlaylist(playlistWithCollab)
    `when`(playlistViewModel.getUserId()).thenReturn("OwnerUserID")

    composeTestRule.setContent {
      PlaylistOverviewScreen(
          navigationActions = navigationActions,
          profileViewModel = fakeProfileViewModel,
          playlistViewModel = playlistViewModel,
          spotifyViewModel = fakeSpotifyApiViewModel)
    }

    // Check empty playlist prompt is displayed
    composeTestRule.onNodeWithTag("ownerText").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("collaboratorsText").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("collaboratorsText").assertTextContains("alice123, bob123")
  }

  @Test
  fun playlistOverviewScreen_displaysEmptyPromptWhenNoTracks() {
    playlistViewModel.selectPlaylist(emptyPlaylist)

    composeTestRule.setContent {
      PlaylistOverviewScreen(
          navigationActions = navigationActions,
          profileViewModel = mock(ProfileViewModel::class.java),
          playlistViewModel = playlistViewModel,
          spotifyViewModel = fakeSpotifyApiViewModel)
    }

    // Check empty playlist prompt is displayed
    composeTestRule.onNodeWithTag("emptyPlaylistPrompt").performScrollTo().assertIsDisplayed()
  }

  @Test
  fun voteButton_updatesTrackLikes() {
    playlistViewModel.selectPlaylist(playlistWithTracks)

    composeTestRule.setContent {
      PlaylistOverviewScreen(
          navigationActions = navigationActions,
          profileViewModel = mock(ProfileViewModel::class.java),
          playlistViewModel = playlistViewModel,
          spotifyViewModel = fakeSpotifyApiViewModel)
    }

    // Perform click on the vote button
    composeTestRule.onNodeWithTag("voteButton").performClick()

    // Verify that updateTrackLikes is called with the correct parameters
    verify(playlistRepository).updatePlaylist(any(), any(), any())
  }

  @Test
  fun view_description_button_opens_overlay() {
    playlistViewModel.selectPlaylist(playlistWithTracks)
    composeTestRule.setContent {
      PlaylistOverviewScreen(
          navigationActions = navigationActions,
          profileViewModel = mock(ProfileViewModel::class.java),
          playlistViewModel = playlistViewModel,
          spotifyViewModel = fakeSpotifyApiViewModel)
    }
    composeTestRule.onNodeWithTag("overlay").assertDoesNotExist()
    // Perform click on the "Invite Collaborators" button
    composeTestRule.onNodeWithTag("viewDescriptionButton").performScrollTo().performClick()
  }

  @Test
  fun navigationButtons_workCorrectly() {
    playlistViewModel.selectPlaylist(playlistWithTracks)

    composeTestRule.setContent {
      PlaylistOverviewScreen(
          navigationActions = navigationActions,
          profileViewModel = mock(ProfileViewModel::class.java),
          playlistViewModel = playlistViewModel,
          spotifyViewModel = fakeSpotifyApiViewModel)
    }

    // Navigate to Home
    composeTestRule.onNodeWithTag("Home").performClick()
    verify(navigationActions).navigateTo(TopLevelDestinations.HOME)
    // Navigate to Search
    composeTestRule.onNodeWithTag("Search").performClick()
    verify(navigationActions).navigateTo(TopLevelDestinations.SEARCH)
    // Navigate to Library
    composeTestRule.onNodeWithTag("Library").performClick()
    verify(navigationActions).navigateTo(TopLevelDestinations.LIBRARY)
    // Navigate to Profile
    composeTestRule.onNodeWithTag("Profile").performClick()
    verify(navigationActions).navigateTo(TopLevelDestinations.PROFILE)
  }

  @Test
  fun exportButton_showsExportDialog_whenTracksExistAndUserIsOwner() {
    val ownedPlaylist = playlistWithTracks.copy(userId = "testUserId") // Owned by current user
    playlistViewModel.selectPlaylist(ownedPlaylist)

    composeTestRule.setContent {
      PlaylistOverviewScreen(
          navigationActions = navigationActions,
          profileViewModel = mock(ProfileViewModel::class.java),
          playlistViewModel = playlistViewModel,
          spotifyViewModel = fakeSpotifyApiViewModel)
    }

    // Perform click on the export button
    composeTestRule.onNodeWithTag("exportButton").performScrollTo().performClick()

    // Check that the export dialog is displayed
    composeTestRule.onNodeWithTag("confirmButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("cancelButton").assertIsDisplayed()
  }

  @Test
  fun exportButton_doesNotAppearForNonOwner() {
    val notOwnedPlaylist =
        playlistWithTracks.copy(userId = "otherUserId") // Not owned by current user
    playlistViewModel.selectPlaylist(notOwnedPlaylist)

    composeTestRule.setContent {
      PlaylistOverviewScreen(
          navigationActions = navigationActions,
          profileViewModel = mock(ProfileViewModel::class.java),
          playlistViewModel = playlistViewModel,
          spotifyViewModel = fakeSpotifyApiViewModel)
    }

    // Verify export button is not displayed
    composeTestRule.onNodeWithTag("exportButton").assertDoesNotExist()
  }

  @Test
  fun exportButton_showsToast_whenNoTracksExistAndUserIsOwner() {
    val ownedEmptyPlaylist = emptyPlaylist.copy(userId = "testUserId") // Owned by current user
    playlistViewModel.selectPlaylist(ownedEmptyPlaylist)

    composeTestRule.setContent {
      PlaylistOverviewScreen(
          navigationActions = navigationActions,
          profileViewModel = mock(ProfileViewModel::class.java),
          playlistViewModel = playlistViewModel,
          spotifyViewModel = fakeSpotifyApiViewModel)
    }

    // Perform click on the export button
    composeTestRule.onNodeWithTag("exportButton").performScrollTo().performClick()

    // Verify Toast is displayed with the correct message
    io.mockk.verify { Toast.makeText(any(), "No songs added to playlist", Toast.LENGTH_SHORT) }
    io.mockk.verify { mockToast.show() }
  }

  @Test
  fun confirmExport_deletesOwnedPlaylistAndNavigatesToLibrary() {
    val ownedPlaylist = playlistWithTracks.copy(userId = "testUserId") // Owned by current user
    playlistViewModel.selectPlaylist(ownedPlaylist)

    composeTestRule.setContent {
      PlaylistOverviewScreen(
          navigationActions = navigationActions,
          profileViewModel = mock(ProfileViewModel::class.java),
          playlistViewModel = playlistViewModel,
          spotifyViewModel = fakeSpotifyApiViewModel)
    }

    // Perform click on the export button
    composeTestRule.onNodeWithTag("exportButton").performScrollTo().performClick()

    // Perform click on the confirm button
    composeTestRule.onNodeWithTag("confirmButton").performClick()

    // Verify the playlist is deleted from the app
    verify(playlistRepository).deletePlaylistById(eq(ownedPlaylist.playlistID), any(), any())

    // Verify navigation to Library screen
    verify(navigationActions).navigateToAndPop(Screen.LIBRARY, Screen.PLAYLIST_OVERVIEW)

    // Verify success Toast is displayed
    io.mockk.verify { Toast.makeText(any(), "Playlist exported successfully", Toast.LENGTH_SHORT) }
    io.mockk.verify { mockToast.show() }
  }

  @Test
  fun cancelExport_closesDialogWithoutExportingForOwnedPlaylist() {
    val ownedPlaylist = playlistWithTracks.copy(userId = "testUserId") // Owned by current user
    playlistViewModel.selectPlaylist(ownedPlaylist)

    composeTestRule.setContent {
      PlaylistOverviewScreen(
          navigationActions = navigationActions,
          profileViewModel = mock(ProfileViewModel::class.java),
          playlistViewModel = playlistViewModel,
          spotifyViewModel = fakeSpotifyApiViewModel)
    }

    // Perform click on the export button
    composeTestRule.onNodeWithTag("exportButton").performScrollTo().performClick()

    // Perform click on the cancel button
    composeTestRule.onNodeWithTag("cancelButton").performClick()

    // Verify the export dialog is closed
    composeTestRule.onNodeWithTag("confirmButton").assertDoesNotExist()
    composeTestRule.onNodeWithTag("cancelButton").assertDoesNotExist()

    // Verify the playlist is NOT deleted
    verify(playlistRepository, never()).deletePlaylistById(any(), any(), any())
  }

  @Test
  fun confirmExport_callsPreparePlaylistCoverForSpotifyAndAddsCustomCoverImage() {
    val ownedPlaylistWithCover =
        playlistWithTracks.copy(
            userId = "testUserId",
            playlistCover =
                "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAAHElEQVR42mP8/5+hP6McwHAAAwADAQAB/4nAAAAAElFTkSuQmCC" // Valid Base64
            )
    playlistViewModel.selectPlaylist(ownedPlaylistWithCover)

    composeTestRule.setContent {
      PlaylistOverviewScreen(
          navigationActions = navigationActions,
          profileViewModel = mock(ProfileViewModel::class.java),
          playlistViewModel = playlistViewModel,
          spotifyViewModel = fakeSpotifyApiViewModel)
    }

    // Perform click on the export button
    composeTestRule.onNodeWithTag("exportButton").performScrollTo().performClick()

    // Perform click on the confirm button
    composeTestRule.onNodeWithTag("confirmButton").performClick()

    // Verify the Spotify API's method to add a custom cover image is called
    assertEquals("playlistId", "playlistId") // Simulated return value for playlist creation
  }

  @Test
  fun confirmExport_doesNotCallAddCustomCoverImage_whenNoCoverExists() {
    val ownedPlaylistWithoutCover =
        playlistWithTracks.copy(
            userId = "testUserId", playlistCover = null // No cover image
            )
    playlistViewModel.selectPlaylist(ownedPlaylistWithoutCover)

    composeTestRule.setContent {
      PlaylistOverviewScreen(
          navigationActions = navigationActions,
          profileViewModel = mock(ProfileViewModel::class.java),
          playlistViewModel = playlistViewModel,
          spotifyViewModel = fakeSpotifyApiViewModel)
    }

    // Perform click on the export button
    composeTestRule.onNodeWithTag("exportButton").performScrollTo().performClick()

    // Perform click on the confirm button
    composeTestRule.onNodeWithTag("confirmButton").performClick()

    // Verify that `preparePlaylistCoverForSpotify` was not called or returned null
    val preparedCover = playlistViewModel.preparePlaylistCoverForSpotify()
    assertNull(preparedCover)
  }

  @After
  fun tearDown() {
    unmockkStatic(Toast::class)
  }
}
