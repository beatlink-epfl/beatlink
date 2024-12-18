package com.epfl.beatlink.ui.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.epfl.beatlink.model.map.user.Location
import com.epfl.beatlink.model.map.user.MapUser
import com.epfl.beatlink.model.profile.ProfileData
import com.epfl.beatlink.viewmodel.map.MapViewModel
import com.epfl.beatlink.viewmodel.map.user.MapUsersViewModel
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel
import com.google.android.gms.maps.model.LatLng

@Composable
fun MapUserTrackingView(
    mapUsersViewModel: MapUsersViewModel,
    mapViewModel: MapViewModel,
    profileViewModel: ProfileViewModel,
    radius: Double
) {
  val currentPosition by mapViewModel.currentPosition
  val playbackState by mapUsersViewModel.playbackState.collectAsState()
  val mapUser by mapUsersViewModel.mapUser.collectAsState()
  val profile by profileViewModel.profile.collectAsState()
  val locationPermitted by mapViewModel.locationPermitted

  LaunchedEffect(currentPosition, locationPermitted) {
    profileViewModel.fetchProfile()
    if (locationPermitted && currentPosition != null && playbackState != null) {
      mapUsersViewModel.fetchMapUsers(
          currentLocation = Location(currentPosition!!.latitude, currentPosition!!.longitude),
          radiusInMeters = radius)
    }
  }

  LaunchedEffect(currentPosition, locationPermitted, playbackState) {
    profileViewModel.fetchProfile()
    if (locationPermitted && currentPosition != null && playbackState != null) {
      mapUserHandling(
          mapUser = mapUser,
          currentPosition = currentPosition!!,
          profile = profile,
          mapUsersViewModel = mapUsersViewModel)
    }
  }
}

fun mapUserHandling(
    mapUser: MapUser?,
    currentPosition: LatLng,
    profile: ProfileData?,
    mapUsersViewModel: MapUsersViewModel
) {
  if (mapUser == null) {
    // Create a new MapUser with the current location and playback state
    profile?.let {
      mapUsersViewModel.addMapUser(
          username = it.username,
          location =
              Location(latitude = currentPosition.latitude, longitude = currentPosition.longitude))
    }
  } else {
    val newLocation =
        Location(latitude = currentPosition.latitude, longitude = currentPosition.longitude)
    mapUsersViewModel.updateMapUser(newLocation)
  }
}
