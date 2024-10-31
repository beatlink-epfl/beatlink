package com.android.sample

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.android.sample.model.map.MapLocationRepository
import com.android.sample.model.map.MapViewModel
import com.android.sample.model.profile.ProfileData
import com.android.sample.model.spotify.SpotifyAuthRepository
import com.android.sample.resources.C
import com.android.sample.ui.authentication.LoginScreen
import com.android.sample.ui.authentication.SignUpScreen
import com.android.sample.ui.authentication.SpotifyAuthViewModel
import com.android.sample.ui.authentication.SpotifyAuthViewModelFactory
import com.android.sample.ui.authentication.WelcomeScreen
import com.android.sample.ui.library.LibraryScreen
import com.android.sample.ui.map.MapScreen
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.profile.ProfileScreen
import com.android.sample.ui.theme.BeatLinkAppTheme
import com.google.android.gms.location.LocationServices
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import okhttp3.OkHttpClient

class MainActivity : ComponentActivity() {
  private lateinit var auth: FirebaseAuth

  private lateinit var spotifyAuthViewModel: SpotifyAuthViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    FirebaseApp.initializeApp(this)

    // Initialize Firebase Auth
    auth = FirebaseAuth.getInstance()
    auth.currentUser?.let {
      // Sign out the user if they are already signed in
      // This is useful for testing purposes
      auth.signOut()
    }

    val client = OkHttpClient()
    val spotifyAuthRepository = SpotifyAuthRepository(client)
    val factory = SpotifyAuthViewModelFactory(application, spotifyAuthRepository)

    spotifyAuthViewModel = ViewModelProvider(this, factory)[SpotifyAuthViewModel::class.java]

    setContent {
      BeatLinkAppTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize().semantics { testTag = C.Tag.main_screen_container }) {
              // SpotifyAuth(spotifyAuthViewModel)
              BeatLinkApp()
            }
      }
    }
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    spotifyAuthViewModel.handleAuthorizationResponse(intent, applicationContext)
  }
}

@Composable
fun BeatLinkApp() {

  val tmpUser = ProfileData("john_doe", "John Doe", "I'm a cool guy", 42, null)

  val navController = rememberNavController()
  val navigationActions = NavigationActions(navController)
  val locationClient = LocationServices.getFusedLocationProviderClient(LocalContext.current)
  val mapLocationRepository =
      MapLocationRepository(
          context = LocalContext.current.applicationContext, locationClient = locationClient)
  val mapViewModel: MapViewModel =
      viewModel(factory = MapViewModel.provideFactory(mapLocationRepository))

  NavHost(navController = navController, startDestination = Route.WELCOME) {
    navigation(startDestination = Screen.WELCOME, route = Route.WELCOME) {
      composable(Screen.WELCOME) { WelcomeScreen(navigationActions) }
      composable(Screen.LOGIN) { LoginScreen(navigationActions) }
      composable(Screen.REGISTER) { SignUpScreen(navigationActions) }

      // Screen to be implemented
      // composable(Screen.PROFILE_BUILD) { }
    }

    navigation(startDestination = Screen.HOME, route = Route.HOME) {
      composable(Screen.HOME) { MapScreen(navigationActions, mapViewModel) }
    }

    navigation(startDestination = Screen.SEARCH, route = Route.SEARCH) {
      composable(Screen.SEARCH) { SearchScreen(navigationActions) }
    }

    navigation(startDestination = Screen.LIBRARY, route = Route.LIBRARY) {
      composable(Screen.LIBRARY) { LibraryScreen(navigationActions) }
    }

    navigation(startDestination = Screen.PROFILE, route = Route.PROFILE) {
      composable(Screen.PROFILE) { ProfileScreen(tmpUser, navigationActions) }
    }
  }
}

/** Temporary screen to test navigation */
@Composable
fun SearchScreen(navigationActions: NavigationActions) {
  Scaffold(
      modifier = Modifier.testTag("searchScreen"),
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = navigationActions.currentRoute())
      },
      content = { pd ->
        Box(modifier = Modifier.padding(pd).fillMaxSize()) {
          Text(text = "Search Screen", modifier = Modifier.align(Alignment.Center))
        }
      })
}
