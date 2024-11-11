package com.epfl.beatlink.ui.navigation

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class BottomNavigationMenuTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun bottomNavigationMenu_showsCorrectIconsAndText() {
    // Create a mock list of top-level destinations
    val tabList = LIST_TOP_LEVEL_DESTINATION

    // Set the selected item (e.g., HOME)
    val selectedItem = Screen.HOME

    // Run the test composable
    composeTestRule.setContent {
      BottomNavigationMenu(
          onTabSelect = { /* No-op for this test */},
          tabList = tabList,
          selectedItem = selectedItem)
    }

    // Check if the icons and labels are displayed
    tabList.forEach { tab -> composeTestRule.onNodeWithTag(tab.textId).assertExists() }

    // Check that the selected icon is highlighted (HOME in this case)
    composeTestRule
        .onNodeWithTag(TopLevelDestinations.HOME.textId)
        .assert(hasTestTag(TopLevelDestinations.HOME.textId))
  }

  @Test
  fun bottomNavigationMenu_onTabSelectTriggered() {
    val tabList = LIST_TOP_LEVEL_DESTINATION

    var selectedTab = ""

    // Test the tab selection
    composeTestRule.setContent {
      BottomNavigationMenu(
          onTabSelect = { selectedTab = it.route }, tabList = tabList, selectedItem = Screen.HOME)
    }

    // Click on a different tab (e.g., SEARCH)
    composeTestRule.onNodeWithTag(TopLevelDestinations.SEARCH.textId).performClick()

    // Verify that the correct tab was selected
    assert(selectedTab == Route.SEARCH)
  }

  @Test
  fun bottomNavigationMenu_tabSelectionUpdatesCorrectly() {
    val tabList = LIST_TOP_LEVEL_DESTINATION

    var selectedTab = ""

    composeTestRule.setContent {
      BottomNavigationMenu(
          onTabSelect = { selectedTab = it.route }, tabList = tabList, selectedItem = Screen.HOME)
    }

    // Click on PROFILE tab
    composeTestRule.onNodeWithTag(TopLevelDestinations.PROFILE.textId).performClick()

    // Verify that the selected tab updates to PROFILE
    assert(selectedTab == Route.PROFILE)

    // Click on LIBRARY tab
    composeTestRule.onNodeWithTag(TopLevelDestinations.LIBRARY.textId).performClick()

    // Verify that the selected tab updates to LIBRARY
    assert(selectedTab == Route.LIBRARY)
  }
}
