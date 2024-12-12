package com.epfl.beatlink.ui.offline

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.epfl.beatlink.ui.navigation.NavigationActions
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class NoInternetScreenTest {
  private lateinit var navigationActions: NavigationActions

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    `when`(navigationActions.currentRoute()).thenReturn("offline")
  }

  @Test
  fun elementsAreDisplayed() {
    composeTestRule.setContent { NoInternetScreen(navigationActions) }
    composeTestRule.onNodeWithTag("offline_screen").assertExists()
  }
}
