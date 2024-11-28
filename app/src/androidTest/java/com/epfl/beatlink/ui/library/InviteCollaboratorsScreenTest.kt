package com.epfl.beatlink.ui.library

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.epfl.beatlink.model.library.PlaylistRepository
import com.epfl.beatlink.model.profile.ProfileData
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.viewmodel.library.PlaylistViewModel
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class InviteCollaboratorsScreenTest {
  private lateinit var playlistRepository: PlaylistRepository
  private lateinit var playlistViewModel: PlaylistViewModel
  private lateinit var profileViewModel: ProfileViewModel
  private lateinit var navigationActions: NavigationActions

  val profile =
      ProfileData(
          bio = "Existing bio",
          links = 3,
          name = "John Doe",
          profilePicture = null,
          username = "TestUser")
  val testProfile =
      ProfileData(
          bio = "", links = 3, name = "Test User", profilePicture = null, username = "testuser")

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    playlistRepository = mock(PlaylistRepository::class.java)
    playlistViewModel = PlaylistViewModel(playlistRepository)
    profileViewModel = mockk(relaxed = true)

    navigationActions = mock(NavigationActions::class.java)
    `when`(navigationActions.currentRoute()).thenReturn(Screen.CREATE_NEW_PLAYLIST)
  }

  @Test
  fun inviteCollaboratorsScreen_initialRender_displaysComponents() {
    composeTestRule.setContent {
      InviteCollaboratorsScreen(navigationActions, profileViewModel, playlistViewModel)
    }
    composeTestRule.onNodeWithTag("inviteCollaboratorsScreen").assertIsDisplayed()
    // Verify the ShortSearchBarLayout is displayed
    composeTestRule.onNodeWithTag("shortSearchBarRow").assertExists()
    // Verify the BottomNavigationMenu is displayed
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertExists()
  }
}
