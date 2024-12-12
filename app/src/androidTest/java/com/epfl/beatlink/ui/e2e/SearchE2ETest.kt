package com.epfl.beatlink.ui.e2e

import android.Manifest
import android.app.Activity.MODE_PRIVATE
import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ApplicationProvider
import androidx.test.rule.GrantPermissionRule
import com.epfl.beatlink.model.spotify.objects.SpotifyArtist
import com.epfl.beatlink.model.spotify.objects.SpotifyTrack
import com.epfl.beatlink.model.spotify.objects.State
import com.epfl.beatlink.repository.network.NetworkStatusTracker
import com.epfl.beatlink.repository.spotify.api.SpotifyApiRepository
import com.epfl.beatlink.repository.spotify.auth.SPOTIFY_AUTH_PREFS
import com.epfl.beatlink.ui.BeatLinkApp
import com.epfl.beatlink.viewmodel.network.NetworkViewModel
import com.epfl.beatlink.viewmodel.spotify.api.SpotifyApiViewModel
import com.epfl.beatlink.viewmodel.spotify.auth.SpotifyAuthViewModel
import okhttp3.OkHttpClient
import okhttp3.internal.immutableListOf
import org.json.JSONObject
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

class SearchE2ETest {
  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule
  val permissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(
          Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

  private lateinit var application: Application

  @Before
  fun setUp() {
    application = ApplicationProvider.getApplicationContext()
    val sharedPreferences = application.getSharedPreferences(SPOTIFY_AUTH_PREFS, MODE_PRIVATE)

    val spotifyAuthViewModel = mock(SpotifyAuthViewModel::class.java)
    val mockSpotifyApiViewModel = MockSpotifyApiViewModel(sharedPreferences)
    // Mock the network status tracker
    val mockNetworkViewModel = FakeNetworkViewModel(initialConnectionState = true)

    composeTestRule.setContent {
      BeatLinkApp(spotifyAuthViewModel, mockSpotifyApiViewModel, mockNetworkViewModel)
    }
  }

  @Test
  fun testEndToEndFlow() {

    if (composeTestRule.onNodeWithTag("welcomeScreen").isDisplayed()) {
      // Step 1: Start at Welcome Screen and verify that it is displayed
      composeTestRule.onNodeWithTag("welcomeScreen").assertIsDisplayed()

      // Step 2: Click the login button and verify navigation to Login Screen
      composeTestRule.onNodeWithTag("welcomeLoginButton").performScrollTo().performClick()

      // Step 3: Log in with test user credentials
      composeTestRule.onNodeWithTag("loginScreen").assertIsDisplayed()
      composeTestRule
          .onNodeWithTag("inputEmail")
          .performScrollTo()
          .performTextInput("testuser@gmail.com")
      composeTestRule
          .onNodeWithTag("inputPassword")
          .performScrollTo()
          .performTextInput("testuserbeatlink")
      composeTestRule.onNodeWithTag("loginButton").performScrollTo().performClick()

      // Wait for the map screen to be displayed
      composeTestRule.waitUntil(4000) { composeTestRule.onNodeWithTag("MapScreen").isDisplayed() }
    }
    // Step 4: Click the search button and verify navigation to Search Screen
    composeTestRule.onNodeWithTag("Search").isDisplayed()
    composeTestRule.onNodeWithTag("Search").performClick()
    composeTestRule.onNodeWithTag("searchScreen").assertIsDisplayed()

    // Step 5: Click on the search bar and verify that the search bar screen is displayed
    composeTestRule.onNodeWithTag("nonWritableSearchBarBox").isDisplayed()
    composeTestRule.onNodeWithTag("nonWritableSearchBarBox").performClick()

    // Step 6: Check that navigation to SearchBarScreen is successful
    composeTestRule.onNodeWithTag("searchScaffold").assertIsDisplayed()

    // Step 7: Input some text in the search bar and verify that the search is successful
    composeTestRule.onNodeWithTag("writableSearchBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("writableSearchBar").performTextInput("Del")
    composeTestRule.waitUntil {
      composeTestRule.onNodeWithText("Delilah", substring = true).isDisplayed()
    }
    composeTestRule.onNodeWithText("Delilah", substring = true).assertIsDisplayed()

    // Step 8: Same but for artists
    composeTestRule.onNodeWithText("Artists").performClick()
    composeTestRule.onNodeWithTag("writableSearchBar").performTextClearance()
    composeTestRule.onNodeWithTag("writableSearchBar").performTextInput("f")
    composeTestRule.onNodeWithTag("writableSearchBar").performTextInput("r")
    composeTestRule.onNodeWithTag("writableSearchBar").performTextInput("e")
    composeTestRule.onNodeWithTag("writableSearchBar").performTextInput("d")
    composeTestRule.waitUntil {
      composeTestRule.onNodeWithText("Fred again..", substring = true).isDisplayed()
    }
    composeTestRule.onNodeWithText("Fred again..", substring = true).assertIsDisplayed()
  }
}

class MockSpotifyApiViewModel(sharedPreferences: SharedPreferences) :
    SpotifyApiViewModel(
        ApplicationProvider.getApplicationContext(),
        SpotifyApiRepository(OkHttpClient(), sharedPreferences)) {
  override fun searchArtistsAndTracks(
      query: String,
      onSuccess: (List<SpotifyArtist>, List<SpotifyTrack>) -> Unit,
      onFailure: (List<SpotifyArtist>, List<SpotifyTrack>) -> Unit
  ) {
    Log.d("MockSpotifyApiViewModel", "Searching for $query")
    val artist =
        createSpotifyArtist(
            JSONObject(
                """
					{
						"external_urls": {
						"spotify": "https://open.spotify.com/artist/4oLeXFyACqeem2VImYeBFe"
					},
						"followers": {
						"href": null,
						"total": 1777331
					},
						"genres": [
						"edm",
						"house",
						"stutter house"
						],
						"href": "https://api.spotify.com/v1/artists/4oLeXFyACqeem2VImYeBFe",
						"id": "4oLeXFyACqeem2VImYeBFe",
						"images": [
						{
							"url": "https://i.scdn.co/image/ab6761610000e5eb2302e6ba3091fcbc6fd5bb54",
							"height": 640,
							"width": 640
						},
						{
							"url": "https://i.scdn.co/image/ab676161000051742302e6ba3091fcbc6fd5bb54",
							"height": 320,
							"width": 320
						},
						{
							"url": "https://i.scdn.co/image/ab6761610000f1782302e6ba3091fcbc6fd5bb54",
							"height": 160,
							"width": 160
						}
						],
						"name": "Fred again..",
						"popularity": 78,
						"type": "artist",
						"uri": "spotify:artist:4oLeXFyACqeem2VImYeBFe"
					}
					"""
                    .trimIndent()))
    val track =
        createSpotifyTrack(
            JSONObject(
                """
				{
				  "album": {
				    "album_type": "single",
				    "artists": [
				      {
				        "external_urls": {
				          "spotify": "https://open.spotify.com/artist/4oLeXFyACqeem2VImYeBFe"
				        },
				        "href": "https://api.spotify.com/v1/artists/4oLeXFyACqeem2VImYeBFe",
				        "id": "4oLeXFyACqeem2VImYeBFe",
				        "name": "Fred again..",
				        "type": "artist",
				        "uri": "spotify:artist:4oLeXFyACqeem2VImYeBFe"
				      }
				    ],
				    "external_urls": {
				      "spotify": "https://open.spotify.com/album/24GbGX038jKJdzZ0KGAIxW"
				    },
				    "href": "https://api.spotify.com/v1/albums/24GbGX038jKJdzZ0KGAIxW",
				    "id": "24GbGX038jKJdzZ0KGAIxW",
				    "images": [
				      {
				        "height": 640,
				        "width": 640,
				        "url": "https://i.scdn.co/image/ab67616d0000b2739c856c6f2c6161af49446bf8"
				      },
				      {
				        "height": 300,
				        "width": 300,
				        "url": "https://i.scdn.co/image/ab67616d00001e029c856c6f2c6161af49446bf8"
				      },
				      {
				        "height": 64,
				        "width": 64,
				        "url": "https://i.scdn.co/image/ab67616d000048519c856c6f2c6161af49446bf8"
				      }
				    ],
				    "is_playable": true,
				    "name": "Delilah (pull me out of this)",
				    "release_date": "2022-10-17",
				    "release_date_precision": "day",
				    "total_tracks": 1,
				    "type": "album",
				    "uri": "spotify:album:24GbGX038jKJdzZ0KGAIxW"
				  },
				  "artists": [
				    {
				      "external_urls": {
				        "spotify": "https://open.spotify.com/artist/4oLeXFyACqeem2VImYeBFe"
				      },
				      "href": "https://api.spotify.com/v1/artists/4oLeXFyACqeem2VImYeBFe",
				      "id": "4oLeXFyACqeem2VImYeBFe",
				      "name": "Fred again..",
				      "type": "artist",
				      "uri": "spotify:artist:4oLeXFyACqeem2VImYeBFe"
				    }
				  ],
				  "disc_number": 1,
				  "duration_ms": 250702,
				  "explicit": false,
				  "external_ids": {
				    "isrc": "GBAHS2201028"
				  },
				  "external_urls": {
				    "spotify": "https://open.spotify.com/track/0Ftrkz2waaHcjKb4qYvLmz"
				  },
				  "href": "https://api.spotify.com/v1/tracks/0Ftrkz2waaHcjKb4qYvLmz",
				  "id": "0Ftrkz2waaHcjKb4qYvLmz",
				  "is_local": false,
				  "is_playable": true,
				  "name": "Delilah (pull me out of this)",
				  "popularity": 61,
				  "preview_url": null,
				  "track_number": 1,
				  "type": "track",
				  "uri": "spotify:track:0Ftrkz2waaHcjKb4qYvLmz"
				}
			"""
                    .trimIndent()))
    val artists = immutableListOf(artist)
    val tracks = immutableListOf(track)

    onSuccess(artists, tracks)
  }

  /** Creates a SpotifyTrack object from a JSON object. */
  private fun createSpotifyTrack(track: JSONObject): SpotifyTrack {
    val artist = track.getJSONArray("artists").getJSONObject(0)
    val album = track.getJSONObject("album")

    // Get cover URL from album images
    val coverUrl =
        if (album.getJSONArray("images").length() == 0) ""
        else album.getJSONArray("images").getJSONObject(0).getString("url")

    return SpotifyTrack(
        name = track.getString("name"),
        artist = artist.getString("name"),
        trackId = track.getString("id"),
        cover = coverUrl,
        duration = track.getInt("duration_ms"),
        popularity = track.getInt("popularity"),
        state = State.PAUSE)
  }

  /** Creates a SpotifyArtist object from a JSON object. */
  private fun createSpotifyArtist(artist: JSONObject): SpotifyArtist {
    val coverUrl =
        if (artist.getJSONArray("images").length() == 0) ""
        else artist.getJSONArray("images").getJSONObject(0).getString("url")
    val genres = mutableListOf<String>()
    val genresArray = artist.getJSONArray("genres")
    for (j in 0 until genresArray.length()) {
      genres.add(genresArray.getString(j))
    }
    return SpotifyArtist(
        image = coverUrl,
        name = artist.getString("name"),
        genres = genres,
        popularity = artist.getInt("popularity"))
  }
}

class FakeNetworkViewModel(initialConnectionState: Boolean) :
    NetworkViewModel(mock(NetworkStatusTracker::class.java)) {
  // MutableLiveData to allow dynamic changes during tests
  private val _isConnected = MutableLiveData(initialConnectionState)
  override val isConnected: LiveData<Boolean> = _isConnected
}
