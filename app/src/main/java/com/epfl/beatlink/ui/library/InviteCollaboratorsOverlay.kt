package com.epfl.beatlink.ui.library

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.model.profile.ProfileData
import com.epfl.beatlink.ui.components.GradientTitle
import com.epfl.beatlink.ui.components.ReusableOverlay
import com.epfl.beatlink.ui.components.library.CollaboratorCard
import com.epfl.beatlink.ui.components.topAppBarModifier
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen.INVITE_COLLABORATORS
import com.epfl.beatlink.viewmodel.library.PlaylistViewModel
import com.epfl.beatlink.viewmodel.profile.FriendRequestViewModel
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel

@Composable
fun InviteCollaboratorsOverlay(
    navigationActions: NavigationActions,
    profileViewModel: ProfileViewModel,
    friendRequestViewModel: FriendRequestViewModel,
    playlistViewModel: PlaylistViewModel,
    onDismissRequest: () -> Unit
) {
  val friends by friendRequestViewModel.allFriends.observeAsState(emptyList())
  val friendsProfileData = remember { mutableStateOf<List<ProfileData?>>(emptyList()) }

  // list of collaborators ID of the playlist
  val playlistCollab by playlistViewModel.tempPlaylistCollaborators.collectAsState()
  val fetchedUsernames = mutableListOf<String>()
  var collabUsernames by remember { mutableStateOf<List<String>>(emptyList()) }

  playlistCollab.forEach { userId ->
    profileViewModel.getUsername(userId) { username ->
      if (username != null) {
        fetchedUsernames.add(username)
      }
      collabUsernames = fetchedUsernames.toList()
    }
  }

  LaunchedEffect(friends) {
    val profiles = mutableSetOf<ProfileData?>()
    friends.forEach { userId ->
      profileViewModel.fetchProfileById(userId) { profileData ->
        if (profileData != null) {
          profiles.add(profileData)
          friendsProfileData.value = profiles.toList()
        }
      }
    }
  }

  ReusableOverlay(
      onDismissRequest = onDismissRequest,
      modifier = Modifier.height(384.dp),
  ) {
    Column(
        verticalArrangement = Arrangement.Top,
        modifier =
            Modifier.fillMaxSize()
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(10.dp))) {
          Spacer(modifier = Modifier.height(15.dp))
          Row(
              modifier =
                  Modifier.fillMaxWidth()
                      .background(color = MaterialTheme.colorScheme.surfaceVariant)
                      .topAppBarModifier()) {
                Row(
                    modifier =
                        Modifier.testTag("searchBar")
                            .fillMaxSize()
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceContainer,
                                shape = RoundedCornerShape(5.dp))
                            .clickable { navigationActions.navigateTo(INVITE_COLLABORATORS) },
                    verticalAlignment = Alignment.CenterVertically) {
                      Icon(
                          imageVector = Icons.Default.Search,
                          contentDescription = "Search Icon",
                          tint = MaterialTheme.colorScheme.primaryContainer,
                          modifier =
                              Modifier.testTag("writableSearchBarIcon")
                                  .size(30.dp)
                                  .padding(start = 5.dp))
                      Text(
                          text = "Search people to collaborate",
                          style = MaterialTheme.typography.bodyMedium,
                          color = MaterialTheme.colorScheme.primaryContainer)
                    }
              }
          Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            GradientTitle("LINKED FRIENDS")
          }

          LazyColumn(
              modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 4.dp),
              verticalArrangement = Arrangement.spacedBy(11.dp)) {
                items(friendsProfileData.value.size) { i ->
                  val profile = friendsProfileData.value[i]
                  val isCollaborator = collabUsernames.contains(profile?.username)
                  if (profile != null) {
                    CollaboratorCard(
                        profile,
                        profileViewModel,
                        isCollaborator,
                        onAdd = {
                          profileViewModel.getUserIdByUsername(
                              username = profile.username,
                              onResult = { userIdToAdd ->
                                if (userIdToAdd != null) {
                                  val updatedCollabList = playlistCollab + userIdToAdd
                                  playlistViewModel.updateTemporallyCollaborators(updatedCollabList)
                                  collabUsernames = collabUsernames + profile.username
                                } else {
                                  Log.e("ERROR", "userId is null")
                                }
                              })
                        },
                        onRemove = {
                          profileViewModel.getUserIdByUsername(
                              username = profile.username,
                              onResult = { userIdToRemove ->
                                if (userIdToRemove != null) {
                                  val updatedCollabList =
                                      playlistCollab.filter { it != userIdToRemove }
                                  playlistViewModel.updateTemporallyCollaborators(updatedCollabList)
                                  collabUsernames =
                                      collabUsernames.filter { it != profile.username }
                                } else {
                                  Log.e("ERROR", "Failed to get userId for username")
                                }
                              })
                        })
                  }
                }
              }
        }
  }
}
