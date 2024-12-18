package com.epfl.beatlink.ui.spotify

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.epfl.beatlink.repository.spotify.auth.SpotifyAuthRepository
import com.epfl.beatlink.viewmodel.spotify.auth.AuthState
import com.epfl.beatlink.viewmodel.spotify.auth.SpotifyAuthViewModel

/** A Fake version of SpotifyAuthViewModel for testing */
class FakeSpotifyAuthViewModel(application: Application, repository: SpotifyAuthRepository) :
    SpotifyAuthViewModel(application, repository) {

  // Override the authState with a default value of AuthState.Authenticated
  private val _fakeAuthState = mutableStateOf<AuthState>(AuthState.Authenticated)
  override val authState: State<AuthState>
    get() = _fakeAuthState
}
