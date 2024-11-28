package com.epfl.beatlink.ui.player

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.epfl.beatlink.R
import com.epfl.beatlink.ui.navigation.BottomNavigationMenu
import com.epfl.beatlink.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.theme.PrimaryGradientBrush
import com.epfl.beatlink.ui.theme.PrimaryPurple
import com.epfl.beatlink.ui.theme.SecondaryPurple
import com.epfl.beatlink.ui.theme.TypographySongs
import com.epfl.beatlink.viewmodel.map.user.MapUsersViewModel
import com.epfl.beatlink.viewmodel.spotify.api.SpotifyApiViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayScreen(
    navigationActions: NavigationActions,
    api: SpotifyApiViewModel,
    mapUsersViewModel: MapUsersViewModel
) {

  SharedPlayerEffect(api, mapUsersViewModel)

  LaunchedEffect(api.playbackActive) {
    if (!api.playbackActive) {
      navigationActions.goBack()
    }
  }

  Scaffold(
      modifier = Modifier.testTag("playScreen"),
      topBar = {
        CenterAlignedTopAppBar(
            modifier = Modifier.testTag("topAppBar"),
            colors = TopAppBarDefaults.topAppBarColors(titleContentColor = PrimaryPurple),
            title = {
              Text(
                  text = "Now Playing",
                  fontSize = 20.sp,
                  modifier = Modifier.testTag("topBarTitle"),
                  style = TypographySongs.headlineLarge)
            },
            navigationIcon = {
              IconButton(
                  modifier = Modifier.testTag("backButton"),
                  onClick = { navigationActions.goBack() }) {
                    Icon(
                        modifier = Modifier.size(30.dp),
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go back",
                        tint = PrimaryPurple)
                  }
            })
      },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = navigationActions.currentRoute())
      },
      content = { pd ->
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(pd)
                    .background(
                        Brush.verticalGradient(colors = listOf(Color.White, SecondaryPurple)))
                    .testTag("playScreenContent")) {
              PlayScreenUpperBox(api)
              PlayScreenLowerBox(api)
            }
      })
}

@Composable
fun PlayScreenUpperBox(api: SpotifyApiViewModel) {
  Column(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.65F)) {
    Card(
        modifier =
            Modifier.align(Alignment.CenterHorizontally)
                .size(250.dp)
                .padding(15.dp)
                .testTag("albumCover"),
        shape = RoundedCornerShape(5.dp),
    ) {
      AsyncImage(
          model = api.currentTrack.cover,
          contentDescription = "Album cover",
          modifier = Modifier.fillMaxSize())
    }
    Text(
        text = api.currentTrack.name,
        modifier =
            Modifier.testTag("trackName")
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 15.dp),
        style = TypographySongs.headlineLarge)
    Text(
        text = api.currentAlbum.artist,
        modifier = Modifier.testTag("artistName").align(Alignment.CenterHorizontally),
        style = TypographySongs.headlineMedium)
    Text(
        text = "${api.currentAlbum.name} - ${api.currentAlbum.year}",
        modifier = Modifier.testTag("albumNameYear").align(Alignment.CenterHorizontally),
        style = TypographySongs.headlineSmall)
    Box(modifier = Modifier.align(Alignment.CenterHorizontally).padding(30.dp)) {
      Row(horizontalArrangement = Arrangement.spacedBy(35.dp, Alignment.CenterHorizontally)) {
        IconButton(
            modifier = Modifier.testTag("previousSongButton"),
            onClick = {
              api.previousSong()
              api.updatePlayer()
            }) {
              Icon(
                  modifier = Modifier.size(50.dp),
                  painter = painterResource(R.drawable.skip_backward),
                  contentDescription = "Go back to previous song",
                  tint = Color.Unspecified)
            }
        IconButton(
            modifier = Modifier.testTag("playSongButton"),
            onClick = {
              if (api.isPlaying) {
                api.pausePlayback()
              } else {
                api.playPlayback()
              }
            }) {
              if (api.isPlaying) {
                Icon(
                    painter = painterResource(R.drawable.pause),
                    contentDescription = "Pause",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(50.dp))
              } else {
                Icon(
                    painter = painterResource(R.drawable.play),
                    contentDescription = "Play",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(50.dp))
              }
            }
        IconButton(
            modifier = Modifier.testTag("skipSongButton"),
            onClick = {
              api.skipSong()
              api.updatePlayer()
            }) {
              Icon(
                  modifier = Modifier.size(50.dp),
                  painter = painterResource(R.drawable.skip_forward),
                  contentDescription = "Skip to next song",
                  tint = Color.Unspecified)
            }
      }
    }
  }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "MutableCollectionMutableState")
@Composable
fun PlayScreenLowerBox(api: SpotifyApiViewModel) {

  var queue by remember { mutableStateOf(api.queue) }

  api.buildQueue()

  LaunchedEffect(api.queue) { queue = api.queue }

  Scaffold(
      modifier = Modifier.fillMaxWidth(),
      containerColor = Color.Transparent,
      topBar = {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp),
            verticalAlignment = Alignment.CenterVertically) {
              Text(
                  text = "NEXT PLAYED",
                  modifier = Modifier.testTag("nextSongTitle"),
                  style =
                      TextStyle(
                          brush = PrimaryGradientBrush,
                          fontSize = 20.sp,
                          fontFamily = FontFamily(Font(R.font.roboto_bold)),
                          fontWeight = FontWeight(700),
                          letterSpacing = 0.22.sp))
              Spacer(modifier = Modifier.weight(1f))
            }
      },
      content = { _ ->
        Column(
            modifier =
                Modifier.padding(top = 35.dp)
                    .verticalScroll(rememberScrollState())
                    .fillMaxHeight()
                    .fillMaxWidth()) {
              if (queue.isEmpty()) {
                Text(
                    text = "The track list is empty",
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 25.dp))
              } else {
                for (i in 0 until queue.size) {
                  Box(
                      modifier =
                          Modifier.padding(vertical = 10.dp)
                              .clip(RoundedCornerShape(5.dp))
                              .fillMaxWidth(0.92F)
                              .align(Alignment.CenterHorizontally)) {
                        Row(
                            modifier =
                                Modifier.background(Color(0x59FFFFFF)).fillMaxWidth().height(60.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                              Card(
                                  modifier =
                                      Modifier.size(55.dp)
                                          .padding(start = 5.dp)
                                          .testTag("albumCover"),
                                  shape = RoundedCornerShape(5.dp),
                              ) {
                                AsyncImage(
                                    model = queue[i].cover,
                                    contentDescription = "Album cover",
                                    modifier = Modifier.fillMaxSize())
                              }
                              Column(
                                  modifier =
                                      Modifier.align(Alignment.CenterVertically)
                                          .padding(start = 10.dp)) {
                                    Text(
                                        text = queue[i].name,
                                        modifier = Modifier.testTag("trackName"),
                                        style = TypographySongs.titleLarge)
                                    Text(
                                        text = "${queue[i].artist} - ${queue[i].name}",
                                        modifier = Modifier.testTag("albumArtistNameIn"),
                                        style = TypographySongs.titleMedium)
                                  }
                              Spacer(modifier = Modifier.weight(1f))
                            }
                      }
                }
              }
            }
      })
}
