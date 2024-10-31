package com.android.sample.ui.player

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
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
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.model.spotify.objects.*
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayScreen(navigationActions: NavigationActions, track: SpotifyTrack, album: SpotifyAlbum) {
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
                  modifier = Modifier.testTag("topBarTitle"))
            },
            navigationIcon = {
              IconButton(
                  modifier = Modifier.testTag("backButton"),
                  onClick = { navigationActions.goBack() }) {
                    Icon(
                        modifier =
                            Modifier.size(30.dp)
                                .graphicsLayer(alpha = 0.99f)
                                .drawWithCache {
                                  onDrawWithContent {
                                    drawContent()
                                    drawRect(PrimaryGradientBrush, blendMode = BlendMode.SrcAtop)
                                  }
                                }
                                .testTag("backButtonIcon"),
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go back")
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
              PlayScreenUpperBox(track, album)
              PlayScreenLowerBox(album)
            }
      })
}

@Composable
fun PlayScreenUpperBox(track: SpotifyTrack, album: SpotifyAlbum) {
  Column(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.65F)) {
    Image(
        painter = painterResource(id = R.drawable.beatlink_logo), // change to album.cover
        contentDescription = "Album cover",
        modifier =
            Modifier.testTag("albumCover")
                .align(Alignment.CenterHorizontally)
                .size(250.dp)
                .padding(15.dp))
    Text(
        text = track.name,
        modifier =
            Modifier.testTag("trackName")
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 15.dp))
    Text(
        text = album.artist,
        modifier = Modifier.testTag("artistName").align(Alignment.CenterHorizontally))
    Text(
        text = "${album.name} - ${album.year}",
        modifier = Modifier.testTag("albumNameYear").align(Alignment.CenterHorizontally))
    Box(modifier = Modifier.align(Alignment.CenterHorizontally).padding(30.dp)) {
      Row(horizontalArrangement = Arrangement.spacedBy(35.dp, Alignment.CenterHorizontally)) {
        IconButton(
            modifier = Modifier.testTag("previousSongButton"),
            onClick = { /*back to previous song*/}) {
              Icon(
                  modifier =
                      Modifier.size(50.dp).graphicsLayer(alpha = 0.99f).drawWithCache {
                        onDrawWithContent {
                          drawContent()
                          drawRect(PrimaryGradientBrush, blendMode = BlendMode.SrcAtop)
                        }
                      },
                  painter = painterResource(R.drawable.skip_backward),
                  contentDescription = "Go back to previous song")
            }
        IconButton(
            modifier = Modifier.testTag("playSongButton"), onClick = { /*change song state*/}) {
              Icon(
                  modifier =
                      Modifier.size(50.dp).graphicsLayer(alpha = 0.99f).drawWithCache {
                        onDrawWithContent {
                          drawContent()
                          drawRect(PrimaryGradientBrush, blendMode = BlendMode.SrcAtop)
                        }
                      },
                  painter = painterResource(R.drawable.play),
                  contentDescription = "Play or pause the song")
            }
        IconButton(
            modifier = Modifier.testTag("skipSongButton"), onClick = { /*change song state*/}) {
              Icon(
                  modifier =
                      Modifier.size(50.dp).graphicsLayer(alpha = 0.99f).drawWithCache {
                        onDrawWithContent {
                          drawContent()
                          drawRect(PrimaryGradientBrush, blendMode = BlendMode.SrcAtop)
                        }
                      },
                  painter = painterResource(R.drawable.skip_forward),
                  contentDescription = "Skip to next song")
            }
      }
    }
  }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PlayScreenLowerBox(album: SpotifyAlbum) {
  Scaffold(
      modifier = Modifier.fillMaxWidth(),
      containerColor = Color.Transparent,
      topBar = {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp),
            verticalAlignment = Alignment.CenterVertically) {
              Text(text = "NEXT PLAYED", modifier = Modifier.testTag("nextSongTitle"))
              Spacer(modifier = Modifier.weight(1f))
              IconButton(
                  modifier = Modifier.testTag("replayButton"),
                  onClick = { /*change to replay mode*/}) {
                    Icon(
                        modifier =
                            Modifier.size(30.dp).graphicsLayer(alpha = 0.99f).drawWithCache {
                              onDrawWithContent {
                                drawContent()
                                drawRect(PrimaryPurple, blendMode = BlendMode.SrcAtop)
                              }
                            },
                        painter = painterResource(R.drawable.replay_button),
                        contentDescription = "")
                  }
              IconButton(
                  modifier = Modifier.testTag("shuffleButton"),
                  onClick = { /*change to shuffle mode*/}) {
                    Icon(
                        modifier =
                            Modifier.size(30.dp).graphicsLayer(alpha = 0.99f).drawWithCache {
                              onDrawWithContent {
                                drawContent()
                                drawRect(PrimaryPurple, blendMode = BlendMode.SrcAtop)
                              }
                            },
                        painter = painterResource(R.drawable.shuffle_button),
                        contentDescription = "Go back")
                  }
            }
      },
      content = { _ ->
        Column(
            modifier =
                Modifier.padding(top = 35.dp)
                    .verticalScroll(rememberScrollState())
                    .fillMaxHeight()
                    .fillMaxWidth()) {
              if (album.tracks.isEmpty()) {
                Text(
                    text = "The track list is empty",
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 25.dp))
              } else {
                for (i in 0 until album.tracks.size) {
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
                              Image(
                                  painter =
                                      painterResource(
                                          id = R.drawable.beatlink_logo), // change to album.cover
                                  contentDescription = "Album cover",
                                  modifier =
                                      Modifier.testTag("albumCover")
                                          .size(55.dp)
                                          .padding(start = 5.dp))
                              Column(
                                  modifier =
                                      Modifier.align(Alignment.CenterVertically)
                                          .padding(start = 10.dp)) {
                                    Text(
                                        text = album.tracks[i].name,
                                        modifier = Modifier.testTag("trackName"))
                                    Text(
                                        text = "${album.artist} - ${album.name}",
                                        modifier = Modifier.testTag("albumArtistNameIn"))
                                  }
                              Spacer(modifier = Modifier.weight(1f))
                              IconButton(
                                  modifier = Modifier.testTag("moveTrackButton"),
                                  onClick = { /*moveTrack*/}) {
                                    Icon(
                                        contentDescription = "Move the track",
                                        modifier =
                                            Modifier.padding(end = 10.dp)
                                                .graphicsLayer(alpha = 0.99f)
                                                .drawWithCache {
                                                  onDrawWithContent {
                                                    drawContent()
                                                    drawRect(
                                                        Color.Gray, blendMode = BlendMode.SrcAtop)
                                                  }
                                                },
                                        painter = painterResource(id = R.drawable.move_icon))
                                  }
                            }
                      }
                }
              }
            }
      })
}
