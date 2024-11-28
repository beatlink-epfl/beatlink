package com.epfl.beatlink.ui.profile.settings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.epfl.beatlink.model.auth.FirebaseAuthRepository
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.viewmodel.auth.FirebaseAuthViewModel
import com.epfl.beatlink.viewmodel.map.user.MapUsersViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
class SettingsScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions
  private lateinit var authRepository: FirebaseAuthRepository
  private lateinit var authViewModel: FirebaseAuthViewModel

  @Before
  fun setUp() {
    navigationActions = mockk(relaxed = true)
    every { navigationActions.currentRoute() } returns Screen.SETTINGS

    authRepository = mock(FirebaseAuthRepository::class.java)
    authViewModel = FirebaseAuthViewModel(authRepository)
  }

  @Test
  fun settingsScreen_rendersCorrectly() {
    composeTestRule.setContent {
      SettingsScreen(
          navigationActions = navigationActions,
          authViewModel,
          mapUsersViewModel = viewModel(factory = MapUsersViewModel.Factory))
    }

    // Check if the title is displayed
    composeTestRule.onNodeWithTag("settingScreenTitle").assertIsDisplayed()

    // Check if buttons are displayed
    composeTestRule.onNodeWithTag("settingScreenContent").assertIsDisplayed()
  }

  @Test
  fun settingsScreen_buttonsNavigateCorrectly() {
    composeTestRule.setContent {
      SettingsScreen(
          navigationActions = navigationActions,
          authViewModel,
          mapUsersViewModel = viewModel(factory = MapUsersViewModel.Factory))
    }

    // Test "Account Settings" button
    composeTestRule.onNodeWithTag("accountSettingButton").performClick()
    verify { navigationActions.navigateTo(Screen.ACCOUNT) }

    // Test "Notification Settings" button
    composeTestRule.onNodeWithTag("notificationSettingsButton").performClick()
    verify { navigationActions.navigateTo(Screen.NOTIFICATIONS) }

    // Test "Invite Friends" button
    composeTestRule.onNodeWithTag("inviteFriendsButton").performClick()

    // Test "Rate BeatLink" button
    composeTestRule.onNodeWithTag("rateBeatLinkButton").performClick()
  }
}
