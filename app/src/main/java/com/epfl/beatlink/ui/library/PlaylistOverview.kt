package com.epfl.beatlink.ui.library

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.R
import com.epfl.beatlink.model.playlist.PlaylistViewModel
import com.epfl.beatlink.ui.components.EditIcon
import com.epfl.beatlink.ui.components.FilledButton
import com.epfl.beatlink.ui.components.IconWithText
import com.epfl.beatlink.ui.components.ScreenTopAppBar
import com.epfl.beatlink.ui.components.ViewDescriptionButton
import com.epfl.beatlink.ui.navigation.BottomNavigationMenu
import com.epfl.beatlink.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.collab
import com.epfl.beatlink.ui.theme.TypographyPlaylist


@Composable
fun PlaylistOverviewScreen(navigationActions: NavigationActions, playlistViewModel: PlaylistViewModel) {
    val selectedPlaylistState = playlistViewModel.selectedPlaylist.collectAsState().value
        ?: return Text("No Playlist selected.")

    Scaffold(
        modifier = Modifier.testTag("playlistOverviewScreen"),
        topBar = {
            ScreenTopAppBar(
                selectedPlaylistState.playlistName,
                "playlistName",
                navigationActions,
                listOf { EditIcon { /* Opens the Edit Playlist Screen */ } })
        },
        bottomBar = {
            BottomNavigationMenu(
                onTabSelect = { route -> navigationActions.navigateTo(route) },
                tabList = LIST_TOP_LEVEL_DESTINATION,
                selectedItem = navigationActions.currentRoute())
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding).padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(36.dp),
                    modifier = Modifier.padding(20.dp).height(132.dp)
                ) {
                    // Cover image
                    Card(
                        modifier = Modifier.testTag("playlistCoverCard"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.cover_test1), // TODO
                            contentDescription = "Playlist cover",
                            modifier = Modifier.size(132.dp))
                    }

                    // Playlist details
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceBetween
                        ) {
                        Text(
                            text = selectedPlaylistState.playlistName,
                            style = TypographyPlaylist.headlineLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        IconWithText("@" + selectedPlaylistState.playlistOwner,
                            "ownerText",
                            Icons.Outlined.AccountCircle,
                            TypographyPlaylist.headlineMedium)
                        IconWithText(selectedPlaylistState.playlistCollaborators.joinToString(", "),
                            "collaboratorsText",
                            collab,
                            TypographyPlaylist.headlineSmall)
                        IconWithText(if(selectedPlaylistState.playlistPublic) "Public" else "Private",
                            "publicText",
                            Icons.Outlined.Lock,
                            TypographyPlaylist.headlineSmall)
                        Spacer(modifier = Modifier.height(10.dp))
                        ViewDescriptionButton {  }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                FilledButton(
                    "Add to this playlist",
                    "addToThisPlaylistButton") {  /* Opens a page to add songs */ }

                if (selectedPlaylistState.nbTracks == 0) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "NO SONGS ADDED",
                            style = TypographyPlaylist.displayMedium,
                        )
                    }
                } else {
                    LazyColumn() {
                        // List of songs
                    }
                }
            }
        }
    )


}