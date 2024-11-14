package com.epfl.beatlink.ui.profile

import android.content.Context
import android.widget.Toast
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.epfl.beatlink.model.profile.ProfileData
import com.epfl.beatlink.model.profile.ProfileRepositoryFirestore
import com.epfl.beatlink.model.profile.ProfileViewModel
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Route
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EditProfileScreenTest {
  @get:Rule val composeTestRule = createComposeRule()

  // Test variables
  private val testName = "John Doe"
  private val testDescription = "This is a test description."

  // Mocks and instances
  private lateinit var profileViewModel: ProfileViewModel
  private lateinit var navigationActions: NavigationActions
  private lateinit var mockRepository: ProfileRepositoryFirestore
  private lateinit var context: Context

  @Before
  fun setUp() {
    navigationActions = mockk(relaxed = true)
    mockRepository = mockk(relaxed = true)
    context = ApplicationProvider.getApplicationContext()

    // Initialize ProfileViewModel with an initial profile state
    profileViewModel =
        ProfileViewModel(
            repository = mockRepository,
            initialProfile =
                ProfileData(
                    bio = testDescription,
                    links = 5,
                    name = testName,
                    profilePicture = null,
                    username = "johndoe"))

    every { navigationActions.currentRoute() } returns Route.PROFILE

    composeTestRule.setContent {
      EditProfileScreen(profileViewModel = profileViewModel, navigationActions = navigationActions)
    }
  }

  @Test
  fun editProfileScreen_rendersCorrectly() {

    // Check if the title is displayed
    composeTestRule
        .onNodeWithTag("editProfileTitle")
        .assertIsDisplayed()
        .assertTextContains("Edit profile")

    // Check if the Back Button exists
    composeTestRule.onNodeWithTag("goBackButton").assertIsDisplayed()

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

  @Test
  fun editProfileScreen_saveButtonTriggersProfileUpdate() {
    composeTestRule.onNodeWithTag("saveProfileButton").performClick()

    verify {
      profileViewModel.updateProfile(
          ProfileData(
              bio = testDescription,
              links = 5,
              name = testName,
              profilePicture = null,
              username = "johndoe"))
    }
  }

  @Test
  fun testProfileUpdate_successful() {
    // Mock Toast.makeText and Toast.show()
    mockkStatic(Toast::class)
    val mockToast = mockk<Toast>(relaxed = true)
    every { Toast.makeText(any(), eq("Profile updated"), eq(Toast.LENGTH_SHORT)) } returns mockToast
    every { mockToast.show() } just Runs

    // Update fields with valid values
    composeTestRule.onNodeWithTag("editProfileNameInput").performTextInput("New Test User")
    composeTestRule.onNodeWithTag("editProfileDescriptionInput").performTextInput("Updated bio")

    // Trigger the Save button
    composeTestRule.onNodeWithTag("saveProfileButton").performClick()

    // Ensure Compose is idle
    composeTestRule.waitForIdle()
    Thread.sleep(100)

    // Verify that updateProfile is called with the new data
    verify {
      profileViewModel.updateProfile(
          ProfileData(
              bio = "Updated bio",
              links = 5,
              name = "New Test User",
              profilePicture = null,
              username = "johndoe"))
    }

    // Verify that Toast.makeText() was called with the "Profile updated" message
    verify { Toast.makeText(any(), eq("Profile updated"), eq(Toast.LENGTH_SHORT)) }

    // Verify that show() was called on the Toast instance
    verify { mockToast.show() }

    // Wait for any asynchronous operations to complete
    composeTestRule.waitForIdle()

    // Verify that navigation to the previous screen happens
    verify(timeout = 500) { navigationActions.goBack() }
  }
}
