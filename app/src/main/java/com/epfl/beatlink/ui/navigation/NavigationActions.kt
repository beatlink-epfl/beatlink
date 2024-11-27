package com.epfl.beatlink.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.epfl.beatlink.ui.navigation.AppIcons.filledHome
import com.epfl.beatlink.ui.navigation.AppIcons.filledLibrary
import com.epfl.beatlink.ui.navigation.AppIcons.filledProfile
import com.epfl.beatlink.ui.navigation.AppIcons.filledSearch
import com.epfl.beatlink.ui.navigation.AppIcons.outlinedLibrary

object Route {
  const val WELCOME = "Welcome"
  const val HOME = "Home"
  const val SEARCH = "Search"
  const val LIBRARY = "Library"
  const val PROFILE = "Profile"
}

object Screen {
  const val WELCOME = "Welcome Screen"
  const val LOGIN = "Login Screen"
  const val REGISTER = "Register Screen"
  const val PROFILE_BUILD = "Profile Build Screen"
  const val HOME = "Home Screen"
  const val SEARCH = "Search Screen"
  const val LIBRARY = "Library Screen"
  const val MY_PLAYLISTS = "My Playlists Screen"
  const val SHARED_WITH_ME_PLAYLISTS = "Shared With Me Playlists"
  const val PUBLIC_PLAYLISTS = "Public Playlists Screen"
  const val CREATE_NEW_PLAYLIST = "Create New Playlist Screen"
  const val EDIT_PLAYLIST = "Edit playlist Screen"
  const val PLAYLIST_OVERVIEW = "Playlist Overview Screen"
    const val INVITE_COLLABORATORS = "Invite Collaborators Screen"
  const val PROFILE = "Profile Screen"
  const val EDIT_PROFILE = "Edit Profile Screen"
  const val SETTINGS = "Settings Screen"
  const val NOTIFICATIONS = "Notifications Screen"
  const val ACCOUNT = "Account Screen"
  const val CHANGE_USERNAME = "Change Username Screen"
  const val CHANGE_PASSWORD = "Change Password Screen"
  const val SEARCH_BAR = "Search Bar Screen"
  const val TRENDING_SONGS = "Trending Songs screen"
  const val MOST_MATCHED_SONGS = "Most Matched Songs Screen"
  const val LIVE_MUSIC_PARTIES = "Live Music Parties screen"
  const val DISCOVER_PEOPLE = "Discover People screen"
}

data class TopLevelDestination(
    val route: String,
    val screen: String,
    val unselectedIconResId: ImageVector,
    val selectedIconResId: ImageVector,
    val textId: String
)

object TopLevelDestinations {
  val HOME =
      TopLevelDestination(
          route = Route.HOME,
          screen = Screen.HOME,
          unselectedIconResId = Icons.Outlined.Home,
          selectedIconResId = filledHome,
          textId = "Home")
  val SEARCH =
      TopLevelDestination(
          route = Route.SEARCH,
          screen = Screen.SEARCH,
          unselectedIconResId = Icons.Outlined.Search,
          selectedIconResId = filledSearch,
          textId = "Search")
  val LIBRARY =
      TopLevelDestination(
          route = Route.LIBRARY,
          screen = Screen.LIBRARY,
          unselectedIconResId = outlinedLibrary,
          selectedIconResId = filledLibrary,
          textId = "Library")
  val PROFILE =
      TopLevelDestination(
          route = Route.PROFILE,
          screen = Screen.PROFILE,
          unselectedIconResId = Icons.Outlined.AccountCircle,
          selectedIconResId = filledProfile,
          textId = "Profile")
}

val LIST_TOP_LEVEL_DESTINATION =
    listOf(
        TopLevelDestinations.HOME,
        TopLevelDestinations.SEARCH,
        TopLevelDestinations.LIBRARY,
        TopLevelDestinations.PROFILE)

open class NavigationActions(private val navController: NavHostController) {
  /**
   * Navigate to the specified [TopLevelDestination]
   *
   * @param destination The top level destination to navigate to Clear the back stack when
   *   navigating to a new destination This is useful when navigating to a new screen from the
   *   bottom navigation bar as we don't want to keep the previous screen in the back stack
   */
  open fun navigateTo(destination: TopLevelDestination) {

    navController.navigate(destination.route) {
      // Pop up to the start destination of the graph to
      // avoid building up a large stack of destinations
      popUpTo(navController.graph.findStartDestination().id) {
        saveState = true
        inclusive = true
      }

      // Avoid multiple copies of the same destination when reselecting same item
      launchSingleTop = true

      // Restore state when reselecting a previously selected item
      if (destination.route != Route.WELCOME) {
        restoreState = true
      }
    }
  }

  /**
   * Navigate to the specified screen.
   *
   * @param screen The screen to navigate to
   */
  open fun navigateTo(screen: String) {
    navController.navigate(screen)
  }

  /** Navigate back to the previous screen. */
  open fun goBack() {
    navController.popBackStack()
  }

  /**
   * Get the current route of the navigation controller.
   *
   * @return The current route
   */
  open fun currentRoute(): String {
    return navController.currentDestination?.route ?: ""
  }

  /**
   * Navigate to the specified screen and clear the specified number of entries from the back stack.
   *
   * The specified number of recent screens will be popped from the back stack, and the new screen
   * will be pushed as the current destination.
   *
   * @param screen The screen to navigate to
   * @param entriesToClear The number of back stack entries to clear
   */
  open fun navigateToAndClearBackStack(screen: String, entriesToClear: Int) {
    // Loop to clear the specified number of entries from the back stack
    repeat(entriesToClear) {
      if (navController.previousBackStackEntry != null) {
        // Pop one back stack entry for each iteration
        navController.popBackStack()
      }
    }
    // Navigate to the new screen
    navController.navigate(screen) {
      launchSingleTop = true // Avoid creating multiple copies of the same destination
    }
  }
}
