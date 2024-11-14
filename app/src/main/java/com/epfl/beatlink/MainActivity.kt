package com.epfl.beatlink

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.lifecycle.ViewModelProvider
import com.epfl.beatlink.model.spotify.api.SpotifyApiRepository
import com.epfl.beatlink.model.spotify.api.SpotifyApiViewModel
import com.epfl.beatlink.model.spotify.auth.SPOTIFY_AUTH_PREFS
import com.epfl.beatlink.model.spotify.auth.SpotifyAuthRepository
import com.epfl.beatlink.resources.C
import com.epfl.beatlink.ui.BeatLinkApp
import com.epfl.beatlink.ui.authentication.SpotifyAuthViewModel
import com.epfl.beatlink.ui.authentication.SpotifyAuthViewModelFactory
import com.epfl.beatlink.ui.theme.BeatLinkAppTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import okhttp3.OkHttpClient

class MainActivity : ComponentActivity() {
  private val client = OkHttpClient()

  // Firebase Auth
  private lateinit var auth: FirebaseAuth

  // Spotify Auth
  private lateinit var spotifyAuthViewModel: SpotifyAuthViewModel
  private val spotifyAuthRepository = SpotifyAuthRepository(client)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Initialize Firebase
    FirebaseApp.initializeApp(this)
    auth = FirebaseAuth.getInstance()

    val spotifyAuthFactory = SpotifyAuthViewModelFactory(application, spotifyAuthRepository)
    spotifyAuthViewModel =
        ViewModelProvider(this, spotifyAuthFactory)[SpotifyAuthViewModel::class.java]

    val sharedPreferences = getSharedPreferences(SPOTIFY_AUTH_PREFS, MODE_PRIVATE)
    val spotifyApiRepository = SpotifyApiRepository(client, sharedPreferences)
    val spotifyApiViewModel = SpotifyApiViewModel(application, spotifyApiRepository)

    setContent {
      BeatLinkAppTheme(darkTheme = false) {
        Surface(
            modifier = Modifier.fillMaxSize().semantics { testTag = C.Tag.main_screen_container }) {
              BeatLinkApp(spotifyAuthViewModel, spotifyApiViewModel)
            }
      }
    }
  }

  // Handle the authorization response from Spotify
  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    spotifyAuthViewModel.handleAuthorizationResponse(intent, applicationContext)
  }
}
