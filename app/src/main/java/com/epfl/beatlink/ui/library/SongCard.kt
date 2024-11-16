package com.epfl.beatlink.ui.library

import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

@Composable
fun SongCard() {
  Card(modifier = Modifier.testTag("favoriteItem")) { Text("one fav song") }
}
