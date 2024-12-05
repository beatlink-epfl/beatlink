package com.epfl.beatlink.ui.components.search

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.model.profile.ProfileData
import com.epfl.beatlink.ui.components.LinkButton
import com.epfl.beatlink.ui.components.PrincipalButton
import com.epfl.beatlink.ui.components.ProfilePicture
import com.epfl.beatlink.ui.theme.TypographySongs
import com.epfl.beatlink.viewmodel.profile.FriendRequestViewModel
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel

@Composable
fun PeopleItem(
    people: ProfileData, profileViewModel: ProfileViewModel,
    friendRequestViewModel: FriendRequestViewModel
) {

    val displayedUserId = remember { mutableStateOf<String?>(null) }

    val ownRequests by friendRequestViewModel.ownRequests.observeAsState(emptyList())
    val friendRequests by friendRequestViewModel.friendRequests.observeAsState(emptyList())
    val allFriends by friendRequestViewModel.allFriends.observeAsState(emptyList())

    val profilePicture = remember { mutableStateOf<Bitmap?>(null) }
    profileViewModel.getUserIdByUsername(people.username) { uid ->
        if (uid == null) {
            return@getUserIdByUsername
        } else {
            displayedUserId.value = uid
            profileViewModel.loadProfilePicture(uid) { profilePicture.value = it }
        }
    }

    var requestStatus = when (displayedUserId.value) {
        in ownRequests -> "Requested"
        in friendRequests -> "Accept"
        in allFriends -> "Linked"
        else -> "Link"
    }

    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(end = 16.dp)
            .testTag("peopleItem")) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .size(60.dp)
                .clip(CircleShape)
                .testTag("peopleImage")
        ) {
            ProfilePicture(profilePicture)
        }
        Spacer(modifier = Modifier.size(10.dp))
        Text(
            text = people.username,
            style = TypographySongs.titleLarge,
            modifier = Modifier.testTag("peopleUsername")
        )
        Spacer(modifier = Modifier.weight(1f))
        LinkButton(
            buttonText = requestStatus,
            onClickLink = {
                val receiverId = displayedUserId.value
                if (receiverId != null) {
                    friendRequestViewModel.sendFriendRequestTo(receiverId)
                    requestStatus = "Requested"
                } else {
                    Log.e("PeopleItem",
                        "Unable to send friend request: Missing sender or receiver ID")
                }
                          },
            onClickRequested = {
                val receiverId = displayedUserId.value
                if (receiverId != null) {
                    friendRequestViewModel.cancelFriendRequestTo(receiverId)
                    requestStatus = "Link"
                } else {
                    Log.e("PeopleItem",
                        "Unable to cancel friend request: Missing sender or receiver ID")
                } },
            onClickAccept = {
                val receiverId = displayedUserId.value
                if (receiverId != null) {
                    friendRequestViewModel.acceptFriendRequestFrom(receiverId)
                    requestStatus = "Linked"
                } else {
                    Log.e("PeopleItem",
                        "Unable to accept friend request: Missing sender or receiver ID")
                }
            },
            onClickLinked = {
                val receiverId = displayedUserId.value
                if (receiverId != null) {
                    friendRequestViewModel.removeFriend(receiverId)
                    requestStatus = "Link"
                } else {
                    Log.e(
                        "PeopleItem",
                        "Unable to remove friend: Missing sender or receiver ID"
                    )
                }
            }
        )
    }
}
