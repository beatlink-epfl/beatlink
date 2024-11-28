package com.epfl.beatlink.ui.library

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.viewmodel.compose.viewModel
import com.epfl.beatlink.model.library.PlaylistRepository
import com.epfl.beatlink.model.profile.ProfileData
import com.epfl.beatlink.repository.profile.ProfileRepositoryFirestore
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.ui.navigation.TopLevelDestinations
import com.epfl.beatlink.viewmodel.library.PlaylistViewModel
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class InviteCollaboratorsScreenTest {
  private lateinit var playlistRepository: PlaylistRepository
  private lateinit var playlistViewModel: PlaylistViewModel
  private lateinit var profileRepository: ProfileRepositoryFirestore
  private lateinit var profileViewModel: ProfileViewModel
  private lateinit var navigationActions: NavigationActions

  // Test variables
  private val testName = "John Doe"
  private val testDescription = "This is a test description."

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
    profileRepository = mock(ProfileRepositoryFirestore::class.java)

    // Initialize ProfileViewModel with an initial profile state
    profileViewModel =
        ProfileViewModel(
            repository = profileRepository,
            initialProfile =
                ProfileData(
                    bio = testDescription,
                    links = 5,
                    name = testName,
                    profilePicture = null,
                    username = "johndoe"))

    navigationActions = mock(NavigationActions::class.java)

    `when`(navigationActions.currentRoute()).thenReturn(Screen.INVITE_COLLABORATORS)
    composeTestRule.setContent {
      InviteCollaboratorsScreen(
          navigationActions, viewModel(factory = ProfileViewModel.Factory), playlistViewModel)
    }
  }

  @Test
  fun inviteCollaboratorsScreen_initialRender_displaysComponents() {
    composeTestRule.onNodeWithTag("inviteCollaboratorsScreen").assertIsDisplayed()
    // Verify the ShortSearchBarLayout is displayed
    composeTestRule.onNodeWithTag("shortSearchBarRow").assertExists()
    // Verify the BottomNavigationMenu is displayed
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertExists()
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
  fun testNavigation() {
    composeTestRule.onNodeWithTag("Home").performClick()
    org.mockito.kotlin.verify(navigationActions).navigateTo(destination = TopLevelDestinations.HOME)
    composeTestRule.onNodeWithTag("Search").performClick()
    org.mockito.kotlin
        .verify(navigationActions)
        .navigateTo(destination = TopLevelDestinations.SEARCH)
    composeTestRule.onNodeWithTag("Library").performClick()
    org.mockito.kotlin
        .verify(navigationActions)
        .navigateTo(destination = TopLevelDestinations.LIBRARY)
    composeTestRule.onNodeWithTag("Profile").performClick()
    org.mockito.kotlin
        .verify(navigationActions)
        .navigateTo(destination = TopLevelDestinations.PROFILE)
  }
}
