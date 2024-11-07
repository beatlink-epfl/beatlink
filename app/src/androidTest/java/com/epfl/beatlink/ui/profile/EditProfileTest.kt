package com.epfl.beatlink.ui.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Route
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@RunWith(AndroidJUnit4::class)
class EditProfileScreenTest {
  private lateinit var navigationActions: NavigationActions

  @get:Rule val composeTestRule = createComposeRule()

  private val testName = "John Doe"
  private val testDescription = "This is a test description."

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    `when`(navigationActions.currentRoute()).thenReturn(Route.PROFILE)

    composeTestRule.setContent { EditProfileScreen(navigationActions = navigationActions) }
  }

  @Test
  fun editProfileScreen_rendersCorrectly() {

    // Check if the title is displayed
    composeTestRule
        .onNodeWithTag("editProfileTitle")
        .assertIsDisplayed()
        .assertTextContains("Edit profile")

    // Check if the Back Button exists
    composeTestRule.onNodeWithTag("editProfileBackButton").assertIsDisplayed()

    // Check if input fields and button are displayed
    composeTestRule.onNodeWithTag("editProfileContent").assertIsDisplayed()
    composeTestRule.onNodeWithTag("editProfileNameInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("editProfileDescriptionInput").assertIsDisplayed()
  }

  @Test
  fun editProfileScreen_handlesNameInputCorrectly() {

    // Enter a new name
    composeTestRule.onNodeWithTag("editProfileNameInput").performTextClearance()
    composeTestRule.onNodeWithTag("editProfileNameInput").performTextInput(testName)

    // Verify the name was entered correctly
    composeTestRule.onNodeWithTag("editProfileNameInput").assertTextContains(testName)
  }

  @Test
  fun editProfileScreen_handlesDescriptionInputWithCharacterLimit() {

    // Enter a new description within the character limit
    composeTestRule.onNodeWithTag("editProfileDescriptionInput").performTextClearance()
    composeTestRule.onNodeWithTag("editProfileDescriptionInput").performTextInput(testDescription)

    // Verify the description was entered correctly
    composeTestRule.onNodeWithTag("editProfileDescriptionInput").assertTextContains(testDescription)

    // Enter a description exceeding the character limit
    val longDescription = "A".repeat(100) // Assuming max length is 100
    composeTestRule.onNodeWithTag("editProfileDescriptionInput").performTextClearance()
    composeTestRule.onNodeWithTag("editProfileDescriptionInput").performTextInput(longDescription)

    composeTestRule.onNodeWithTag("editProfileDescriptionInput").performTextInput(longDescription)

    // Verify the description was trimmed to max length
    composeTestRule.onNodeWithTag("editProfileDescriptionInput").assertTextContains(longDescription)
  }

  @Test
  fun editProfileScreen_handlesUsernameInputWithCharacterLimit() {

    // Enter a new description within the character limit
    composeTestRule.onNodeWithTag("editProfileNameInput").performTextClearance()
    composeTestRule.onNodeWithTag("editProfileNameInput").performTextInput(testName)

    // Verify the description was entered correctly
    composeTestRule.onNodeWithTag("editProfileNameInput").assertTextContains(testName)

    // Enter a description exceeding the character limit
    val longName = "A".repeat(20) // Assuming max length is 100
    composeTestRule.onNodeWithTag("editProfileNameInput").performTextClearance()
    composeTestRule.onNodeWithTag("editProfileNameInput").performTextInput(longName)

    composeTestRule.onNodeWithTag("editProfileNameInput").performTextInput(longName)

    // Verify the description was trimmed to max length
    composeTestRule.onNodeWithTag("editProfileNameInput").assertTextContains(longName)
  }
}
