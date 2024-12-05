package com.epfl.beatlink.model.map.user

import com.google.firebase.Timestamp

/**
 * Data class representing a user to be displayed on the map along with additional information about
 * the track they are currently listening to.
 *
 * A `MapUser` should only exist if the user is actively listening to a track.
 *
 * @property username The username of the user.
 * @property currentPlayingTrack The track the user is currently listening to.
 * @property location The current location of the user.
 * @property lastUpdated The timestamp of when the MapUser's information was last updated.
 */
data class MapUser(
    val username: String,
    val currentPlayingTrack: CurrentPlayingTrack,
    val location: Location,
    val lastUpdated: Timestamp
)
