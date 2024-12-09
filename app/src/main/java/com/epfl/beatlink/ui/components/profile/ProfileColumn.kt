package com.epfl.beatlink.ui.components.profile

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.model.library.UserPlaylist
import com.epfl.beatlink.model.profile.ProfileData
import com.epfl.beatlink.model.spotify.objects.SpotifyArtist
import com.epfl.beatlink.model.spotify.objects.SpotifyTrack
import com.epfl.beatlink.ui.components.ArtistCard
import com.epfl.beatlink.ui.components.GradientTitle
import com.epfl.beatlink.ui.components.ProfilePicture
import com.epfl.beatlink.ui.components.TrackCard
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.ui.theme.PrimaryGradientBrush
import com.epfl.beatlink.ui.theme.lightThemeBackground
import com.epfl.beatlink.viewmodel.spotify.api.SpotifyApiViewModel

/**
 * Displays a detailed profile view in a vertically scrollable column layout, including user profile
 * information, top songs, top artists, playlists, and favorite music genres.
 *
 * @param profileData The [ProfileData] object containing the user's profile information, such as
 *   name, bio, and links. Can be null if no profile data is available.
 * @param navigationAction An instance of [NavigationActions] to handle navigation actions, such as
 *   navigating to the "Edit Profile" screen.
 * @param topSongsState A list of [SpotifyTrack] objects representing the user's top songs.
 * @param topArtistsState A list of [SpotifyArtist] objects representing the user's top artists.
 * @param userPlaylists A list of [UserPlaylist] objects representing the user's playlists.
 * @param paddingValue Padding values to be applied to the column layout.
 * @param profilePicture A mutable state containing the user's profile picture as a [Bitmap].
 * @param ownProfile A boolean variable that determines wich type of Profile screen needs to be
 *   displayed.
 * @param buttonTestTag A test tag for a button that changes depending on the Profile screen
 *   displayed
 */
@Composable
fun ProfileColumn(
  profileData: ProfileData?,
  navigationAction: NavigationActions,
  spotifyApiViewModel: SpotifyApiViewModel,
  topSongsState: List<SpotifyTrack>,
  topArtistsState: List<SpotifyArtist>,
  userPlaylists: List<UserPlaylist>,
  paddingValue: PaddingValues,
  profilePicture: MutableState<Bitmap?>,
  ownProfile: Boolean,
  buttonTestTag: String
) {
  Column(
      modifier =
          Modifier.fillMaxSize()
              .padding(paddingValue)
              .padding(16.dp)
              .verticalScroll(rememberScrollState())) {
        Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {

          // Profile picture
          ProfilePicture(profilePicture)

          Spacer(modifier = Modifier.width(24.dp))

          Column {
            Text(
                text = "${profileData?.links ?: 0} Links",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyLarge,
                modifier =
                    Modifier.align(Alignment.CenterHorizontally)
                        .padding(18.dp)
                        .testTag("linksCount"))

            Box(
                modifier =
                    Modifier.border(1.dp, PrimaryGradientBrush, RoundedCornerShape(30.dp))
                        .testTag(buttonTestTag + "Container")
                        .width(233.dp)
                        .height(32.dp)) {
                  Button(
                      onClick = {
                        if (ownProfile) {
                          navigationAction.navigateTo(Screen.EDIT_PROFILE)
                        } else {
                          /*Link action*/
                        }
                      },
                      modifier =
                          Modifier.fillMaxWidth()
                              .testTag(buttonTestTag)
                              .then(
                                  if (ownProfile) {
                                    Modifier.background(
                                        color = lightThemeBackground,
                                        shape = RoundedCornerShape(30.dp))
                                  } else {
                                    Modifier.background(
                                        brush = PrimaryGradientBrush,
                                        shape = RoundedCornerShape(30.dp))
                                  })
                              .padding(vertical = 2.dp),
                      colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)) {
                        Text(
                            text = if (ownProfile) "Edit Profile" else "Link",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelSmall,
                            color =
                                if (ownProfile) {
                                  MaterialTheme.colorScheme.primary
                                } else {
                                  Color.White
                                })
                      }
                }
          }
        }

        Spacer(modifier = Modifier.padding(vertical = 8.dp))

        // Name
        Text(
            text = profileData?.name ?: "",
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(horizontal = 10.dp).testTag("name"))

        Spacer(modifier = Modifier.height(5.dp))

        // Bio
        Text(
            text = profileData?.bio ?: "No description provided",
            color = Color.Black,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 10.dp).testTag("bio"))

        Spacer(modifier = Modifier.height(32.dp))

        // Favorite music genres
        if (profileData?.favoriteMusicGenres?.isNotEmpty() == true) {
          GradientTitle("MUSIC GENRES")
          Row(
              modifier = Modifier.padding(vertical = 16.dp).testTag("favoriteMusicGenresRow"),
              horizontalArrangement = Arrangement.spacedBy(16.dp),
          ) {
            profileData.favoriteMusicGenres.forEach { genre ->
              val genreGradient = genreGradients[genre] ?: PrimaryGradientBrush
              MusicGenreCard(genre = genre, brush = genreGradient, onClick = {})
            }
          }
        }

        // Display top songs if available
        if (topSongsState.isNotEmpty()) {
          GradientTitle("TOP SONGS")
          LazyRow(
              horizontalArrangement = Arrangement.spacedBy(11.dp),
              modifier = Modifier.padding(vertical = 16.dp)) {
                items(topSongsState.size) { i -> TrackCard(topSongsState[i]) }
              }
        }

        // Display top artists if available
        if (topArtistsState.isNotEmpty()) {
          GradientTitle("TOP ARTISTS")
          LazyRow(
              horizontalArrangement = Arrangement.spacedBy(11.dp),
              modifier = Modifier.padding(vertical = 16.dp)) {
                items(topArtistsState.size) { i -> ArtistCard(topArtistsState[i]) }
              }
        }

        if (userPlaylists.isNotEmpty()) {
          GradientTitle("PLAYLISTS")
          LazyColumn(
              verticalArrangement = Arrangement.spacedBy(11.dp),
              modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp).heightIn(max = 400.dp)) {
                items(userPlaylists.size) { i -> UserPlaylistCard(userPlaylists[i], spotifyApiViewModel) }
              }
        }
      }
}
