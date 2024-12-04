package com.epfl.beatlink

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.lifecycle.ViewModelProvider
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.epfl.beatlink.repository.map.user.ExpiredMapUsersWorker
import com.epfl.beatlink.repository.spotify.api.SpotifyApiRepository
import com.epfl.beatlink.repository.spotify.auth.SPOTIFY_AUTH_PREFS
import com.epfl.beatlink.repository.spotify.auth.SpotifyAuthRepository
import com.epfl.beatlink.resources.C
import com.epfl.beatlink.ui.BeatLinkApp
import com.epfl.beatlink.ui.theme.BeatLinkAppTheme
import com.epfl.beatlink.viewmodel.spotify.api.SpotifyApiViewModel
import com.epfl.beatlink.viewmodel.spotify.auth.SpotifyAuthViewModel
import com.epfl.beatlink.viewmodel.spotify.auth.SpotifyAuthViewModelFactory
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient

private const val WORK_INTERVAL_MINUTES = 15L

class MainActivity : ComponentActivity() {
  private val client = OkHttpClient()

  // Spotify Auth
  private lateinit var spotifyAuthViewModel: SpotifyAuthViewModel
  private val spotifyAuthRepository = SpotifyAuthRepository(client)

  @RequiresApi(Build.VERSION_CODES.TIRAMISU)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Set up the worker to delete expired MapUsers
    val periodicWork =
        PeriodicWorkRequest.Builder(
                ExpiredMapUsersWorker::class.java, WORK_INTERVAL_MINUTES, TimeUnit.MINUTES)
            .build()

    WorkManager.getInstance(this)
        .enqueueUniquePeriodicWork(
            "Delete expired MapUsers", ExistingPeriodicWorkPolicy.KEEP, periodicWork)

    val spotifyAuthFactory = SpotifyAuthViewModelFactory(application, spotifyAuthRepository)
    spotifyAuthViewModel =
        ViewModelProvider(this, spotifyAuthFactory)[SpotifyAuthViewModel::class.java]

    val sharedPreferences = getSharedPreferences(SPOTIFY_AUTH_PREFS, MODE_PRIVATE)
    val spotifyApiRepository = SpotifyApiRepository(client, sharedPreferences)
    val spotifyApiViewModel = SpotifyApiViewModel(application, spotifyApiRepository)

    setContent {
      BeatLinkAppTheme(darkTheme = false) {
        // A surface container using the 'background' color from the theme
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
