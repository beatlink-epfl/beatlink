package com.epfl.beatlink.ui.library

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.R
import com.epfl.beatlink.model.playlist.PlaylistViewModel
import com.epfl.beatlink.ui.components.EditIcon
import com.epfl.beatlink.ui.components.PageTopAppBar
import com.epfl.beatlink.ui.components.PlaylistCard
import com.epfl.beatlink.ui.components.ScreenTopAppBar
import com.epfl.beatlink.ui.components.SongCard
import com.epfl.beatlink.ui.components.TitleWithArrow
import com.epfl.beatlink.ui.navigation.BottomNavigationMenu
import com.epfl.beatlink.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
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
                listOf { EditIcon { } })
        },
        bottomBar = {
            BottomNavigationMenu(
                onTabSelect = { route -> navigationActions.navigateTo(route) },
                tabList = LIST_TOP_LEVEL_DESTINATION,
                selectedItem = navigationActions.currentRoute())
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding).padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically, // Center items vertically
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(20.dp)
                ) {
                    // Cover image
                    Image(
                        painter = painterResource(id = R.drawable.cover_test1), // TODO
                        contentDescription = "Playlist cover",
                        modifier = Modifier.size(132.dp))
                    // Playlist details
                    Column(
                        modifier = Modifier.weight(1f),
                        ) {
                        Text(
                            text = selectedPlaylistState.playlistName,
                            style = TypographyPlaylist.headlineLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AccountCircle,
                                contentDescription = "owner",
                                modifier = Modifier.size(18.dp)

                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "@" + selectedPlaylistState.playlistOwner,
                                style = TypographyPlaylist.headlineMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Text(
                            text = selectedPlaylistState.nbTracks.toString() + " tracks",
                            style = TypographyPlaylist.titleSmall,
                        )
                    }


                }


            }
        }
    )


}