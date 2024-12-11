package com.epfl.beatlink.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.epfl.beatlink.ui.components.ScreenTopAppBar
import com.epfl.beatlink.ui.components.search.PeopleItem
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.theme.primaryGray
import com.epfl.beatlink.viewmodel.profile.FriendRequestViewModel
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel

@Composable
fun LinksScreen(
    navigationActions: NavigationActions,
    profileViewModel: ProfileViewModel,
    friendRequestViewModel: FriendRequestViewModel
) {

  LaunchedEffect(Unit) { profileViewModel.unreadyProfile() }

  val allFriends by friendRequestViewModel.allFriends.observeAsState(emptyList())
  val allFriendsProfileData = remember { mutableStateOf<List<ProfileData?>>(emptyList()) }

  // Fetches the ProfileData for all friends to display them
  LaunchedEffect(allFriends) {
    val profiles = mutableSetOf<ProfileData?>()
    allFriends.forEach { userId ->
      profileViewModel.fetchProfileById(userId) { profileData ->
        if (profileData != null) {
          profiles.add(profileData)
          allFriendsProfileData.value = profiles.toList()
        }
      }
    }
  }

  Scaffold(
      topBar = { ScreenTopAppBar("Links", "LinksScreenTitle", navigationActions) },
      content = { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
              if (allFriends.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().testTag("emptyLinksPrompt"),
                    contentAlignment = Alignment.Center) {
                      Text(
                          text = "No Links :(",
                          style = MaterialTheme.typography.bodyLarge,
                          color = MaterialTheme.colorScheme.primaryGray,
                      )
                    }
              } else {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                  items(allFriendsProfileData.value.size) { i ->
                    val profile = allFriendsProfileData.value[i]
                    PeopleItem(
                        selectedProfileData = profile,
                        navigationActions = navigationActions,
                        profileViewModel = profileViewModel,
                        friendRequestViewModel = friendRequestViewModel)
                  }
                }
              }
            }
      })
}
