package com.epfl.beatlink.ui.profile.settings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.epfl.beatlink.model.profile.ProfileData
import com.epfl.beatlink.repository.profile.ProfileRepositoryFirestore
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Route
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChangeUsernameTest {

  @get:Rule val composeTestRule = createComposeRule()

  private val testUsername = "johndoe"

  private lateinit var navigationActions: NavigationActions
  private lateinit var profileViewModel: ProfileViewModel
  private lateinit var mockRepository: ProfileRepositoryFirestore

  @Before
  fun setUp() {
    navigationActions = mockk(relaxed = true)
    mockRepository = mockk(relaxed = true)

    profileViewModel =
        ProfileViewModel(
            repository = mockRepository,
            initialProfile =
                ProfileData(
                    bio = "Test Bio",
                    links = 5,
                    name = "Test Name",
                    profilePicture = null,
                    username = testUsername))
    every { navigationActions.currentRoute() } returns Route.PROFILE

    composeTestRule.setContent { ChangeUsername(navigationActions, profileViewModel) }
  }

  @Test
  fun changeUsername_rendersCorrectly() {
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
            username = newUsername)

    // Verify initial username is displayed
    composeTestRule.onNodeWithTag("changeUsernameInput").assertIsDisplayed()

    // Update the username
    composeTestRule.onNodeWithTag("changeUsernameInput").performTextClearance()
    composeTestRule.onNodeWithTag("changeUsernameInput").performTextInput(newUsername)

    // Perform Save button click
    composeTestRule.onNodeWithTag("saveButton").performClick()

    // Verify updateProfile method is called with new data
    verify { profileViewModel.updateProfile(newProfileData) }
  }
}
