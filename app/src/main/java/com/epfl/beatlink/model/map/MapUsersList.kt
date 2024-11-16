package com.epfl.beatlink.model.map

import com.epfl.beatlink.R
import com.epfl.beatlink.model.map.user.CurrentPlayingTrack
import com.epfl.beatlink.model.map.user.Location
import com.epfl.beatlink.model.map.user.MapUser

class MapUsersList {
  companion object {
    private val track1 =
        CurrentPlayingTrack(
            songName = "Die With A Smile",
            artistName = "Lady Gaga & Bruno Mars",
            albumName = "Die With A Smile",
            albumCover = R.drawable.cover_test1.toString())

    private val track2 =
        CurrentPlayingTrack(
            songName = "Nonsense",
            artistName = "Sabrina Carpenter",
            albumName = "Emails I Can't Send",
            albumCover = R.drawable.cover_test2.toString())

    private val track3 =
        CurrentPlayingTrack(
            songName = "Shy Away",
            artistName = "Twenty One Pilots",
            albumName = "Scaled and Icy",
            albumCover = R.drawable.cover_test3.toString())

    private val track4 =
        CurrentPlayingTrack(
            songName = "Love Galore",
            artistName = "SZA",
            albumName = "Ctrl",
            albumCover = R.drawable.cover_test4.toString())

    private val track5 =
        CurrentPlayingTrack(
            songName = "Needed Me",
            artistName = "Rihanna",
            albumName = "Anti",
            albumCover = R.drawable.cover_test5.toString())

    val mutableList: MutableList<MapUser> =
        mutableListOf(
            MapUser(
                username = "melody_maven",
                currentPlayingTrack = track1,
                location = Location(latitude = 46.518680, 6.568270)),
            MapUser(
                username = "beats_fanatic",
                currentPlayingTrack = track2,
                location = Location(latitude = 46.518194, 6.569130)),
            MapUser(
                username = "pilot_listener",
                currentPlayingTrack = track3,
                location = Location(latitude = 46.51848, 6.56852)),
            MapUser(
                username = "ctrl_soul",
                currentPlayingTrack = track4,
                location = Location(latitude = 46.517956, 6.569550)),
            MapUser(
                username = "rihanna_rebel",
                currentPlayingTrack = track5,
                location = Location(latitude = 46.517878, 6.566991)))
  }
}
