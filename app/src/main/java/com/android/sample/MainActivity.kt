package com.android.sample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.tooling.preview.Preview
import com.android.sample.model.spotify.SpotifyViewModel
import com.android.sample.resources.C
import com.android.sample.ui.authentication.SpotifyLogin
import com.android.sample.ui.theme.SampleAppTheme

class MainActivity : ComponentActivity() {
    private val spotifyViewModel = SpotifyViewModel()

  override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      Log.i("MainActivity", "onCreate")
      setContent {
          SampleAppTheme {
              // A surface container using the 'background' color from the theme
              Surface(
                  modifier = Modifier.fillMaxSize().semantics { testTag = C.Tag.main_screen_container },
                  color = MaterialTheme.colorScheme.background) {
                  SpotifyLogin(spotifyViewModel)
              }
      }
    }
  }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        spotifyViewModel.handleAuthorizationResponse(intent, applicationContext)
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(text = "Hello $name!", modifier = modifier.semantics { testTag = C.Tag.greeting })
}


