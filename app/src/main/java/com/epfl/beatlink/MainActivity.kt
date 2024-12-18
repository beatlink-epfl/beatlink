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
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.epfl.beatlink.repository.map.user.ExpiredMapUsersWorker
import com.epfl.beatlink.repository.map.user.MapUsersRepositoryFirestore
import com.epfl.beatlink.repository.map.user.WorkerFactory
import com.epfl.beatlink.repository.network.NetworkStatusTracker
import com.epfl.beatlink.repository.spotify.api.SpotifyApiRepository
import com.epfl.beatlink.repository.spotify.auth.SPOTIFY_AUTH_PREFS
import com.epfl.beatlink.repository.spotify.auth.SpotifyAuthRepository
import com.epfl.beatlink.resources.C
import com.epfl.beatlink.ui.BeatLinkApp
import com.epfl.beatlink.ui.theme.BeatLinkAppTheme
import com.epfl.beatlink.viewmodel.network.NetworkViewModel
import com.epfl.beatlink.viewmodel.spotify.api.SpotifyApiViewModel
import com.epfl.beatlink.viewmodel.spotify.auth.SpotifyAuthViewModel
import com.epfl.beatlink.viewmodel.spotify.auth.SpotifyAuthViewModelFactory
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient

private const val WORK_INTERVAL_MINUTES = 15L

class MainActivity : ComponentActivity() {
  private val client = OkHttpClient()

  // Spotify Auth
  private lateinit var spotifyAuthViewModel: SpotifyAuthViewModel
  private val spotifyAuthRepository = SpotifyAuthRepository(client)
  private lateinit var networkStatusTracker: NetworkStatusTracker

  @RequiresApi(Build.VERSION_CODES.TIRAMISU)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Initialize WorkManager
    initializeWorkManager()

    val spotifyAuthFactory = SpotifyAuthViewModelFactory(application, spotifyAuthRepository)
    spotifyAuthViewModel =
        ViewModelProvider(this, spotifyAuthFactory)[SpotifyAuthViewModel::class.java]

    networkStatusTracker = NetworkStatusTracker(this)

    val sharedPreferences = getSharedPreferences(SPOTIFY_AUTH_PREFS, MODE_PRIVATE)
    val spotifyApiRepository = SpotifyApiRepository(client, sharedPreferences)
    val spotifyApiViewModel = SpotifyApiViewModel(application, spotifyApiRepository)
    val networkViewModel = NetworkViewModel(networkStatusTracker)

    setContent {
      BeatLinkAppTheme(darkTheme = false) {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize().semantics { testTag = C.Tag.main_screen_container }) {
              BeatLinkApp(spotifyAuthViewModel, spotifyApiViewModel, networkViewModel)
            }
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    networkStatusTracker.unregisterCallback()
  }

  // Handle the authorization response from Spotify
  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    spotifyAuthViewModel.handleAuthorizationResponse(intent, applicationContext)
  }

  /**
   * Initialize WorkManager.
   *
   * This method initializes the WorkManager with a custom WorkerFactory. It then schedules a
   * periodic work request to delete expired MapUsers at regular intervals specified by
   * `WORK_INTERVAL_MINUTES`.
   */
  private fun initializeWorkManager() {
    if (!WorkManager.isInitialized()) {
      val mapUsersRepository = MapUsersRepositoryFirestore(FirebaseFirestore.getInstance())
      val workerFactory = WorkerFactory(mapUsersRepository)
      val config = Configuration.Builder().setWorkerFactory(workerFactory).build()

      WorkManager.initialize(this, config)

      // Build the periodic work request
      val periodicWork =
          PeriodicWorkRequest.Builder(
                  ExpiredMapUsersWorker::class.java, WORK_INTERVAL_MINUTES, TimeUnit.MINUTES)
              .build()

      // Enqueue the periodic work request
      WorkManager.getInstance(this)
          .enqueueUniquePeriodicWork(
              "Delete expired MapUsers", ExistingPeriodicWorkPolicy.KEEP, periodicWork)
    }
  }
}
