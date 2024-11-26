package com.epfl.beatlink.viewmodel.spotify.auth

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
import com.epfl.beatlink.repository.spotify.auth.SpotifyAuthRepository
import kotlinx.coroutines.launch

open class SpotifyAuthViewModel(
    application: Application,
    private val repository: SpotifyAuthRepository
) : AndroidViewModel(application) {
  private var _authState = mutableStateOf<AuthState>(AuthState.Idle)
  val authState: State<AuthState>
    get() = _authState

  @VisibleForTesting internal val accessToken = mutableStateOf<String?>(null)
  @VisibleForTesting internal val refreshToken = mutableStateOf<String?>(null)
  @VisibleForTesting internal val expiryTime = mutableStateOf<Long?>(null)

  init {
    loadTokens(application)
    loadAuthState()
    if (doesTokenExist() && !isTokenValid()) refreshAccessToken(application)
  }

  /** Updates the data values in the view model */
  fun loadTokens(context: Context) {
    val newAccessToken = repository.getAccessToken(context)
    val newRefreshToken = repository.getRefreshToken(context)
    val newExpiryTime = repository.getExpiryTime(context)

    accessToken.value = newAccessToken
    refreshToken.value = newRefreshToken
    expiryTime.value = newExpiryTime
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
    accessToken.value = null
    refreshToken.value = null
    expiryTime.value = null
    _authState.value = AuthState.Idle
  }

  /** Checks if an access token exists in the view model */
  private fun doesTokenExist(): Boolean {
    return accessToken.value != null
  }

  /** Checks if the access token is still valid */
  private fun isTokenValid(): Boolean {
    val expiryTime = expiryTime.value

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
    val refreshToken = refreshToken.value
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
