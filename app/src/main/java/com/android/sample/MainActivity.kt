package com.android.sample

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
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.android.sample.resources.C
import com.android.sample.ui.authentication.LoginScreen
import com.android.sample.ui.authentication.SignUpScreen
import com.android.sample.ui.authentication.WelcomeScreen
import com.android.sample.ui.library.LibraryScreen
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.navigation.Screen
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
    val navController = rememberNavController()
    val navigationActions = NavigationActions(navController)

    NavHost(navController = navController, startDestination = Route.WELCOME) {
        navigation(
            startDestination = Screen.WELCOME,
            route = Route.WELCOME
        ) {
            composable(Screen.WELCOME) { WelcomeScreen(navigationActions) }
            composable(Screen.LOGIN) { LoginScreen(navigationActions) }
            composable(Screen.REGISTER) { SignUpScreen(navigationActions) }

            // Screen to be implemented
            //composable(Screen.PROFILE_BUILD) { }
        }

        navigation(
            startDestination = Screen.HOME,
            route = Route.HOME
        ) {
            composable(Screen.HOME) { MapScreen(navigationActions) }
        }

        navigation(
            startDestination = Screen.SEARCH,
            route = Route.SEARCH
        ) {
            composable(Screen.SEARCH) { SearchScreen(navigationActions) }
        }

        navigation(
            startDestination = Screen.LIBRARY,
            route = Route.LIBRARY
        ) {
            composable(Screen.LIBRARY) { LibraryScreen(navigationActions) }
        }

        navigation(
            startDestination = Screen.PROFILE,
            route = Route.PROFILE
        ) {
            composable(Screen.PROFILE) { ProfileScreen(navigationActions) }
        }
    }
}

/**
 * Temporary screen to test navigation
 */
@Composable
fun MapScreen(navigationActions: NavigationActions) {
    Scaffold(
        bottomBar = {
            BottomNavigationMenu(
                onTabSelect = { route -> navigationActions.navigateTo(route) },
                tabList = LIST_TOP_LEVEL_DESTINATION,
                selectedItem = navigationActions.currentRoute()
            )
        },
        content = { pd ->
            Box(
                modifier = Modifier.padding(pd).fillMaxSize()
            ) {
                Text(text = "Map Screen", modifier = Modifier.align(Alignment.Center))
            }

        }
    )
}

/**
 * Temporary screen to test navigation
 */
@Composable
fun SearchScreen(navigationActions: NavigationActions) {
    Scaffold(
        bottomBar = {
            BottomNavigationMenu(
                onTabSelect = { route -> navigationActions.navigateTo(route) },
                tabList = LIST_TOP_LEVEL_DESTINATION,
                selectedItem = navigationActions.currentRoute()
            )
        },
        content = { pd ->
            Box(
                modifier = Modifier.padding(pd).fillMaxSize()
            ) {
                Text(text = "Search Screen", modifier = Modifier.align(Alignment.Center))
            }

        }
    )
}

/**
 * Temporary screen to test navigation
 */
@Composable
fun ProfileScreen(navigationActions: NavigationActions) {
    Scaffold(
        bottomBar = {
            BottomNavigationMenu(
                onTabSelect = { route -> navigationActions.navigateTo(route) },
                tabList = LIST_TOP_LEVEL_DESTINATION,
                selectedItem = navigationActions.currentRoute()
            )
        },
        content = { pd ->
            Box(
                modifier = Modifier.padding(pd).fillMaxSize(),
            ) {
                Text(text = "Profile Screen", modifier = Modifier.align(Alignment.Center))
            }
        }
    )
}