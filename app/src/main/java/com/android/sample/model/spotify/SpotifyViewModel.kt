package com.android.sample.model.spotify

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class SpotifyViewModel() : ViewModel() {
    private val spotifyService = SpotifyService()

    private val _authState = mutableStateOf<AuthState>(AuthState.Idle)
    val authState: State<AuthState> = _authState

    /**
     * Resets the authentication state to idle
     */
    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }

    /**
     * Requests user authorization to access Spotify data
     */
    fun requestUserAuthorization(context: Context) {
        val authUrl = spotifyService.buildSpotifyAuthUrl(context)
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
                spotifyService.requestAccessToken(code, context) { accessToken, error ->
                    if (accessToken != null) {
                        // Authorization was successful, token was received
                        _authState.value = AuthState.Success(accessToken)
                    } else {
                        // Token request failed
                        _authState.value = AuthState.Error(error ?: "Unknown error")
                    }
                }
            } else {
                val error = uri.getQueryParameter("error")
                _authState.value = AuthState.Error(error ?: "Unknown error")
            }
        }
    }
}

/**
 * Represents the possible states of the authentication process
 */
sealed class AuthState {
    object Idle : AuthState()
    data class Success(val accessToken: String) : AuthState()
    data class Error(val error: String) : AuthState()
}