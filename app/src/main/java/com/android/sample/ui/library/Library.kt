package com.android.sample.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.theme.PrimaryGradientBrush
import com.android.sample.ui.theme.TypographyBeatLink

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(navigationActions: NavigationActions) {

  Scaffold(
      modifier = Modifier.testTag("libraryScreen"),
      topBar = {
        TopAppBar(
            title = {
              Text(
                  text = "My Library",
                  style = TypographyBeatLink.headlineLarge,
                  modifier = Modifier.padding(top = 14.dp).testTag("libraryTitle"))
            },
            actions = {
              // ICON Search
              IconButton(onClick = {}, modifier = Modifier.testTag("searchButton")) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Search",
                    modifier =
                        Modifier.size(28.dp).graphicsLayer(alpha = 0.99f).drawWithCache {
                          onDrawWithContent {
                            drawContent()
                            drawRect(PrimaryGradientBrush, blendMode = BlendMode.SrcAtop)
                          }
                        })
              }
              // ICON Add playlist
              IconButton(onClick = {}, modifier = Modifier.testTag("addPlaylistButton")) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "Add playlist",
                    modifier =
                        Modifier.size(28.dp).graphicsLayer(alpha = 0.99f).drawWithCache {
                          onDrawWithContent {
                            drawContent()
                            drawRect(PrimaryGradientBrush, blendMode = BlendMode.SrcAtop)
                          }
                        })
              }
            },
            modifier =
                Modifier.shadow(
                        elevation = 2.dp,
                        spotColor = Color(0x1A000000),
                        ambientColor = Color(0x1A000000))
                    .border(width = 1.dp, color = Color(0xFFEADDFF))
                    .width(412.dp)
                    .height(48.dp)
                    .background(color = Color(0xFFFFFFFF)),
        )
      },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = navigationActions.currentRoute())
      },
      content = { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
          // FAVORITES
          TitleWithArrow("FAVORITES") {}

          LazyRow(
              horizontalArrangement = Arrangement.spacedBy(16.dp),
              modifier = Modifier.fillMaxWidth().height(115.dp)) {
                items(1) { FavoriteItem() }
              }

          // PLAYLISTS
          TitleWithArrow("PLAYLISTS") {}

          LazyColumn(
              verticalArrangement = Arrangement.spacedBy(16.dp),
              modifier = Modifier.fillMaxWidth()) {
                items(1) { PlaylistItem() }
              }
        }
      })
}

@Composable
fun FavoriteItem() {
  Card(modifier = Modifier.testTag("favoriteItem")) { Text("one fav song") }
}

@Composable
fun PlaylistItem() {
  Card(modifier = Modifier.testTag("playlistItem")) { Text("playlist 1") }
}

@Composable
fun TitleWithArrow(title: String, onClick: () -> Unit) {
  Row(
      modifier =
          Modifier.padding(top = 17.dp).testTag(title + "TitleWithArrow").clickable { onClick() },
      verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = title,
            style = TypographyBeatLink.headlineLarge,
            modifier =
                Modifier.graphicsLayer(alpha = 0.99f).drawWithCache {
                  onDrawWithContent {
                    drawContent()
                    drawRect(PrimaryGradientBrush, blendMode = BlendMode.SrcAtop)
                  }
                })
        Spacer(modifier = Modifier.width(6.dp)) // Spacing between text and arrow
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
            contentDescription = "arrow right",
            modifier =
                Modifier.size(24.dp).graphicsLayer(alpha = 0.99f).drawWithCache {
                  onDrawWithContent {
                    drawContent()
                    drawRect(PrimaryGradientBrush, blendMode = BlendMode.SrcAtop)
                  }
                })
      }
}
