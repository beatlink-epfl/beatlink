package com.epfl.beatlink.ui.search.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.materialIcon
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.epfl.beatlink.R
import com.epfl.beatlink.ui.components.BackArrowButton
import com.epfl.beatlink.ui.components.topAppBarModifier
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.ui.theme.BorderColor
import com.epfl.beatlink.ui.theme.LightGray
import com.epfl.beatlink.ui.theme.PrimaryGray
import com.epfl.beatlink.ui.theme.ShadowColor
import com.epfl.beatlink.ui.theme.lightThemeBackground

@Composable
fun FullSearchBar(navigationActions: NavigationActions) {

  Row(
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically,
      modifier =
          Modifier.testTag("nonWritableSearchBar")
              .background(color = MaterialTheme.colorScheme.background)
              .topAppBarModifier()) {
        Box(
            modifier =
                Modifier.testTag("nonWritableSearchBarBox")
                    .fillMaxSize()
                    .padding(horizontal = 8.dp, vertical = 6.dp)
                    .background(color = LightGray, shape = RoundedCornerShape(size = 5.dp))
                    .clickable {
                      navigationActions.navigateTo(Screen.SEARCH_BAR)
                    }
            ) {
              Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.Start,
                  modifier = Modifier.testTag("nonWritableSearchBarBoxRow")) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon",
                        tint = MaterialTheme.colorScheme.primaryContainer,
                        modifier =
                            Modifier.testTag("nonWritableSearchBarIcon")
                                .padding(start = 5.dp, top = 4.dp, bottom = 4.dp))
                    Text(
                        text = "Search songs, artists, live music parties, ... ",
                        style =
                            TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight(400),
                                color =  MaterialTheme.colorScheme.primaryContainer,
                            ),
                        modifier =
                            Modifier.testTag("nonWritableSearchBarText")
                                .padding(start = 8.dp, top = 4.dp, bottom = 4.dp))
                  }
            }
      }
}

@Composable
fun ShortSearchBarLayout(
    navigationActions: NavigationActions,
    searchQuery: TextFieldValue = TextFieldValue(""),
    onQueryChange: (TextFieldValue) -> Unit = {}
) {
  Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier =
          Modifier.testTag("shortSearchBarRow")
              .fillMaxWidth()
              .height(60.dp)
              .background(color = MaterialTheme.colorScheme.background)
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
              .shadow(elevation = 2.dp, spotColor = ShadowColor, ambientColor = ShadowColor)
              ) {
        BackArrowButton { navigationActions.goBack() }
      Spacer(Modifier.width(12.dp))
        // Search Bar
        ShortSearchBar(searchQuery = searchQuery, onQueryChange = onQueryChange)
      }
}

@Composable
fun ShortSearchBar(searchQuery: TextFieldValue, onQueryChange: (TextFieldValue) -> Unit) {
  val focusRequester = remember { FocusRequester() }

  // Request focus when the composable is first loaded
  LaunchedEffect(Unit) { focusRequester.requestFocus() }
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
                    modifier =
                    Modifier.testTag("writableSearchBarIcon")
                        .size(28.dp)
                        .padding(start = 5.dp)
                )
            },
            colors =
            OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                cursorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            shape = RoundedCornerShape(5.dp),
            modifier =
            Modifier.testTag("writableSearchBar")
                .focusRequester(focusRequester)
                .shadow(elevation = 4.dp, spotColor = LightGray, ambientColor = LightGray)
                .width(350.dp)
                .height(50.dp)
                .background(color = LightGray, shape = RoundedCornerShape(5.dp))
        )


}
