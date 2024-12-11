package com.epfl.beatlink.ui.components.profile

import android.graphics.Bitmap
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.epfl.beatlink.model.library.UserPlaylist
import com.epfl.beatlink.model.profile.ProfileData
import com.epfl.beatlink.model.spotify.objects.SpotifyArtist
import com.epfl.beatlink.model.spotify.objects.SpotifyTrack
import com.epfl.beatlink.ui.components.ArtistCard
import com.epfl.beatlink.ui.components.EditProfileButton
import com.epfl.beatlink.ui.components.GradientTitle
import com.epfl.beatlink.ui.components.LinkButton
import com.epfl.beatlink.ui.components.ProfileLinkButton
import com.epfl.beatlink.ui.components.ProfilePicture
import com.epfl.beatlink.ui.components.TrackCard
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.ui.navigation.Screen.EDIT_PROFILE
import com.epfl.beatlink.ui.navigation.Screen.LINKS
import com.epfl.beatlink.ui.theme.PrimaryGradientBrush
import com.epfl.beatlink.viewmodel.profile.FriendRequestViewModel
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel
import com.epfl.beatlink.ui.theme.lightThemeBackground
import com.epfl.beatlink.viewmodel.spotify.api.SpotifyApiViewModel

/**
 * Displays a detailed profile view in a vertically scrollable column layout, including user profile
 * information, top songs, top artists, playlists, and favorite music genres.
 */
@Composable
fun ProfileColumn(
    navigationActions: NavigationActions,
    profileViewModel: ProfileViewModel,
    friendRequestViewModel: FriendRequestViewModel,
    spotifyApiViewModel: SpotifyApiViewModel,
    userPlaylists: List<UserPlaylist>,
    paddingValue: PaddingValues,
    profilePicture: MutableState<Bitmap?>,
    ownProfile: Boolean
) {
    val profileData by profileViewModel.profile.collectAsState()

    val ownRequests by friendRequestViewModel.ownRequests.observeAsState(emptyList())
    val friendRequests by friendRequestViewModel.friendRequests.observeAsState(emptyList())
    val allFriends by friendRequestViewModel.allFriends.observeAsState(emptyList())

    // Info of the selected user
    val selectedUserUserId by profileViewModel.selectedUserUserId.collectAsState()
    val selectedProfileData by profileViewModel.selectedUserProfile.collectAsState()

    val fetchOtherProfileFriends = remember { mutableStateOf(false) }
    val otherProfileAllFriends by friendRequestViewModel.otherProfileAllFriends.observeAsState(emptyList())


    val topSongsState =
        if (ownProfile) {
            profileData?.topSongs ?: emptyList()
        } else {
            selectedProfileData?.topSongs ?: emptyList()
        }
    val topArtistsState =
        if (ownProfile) {
            profileData?.topArtists ?: emptyList()
        } else {
            selectedProfileData?.topArtists ?: emptyList()
        }

    var requestStatus =
        when (selectedUserUserId) {
            in ownRequests -> "Requested"
            in friendRequests -> "Accept"
            in allFriends -> "Linked"
            else -> "Link"
        }

    LaunchedEffect(selectedUserUserId, allFriends, fetchOtherProfileFriends) {
        if (!ownProfile) {
            // Fetch the friends of the displayed user
            friendRequestViewModel.getOtherProfileAllFriends(selectedUserUserId) { }
        }
        if (profileData?.links != allFriends.size) {
            profileData?.let { currentProfile ->
                profileViewModel.updateNbLinks(currentProfile, allFriends.size)
            }
        }
        if (selectedProfileData?.links != otherProfileAllFriends.size && selectedUserUserId != "" && !fetchOtherProfileFriends.value) {
            selectedProfileData?.let { selectedProfile ->
                profileViewModel.updateOtherProfileNbLinks(
                    selectedProfile,
                    selectedUserUserId,
                    otherProfileAllFriends.size
                )
            }
        }
    }

    Column(
        modifier =
        Modifier
            .fillMaxSize()
            .padding(paddingValue)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {

            // Profile picture
            ProfilePicture(profilePicture)

            Spacer(modifier = Modifier.width(24.dp))

            Column {
                Text(
                    text = if (ownProfile) "${profileData?.links} Links" else "${selectedProfileData?.links} Links",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier =
                    Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(18.dp)
                        .clickable {
                            if (ownProfile) {
                                navigationActions.navigateTo(LINKS)
                            }
                        }
                        .testTag("linksCount"))

                if (ownProfile) {
                    EditProfileButton { navigationActions.navigateTo(EDIT_PROFILE) }
                } else {
                    ProfileLinkButton(
                        buttonText = requestStatus
                    ) {
                        when (requestStatus) {
                            "Link" -> {
                                selectedUserUserId.let { friendRequestViewModel.sendFriendRequestTo(it) }
                                requestStatus = "Requested"
                            }
                            "Requested" -> {
                                selectedUserUserId.let { friendRequestViewModel.cancelFriendRequestTo(it) }
                                requestStatus = "Link"
                            }
                            "Accept" -> {
                                selectedUserUserId.let { friendRequestViewModel.acceptFriendRequestFrom(it) }
                                requestStatus= "Linked"
                            }
                            "Linked" -> {
                                selectedUserUserId.let { friendRequestViewModel.removeFriend(it) }
                                requestStatus = "Link"
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.padding(vertical = 8.dp))

        // Name
        Text(
            text = if (ownProfile) profileData?.name ?: "" else selectedProfileData?.name ?: "",
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .testTag("name")
        )

        Spacer(modifier = Modifier.height(5.dp))

        // Bio
        Text(
            text = if (ownProfile) profileData?.bio ?: "" else selectedProfileData?.bio ?: "",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .testTag("bio")
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Favorite music genres
        if (ownProfile) {
            if (profileData?.favoriteMusicGenres?.isNotEmpty() == true) {
                GradientTitle("MUSIC GENRES")
                Row(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .testTag("favoriteMusicGenresRow"),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    profileData!!.favoriteMusicGenres.forEach { genre ->
                        val genreGradient = genreGradients[genre] ?: PrimaryGradientBrush
                        MusicGenreCard(genre = genre, brush = genreGradient, onClick = {})
                    }
                }
            }
        } else {
            if (selectedProfileData?.favoriteMusicGenres?.isNotEmpty() == true) {
                GradientTitle("MUSIC GENRES")
                Row(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .testTag("favoriteMusicGenresRow"),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    selectedProfileData!!.favoriteMusicGenres.forEach { genre ->
                        val genreGradient = genreGradients[genre] ?: PrimaryGradientBrush
                        MusicGenreCard(genre = genre, brush = genreGradient, onClick = {})
                    }
                }
            }
        }

        // Display top songs if available
        if (topSongsState.isNotEmpty()) {
            GradientTitle("TOP SONGS")
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(11.dp),
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                items(topSongsState.size) { i -> TrackCard(topSongsState[i]) }
            }
        }

        // Display top artists if available
        if (topArtistsState.isNotEmpty()) {
            GradientTitle("TOP ARTISTS")
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(11.dp),
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                items(topArtistsState.size) { i -> ArtistCard(topArtistsState[i]) }
            }
        }

        if (userPlaylists.isNotEmpty()) {
            GradientTitle("PLAYLISTS")
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(11.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .heightIn(max = 400.dp)
            ) {
                items(userPlaylists.size) { i -> UserPlaylistCard(userPlaylists[i], spotifyApiViewModel) }
            }
        }
    }
}
