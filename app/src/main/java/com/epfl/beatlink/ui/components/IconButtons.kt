package com.epfl.beatlink.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.ui.theme.IconsGradientBrush
import com.epfl.beatlink.ui.theme.primaryWhite

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

@Composable
fun EditButton(onClick: () -> Unit) {
  IconButton(onClick) {
    Box(
        modifier =
            Modifier.testTag("editButton")
                .size(28.dp)
                .background(brush = IconsGradientBrush, shape = CircleShape)
                .clickable { onClick() }) {
          Icon(
              imageVector = Icons.Filled.Edit,
              contentDescription = "Edit",
              modifier = Modifier.padding(6.dp),
              tint = MaterialTheme.colorScheme.primaryWhite)
        }
  }
}
