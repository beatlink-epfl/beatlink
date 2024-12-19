package com.epfl.beatlink.ui.player

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.epfl.beatlink.R
import com.epfl.beatlink.model.spotify.objects.SpotifyTrack
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.theme.PrimaryGradientBrush
import com.epfl.beatlink.ui.theme.PrimaryPurple
import com.epfl.beatlink.ui.theme.SecondaryPurple
import com.epfl.beatlink.ui.theme.TypographySongs
import com.epfl.beatlink.viewmodel.map.user.MapUsersViewModel
import com.epfl.beatlink.viewmodel.spotify.api.SpotifyApiViewModel

@Composable
fun PlayScreen(
    navigationActions: NavigationActions,
    spotifyApiViewModel: SpotifyApiViewModel,
    mapUsersViewModel: MapUsersViewModel
) {

  SharedPlayerEffect(spotifyApiViewModel, mapUsersViewModel)

  LaunchedEffect(spotifyApiViewModel.playbackActive) {
    if (!spotifyApiViewModel.playbackActive) navigationActions.goBack()
  }

  Scaffold(
      modifier = Modifier.testTag("playScreen"),
      topBar = { PlayScreenTopBar(navigationActions) },
      content = { pd ->
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(pd)
                    .background(
                        Brush.verticalGradient(colors = listOf(Color.White, SecondaryPurple)))
                    .testTag("playScreenContent")) {
              PlayScreenUpperBox(spotifyApiViewModel)
              Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                PlayScreenLowerBox(spotifyApiViewModel)
              }
            }
      })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayScreenTopBar(navigationActions: NavigationActions) {
  CenterAlignedTopAppBar(
      modifier = Modifier.testTag("topAppBar"),
      colors = TopAppBarDefaults.topAppBarColors(titleContentColor = PrimaryPurple),
      title = {
        Text(
            text = "Now Playing",
            fontSize = 20.sp,
            style = TypographySongs.headlineLarge,
            modifier = Modifier.testTag("topBarTitle"))
      },
      navigationIcon = {
        IconButton(
            onClick = { navigationActions.goBack() }, modifier = Modifier.testTag("backButton")) {
              Icon(
                  imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                  contentDescription = "Go back",
                  tint = PrimaryPurple,
                  modifier = Modifier.size(30.dp))
            }
      })
}

@Composable
fun TrackCard(imageUrl: String, size: Dp, contentDescription: String) {
  Card(
      modifier = Modifier.size(size).testTag("albumCover"),
      shape = RoundedCornerShape(5.dp),
  ) {
    AsyncImage(
        model = imageUrl,
        contentDescription = contentDescription,
        modifier = Modifier.fillMaxSize())
  }
}

@Composable
fun PlayerButton(onClick: () -> Unit, @DrawableRes iconRes: Int, description: String) {
  IconButton(onClick = onClick) {
    Icon(
        painter = painterResource(iconRes),
        contentDescription = description,
        modifier = Modifier.size(50.dp),
        tint = Color.Unspecified)
  }
}

@Composable
fun PlaybackControls(spotifyApiViewModel: SpotifyApiViewModel) {
  Row(
      horizontalArrangement = Arrangement.spacedBy(35.dp, Alignment.CenterHorizontally),
      modifier = Modifier.padding(30.dp)) {
        PlayerButton(
            onClick = {
              spotifyApiViewModel.previousSong()
              spotifyApiViewModel.updatePlayer()
            },
            iconRes = R.drawable.skip_backward,
            description = "Go back to previous song")
        PlayerButton(
            onClick = {
              if (spotifyApiViewModel.isPlaying) spotifyApiViewModel.pausePlayback()
              else spotifyApiViewModel.playPlayback()
            },
            iconRes = if (spotifyApiViewModel.isPlaying) R.drawable.pause else R.drawable.play,
            description = if (spotifyApiViewModel.isPlaying) "Pause" else "Play")
        PlayerButton(
            onClick = {
              spotifyApiViewModel.skipSong()
              spotifyApiViewModel.updatePlayer()
            },
            iconRes = R.drawable.skip_forward,
            description = "Skip to next song")
      }
}

@Composable
fun PlayScreenUpperBox(spotifyApiViewModel: SpotifyApiViewModel) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Box(modifier = Modifier.align(Alignment.CenterHorizontally).padding(15.dp)) {
      TrackCard(spotifyApiViewModel.currentAlbum.cover, 250.dp, "Album cover")
    }
    Text(
        text = spotifyApiViewModel.currentTrack.name,
        modifier =
            Modifier.testTag("trackName")
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 15.dp),
        style = TypographySongs.headlineLarge)
    Text(
        text = spotifyApiViewModel.currentAlbum.artist,
        modifier = Modifier.testTag("artistName").align(Alignment.CenterHorizontally),
        style = TypographySongs.headlineMedium)
    Text(
        text =
            "${spotifyApiViewModel.currentAlbum.name} - ${spotifyApiViewModel.currentAlbum.year}",
        modifier = Modifier.testTag("albumNameYear").align(Alignment.CenterHorizontally),
        style = TypographySongs.headlineSmall)
    Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
      PlaybackControls(spotifyApiViewModel)
    }
  }
}

@Composable
fun TrackList(spotifyApiViewModel: SpotifyApiViewModel) {
  Column(
      modifier =
          Modifier.padding(top = 35.dp)
              .verticalScroll(rememberScrollState())
              .fillMaxHeight()
              .fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally) {
        if (spotifyApiViewModel.queue.isEmpty()) {
          Text(
              text = "The track list is empty",
              modifier =
                  Modifier.align(Alignment.CenterHorizontally)
                      .padding(top = 25.dp)
                      .testTag("emptyQueue"))
        } else {
          spotifyApiViewModel.queue.forEachIndexed { index, track -> TrackItem(track, index) }
        }
      }
}

@Composable
fun TrackItem(track: SpotifyTrack, index: Int) {
  Box(
      modifier =
          Modifier.padding(5.dp)
              .clip(RoundedCornerShape(5.dp))
              .fillMaxWidth(0.92f)
              .testTag("trackItem $index")) {
        Row(
            modifier = Modifier.background(Color(0x59FFFFFF)).fillMaxWidth().height(60.dp),
            verticalAlignment = Alignment.CenterVertically) {
              Box(modifier = Modifier.padding(3.dp)) {
                TrackCard(imageUrl = track.cover, size = 55.dp, contentDescription = "Album cover")
              }
              Column(modifier = Modifier.padding(start = 10.dp)) {
                Text(text = track.name, style = TypographySongs.titleLarge)
                Text(text = track.artist, style = TypographySongs.titleMedium)
              }
            }
      }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "MutableCollectionMutableState")
@Composable
fun PlayScreenLowerBox(spotifyApiViewModel: SpotifyApiViewModel) {
  Scaffold(
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
      content = { TrackList(spotifyApiViewModel) })
}
