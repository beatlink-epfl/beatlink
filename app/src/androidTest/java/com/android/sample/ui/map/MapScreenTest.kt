package com.android.sample.ui.map

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import com.android.sample.model.map.MapViewModel
import com.android.sample.ui.navigation.NavigationActions
import org.junit.Rule
import org.junit.Test

class MapScreenTest {

  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  @Test
  fun mapScreen_displaysLoadingMapText_whenMapIsNotLoaded() {
    // Mock ViewModel with default states
    val fakeMapLocationRepository = FakeMapLocationRepository()
    val mapViewModel =
        MapViewModel(fakeMapLocationRepository).apply {
          isMapLoaded.value = false
          permissionRequired.value = false
        }

    composeTestRule.setContent {
      MapScreen(
          navigationActions = NavigationActions(rememberNavController()),
          mapViewModel = mapViewModel)
    }

    composeTestRule.onNodeWithTag("MapScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("MapContainer").assertIsDisplayed()
    composeTestRule.onNodeWithTag("playerContainer").assertIsDisplayed()
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()

    // Verify that "Loading map..." text is displayed when the map is not loaded
    composeTestRule.onNodeWithText("Loading map...").assertIsDisplayed()
  }

  @Test
  fun mapScreen_displaysMap_whenMapIsLoaded() {
    // Mock ViewModel with map loaded
    val mapViewModel =
        MapViewModel(FakeMapLocationRepository()).apply {
          isMapLoaded.value = true // Simulate that the map is loaded
        }

    composeTestRule.setContent {
      MapScreen(
          navigationActions = NavigationActions(rememberNavController()),
          mapViewModel = mapViewModel)
    }

    composeTestRule.onNodeWithTag("MapScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("MapContainer").assertIsDisplayed()
    composeTestRule.onNodeWithTag("playerContainer").assertIsDisplayed()
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()

    // Verify that the map is displayed when loaded
    composeTestRule.onNodeWithTag("Map").assertIsDisplayed()
    composeTestRule.onNodeWithTag("currentLocationFab").assertIsDisplayed()
  }
}
