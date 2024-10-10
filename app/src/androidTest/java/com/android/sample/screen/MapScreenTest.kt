package com.android.sample.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.android.sample.model.map.MapViewModel
import com.android.sample.ui.map.MapScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MapScreenTest {

  private lateinit var mapViewModel: MapViewModel

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    mapViewModel = MapViewModel()
    composeTestRule.setContent { MapScreen(mapViewModel) }
  }

  @Test
  fun hasRequiredComponents() {
    composeTestRule.onNodeWithTag("mapScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("bottomNavigationBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("mapContainer").assertIsDisplayed()
    composeTestRule.onNodeWithTag("playerContainer").assertIsDisplayed()
    composeTestRule.onNodeWithTag("currentLocationButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("GoogleMap").assertIsDisplayed()
    composeTestRule.onNodeWithTag("playerText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("currentLocationFab").assertIsDisplayed()
    composeTestRule.onNodeWithTag("currentLocationIcon").assertIsDisplayed()
  }
}
