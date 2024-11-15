package com.epfl.beatlink.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

@Composable
fun AddButton(onClick: () -> Unit) {
  CornerIcons(
      onClick = onClick,
      icon = Icons.Outlined.Add,
      contentDescription = "Add",
      modifier = Modifier.testTag("addButton"))
}

@Composable
fun SearchButton(onClick: () -> Unit) {
  CornerIcons(
      onClick = onClick,
      icon = Icons.Outlined.Search,
      contentDescription = "Search",
      modifier = Modifier.testTag("searchButton"))
}
