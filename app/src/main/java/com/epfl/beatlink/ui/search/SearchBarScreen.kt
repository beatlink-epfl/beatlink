package com.epfl.beatlink.ui.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.epfl.beatlink.R
import com.epfl.beatlink.ui.navigation.BottomNavigationMenu
import com.epfl.beatlink.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.search.components.ShortSearchBarLayout
import com.epfl.beatlink.ui.search.components.StandardLazyColumn
import com.epfl.beatlink.ui.theme.PrimaryPurple
import com.epfl.beatlink.ui.theme.PrimaryRed
import com.epfl.beatlink.ui.theme.lightThemeBackground

@Composable
fun SearchBarScreen(navigationActions: NavigationActions) {
  val selectedCategory = remember { mutableStateOf("Songs") }
  val recentSearches = remember { mutableStateOf(listOf("Song 1")) }

  Scaffold(
      topBar = { ShortSearchBarLayout(navigationActions) },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = navigationActions.currentRoute())
      },
      modifier = Modifier.testTag("searchBarScreen")) { paddingValues ->
        Column(
            modifier =
                Modifier.testTag("recentSearchesColumn")
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(color = lightThemeBackground)) {
              HorizontalDivider(
                  color = Color.LightGray, thickness = 1.dp, modifier = Modifier.testTag("divider"))

              Spacer(modifier = Modifier.testTag("spacer").height(8.dp))

              Row(
                  horizontalArrangement = Arrangement.SpaceEvenly,
                  verticalAlignment = Alignment.CenterVertically,
                  modifier =
                      Modifier.testTag("categoryRow")
                          .fillMaxWidth()
                          .padding(horizontal = 12.dp, vertical = 8.dp)) {
                    CategoryButton("Songs", selectedCategory.value, PrimaryRed) {
                      selectedCategory.value = "Songs"
                      recentSearches.value = listOf("Song A", "Song B", "Song C")
                    }
                    CategoryButton(
                        "Events", selectedCategory.value, Color(0xFFFFA500)) { // Orange color
                          selectedCategory.value = "Events"
                          recentSearches.value = listOf("Event A", "Event B", "Event C")
                        }
                    CategoryButton(
                        "People", selectedCategory.value, PrimaryPurple) { // Purple color
                          selectedCategory.value = "People"
                          recentSearches.value = listOf("Person A", "Person B", "Person C")
                        }
                  }

              Spacer(modifier = Modifier.testTag("spacer").height(22.dp))

              StandardLazyColumn(title = "RECENT SEARCHES", list = recentSearches.value)
            }
      }
}

@Composable
fun CategoryButton(
    category: String,
    selectedCategory: String,
    categoryColor: Color,
    onClick: () -> Unit
) {
  val isSelected = selectedCategory == category
  val backgroundColor = if (isSelected) categoryColor else Color.Transparent
  val contentColor = if (isSelected) Color.White else categoryColor

  Button(
      onClick = onClick,
      colors =
          ButtonDefaults.buttonColors(
              containerColor = backgroundColor, contentColor = contentColor),
      shape = RoundedCornerShape(20.dp),
      border = BorderStroke(2.dp, categoryColor),
      modifier =
          Modifier.testTag(category + "categoryButton").height(36.dp).padding(horizontal = 4.dp)) {
        Text(
            text = category,
            style =
                TextStyle(
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontFamily = FontFamily(Font(R.font.roboto)),
                    fontWeight = FontWeight(500),
                    letterSpacing = 0.14.sp,
                ),
            modifier = Modifier.testTag(category + "categoryText"))
      }
}
