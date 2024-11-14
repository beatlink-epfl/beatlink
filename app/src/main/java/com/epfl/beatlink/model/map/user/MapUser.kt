package com.epfl.beatlink.model.map.user

/**
 * Data class representing a user to be displayed on the map along with additional information about
 * the track they are currently listening to.
 *
 * A `MapUser` should only exist if the user is actively listening to a track.
 */
data class MapUser(
    val username: String,
    val currentPlayingTrack: CurrentPlayingTrack,
    val location: Location
)
