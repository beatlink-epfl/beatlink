package com.android.sample.model.spotify.objects

import org.junit.Assert.assertEquals
import org.junit.Test

class SpotifyPlaylistTest {
  @Test
  fun testSpotifyPlaylist() {
    val spotifyPlaylist = SpotifyPlaylist("name", "cover", listOf("track1", "track2"), 2, 100)

    assertEquals("name", spotifyPlaylist.name)
    assertEquals("cover", spotifyPlaylist.cover)
    assertEquals(listOf("track1", "track2"), spotifyPlaylist.tracks)
    assertEquals(2, spotifyPlaylist.size)
    assertEquals(100, spotifyPlaylist.popularity)
  }

  @Test
  fun testSpotifyPlaylistCopy() {
    val spotifyPlaylist = SpotifyPlaylist("name", "cover", listOf("track1", "track2"), 2, 100)

    val spotifyPlaylistCopy = spotifyPlaylist.copy()

    assertEquals(spotifyPlaylist.name, spotifyPlaylistCopy.name)
    assertEquals(spotifyPlaylist.cover, spotifyPlaylistCopy.cover)
    assertEquals(spotifyPlaylist.tracks, spotifyPlaylistCopy.tracks)
    assertEquals(spotifyPlaylist.size, spotifyPlaylistCopy.size)
    assertEquals(spotifyPlaylist.popularity, spotifyPlaylistCopy.popularity)
  }

  @Test
  fun testSpotifyPlaylistEquals() {
    val spotifyPlaylist1 = SpotifyPlaylist("name", "cover", listOf("track1", "track2"), 2, 100)
    val spotifyPlaylist2 = SpotifyPlaylist("name", "cover", listOf("track1", "track2"), 2, 100)

    assertEquals(spotifyPlaylist1, spotifyPlaylist2)
  }
}
