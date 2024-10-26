package com.android.sample.ui.authentication

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.android.sample.model.spotify.SpotifyAuthRepository
import kotlinx.coroutines.launch

class SpotifyAuthViewModel(application: Application, private val repository: SpotifyAuthRepository) : AndroidViewModel(application) {
    private val _authState = mutableStateOf<AuthState>(AuthState.Idle)
    val authState: State<AuthState> = _authState

    private val _accessToken = mutableStateOf<String?>(null)
    private val _refreshToken = mutableStateOf<String?>(null)
    private val _expiryTime = mutableStateOf<Long?>(null)

    init {
        // Update the data values in the view model and the auth state
        updateDataValues(application)

        if (doesTokenExist()) {
            if (isTokenValid()) {
                _authState.value = AuthState.Authenticated(_accessToken.value!!)
            } else {
                refreshAccessToken(application)
            }
        }
    }

    fun clearAuthData(context: Context) {
        repository.clearAuthData(context)
        _accessToken.value = null
        _refreshToken.value = null
        _expiryTime.value = null
        _authState.value = AuthState.Idle
    }

    /**
     * Checks if an access token exists in the view model
     */
    private fun doesTokenExist(): Boolean {
        Log.d("SpotifyAuthViewModel", "doesTokenExist: ${_accessToken.value}")

        return _accessToken.value != null
    }

    /**
     * Checks if the access token is still valid
     */
    private fun isTokenValid(): Boolean {
        val expiryTime = _expiryTime.value

        return expiryTime != null && expiryTime > System.currentTimeMillis()
    }

    /**
     * Updates the data values in the view model
     */
    private fun updateDataValues(context: Context = getApplication()) {
        val accessToken = repository.getAccessToken(context)
        val refreshToken = repository.getRefreshToken(context)
        val expiryTime = repository.getExpiryTime(context)

        _accessToken.value = accessToken
        _refreshToken.value = refreshToken
        _expiryTime.value = expiryTime
    }

    /**
     * Requests user authorization to access Spotify data
     */
    fun requestUserAuthorization(context: Context) {
        val authUrl = repository.buildSpotifyAuthUrl(context)
        redirectToSpotifyAuth(context, authUrl)
    }

    /**
     * Redirects the user to the Spotify authorization page
     */
    private fun redirectToSpotifyAuth(context: Context, authUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(authUrl))
        context.startActivity(intent)
    }

    /**
     * Handles the authorization response from Spotify
     */
    fun handleAuthorizationResponse(intent: Intent, context: Context) {
        val uri  = intent.data
        uri?.let {
            val code = uri.getQueryParameter("code")
            if (code != null) {
                // Authorization was successful, use the code to request an access token
                viewModelScope.launch {
                    repository.requestAccessToken(code, context)
                        .collect { result ->
                            result.onSuccess { accessToken ->
                                _authState.value = AuthState.Authenticated(accessToken)
                            }
                            result.onFailure { error ->
                                _authState.value = AuthState.Idle
                            }
                        }
                }
            } else {
                val error = uri.getQueryParameter("error")
                _authState.value = AuthState.Idle
            }
        }
        updateDataValues(context)
    }

    /**
     * Refreshes the access token using the refresh token
     */
    fun refreshAccessToken(application: Application) {
        val refreshToken = _refreshToken.value
        if (refreshToken != null) {
            viewModelScope.launch {
                repository.refreshAccessToken(refreshToken, application)
                    .collect { result ->
                        result.onSuccess { accessToken ->
                            _authState.value = AuthState.Authenticated(accessToken)
                        }
                        result.onFailure { error ->
                            _authState.value = AuthState.Idle
                        }
                    }
            }
        } else {
            _authState.value = AuthState.Idle
        }
        updateDataValues(application)
    }
}

/**
 * Represents the possible states of the authentication process
 */
sealed class AuthState {
    data class Authenticated(val accessToken: String) : AuthState()
    object Idle : AuthState()
}

class SpotifyAuthViewModelFactory(
    private val application: Application,
    private val repository: SpotifyAuthRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SpotifyAuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SpotifyAuthViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}