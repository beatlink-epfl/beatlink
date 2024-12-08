package com.epfl.beatlink.ui.profile

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.epfl.beatlink.model.library.UserPlaylist
import com.epfl.beatlink.ui.components.CornerIcons
import com.epfl.beatlink.ui.components.ScreenTopAppBar
import com.epfl.beatlink.ui.components.profile.ProfileColumn
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.viewmodel.profile.FriendRequestViewModel
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel
import com.epfl.beatlink.viewmodel.spotify.api.SpotifyApiViewModel

@Composable
fun OtherProfileScreen(
    profileViewModel: ProfileViewModel,
    friendRequestViewModel: FriendRequestViewModel,
    navigationAction: NavigationActions,
    spotifyApiViewModel: SpotifyApiViewModel
) {
    LaunchedEffect(Unit) { profileViewModel.fetchProfile() }
    val profileData by profileViewModel.profile.collectAsState()

    val selectedUserId by profileViewModel.selectedUserUserId.collectAsState()
    val selectedProfileData by profileViewModel.selectedUserProfile.collectAsState()

    val friendCount by friendRequestViewModel.friendCount.observeAsState()

    LaunchedEffect(friendCount) {
        friendCount?.let { count ->
            selectedProfileData?.let { profile ->
                profileViewModel.updateNbLinks(profile, count)
            }
        }
    }

    LaunchedEffect(selectedUserId) {
        if (selectedUserId.isNotEmpty()) {
            profileViewModel.loadProfilePicture { profileViewModel.profilePicture.value = it }
        }
    }

    val userPlaylists = remember { mutableStateOf<List<UserPlaylist>>(emptyList()) }

    // Fetch user's playlists
    LaunchedEffect(selectedProfileData?.spotifyId) {
        val spotifyId = selectedProfileData?.spotifyId
        if (!spotifyId.isNullOrEmpty()) {
            spotifyApiViewModel.getUserPlaylists(
                userId = selectedProfileData?.spotifyId ?: "",
                onSuccess = { playlist -> userPlaylists.value = playlist },
                onFailure = { userPlaylists.value = emptyList() })
        }
    }

    Scaffold(
        modifier = Modifier.testTag("otherProfileScreen"),
        topBar = {
            ScreenTopAppBar(
                selectedProfileData?.username ?: "",
                "titleUsername",
                navigationAction,
                listOf {
                    CornerIcons(
                        onClick = { /*click action*/ },
                        icon = Icons.Filled.MoreVert,
                        contentDescription = "MoreVert",
                        modifier = Modifier.testTag("profileScreenMoreVertButton")
                    )
                })
        },
        content = { paddingValues ->
            ProfileColumn(
                navigationActions = navigationAction,
                profileViewModel = profileViewModel,
                friendRequestViewModel = friendRequestViewModel,
                userPlaylists = userPlaylists.value,
                paddingValue = paddingValues,
                profilePicture = profileViewModel.profilePicture,
                ownProfile = (profileData == selectedProfileData)
            )
        })
}
