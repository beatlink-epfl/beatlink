package com.epfl.beatlink.model.spotify.objects

import org.junit.Assert.assertEquals
import org.junit.Test

class SpotifyTrackTest {
  @Test
  fun testSpotifyTrack() {
    val spotifyTrack = SpotifyTrack("name", "trackId", "cover", 100, 100)

    assertEquals("name", spotifyTrack.name)
    assertEquals("trackId", spotifyTrack.trackId)
    assertEquals("cover", spotifyTrack.cover)
    assertEquals(100, spotifyTrack.duration)
    assertEquals(100, spotifyTrack.popularity)
  }

  @Test
  fun testSpotifyTrackCopy() {
    val spotifyTrack = SpotifyTrack("name", "trackId", "cover", 100, 100)

    val spotifyTrackCopy = spotifyTrack.copy()

    assertEquals(spotifyTrack.name, spotifyTrackCopy.name)
    assertEquals(spotifyTrack.trackId, spotifyTrackCopy.trackId)
    assertEquals(spotifyTrack.cover, spotifyTrackCopy.cover)
    assertEquals(spotifyTrack.duration, spotifyTrackCopy.duration)
    assertEquals(spotifyTrack.popularity, spotifyTrackCopy.popularity)
  }

  @Test
  fun testSpotifyTrackEquals() {
    val spotifyTrack1 = SpotifyTrack("name", "trackId", "cover", 100, 100)
    val spotifyTrack2 = SpotifyTrack("name", "trackId", "cover", 100, 100)

    assertEquals(spotifyTrack1, spotifyTrack2)
  }
}
