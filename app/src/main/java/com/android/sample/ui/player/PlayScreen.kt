package com.android.sample.ui.player

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
fun PlayScreen(navigationActions: NavigationActions){
    var track = SpotifyTrack("Thank god", "trackId", "cover", 0, 0, State.PLAY)
    var album = SpotifyAlbum("spotifyId", "Utopia", "cover", "Travis Scott", 2023, listOf("trackId"), 0, listOf("genre"), 0)

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
                        modifier = Modifier.testTag("topBarTitle")
                    )
                },
                navigationIcon = {
                    IconButton(
                        modifier = Modifier.testTag("backButton"),
                        onClick = { navigationActions.goBack() }
                    ) {
                        Icon(
                            modifier =
                            Modifier.size(30.dp).graphicsLayer(alpha = 0.99f).drawWithCache {
                                onDrawWithContent {
                                    drawContent()
                                    drawRect(PrimaryGradientBrush, blendMode = BlendMode.SrcAtop)
                                }
                            },
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationMenu(
                onTabSelect = { route -> navigationActions.navigateTo(route) },
                tabList = LIST_TOP_LEVEL_DESTINATION,
                selectedItem = navigationActions.currentRoute()
            )
        },
        content = { pd ->
            Column(modifier = Modifier.fillMaxSize().padding(pd).background(Brush.verticalGradient(colors = listOf(Color.White, SecondaryPurple)))){
                Image(
                    painter = painterResource(id = R.drawable.beatlink_logo), //change to album.cover
                    contentDescription = "Album cover",
                    modifier = Modifier.testTag("albumCover").align(Alignment.CenterHorizontally).padding(pd)
                )
                Text(
                    text = track.name,
                    modifier = Modifier.testTag("trackName").align(Alignment.CenterHorizontally).padding(15.dp)
                )
                Text(
                    text = album.artist,
                    modifier = Modifier.testTag("artistName").align(Alignment.CenterHorizontally)
                )
                Text(
                    text = "${album.name} - ${album.year}",
                    modifier = Modifier.testTag("albumNameYear").align(Alignment.CenterHorizontally)
                )
                Box(modifier = Modifier.align(Alignment.CenterHorizontally).padding(30.dp)){
                    Row(horizontalArrangement = Arrangement.spacedBy(35.dp, Alignment.CenterHorizontally)){
                        IconButton(
                            modifier = Modifier.testTag("previousSongButton"),
                            onClick = {/*back to previous song*/}
                        ){
                          Icon(
                              modifier =
                              Modifier.size(50.dp).graphicsLayer(alpha = 0.99f).drawWithCache {
                                  onDrawWithContent {
                                      drawContent()
                                      drawRect(PrimaryGradientBrush, blendMode = BlendMode.SrcAtop)
                                  }
                              },
                              painter = painterResource(R.drawable.skip_backward),
                              contentDescription = "Go back to previous song"
                          )
                        }
                        IconButton(
                            modifier = Modifier.testTag("playSongButton"),
                            onClick = {/*change song state*/}
                        ){
                            Icon(
                                modifier =
                                Modifier.size(50.dp).graphicsLayer(alpha = 0.99f).drawWithCache {
                                    onDrawWithContent {
                                        drawContent()
                                        drawRect(PrimaryGradientBrush, blendMode = BlendMode.SrcAtop)
                                    }
                                },
                                painter = painterResource(R.drawable.play),
                                contentDescription = "Play or pause the song"
                            )
                        }
                        IconButton(
                            modifier = Modifier.testTag("skipSongButton"),
                            onClick = {/*change song state*/}
                        ){
                            Icon(
                                modifier =
                                Modifier.size(50.dp).graphicsLayer(alpha = 0.99f).drawWithCache {
                                    onDrawWithContent {
                                        drawContent()
                                        drawRect(PrimaryGradientBrush, blendMode = BlendMode.SrcAtop)
                                    }
                                },
                                painter = painterResource(R.drawable.skip_forward),
                                contentDescription = "Skip to next song"
                            )
                        }
                    }
                }
            }
        }
    )
}