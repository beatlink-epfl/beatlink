package com.epfl.beatlink.ui.library

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.epfl.beatlink.ui.components.library.CollabList
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CollaboratorsSectionTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Before fun setUp() {}

  @Test
  fun collabList_showsEmptyState_whenNoCollaborators() {
    composeTestRule.setContent { CollabList(collaborators = emptyList(), onRemove = {}) }

    // Verify the empty state message is displayed
    composeTestRule.onNodeWithTag("emptyCollab").assertIsDisplayed()
    composeTestRule.onNodeWithText("NO COLLABORATORS").assertExists()
  }

  @Test
  fun collabList_displaysCollaborators() {
    val collaborators = listOf("Alice", "Bob", "Charlie")

    composeTestRule.setContent { CollabList(collaborators = collaborators, onRemove = {}) }

    // Verify each collaborator is displayed
    collaborators.forEach { collaborator ->
      composeTestRule.onNodeWithText("@$collaborator").assertIsDisplayed()
    }
  }

  @Test
  fun collabList_triggersOnRemove_whenCloseButtonClicked() {
    val collaborators = listOf("Alice")
    var removedCollaborator: String? = null

    composeTestRule.setContent {
      CollabList(collaborators = collaborators, onRemove = { removedCollaborator = it })
    }

    // Click the remove button for Alice
    composeTestRule
        .onNodeWithTag("collabCard")
        .onChildAt(1) // Assuming the close button is the second child
        .performClick()

    // Verify the correct collaborator was removed
    assertEquals("Alice", removedCollaborator)
  }
}
