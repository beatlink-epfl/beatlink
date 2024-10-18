package com.android.sample.model.spotify.objects

import org.junit.Assert.assertEquals
import org.junit.Test

class SpotifyArtistTest {
  @Test
  fun testSpotifyArtist() {
    val spotifyArtist = SpotifyArtist("image", "name", listOf("genre1", "genre2"), 100)

    assertEquals("image", spotifyArtist.image)
    assertEquals("name", spotifyArtist.name)
    assertEquals(listOf("genre1", "genre2"), spotifyArtist.genres)
    assertEquals(100, spotifyArtist.popularity)
  }

  @Test
  fun testSpotifyArtistCopy() {
    val spotifyArtist = SpotifyArtist("image", "name", listOf("genre1", "genre2"), 100)

    val spotifyArtistCopy = spotifyArtist.copy()

    assertEquals(spotifyArtist.image, spotifyArtistCopy.image)
    assertEquals(spotifyArtist.name, spotifyArtistCopy.name)
    assertEquals(spotifyArtist.genres, spotifyArtistCopy.genres)
    assertEquals(spotifyArtist.popularity, spotifyArtistCopy.popularity)
  }

  @Test
  fun testSpotifyArtistEquals() {
    val spotifyArtist1 = SpotifyArtist("image", "name", listOf("genre1", "genre2"), 100)
    val spotifyArtist2 = SpotifyArtist("image", "name", listOf("genre1", "genre2"), 100)

    assertEquals(spotifyArtist1, spotifyArtist2)
  }
}
