package com.android.sample.ui.profile

import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.model.profile.ProfileData
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileTest {

  private val user =
      ProfileData(
          username = "username", name = "name", bio = "bio", links = 0, profilePicture = null)

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun elementsAreDisplayed() {
    // Launch the composable under test
    composeTestRule.setContent { ProfileScreen(user) }

    // Check if the icons are displayed
    composeTestRule.onNodeWithTag("profileScreenNotificationsButton").assertExists()
    composeTestRule.onNodeWithTag("profileScreenSettingsButton").assertExists()

    // Check if the user's profile picture is displayed
    composeTestRule.onNodeWithTag("profilePicture").assertExists()

    // Check if the user's link's count is displayed
    composeTestRule
        .onNodeWithTag("linksCount")
        .assertExists()
        .assertTextContains("${user.links} Links")

    // Check if the edit button is displayed
    composeTestRule.onNodeWithTag("editProfileButton").assertExists()

    // Check if the user's name is displayed
    composeTestRule.onNodeWithTag("name").assertExists().assertTextContains(user.name!!)

    // Check if the user's bio is displayed
    composeTestRule.onNodeWithTag("bio").assertExists().assertTextContains(user.bio!!)
  }
}
