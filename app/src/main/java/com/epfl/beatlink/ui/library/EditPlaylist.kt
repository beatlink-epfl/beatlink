package com.epfl.beatlink.ui.library

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.epfl.beatlink.model.library.Playlist
import com.epfl.beatlink.ui.components.CollabButton
import com.epfl.beatlink.ui.components.CollabList
import com.epfl.beatlink.ui.components.CustomInputField
import com.epfl.beatlink.ui.components.DeleteButton
import com.epfl.beatlink.ui.components.EditButton
import com.epfl.beatlink.ui.components.PrincipalButton
import com.epfl.beatlink.ui.components.ScreenTopAppBar
import com.epfl.beatlink.ui.components.SettingsSwitch
import com.epfl.beatlink.ui.navigation.BottomNavigationMenu
import com.epfl.beatlink.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Route.LIBRARY
import com.epfl.beatlink.ui.navigation.Screen.EDIT_PLAYLIST
import com.epfl.beatlink.ui.navigation.Screen.MY_PLAYLISTS
import com.epfl.beatlink.ui.theme.PrimaryGray
import com.epfl.beatlink.ui.theme.SecondaryGray
import com.epfl.beatlink.viewmodel.library.PlaylistViewModel
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel

@Composable
fun EditPlaylist(
    navigationActions: NavigationActions,
    profileViewModel: ProfileViewModel,
    playlistViewModel: PlaylistViewModel = viewModel(factory = PlaylistViewModel.Factory)
) {
    val context = LocalContext.current
    val selectedPlaylistState =
        playlistViewModel.selectedPlaylist.collectAsState().value
            ?: return Text("No Playlist selected.")

    Scaffold(
        modifier = Modifier.testTag("editPlaylistScreen"),
        topBar = {
            ScreenTopAppBar(
                "Edit " + selectedPlaylistState.playlistName,
                "editPlaylistName",
                navigationActions,
                listOf {
                    DeleteButton {
                        selectedPlaylistState.playlistID.let { playlistViewModel.deletePlaylist(it) }
                    navigationActions.navigateTo(MY_PLAYLISTS)
                    Toast.makeText(context, "Todo deleted successful!", Toast.LENGTH_LONG).show() } })
        },
        bottomBar = {
            BottomNavigationMenu(
                onTabSelect = { route -> navigationActions.navigateTo(route) },
                tabList = LIST_TOP_LEVEL_DESTINATION,
                selectedItem = navigationActions.currentRoute())
        },
        content = { innerPadding ->
            Column(
                modifier =
                Modifier.fillMaxWidth()
                    .padding(innerPadding)
                    .padding(top = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

            }

        }
    )

}