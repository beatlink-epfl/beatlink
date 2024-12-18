package com.epfl.beatlink.ui.components.search

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.model.profile.ProfileData
import com.epfl.beatlink.ui.components.ProfileCardLinkButton
import com.epfl.beatlink.ui.components.ProfilePicture
import com.epfl.beatlink.ui.components.RejectButton
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.viewmodel.profile.FriendRequestViewModel
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel

@Composable
fun PeopleItem(
    selectedProfileData: ProfileData?,
    navigationActions: NavigationActions,
    profileViewModel: ProfileViewModel,
    friendRequestViewModel: FriendRequestViewModel,
) {
  // Updates the sent/received friend requests instantly by reloading the screen
  LaunchedEffect(Unit) {
    friendRequestViewModel.getOwnRequests()
    friendRequestViewModel.getFriendRequests()
    friendRequestViewModel.getAllFriends()
  }
  val profileData by profileViewModel.profile.collectAsState()

  val ownRequests by friendRequestViewModel.ownRequests.observeAsState(emptyList())
  val friendRequests by friendRequestViewModel.friendRequests.observeAsState(emptyList())
  val allFriends by friendRequestViewModel.allFriends.observeAsState(emptyList())

  // use only when accepting or removing
  val selectedUserUserId = remember { mutableStateOf("") }
  val selectedProfileDataNbLinks = remember { mutableStateOf(selectedProfileData?.links) }

  val profilePicture = remember { mutableStateOf<Bitmap?>(null) }

  if (selectedProfileData != null) {
    profileViewModel.getUserIdByUsername(selectedProfileData.username) { uid ->
      if (uid == null) {
        return@getUserIdByUsername
      } else {
        selectedUserUserId.value = uid
        profileViewModel.loadProfilePicture(uid) { profilePicture.value = it }
      }
    }
  } else {
    Log.d("PeopleItem", "profile data null")
  }

  var requestStatus =
      when (selectedUserUserId.value) {
        in ownRequests -> "Requested"
        in friendRequests -> "Accept"
        in allFriends -> "Linked"
        else -> "Link"
      }

  LaunchedEffect(allFriends, selectedProfileDataNbLinks) {
    if (profileData?.links != allFriends.size) {
      profileData?.let { currentProfile ->
        profileViewModel.updateNbLinks(currentProfile, allFriends.size)
      }
    }
    if (selectedProfileData?.links != selectedProfileDataNbLinks.value) {
      selectedProfileData?.let { selectedProfile ->
        selectedProfileDataNbLinks.value?.let {
          profileViewModel.updateOtherProfileNbLinks(selectedProfile, selectedUserUserId.value, it)
        }
      }
    }
  }

  Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier =
          Modifier.clickable {
                if (selectedUserUserId.value.isNotEmpty()) {
                  profileViewModel.selectSelectedUser(
                      selectedUserUserId.value) // Set the selected user
                  profileViewModel.fetchUserProfile() // Fetch the user's profile
                  navigationActions.navigateTo(Screen.OTHER_PROFILE)
                }
              }
              .height(78.dp)
              .padding(end = 12.dp)
              .testTag("peopleItem")) {
        Box(
            modifier =
                Modifier.padding(horizontal = 12.dp)
                    .size(60.dp)
                    .clip(CircleShape)
                    .testTag("peopleImage")) {
              ProfilePicture(profilePicture)
            }
        if (selectedProfileData != null) {
          Column(
              verticalArrangement = Arrangement.spacedBy(6.dp),
              modifier = Modifier.fillMaxWidth(0.5f)) {
                Text(
                    text = selectedProfileData.name ?: "",
                    style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = "@${selectedProfileData.username.uppercase()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis)
              }
        } else {
          Log.d("PeopleItem", "profile data null")
        }
        Spacer(modifier = Modifier.weight(1f))
        if (profileData != selectedProfileData) {
          Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.End,
              modifier = Modifier.width(130.dp)) {
                ProfileCardLinkButton(buttonText = requestStatus) {
                  when (requestStatus) {
                    "Link" -> {
                      selectedUserUserId.value.let {
                        friendRequestViewModel.sendFriendRequestTo(it)
                      }
                      requestStatus = "Requested"
                    }
                    "Requested" -> {
                      selectedUserUserId.value.let {
                        friendRequestViewModel.cancelFriendRequestTo(it)
                      }
                      requestStatus = "Link"
                    }
                    "Accept" -> {
                      selectedUserUserId.value.let {
                        friendRequestViewModel.acceptFriendRequestFrom(it)
                      }
                      requestStatus = "Linked"
                      selectedProfileDataNbLinks.value = selectedProfileDataNbLinks.value?.plus(1)
                    }
                    "Linked" -> {
                      selectedUserUserId.value.let { friendRequestViewModel.removeFriend(it) }
                      requestStatus = "Link"
                      selectedProfileDataNbLinks.value = selectedProfileDataNbLinks.value?.minus(1)
                    }
                  }
                }
                Spacer(Modifier.width(10.dp))
                if (requestStatus == "Accept") {
                  RejectButton {
                    selectedUserUserId.value.let {
                      friendRequestViewModel.rejectFriendRequestFrom(it)
                      requestStatus = "Link"
                    }
                  }
                }
              }
        }
      }
}
