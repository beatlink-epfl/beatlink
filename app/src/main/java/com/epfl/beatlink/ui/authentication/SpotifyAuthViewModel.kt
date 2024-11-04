package com.epfl.beatlink.ui.authentication

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.epfl.beatlink.model.spotify.SpotifyAuthRepository
import kotlinx.coroutines.launch

open class SpotifyAuthViewModel(
    application: Application,
    private val repository: SpotifyAuthRepository
) : AndroidViewModel(application) {
  private val _authState = mutableStateOf<AuthState>(AuthState.Idle)
  val authState: State<AuthState> = _authState

  @VisibleForTesting internal val _accessToken = mutableStateOf<String?>(null)
  @VisibleForTesting internal val _refreshToken = mutableStateOf<String?>(null)
  @VisibleForTesting internal val _expiryTime = mutableStateOf<Long?>(null)

  init {
    loadTokens(application)
    loadAuthState()
  }

  /** Updates the data values in the view model */
  fun loadTokens(context: Context) {
    val accessToken = repository.getAccessToken(context)
    val refreshToken = repository.getRefreshToken(context)
    val expiryTime = repository.getExpiryTime(context)

    _accessToken.value = accessToken
    _refreshToken.value = refreshToken
    _expiryTime.value = expiryTime
  }

  /** Loads the authentication state based on the tokens' values */
  fun loadAuthState() {
    if (doesTokenExist()) {
      if (isTokenValid()) {
        _authState.value = AuthState.Authenticated
      } else {
        _authState.value = AuthState.Idle
      }
    }
  }

  /** Clears the authentication data stored in the repository */
  fun clearAuthData(context: Context) {
    // Clear the authentication data stored in the repository
    repository.clearAuthData(context)

    // Clear the data values in the view model
    _accessToken.value = null
    _refreshToken.value = null
    _expiryTime.value = null
    _authState.value = AuthState.Idle
  }

  /** Checks if an access token exists in the view model */
  private fun doesTokenExist(): Boolean {
    return _accessToken.value != null
  }

  /** Checks if the access token is still valid */
  private fun isTokenValid(): Boolean {
    val expiryTime = _expiryTime.value

    return expiryTime != null && expiryTime > System.currentTimeMillis()
  }

  /** Requests user authorization to access Spotify data */
  fun requestUserAuthorization(context: Context) {
    val authUrl = repository.buildSpotifyAuthUrl(context)
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(authUrl))
    context.startActivity(intent)
  }

  /** Handles the authorization response from Spotify */
  fun handleAuthorizationResponse(intent: Intent, context: Context) {
    val uri = intent.data
    uri?.let {
      val code = uri.getQueryParameter("code")
      if (code != null) {
        // Authorization was successful, use the code to request an access token
        viewModelScope.launch {
          val result = repository.requestAccessToken(code, context)
          if (result.isSuccess) {
            loadTokens(context) // load the new tokens
            _authState.value = AuthState.Authenticated
          } else {
            _authState.value = AuthState.Idle
          }
        }
      } else {
        _authState.value = AuthState.Idle
      }
    }
  }

  /** Refreshes the access token using the refresh token */
  fun refreshAccessToken(context: Context) {
    val refreshToken = _refreshToken.value
    if (refreshToken != null) {
      viewModelScope.launch {
        // Call the repository function and check the result
        val result = repository.refreshAccessToken(refreshToken, context)
        if (result.isSuccess) {
          loadTokens(context) // load the new tokens
          _authState.value = AuthState.Authenticated
        } else {
          _authState.value = AuthState.Idle
        }
      }
    } else {
      _authState.value = AuthState.Idle
    }
  }
}

/** Represents the possible states of the authentication process */
sealed class AuthState {
  object Authenticated : AuthState()

  object Idle : AuthState()
}

class SpotifyAuthViewModelFactory(
    private val application: Application,
    private val repository: SpotifyAuthRepository
) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(SpotifyAuthViewModel::class.java)) {
      @Suppress("UNCHECKED_CAST") return SpotifyAuthViewModel(application, repository) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }
}