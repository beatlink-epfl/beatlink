package com.epfl.beatlink.ui.profile.notifications

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Route
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NotificationsScreenTest {
  @get:Rule val composeTestRule = createComposeRule()

  // Mocks and instances
  private lateinit var navigationActions: NavigationActions

  @Before
  fun setUp() {
    navigationActions = mockk(relaxed = true)

    every { navigationActions.currentRoute() } returns Route.PROFILE

    composeTestRule.setContent { NotificationsScreen(navigationActions = navigationActions) }
  }

  @Test
  fun everythingIsDisplayed() {
    composeTestRule.onNodeWithTag("notificationsScreenTitle").assertExists()
    composeTestRule.onNodeWithText("Notifications").assertExists()
    composeTestRule.onNodeWithTag("linkRequestsButton")
  }
}
