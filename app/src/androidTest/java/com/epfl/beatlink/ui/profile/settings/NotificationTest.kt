package com.epfl.beatlink.ui.profile.settings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotificationScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions

  @Before
  fun setUp() {
    navigationActions = mockk(relaxed = true)
    every { navigationActions.currentRoute() } returns Screen.NOTIFICATIONS
  }

  @Test
  fun notificationScreen_rendersCorrectly() {
    composeTestRule.setContent { NotificationScreen(navigationActions = navigationActions) }

    // Check if the title is displayed
    composeTestRule.onNodeWithTag("notificationScreenTitle").assertIsDisplayed()

    // Check if the content is displayed
    composeTestRule.onNodeWithTag("notificationScreenContent").assertIsDisplayed()

    // Check if the "Allow Notifications" text and switch are displayed
    composeTestRule.onNodeWithTag("allowNotificationSwitch").assertIsDisplayed()
    composeTestRule.onNodeWithTag("allowNotificationSwitch").performClick()
  }
}
