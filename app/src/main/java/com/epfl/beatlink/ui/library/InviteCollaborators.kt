package com.epfl.beatlink.ui.library

import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.R
import com.epfl.beatlink.model.spotify.objects.SpotifyArtist
import com.epfl.beatlink.model.spotify.objects.SpotifyTrack
import com.epfl.beatlink.ui.components.CornerIcons
import com.epfl.beatlink.ui.components.PageTitle
import com.epfl.beatlink.ui.components.ScreenTopAppBar
import com.epfl.beatlink.ui.components.library.CollaboratorCard
import com.epfl.beatlink.ui.components.topAppBarModifier
import com.epfl.beatlink.ui.navigation.BottomNavigationMenu
import com.epfl.beatlink.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.ui.search.components.FullSearchBar
import com.epfl.beatlink.ui.search.components.ShortSearchBarLayout
import com.epfl.beatlink.ui.theme.LightGray
import com.epfl.beatlink.ui.theme.PrimaryGray
import com.epfl.beatlink.viewmodel.library.PlaylistViewModel
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel


@Composable
fun InviteCollaboratorsScreen(navigationActions: NavigationActions,
                              profileViewModel: ProfileViewModel,
                              playlistViewModel: PlaylistViewModel) {

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
    val results by profileViewModel.searchResult.observeAsState(emptyList())

    val profilePicture = remember { mutableStateOf<Bitmap?>(null) }

    // Observe search query changes and fetch corresponding results
    LaunchedEffect(searchQuery.value.text) {
        if (searchQuery.value.text.isNotEmpty()) {
            profileViewModel.searchUsers(query = searchQuery.value.text)
        }
    }

    Scaffold(
        modifier = Modifier.testTag("inviteCollaboratorsScreen"),
        topBar = {
            ShortSearchBarLayout(navigationActions = navigationActions,
                searchQuery = searchQuery.value,
                onQueryChange = { newQuery -> searchQuery.value = newQuery })
        },
        bottomBar = {
            BottomNavigationMenu(
                onTabSelect = { route -> navigationActions.navigateTo(route) },
                tabList = LIST_TOP_LEVEL_DESTINATION,
                selectedItem = navigationActions.currentRoute())
        },
        content = { paddingValues ->
            LazyColumn(
                modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background),
                verticalArrangement = Arrangement.spacedBy(16.dp),) {

                items(results.size) { i ->
                    val profile = results[i]
                    val isCollaborator = collabUsernames.contains(profile.username)
                    CollaboratorCard(
                        profile.name,
                        profile.username,
                        profilePicture,
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
                        }
                        )
                }
            }
        })
}