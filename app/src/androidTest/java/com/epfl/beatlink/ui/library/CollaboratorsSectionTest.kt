package com.epfl.beatlink.ui.library

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.epfl.beatlink.model.profile.ProfileData
import com.epfl.beatlink.ui.components.library.CollabList
import com.epfl.beatlink.ui.components.library.CollaboratorCard
import com.epfl.beatlink.ui.profile.FakeProfileViewModel
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CollaboratorsSectionTest {

  @get:Rule val composeTestRule = createComposeRule()

  private val profileData =
      ProfileData(bio = "this is a bio", links = 0, name = "Alice", username = "alice123")

  @Before fun setUp() {}

  @Test
  fun collabList_showsEmptyState_whenNoCollaborators() {
    composeTestRule.setContent { CollabList(emptyList(), emptyList(), onRemove = {}) }

    // Verify the empty state message is displayed
    composeTestRule.onNodeWithTag("emptyCollab").assertIsDisplayed()
    composeTestRule.onNodeWithText("NO COLLABORATORS").assertExists()
  }

  @Test
  fun collabList_displaysCollaborators() {
    val collaboratorsUsernames = listOf("alice", "bob", "charlie")
    val collaboratorsProfileData =
        listOf(
            ProfileData(name = "Alice", username = "alice"),
            ProfileData(name = "Bob", username = "bob"),
            ProfileData(name = "Charlie", username = "charlie"))

    composeTestRule.setContent {
      CollabList(collaboratorsUsernames, collaboratorsProfileData, onRemove = {})
    }

    // Verify each collaborator is displayed
    collaboratorsProfileData.forEach { profile ->
      composeTestRule.onNodeWithText("${profile.name} @${profile.username}").assertExists()
    }
  }

  @Test
  fun collabList_triggersOnRemove_whenCloseButtonClicked() {
    val collaborators = listOf("alice")
    val collaboratorsProfileData = listOf(ProfileData(name = "Alice", username = "alice"))
    var removedCollaborator: String? = null

    composeTestRule.setContent {
      CollabList(collaborators, collaboratorsProfileData, onRemove = { removedCollaborator = it })
    }

    // Click the remove button for Alice
    composeTestRule
        .onNodeWithTag("collabCard")
        .onChildAt(1) // Assuming the close button is the second child
        .performClick()

    // Verify the correct collaborator was removed
    assertEquals("alice", removedCollaborator)
  }

  @Test
  fun collaboratorCardHasCheckWhenUserIsCollaborator() {
    val fakeProfileViewModel = FakeProfileViewModel()
    val isCollab = true
    composeTestRule.setContent {
      CollaboratorCard(profileData, fakeProfileViewModel, isCollab, {}, {})
    }
    composeTestRule.onNodeWithTag("checkButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("checkButton").performClick()
    composeTestRule.onNodeWithTag("profilePic").assertIsDisplayed()
    composeTestRule.onNodeWithText("Alice").assertExists()
    composeTestRule.onNodeWithText("@ALICE123").assertExists()
  }
}
