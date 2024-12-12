package com.epfl.beatlink.ui.library

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.model.profile.ProfileData
import com.epfl.beatlink.ui.components.ReusableOverlay
import com.epfl.beatlink.ui.components.library.CollaboratorCard
import com.epfl.beatlink.ui.components.topAppBarModifier
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen.INVITE_COLLABORATORS
import com.epfl.beatlink.viewmodel.profile.FriendRequestViewModel
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel

@Composable
fun InviteCollaboratorsOverlay(
    navigationActions: NavigationActions,
    profileViewModel: ProfileViewModel,
    friendRequestViewModel: FriendRequestViewModel,
    onDismissRequest: () -> Unit
) {
  val profilePicture = remember { mutableStateOf<Bitmap?>(null) }

  val friends by friendRequestViewModel.allFriends.observeAsState(emptyList())
  val friendsProfileData = remember { mutableStateOf<List<ProfileData?>>(emptyList()) }

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
                          text = "Search friends to collaborate",
                          style = MaterialTheme.typography.bodyMedium,
                          color = MaterialTheme.colorScheme.primaryContainer)
                    }
              }
          LazyColumn(
              modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 10.dp),
              verticalArrangement = Arrangement.spacedBy(11.dp)) {
                items(friendsProfileData.value.size) { i ->
                  val profile = friendsProfileData.value[i]
                  if (profile != null) {
                    CollaboratorCard(
                        profile.name,
                        profile.username,
                        profilePicture,
                        false,
                        onAdd = {},
                        onRemove = {})
                  }
                }
              }
        }
  }
}
