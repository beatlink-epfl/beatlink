package com.epfl.beatlink.ui.profile

import android.os.Handler
import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.epfl.beatlink.model.spotify.objects.SpotifyArtist
import com.epfl.beatlink.model.spotify.objects.SpotifyTrack
import com.epfl.beatlink.repository.spotify.api.SpotifyApiRepository
import com.epfl.beatlink.viewmodel.spotify.api.SpotifyApiViewModel
import org.mockito.Mockito.mock

open class FakeSpotifyApiViewModel(
    private val apiRepository: SpotifyApiRepository = mock(SpotifyApiRepository::class.java)
) : SpotifyApiViewModel(ApplicationProvider.getApplicationContext(), apiRepository) {

  private var topTracks: List<SpotifyTrack> = emptyList()
  private var topArtists: List<SpotifyArtist> = emptyList()

  fun setTopTracks(tracks: List<SpotifyTrack>) {
    topTracks = tracks
  }

  fun setTopArtists(artists: List<SpotifyArtist>) {
    topArtists = artists
  }

  override fun getCurrentUserTopTracks(
      onSuccess: (List<SpotifyTrack>) -> Unit,
      onFailure: (List<SpotifyTrack>) -> Unit
  ) {
    // Dispatch on the main thread
    Handler(Looper.getMainLooper()).post {
      if (topTracks.isNotEmpty()) {
        onSuccess(topTracks)
      } else {
        onFailure(emptyList())
      }
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
}
