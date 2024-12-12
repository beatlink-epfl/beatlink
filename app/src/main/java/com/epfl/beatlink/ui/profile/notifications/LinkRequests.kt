package com.epfl.beatlink.ui.profile.notifications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.model.profile.ProfileData
import com.epfl.beatlink.ui.components.ScreenTopAppBar
import com.epfl.beatlink.ui.components.search.PeopleItem
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.theme.primaryGray
import com.epfl.beatlink.viewmodel.profile.FriendRequestViewModel
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel

@Composable
fun LinkRequestsScreen(
    navigationActions: NavigationActions,
    profileViewModel: ProfileViewModel,
    friendRequestViewModel: FriendRequestViewModel
) {

  val ownRequests by friendRequestViewModel.ownRequests.observeAsState(emptyList())
  val ownRequestProfileData = remember { mutableStateOf<List<ProfileData?>>(emptyList()) }
  val friendRequests by friendRequestViewModel.friendRequests.observeAsState(emptyList())
  val friendRequestProfileData = remember { mutableStateOf<List<ProfileData?>>(emptyList()) }
  val friends by friendRequestViewModel.allFriends.observeAsState(emptyList())
  val friendsProfileData = remember { mutableStateOf<List<ProfileData?>>(emptyList()) }
  var state by remember { mutableIntStateOf(0) }
  val titles = listOf("SENT (${ownRequests.size})", "RECEIVED (${friendRequests.size})")

  LaunchedEffect(ownRequests) {
    val profiles = mutableSetOf<ProfileData?>()
    ownRequests.forEach { userId ->
      profileViewModel.fetchProfileById(userId) { profileData ->
        if (profileData != null) {
          profiles.add(profileData)
          ownRequestProfileData.value = profiles.toList()
        }
      }
    }
  }
  LaunchedEffect(friendRequests) {
    val friendProfiles = mutableSetOf<ProfileData?>()
    friendRequests.forEach { userId ->
      profileViewModel.fetchProfileById(userId) { profileData ->
        if (profileData != null) {
          friendProfiles.add(profileData)
          friendRequestProfileData.value = friendProfiles.toList()
        }
      }
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

  Scaffold(
      modifier = Modifier.testTag("linkRequestsScreen"),
      topBar = { ScreenTopAppBar("Link Requests", "linkRequestsScreenTitle", navigationActions) },
      content = { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
              TabRow(
                  selectedTabIndex = state,
                  modifier = Modifier.fillMaxWidth().testTag("tabRow"),
                  containerColor = MaterialTheme.colorScheme.background) {
                    titles.forEachIndexed { index, title ->
                      Tab(
                          text = { Text(title) },
                          selected = state == index,
                          onClick = { state = index })
                    }
                  }
              Spacer(modifier = Modifier.height(16.dp))
              when (state) {
                0 -> {
                  if (ownRequests.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize().testTag("emptyRequestsPrompt"),
                        contentAlignment = Alignment.Center) {
                          Text(
                              text = "No requests sent.",
                              style = MaterialTheme.typography.bodyLarge,
                              color = MaterialTheme.colorScheme.primaryGray,
                          )
                        }
                  } else {
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                      items(ownRequestProfileData.value.size) { i ->
                        val profile = ownRequestProfileData.value[i]
                        PeopleItem(
                            selectedProfileData = profile,
                            navigationActions = navigationActions,
                            profileViewModel = profileViewModel,
                            friendRequestViewModel = friendRequestViewModel)
                      }
                    }
                  }
                }
                1 -> {
                  if (friendRequests.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize().testTag("emptyRequestsPrompt"),
                        contentAlignment = Alignment.Center) {
                          Text(
                              text = "No requests received.",
                              style = MaterialTheme.typography.bodyLarge,
                              color = MaterialTheme.colorScheme.primaryGray,
                          )
                        }
                  } else {
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                      items(friendRequestProfileData.value.size) { i ->
                        val profile = friendRequestProfileData.value[i]
                        PeopleItem(
                            selectedProfileData = profile,
                            navigationActions = navigationActions,
                            profileViewModel = profileViewModel,
                            friendRequestViewModel = friendRequestViewModel)
                      }
                    }
                  }
                }
              }
            }
      })
}
