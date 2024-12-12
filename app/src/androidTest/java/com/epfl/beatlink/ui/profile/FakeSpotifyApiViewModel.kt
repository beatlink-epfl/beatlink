package com.epfl.beatlink.ui.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.test.core.app.ApplicationProvider
import com.epfl.beatlink.model.library.UserPlaylist
import com.epfl.beatlink.model.spotify.objects.SpotifyAlbum
import com.epfl.beatlink.model.spotify.objects.SpotifyArtist
import com.epfl.beatlink.model.spotify.objects.SpotifyTrack
import com.epfl.beatlink.model.spotify.objects.State
import com.epfl.beatlink.repository.spotify.api.SpotifyApiRepository
import com.epfl.beatlink.viewmodel.spotify.api.SpotifyApiViewModel
import org.mockito.Mockito.mock

open class FakeSpotifyApiViewModel(
    private val apiRepository: SpotifyApiRepository = mock(SpotifyApiRepository::class.java)
) : SpotifyApiViewModel(ApplicationProvider.getApplicationContext(), apiRepository) {

  private var topTracks: List<SpotifyTrack> = emptyList()
  private var topArtists: List<SpotifyArtist> = emptyList()
  private var userPlaylists: List<UserPlaylist> = emptyList()
  private var otherUserPlaylists: List<UserPlaylist> = emptyList()
  private var currentListeningTrack =
      mutableStateOf(SpotifyTrack("", "", "", "", 0, 0, State.PAUSE))

  private var currentListeningAlbum =
      mutableStateOf(SpotifyAlbum("", "", "", "", 0, listOf(), 0, listOf(), 0))

  private var currentListeningArtist = mutableStateOf(SpotifyArtist("", "", listOf(), 0))

  fun setPlaybackState(state: Boolean) {
    playbackActive = state
  }

  fun setTrack(song: SpotifyTrack) {
    currentTrack = song
  }

  fun setAlbum(album: SpotifyAlbum) {
    currentAlbum = album
  }

  fun setArtist(artist: SpotifyArtist) {
    currentArtist = artist
  }

  fun setTopTracks(tracks: List<SpotifyTrack>) {
    topTracks = tracks
  }

  fun setTopArtists(artists: List<SpotifyArtist>) {
    topArtists = artists
  }

  fun setUserPlaylists(playlists: List<UserPlaylist>) {
    userPlaylists = playlists
  }

  fun setOtherUserPlaylists(playlists: List<UserPlaylist>) {
    otherUserPlaylists = playlists
  }

  override fun getCurrentUserTopTracks(
      onSuccess: (List<SpotifyTrack>) -> Unit,
      onFailure: (List<SpotifyTrack>) -> Unit
  ) {
    if (topTracks.isNotEmpty()) {
      onSuccess(topTracks)
    } else {
      onFailure(emptyList())
    }
  }

  override fun getCurrentUserTopArtists(
      onSuccess: (List<SpotifyArtist>) -> Unit,
      onFailure: (List<SpotifyArtist>) -> Unit
  ) {
    if (topArtists.isNotEmpty()) {
      onSuccess(topArtists)
    } else {
      onFailure(emptyList())
    }
  }

  override fun getCurrentUserPlaylists(
      onSuccess: (List<UserPlaylist>) -> Unit,
      onFailure: (List<UserPlaylist>) -> Unit
  ) {
    if (userPlaylists.isNotEmpty()) {
      onSuccess(userPlaylists)
    } else {
      onFailure(emptyList())
    }
  }

  override fun getUserPlaylists(
      userId: String,
      onSuccess: (List<UserPlaylist>) -> Unit,
      onFailure: (List<UserPlaylist>) -> Unit
  ) {
    if (userPlaylists.isNotEmpty()) {
      onSuccess(userPlaylists)
    } else {
      onFailure(emptyList())
    }
  }

  override fun searchArtistsAndTracks(
      query: String,
      onSuccess: (List<SpotifyArtist>, List<SpotifyTrack>) -> Unit,
      onFailure: (List<SpotifyArtist>, List<SpotifyTrack>) -> Unit
  ) {
    if (query.isNotEmpty()) {
      // Directly use topArtists and topTracks
      if (topArtists.isNotEmpty() || topTracks.isNotEmpty()) {
        onSuccess(topArtists, topTracks)
      } else {
        onFailure(emptyList(), emptyList())
      }
    } else {
      onFailure(emptyList(), emptyList())
    }
  }

  override fun createBeatLinkPlaylist(
      playlistName: String,
      playlistDescription: String,
      tracks: List<SpotifyTrack>,
      onResult: (String?) -> Unit
  ) {
    onResult("playlistId")
  }

  override fun updatePlayer() {
    // Simulate fetching playback state
  }
}
