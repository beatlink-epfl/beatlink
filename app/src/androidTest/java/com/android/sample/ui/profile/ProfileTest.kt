package com.android.sample.ui.profile

import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.model.profile.ProfileData
import com.android.sample.model.profile.ProfileViewModel
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.google.firebase.FirebaseApp
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@RunWith(AndroidJUnit4::class)
class ProfileTest {
  private lateinit var navigationActions: NavigationActions

  private val user =
      ProfileData(username = "", name = null, bio = null, links = 0, profilePicture = null)

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    `when`(navigationActions.currentRoute()).thenReturn(Route.PROFILE)

    // Initialize Firebase if necessary
    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }
    // Launch the composable under test
    composeTestRule.setContent {
      ProfileScreen(viewModel(factory = ProfileViewModel.Factory), navigationActions)
    }
  }

  @Test
  fun elementsAreDisplayed() {
    // Check if title is displayed
    /*composeTestRule
    .onNodeWithTag("titleUsername")
    .assertIsDisplayed()
    .assertTextContains(user.username)*/

    // Check if the icons are displayed
    composeTestRule
        .onNodeWithTag("profileScreenNotificationsButton")
        .assertExists()
        .assertContentDescriptionEquals("Notifications")
    composeTestRule
        .onNodeWithTag("profileScreenSettingsButton")
        .assertExists()
        .assertContentDescriptionEquals("Settings")

    // Check if the user's profile picture is displayed
    composeTestRule
        .onNodeWithTag("profilePicture")
        .assertExists()
        .assertContentDescriptionEquals("Profile Picture")

    // Check if the user's link's count is displayed
    composeTestRule
        .onNodeWithTag("linksCount")
        .assertExists()
        .assertTextContains("${user.links} Links")

    // Check if the edit button is displayed
    composeTestRule.onNodeWithTag("editProfileButtonContainer").assertExists()
    composeTestRule
        .onNodeWithTag("editProfileButton")
        .assertExists()
        .assertTextContains("Edit Profile")

    // Check if the user's name is displayed
    composeTestRule.onNodeWithTag("name").assertExists()

    // Check if the user's bio is displayed
    composeTestRule.onNodeWithTag("bio").assertExists()
  }

  @Test
  fun buttonsAreClickable() {
    // Perform click action on the notifications button
    composeTestRule.onNodeWithTag("profileScreenNotificationsButton").performClick()

    // Perform click action on the settings button
    composeTestRule.onNodeWithTag("profileScreenSettingsButton").performClick()

    // Perform click action on the edit button
    composeTestRule.onNodeWithTag("editProfileButton").performClick()
  }
}
