package com.epfl.beatlink.ui.library

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.model.profile.ProfileData
import com.epfl.beatlink.ui.components.library.CollaboratorCard
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.search.components.ShortSearchBarLayout
import com.epfl.beatlink.viewmodel.library.PlaylistViewModel
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel

@Composable
fun InviteCollaboratorsScreen(
    navigationActions: NavigationActions,
    profileViewModel: ProfileViewModel,
    playlistViewModel: PlaylistViewModel
) {
  val profileData by profileViewModel.profile.collectAsState()
  // list of collaborators ID
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

  val searchQuery = remember { mutableStateOf(TextFieldValue("")) }
  val results = remember { mutableStateOf(emptyList<ProfileData>()) }

  // Observe search query changes and fetch corresponding results
  LaunchedEffect(searchQuery.value.text) {
    if (searchQuery.value.text.isNotEmpty()) {
      profileViewModel.searchUsers(query = searchQuery.value.text) { fetchedResults ->
        profileData?.let { currentProfile ->
          results.value = fetchedResults.filter { userProfile -> userProfile != currentProfile }
        }
      }
    }
  }

  Scaffold(
      modifier = Modifier.testTag("inviteCollaboratorsScreen"),
      topBar = {
        ShortSearchBarLayout(
            navigationActions = navigationActions,
            backArrowButton = true,
            searchQuery = searchQuery.value,
            onQueryChange = { newQuery -> searchQuery.value = newQuery },
            placeholder = "Search people to collaborate")
      },
      content = { paddingValues ->
        LazyColumn(
            modifier =
                Modifier.padding(paddingValues)
                    .padding(16.dp)
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
          items(results.value.size) { i ->
            val profile = results.value[i]
            val isCollaborator = collabUsernames.contains(profile.username)
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
                          Log.e("ERROR", "Failed to get userId for username: $userIdToAdd")
                        }
                      })
                },
                onRemove = {
                  profileViewModel.getUserIdByUsername(
                      username = profile.username,
                      onResult = { userIdToRemove ->
                        if (userIdToRemove != null) {
                          val updatedCollabList = playlistCollab.filter { it != userIdToRemove }
                          playlistViewModel.updateTemporallyCollaborators(updatedCollabList)
                          collabUsernames = collabUsernames.filter { it != profile.username }
                        } else {
                          Log.e("ERROR", "Failed to get userId for username")
                        }
                      })
                })
          }
        }
      })
}
