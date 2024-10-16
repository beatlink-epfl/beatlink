package com.android.sample.model.spotify.objects

import org.junit.Assert.*
import org.junit.Test

class SpotifyAlbumTest {

  @Test
  fun testSpotifyAlbum() {
    val spotifyAlbum =
        SpotifyAlbum(
            "spotifyId",
            "name",
            "cover",
            "artist",
            listOf("track1", "track2"),
            10,
            listOf("genre1", "genre2"),
            100)

    assertEquals("spotifyId", spotifyAlbum.spotifyId)
    assertEquals("name", spotifyAlbum.name)
    assertEquals("cover", spotifyAlbum.cover)
    assertEquals("artist", spotifyAlbum.artist)
    assertEquals(listOf("track1", "track2"), spotifyAlbum.tracks)
    assertEquals(10, spotifyAlbum.size)
    assertEquals(listOf("genre1", "genre2"), spotifyAlbum.genres)
    assertEquals(100, spotifyAlbum.popularity)
  }

  @Test
  fun testSpotifyAlbumCopy() {
    val spotifyAlbum =
        SpotifyAlbum(
            "spotifyId",
            "name",
            "cover",
            "artist",
            listOf("track1", "track2"),
            10,
            listOf("genre1", "genre2"),
            100)

    val spotifyAlbumCopy = spotifyAlbum.copy()

    assertEquals(spotifyAlbum.spotifyId, spotifyAlbumCopy.spotifyId)
    assertEquals(spotifyAlbum.name, spotifyAlbumCopy.name)
    assertEquals(spotifyAlbum.cover, spotifyAlbumCopy.cover)
    assertEquals(spotifyAlbum.artist, spotifyAlbumCopy.artist)
    assertEquals(spotifyAlbum.tracks, spotifyAlbumCopy.tracks)
    assertEquals(spotifyAlbum.size, spotifyAlbumCopy.size)
    assertEquals(spotifyAlbum.genres, spotifyAlbumCopy.genres)
    assertEquals(spotifyAlbum.popularity, spotifyAlbumCopy.popularity)
  }

  @Test
  fun testSpotifyAlbumEquals() {
    val spotifyAlbum1 =
        SpotifyAlbum(
            "spotifyId",
            "name",
            "cover",
            "artist",
            listOf("track1", "track2"),
            10,
            listOf("genre1", "genre2"),
            100)

    val spotifyAlbum2 =
        SpotifyAlbum(
            "spotifyId",
            "name",
            "cover",
            "artist",
            listOf("track1", "track2"),
            10,
            listOf("genre1", "genre2"),
            100)

    assertEquals(spotifyAlbum1, spotifyAlbum2)
  }
}
