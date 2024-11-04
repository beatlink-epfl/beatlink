package com.android.sample.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.sample.ui.components.ShortSearchBarLayout
import com.android.sample.ui.components.StandardLazyColumn
import com.android.sample.ui.navigation.NavigationActions

@Composable
fun TrendingSongsScreen(navigationActions: NavigationActions) {
  val trendingSongsList = remember { mutableStateOf(listOf("Song 1")) }

  Scaffold(
      topBar = { ShortSearchBarLayout(navigationActions) },
      modifier = Modifier.testTag("trendingSongsScreen")) { paddingValues ->
        Column(
            modifier =
                Modifier.testTag("trendingSongsColumn")
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(color = Color.White)) {
              HorizontalDivider(
                  color = Color.LightGray, thickness = 1.dp, modifier = Modifier.testTag("divider"))

              Spacer(modifier = Modifier.testTag("spacer").height(17.dp))

              StandardLazyColumn(title = "TRENDING SONGS", list = trendingSongsList.value)
            }
      }
}