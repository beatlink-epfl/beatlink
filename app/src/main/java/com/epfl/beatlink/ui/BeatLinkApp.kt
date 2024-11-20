package com.epfl.beatlink.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.epfl.beatlink.repository.map.MapLocationRepository
import com.epfl.beatlink.ui.authentication.LoginScreen
import com.epfl.beatlink.ui.authentication.ProfileBuildScreen
import com.epfl.beatlink.ui.authentication.SignUpScreen
import com.epfl.beatlink.ui.authentication.WelcomeScreen
import com.epfl.beatlink.ui.library.CreateNewPlaylistScreen
import com.epfl.beatlink.ui.library.LibraryScreen
import com.epfl.beatlink.ui.library.MyPlaylistsScreen
import com.epfl.beatlink.ui.map.MapScreen
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Route
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.ui.profile.ChangePassword
import com.epfl.beatlink.ui.profile.EditProfileScreen
import com.epfl.beatlink.ui.profile.ProfileScreen
import com.epfl.beatlink.ui.search.DiscoverPeopleScreen
import com.epfl.beatlink.ui.search.LiveMusicPartiesScreen
import com.epfl.beatlink.ui.search.MostMatchedSongsScreen
import com.epfl.beatlink.ui.search.SearchBarScreen
import com.epfl.beatlink.ui.search.SearchScreen
import com.epfl.beatlink.ui.search.TrendingSongsScreen
import com.epfl.beatlink.viewmodel.auth.FirebaseAuthViewModel
import com.epfl.beatlink.viewmodel.library.PlaylistViewModel
import com.epfl.beatlink.viewmodel.map.MapViewModel
import com.epfl.beatlink.viewmodel.map.user.MapUsersViewModel
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel
import com.epfl.beatlink.viewmodel.spotify.api.SpotifyApiViewModel
import com.epfl.beatlink.viewmodel.spotify.auth.SpotifyAuthViewModel
import com.google.android.gms.location.LocationServices

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun BeatLinkApp(
    spotifyAuthViewModel: SpotifyAuthViewModel,
    spotifyApiViewModel: SpotifyApiViewModel
) {
  val navController = rememberNavController()
  val navigationActions = NavigationActions(navController)

  val firebaseAuthViewModel: FirebaseAuthViewModel =
      viewModel(factory = FirebaseAuthViewModel.Factory)
  val profileViewModel: ProfileViewModel = viewModel(factory = ProfileViewModel.Factory)
  val playlistViewModel: PlaylistViewModel = viewModel(factory = PlaylistViewModel.Factory)

  val locationClient = LocationServices.getFusedLocationProviderClient(LocalContext.current)
  val mapLocationRepository =
      MapLocationRepository(
          context = LocalContext.current.applicationContext, locationClient = locationClient)
  val mapViewModel: MapViewModel =
      viewModel(factory = MapViewModel.provideFactory(mapLocationRepository))

  val mapUsersViewModel: MapUsersViewModel = viewModel(factory = MapUsersViewModel.Factory)

  NavHost(navController = navController, startDestination = Route.WELCOME) {
    navigation(startDestination = Screen.WELCOME, route = Route.WELCOME) {
      composable(Screen.WELCOME) { WelcomeScreen(navigationActions) }
      composable(Screen.LOGIN) { LoginScreen(navigationActions, firebaseAuthViewModel) }
      composable(Screen.REGISTER) {
        SignUpScreen(
            navigationActions, firebaseAuthViewModel, spotifyAuthViewModel, profileViewModel)
      }
      composable(Screen.PROFILE_BUILD) { ProfileBuildScreen(navigationActions, profileViewModel) }
    }

    navigation(startDestination = Screen.HOME, route = Route.HOME) {
      composable(Screen.HOME) {
        MapScreen(
            navigationActions,
            mapViewModel,
            spotifyApiViewModel,
            profileViewModel,
            mapUsersViewModel)
      }
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
      composable(Screen.LIBRARY) { LibraryScreen(navigationActions, playlistViewModel) }
      composable(Screen.CREATE_NEW_PLAYLIST) {
        CreateNewPlaylistScreen(navigationActions, profileViewModel, playlistViewModel)
      }
      composable(Screen.MY_PLAYLISTS) { MyPlaylistsScreen(navigationActions, playlistViewModel) }
    }

    navigation(startDestination = Screen.PROFILE, route = Route.PROFILE) {
      composable(Screen.PROFILE) { ProfileScreen(profileViewModel, navigationActions) }
      composable(Screen.EDIT_PROFILE) { EditProfileScreen(profileViewModel, navigationActions) }
      composable(Screen.CHANGE_PASSWORD) { ChangePassword(navigationActions) }
    }
  }
}
