package com.epfl.beatlink.ui.library

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class ViewDescriptionOverlayTest {
  private val description = "This is a test description."
  private lateinit var mockOnDismissRequest: () -> Unit

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    mockOnDismissRequest = mock()

    composeTestRule.setContent { ViewDescriptionOverlay(mockOnDismissRequest, description) }
  }

  @Test
  fun everythingIsDisplayed() {
    composeTestRule.onNodeWithTag("overlay").assertIsDisplayed()
    composeTestRule.onNodeWithTag("description").assertIsDisplayed()
    composeTestRule.onNodeWithTag("closeButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("descriptionTitle").assertIsDisplayed()
  }

  @Test
  fun closeButtonInvokesOnDismissRequest() {
    // Find the CloseButton using its testTag and perform a click
    composeTestRule.onNodeWithTag("closeButton").performClick()
    // Verify that onDismissRequest was called
    verify(mockOnDismissRequest).invoke()
  }
}
