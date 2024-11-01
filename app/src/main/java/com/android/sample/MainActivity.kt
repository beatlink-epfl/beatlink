package com.android.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.android.sample.model.profile.ProfileData
import com.android.sample.resources.C
import com.android.sample.ui.authentication.LoginScreen
import com.android.sample.ui.authentication.SignUpScreen
import com.android.sample.ui.authentication.WelcomeScreen
import com.android.sample.ui.library.LibraryScreen
import com.android.sample.ui.map.MapScreen
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.profile.ProfileScreen
import com.android.sample.ui.search.DiscoverPeopleScreen
import com.android.sample.ui.search.LiveMusicPartiesScreen
import com.android.sample.ui.search.MostMatchedSongsScreen
import com.android.sample.ui.search.SearchBarScreen
import com.android.sample.ui.search.SearchScreen
import com.android.sample.ui.search.TrendingSongsScreen
import com.android.sample.ui.theme.SampleAppTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      SampleAppTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize().semantics { testTag = C.Tag.main_screen_container }) {
              BeatLinkApp()
            }
      }
    }
  }
}

@Composable
fun BeatLinkApp() {

  val tmpUser = ProfileData("john_doe", "John Doe", "I'm a cool guy", 42, null)

  val navController = rememberNavController()
  val navigationActions = NavigationActions(navController)

  NavHost(navController = navController, startDestination = Route.WELCOME) {
    navigation(startDestination = Screen.WELCOME, route = Route.WELCOME) {
      composable(Screen.WELCOME) { WelcomeScreen(navigationActions) }
      composable(Screen.LOGIN) { LoginScreen(navigationActions) }
      composable(Screen.REGISTER) { SignUpScreen(navigationActions) }

      // Screen to be implemented
      // composable(Screen.PROFILE_BUILD) { }
    }

    navigation(startDestination = Screen.HOME, route = Route.HOME) {
      composable(Screen.HOME) { MapScreen(navigationActions) }
    }

    navigation(startDestination = Screen.SEARCH, route = Route.SEARCH) {
      composable(Screen.SEARCH) { SearchScreen(navigationActions) }
      composable(Screen.SEARCH_BAR) { SearchBarScreen(navigationActions) }
      composable(Screen.TRENDING_SONGS) { TrendingSongsScreen(navigationActions) }
      composable(Screen.MOST_MATCHED_SONGS) { MostMatchedSongsScreen(navigationActions) }
      composable(Screen.LIVE_MUSIC_PARTIES) { LiveMusicPartiesScreen(navigationActions) }
      composable(Screen.DISCOVER_PEOPLE) { DiscoverPeopleScreen(navigationActions) }
    }

    navigation(startDestination = Screen.LIBRARY, route = Route.LIBRARY) {
      composable(Screen.LIBRARY) { LibraryScreen(navigationActions) }
    }

    navigation(startDestination = Screen.PROFILE, route = Route.PROFILE) {
      composable(Screen.PROFILE) { ProfileScreen(tmpUser, navigationActions) }
    }
  }
}
