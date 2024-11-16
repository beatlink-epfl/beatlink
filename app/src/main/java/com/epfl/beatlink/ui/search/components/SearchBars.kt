package com.epfl.beatlink.ui.search.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.epfl.beatlink.R
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.ui.theme.lightThemeBackground

@Composable
fun FullSearchBar(navigationActions: NavigationActions) {

  Row(
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically,
      modifier =
          Modifier.testTag("nonWritableSearchBar")
              .padding(start = 8.dp, top = 6.dp, end = 8.dp, bottom = 6.dp)
              .background(color = lightThemeBackground)) {
        Box(
            modifier =
                Modifier.testTag("nonWritableSearchBarBox")
                    .fillMaxWidth()
                    .height(36.dp)
                    .shadow(
                        elevation = 4.dp,
                        spotColor = Color(0x26000000),
                        ambientColor = Color(0x26000000))
                    .background(color = Color(0xFFF2F2F2), shape = RoundedCornerShape(size = 5.dp))
                    .border(
                        width = 1.dp,
                        color = Color.Transparent,
                        shape = RoundedCornerShape(size = 5.dp))
                    .clickable {
                      navigationActions.navigateTo(Screen.SEARCH_BAR)
                    } // Handle click here
            ) {
              Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.Start,
                  modifier = Modifier.testTag("nonWritableSearchBarBoxRow")) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon",
                        tint = Color(0xFF6F6F6F),
                        modifier =
                            Modifier.testTag("nonWritableSearchBarIcon")
                                .padding(start = 5.dp, top = 4.dp, bottom = 4.dp))
                    Text(
                        text = "Search songs, live music parties, people, ... ",
                        style =
                            TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight(400),
                                color = Color(0xFF6F6F6F),
                            ),
                        modifier =
                            Modifier.testTag("nonWritableSearchBarText")
                                .padding(start = 8.dp, top = 4.dp, bottom = 4.dp))
                  }
            }
      }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShortSearchBar() {
  val searchQuery = remember { mutableStateOf(TextFieldValue("")) }
  val focusRequester = remember { FocusRequester() }

  // Automatically request focus when the composable is first loaded
  LaunchedEffect(Unit) { focusRequester.requestFocus() }

  OutlinedTextField(
      value = searchQuery.value,
      onValueChange = { searchQuery.value = it },
      placeholder = {
        Text(
            text = "",
            style =
                TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF6F6F6F),
                    letterSpacing = 0.16.sp,
                ),
            modifier = Modifier.testTag("writableSearchBarText").padding(top = 4.dp, bottom = 4.dp))
      },
      singleLine = true,
      leadingIcon = {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search Icon",
            tint = Color(0xFF6F6F6F),
            modifier =
                Modifier.testTag("writableSearchBarIcon")
                    .size(28.dp)
                    .padding(start = 5.dp, top = 4.dp, bottom = 4.dp))
      },
      colors =
          OutlinedTextFieldDefaults.colors(
              focusedBorderColor = Color.Transparent, // Color when selected (focused)
              unfocusedBorderColor = Color.Transparent, // Color when not selected
              cursorColor = Color.Black),
      shape = RoundedCornerShape(5.dp),
      modifier =
          Modifier.testTag("writableSearchBar")
              .focusRequester(focusRequester)
              .shadow(
                  elevation = 4.dp, spotColor = Color(0x26000000), ambientColor = Color(0x26000000))
              .border(width = 1.dp, color = Color.White)
              .width(334.dp)
              .height(48.dp)
              .border(
                  width = 1.dp, color = Color.Transparent, shape = RoundedCornerShape(size = 5.dp))
              .background(color = Color(0xFFF2F2F2), shape = RoundedCornerShape(size = 5.dp)))
}

@Composable
fun ShortSearchBarLayout(navigationActions: NavigationActions) {
  Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier =
          Modifier.testTag("shortSearchBarRow")
              .fillMaxWidth()
              .padding(start = 21.dp, end = 8.dp, top = 6.dp, bottom = 6.dp)) {
        // Back Icon
        Icon(
            painter = painterResource(id = R.drawable.back_arrow),
            contentDescription = "Back Icon",
            tint = Color.Unspecified,
            modifier =
                Modifier.testTag("backButton").size(24.dp).clickable {
                  // Handle back navigation here
                  navigationActions.goBack()
                })

        Spacer(modifier = Modifier.width(21.dp)) // Space between back icon and search bar

        // Short Search Bar
        ShortSearchBar()
      }
}
