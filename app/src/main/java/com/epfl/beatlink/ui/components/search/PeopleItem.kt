package com.epfl.beatlink.ui.components.search

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.distinctUntilChanged
import com.epfl.beatlink.model.profile.ProfileData
import com.epfl.beatlink.ui.components.LinkButton
import com.epfl.beatlink.ui.components.ProfileCardLinkButton
import com.epfl.beatlink.ui.components.ProfileLinkButton
import com.epfl.beatlink.ui.components.ProfilePicture
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.ui.theme.TypographySongs
import com.epfl.beatlink.viewmodel.profile.FriendRequestViewModel
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel

@Composable
fun PeopleItem(
    people: ProfileData?,
    navigationActions: NavigationActions,
    profileViewModel: ProfileViewModel,
    friendRequestViewModel: FriendRequestViewModel,
) {
    LaunchedEffect(Unit) {
        // profileViewModel.fetchProfile()
        profileViewModel.clearSelectedUser()
    }
    val profileData by profileViewModel.profile.collectAsState()

    val ownRequests by friendRequestViewModel.ownRequests.observeAsState(emptyList())
    val friendRequests by friendRequestViewModel.friendRequests.observeAsState(emptyList())
    val allFriends by friendRequestViewModel.allFriends.observeAsState(emptyList())

    val displayedUserId = remember { mutableStateOf<String?>(null) }
    val selectedUserUserId = remember { mutableStateOf("") }

    val fetchOtherProfileFriends = remember { mutableStateOf(false) }
    val otherProfileAllFriends by friendRequestViewModel.otherProfileAllFriends.observeAsState(emptyList())
    Log.d("PROFILE_LINK","PEOPLE ITEM -- other friend list : $otherProfileAllFriends")
    val profilePicture = remember { mutableStateOf<Bitmap?>(null) }

    if (people != null) {
        profileViewModel.getUserIdByUsername(people.username) { uid ->
            if (uid == null) {
                return@getUserIdByUsername
            } else {
                displayedUserId.value = uid
                selectedUserUserId.value = uid
                profileViewModel.loadProfilePicture(uid) { profilePicture.value = it }
            }
        }
    } else {
        Log.d("PeopleItem", "profile data null")
    }

    Log.d("PROFILE_LINK", " PEOPLE ITEM -- selectedUserId: ${selectedUserUserId.value}")
    Log.d("PROFILE_LINK", " PEOPLE ITEM -- FRIENDS: $allFriends /// OTHER FRIENDS: $otherProfileAllFriends")

  var requestStatus =
      when (displayedUserId.value) {
        in ownRequests -> "Requested"
        in friendRequests -> "Accept"
        in allFriends -> "Linked"
        else -> "Link"
      }

    LaunchedEffect(allFriends, fetchOtherProfileFriends.value) {
        if (profileData?.links != allFriends.size) {
            profileData?.let { currentProfile ->
                Log.d("PROFILE_USERNAME", " username of current user before update: ${currentProfile.username} and ${allFriends.size}")
                profileViewModel.updateNbLinks(currentProfile, allFriends.size)
            }
        }
        if (fetchOtherProfileFriends.value) {
            friendRequestViewModel.getOtherProfileAllFriends(selectedUserUserId.value)
            Log.d("PROFILE", "peopleItem: after getOtherProfile ${otherProfileAllFriends.size} and nbLinks ${people?.links}")
            if (people?.links != otherProfileAllFriends.size) {
                people?.let { selectedProfile ->
                    Log.d(
                        "PROFILE_USERNAME",
                        " PEOPLE ITEM -- username of selected user before update: ${selectedProfile.username} and ${otherProfileAllFriends.size}"
                    )
                    profileViewModel.updateOtherProfileNbLinks(
                        selectedProfile,
                        selectedUserUserId.value,
                        otherProfileAllFriends.size
                    )
                }
            }
            fetchOtherProfileFriends.value = false // Reset trigger
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
        Modifier
            .clickable {
                if (selectedUserUserId.value.isNotEmpty()) {
                    profileViewModel.selectSelectedUser(selectedUserUserId.value) // Set the selected user
                    profileViewModel.fetchUserProfile() // Fetch the user's profile
                    navigationActions.navigateTo(Screen.OTHER_PROFILE)
                }
            }
            .height(78.dp)
            .padding(end = 16.dp)
            .testTag("peopleItem")) {
        Box(
            modifier =
            Modifier
                .padding(horizontal = 16.dp)
                .size(60.dp)
                .clip(CircleShape)
                .testTag("peopleImage")
        ) {
            ProfilePicture(profilePicture)
        }
        Spacer(modifier = Modifier.size(10.dp))
        if (people != null) {
            Text(
                text = people.username,
                style = TypographySongs.titleLarge,
                modifier = Modifier.testTag("peopleUsername")
            )
        } else {
            Log.d("PeopleItem", "profile data null")
        }
        Spacer(modifier = Modifier.weight(1f))
        if (profileData != people) {
            ProfileCardLinkButton(
                buttonText = requestStatus
            ) {
                when (requestStatus) {
                    "Link" -> {
                        selectedUserUserId.value.let { friendRequestViewModel.sendFriendRequestTo(it) }
                        requestStatus = "Requested"
                    }
                    "Requested" -> {
                        selectedUserUserId.value.let { friendRequestViewModel.cancelFriendRequestTo(it) }
                        requestStatus = "Link"
                    }
                    "Accept" -> {
                        selectedUserUserId.value.let { friendRequestViewModel.acceptFriendRequestFrom(it) }
                        requestStatus = "Linked"
                        fetchOtherProfileFriends.value = true
                    }
                    "Linked" -> {
                        selectedUserUserId.value.let { friendRequestViewModel.removeFriend(it) }
                        requestStatus = "Link"
                        fetchOtherProfileFriends.value = true
                    }
                }
            }
        }
    }
}
