package com.epfl.beatlink.ui.library

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.epfl.beatlink.model.library.PlaylistRepository
import com.epfl.beatlink.model.profile.ProfileData
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.ui.profile.FakeProfileViewModel
import com.epfl.beatlink.viewmodel.library.PlaylistViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class InviteCollaboratorsScreenTest {
  private lateinit var playlistRepository: PlaylistRepository
  private lateinit var playlistViewModel: PlaylistViewModel
  private lateinit var navigationActions: NavigationActions

  private val profiles =
      listOf(ProfileData(username = "username1"), ProfileData(username = "username2"))

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
    playlistRepository =
        mock(
            PlaylistRepository::class
                .java) // Use relaxed if you don't want to manually mock every behavior
    playlistViewModel = PlaylistViewModel(playlistRepository)

    val fakeProfileViewModel = FakeProfileViewModel()
    fakeProfileViewModel.setFakeProfiles(profiles)

    fakeProfileViewModel.setFakeProfiles(profiles)

    navigationActions = mock(NavigationActions::class.java)

    `when`(navigationActions.currentRoute()).thenReturn(Screen.INVITE_COLLABORATORS)
    composeTestRule.setContent {
      InviteCollaboratorsScreen(navigationActions, fakeProfileViewModel, playlistViewModel)
    }
  }

  @Test
  fun inviteCollaboratorsScreen_initialRender_displaysComponents() {
    composeTestRule.onNodeWithTag("inviteCollaboratorsScreen").assertIsDisplayed()
    // Verify the ShortSearchBarLayout is displayed
    composeTestRule.onNodeWithTag("shortSearchBarRow").assertExists()
  }

  @Test
  fun testBackNavigation() {
    composeTestRule.onNodeWithTag("goBackButton").performClick()
    org.mockito.kotlin.verify(navigationActions).goBack()
  }

  @Test
  fun testSearchBarInteraction() {
    composeTestRule.onNodeWithTag("writableSearchBar").performTextInput("John Doe")
    composeTestRule.onNodeWithTag("writableSearchBar").assertTextEquals("John Doe")
  }

  @Test
  fun searchResultsDisplayPeopleWhenSearching() {
    composeTestRule.onNodeWithTag("writableSearchBar").performClick()
    composeTestRule.onNodeWithTag("writableSearchBar").performTextInput("username")
    composeTestRule.onAllNodesWithTag("CollabCard").assertCountEquals(profiles.size)
  }
}
