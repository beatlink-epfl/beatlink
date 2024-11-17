package com.epfl.beatlink.repository.spotify.objects

import com.epfl.beatlink.model.spotify.objects.SpotifyAlbum
import com.epfl.beatlink.model.spotify.objects.SpotifyTrack
import com.epfl.beatlink.model.spotify.objects.State
import org.junit.Assert.assertEquals
import org.junit.Test

class SpotifyAlbumTest {

  @Test
  fun testSpotifyAlbum() {
    val track = SpotifyTrack("track", "artist","trackId", "cover", 100, 100, State.PLAY)
    val spotifyAlbum =
        SpotifyAlbum(
            "spotifyId",
            "name",
            "cover",
            "artist",
            2024,
            listOf(track),
            10,
            listOf("genre1", "genre2"),
            100)

    assertEquals("spotifyId", spotifyAlbum.spotifyId)
    assertEquals("name", spotifyAlbum.name)
    assertEquals("cover", spotifyAlbum.cover)
    assertEquals("artist", spotifyAlbum.artist)
    assertEquals(2024, spotifyAlbum.year)
    assertEquals(listOf(track), spotifyAlbum.tracks)
    assertEquals(10, spotifyAlbum.size)
    assertEquals(listOf("genre1", "genre2"), spotifyAlbum.genres)
    assertEquals(100, spotifyAlbum.popularity)
  }

  @Test
  fun testSpotifyAlbumCopy() {
    val track = SpotifyTrack("track", "artist", "trackId", "cover", 100, 100, State.PLAY)
    val spotifyAlbum =
        SpotifyAlbum(
            "spotifyId",
            "name",
            "cover",
            "artist",
            2024,
            listOf(track),
            10,
            listOf("genre1", "genre2"),
            100)

    val spotifyAlbumCopy = spotifyAlbum.copy()

    assertEquals(spotifyAlbum.spotifyId, spotifyAlbumCopy.spotifyId)
    assertEquals(spotifyAlbum.name, spotifyAlbumCopy.name)
    assertEquals(spotifyAlbum.cover, spotifyAlbumCopy.cover)
    assertEquals(spotifyAlbum.artist, spotifyAlbumCopy.artist)
    assertEquals(spotifyAlbum.year, spotifyAlbumCopy.year)
    assertEquals(spotifyAlbum.tracks, spotifyAlbumCopy.tracks)
    assertEquals(spotifyAlbum.size, spotifyAlbumCopy.size)
    assertEquals(spotifyAlbum.genres, spotifyAlbumCopy.genres)
    assertEquals(spotifyAlbum.popularity, spotifyAlbumCopy.popularity)
  }

  @Test
  fun testSpotifyAlbumEquals() {
    val track = SpotifyTrack("track", "artist", "trackId", "cover", 100, 100, State.PLAY)
    val spotifyAlbum1 =
        SpotifyAlbum(
            "spotifyId",
            "name",
            "cover",
            "artist",
            2024,
            listOf(track),
            10,
            listOf("genre1", "genre2"),
            100)

    val spotifyAlbum2 =
        SpotifyAlbum(
            "spotifyId",
            "name",
            "cover",
            "artist",
            2024,
            listOf(track),
            10,
            listOf("genre1", "genre2"),
            100)

    assertEquals(spotifyAlbum1, spotifyAlbum2)
  }
}
