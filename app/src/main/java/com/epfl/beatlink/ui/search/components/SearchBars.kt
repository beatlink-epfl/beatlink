package com.epfl.beatlink.ui.search.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.ui.components.BackArrowButton
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.theme.BorderColor
import com.epfl.beatlink.ui.theme.LightGray
import com.epfl.beatlink.ui.theme.ShadowColor

@Composable
fun ShortSearchBarLayout(
    backArrowButton: Boolean,
    navigationActions: NavigationActions,
    searchQuery: TextFieldValue = TextFieldValue(""),
    onQueryChange: (TextFieldValue) -> Unit = {},
    placeholder: String = "Search songs, artists or people"
) {
  Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier =
          Modifier.testTag("shortSearchBarRow")
              .fillMaxWidth()
              .height(60.dp)
              .drawWithCache {
                onDrawWithContent {
                  drawContent()
                  drawLine(
                      color = BorderColor,
                      strokeWidth = 1.dp.toPx(),
                      start = Offset(0f, size.height), // Bottom left
                      end = Offset(size.width, size.height) // Bottom right
                      )
                }
              }
              .shadow(elevation = 2.dp, spotColor = ShadowColor, ambientColor = ShadowColor)) {
        if (backArrowButton) {
          BackArrowButton { navigationActions.goBack() }
          Spacer(Modifier.width(12.dp))
        }

        // Search Bar
        ShortSearchBar(searchQuery = searchQuery, onQueryChange = onQueryChange, placeholder)
      }
}

@Composable
fun ShortSearchBar(
    searchQuery: TextFieldValue,
    onQueryChange: (TextFieldValue) -> Unit,
    placeholder: String = "Search songs, artists or people"
) {
  val focusRequester = remember { FocusRequester() }

  OutlinedTextField(
      value = searchQuery,
      onValueChange = onQueryChange,
      singleLine = true,
      textStyle = MaterialTheme.typography.bodyLarge,
      leadingIcon = {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search Icon",
            tint = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.testTag("writableSearchBarIcon").size(28.dp).padding(start = 5.dp))
      },
      placeholder = {
        Text(
            text = placeholder,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.testTag("searchBarPlaceholder"))
      },
      colors =
          OutlinedTextFieldDefaults.colors(
              focusedTextColor = MaterialTheme.colorScheme.primary,
              focusedBorderColor = Color.Transparent,
              unfocusedBorderColor = Color.Transparent,
              cursorColor = MaterialTheme.colorScheme.primaryContainer),
      shape = RoundedCornerShape(5.dp),
      modifier =
          Modifier.testTag("writableSearchBar")
              .focusRequester(focusRequester)
              .padding(top = 4.dp, bottom = 4.dp, start = 8.dp, end = 8.dp)
              .shadow(elevation = 4.dp, spotColor = LightGray, ambientColor = LightGray)
              .fillMaxSize()
              .background(
                  color = MaterialTheme.colorScheme.surfaceContainer,
                  shape = RoundedCornerShape(5.dp)))
}
