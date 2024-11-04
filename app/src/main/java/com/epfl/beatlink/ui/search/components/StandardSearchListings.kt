package com.epfl.beatlink.ui.search.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.epfl.beatlink.ui.navigation.NavigationActions

@Composable
fun StandardLazyColumn(title: String, list: List<String>) {
  GradientTitle(title, false) {}

  // Display recent searches based on the selected category
  LazyColumn(
      modifier =
          Modifier.testTag("lazyColumn")
              .fillMaxSize()
              .padding(start = 18.dp, end = 18.dp, top = 11.dp)) {
        items(list.size) { index ->
          Text(
              text = list[index],
              fontSize = 20.sp,
              modifier =
                  Modifier.testTag("recentSearch_${list[index]}")
                      .fillMaxWidth()
                      .height(67.dp)
                      .padding(bottom = 11.dp),
              color = Color.Black)
        }
      }
}

@Composable
fun <T> StandardLazyRow(
    title: String,
    listOfItems: List<T>,
    itemContent: @Composable (T) -> Unit,
    horizontalSpace: Int,
    navigationActions: NavigationActions,
    screen: String
) {
  GradientTitle(title, true) { navigationActions.navigateTo(screen) }

  LazyRow(
      horizontalArrangement = Arrangement.spacedBy(horizontalSpace.dp),
      modifier =
          Modifier.testTag(title + "LazyColumn")
              .fillMaxWidth()
              .height(115.dp)
              .padding(horizontal = 21.dp)
              .padding(top = 15.dp, bottom = 15.dp)) {
        items(listOfItems.size) { index -> itemContent(listOfItems[index]) }
      }
}
