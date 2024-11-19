package com.epfl.beatlink.ui.profile.settings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.epfl.beatlink.repository.profile.ProfileData
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChangeUsernameTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions
  private lateinit var profileViewModel: ProfileViewModel

  @Before
  fun setUp() {
    navigationActions = mockk(relaxed = true)
    profileViewModel = mockk(relaxed = true)

    every { navigationActions.currentRoute() } returns Screen.CHANGE_USERNAME
    every { profileViewModel.profile } returns
        MutableStateFlow(
            ProfileData(
                bio = "Test Bio",
                links = 5,
                name = "Test Name",
                profilePicture = null,
                username = "currentUsername",
                favoriteMusicGenres = listOf("Pop", "Rock")))
  }

  @Test
  fun changeUsername_rendersCorrectly() {
    composeTestRule.setContent { ChangeUsername(navigationActions, profileViewModel) }

    // Verify the top bar
    composeTestRule.onNodeWithTag("changeUsernameScreenTitle").assertIsDisplayed()

    // Verify the content
    composeTestRule.onNodeWithTag("changeUsernameScreenContent").assertIsDisplayed()
  }

  @Test
  fun changeUsername_updatesUsernameSuccessfully() {
    val newUsername = "newUsername"
    val newProfileData =
        ProfileData(
            bio = "Test Bio",
            links = 5,
            name = "Test Name",
            profilePicture = null,
            username = newUsername,
            favoriteMusicGenres = listOf("Pop", "Rock"))

    composeTestRule.setContent { ChangeUsername(navigationActions, profileViewModel) }

    // Verify initial username is displayed
    composeTestRule.onNodeWithText("currentUsername").assertIsDisplayed()

    // Update the username
    composeTestRule.onNodeWithTag("changeUsernameInput").performTextClearance()
    composeTestRule.onNodeWithTag("changeUsernameInput").performTextInput(newUsername)

    // Perform Save button click
    composeTestRule.onNodeWithTag("saveButton").performClick()

    // Verify updateProfile method is called with new data
    verify { profileViewModel.updateProfile(newProfileData) }
  }
}
