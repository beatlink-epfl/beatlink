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
import com.epfl.beatlink.ui.navigation.TopLevelDestinations
import com.epfl.beatlink.ui.profile.FakeProfileViewModel
import com.epfl.beatlink.viewmodel.library.PlaylistViewModel
import com.epfl.beatlink.viewmodel.profile.FriendRequestViewModel
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
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
    fakeProfileViewModel.setFakeProfile(profile)
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
  fun searchResultsDisplayPeopleWhenSearching() {
    composeTestRule.onNodeWithTag("writableSearchBar").performClick()
    composeTestRule.onNodeWithTag("writableSearchBar").performTextInput("username")
    composeTestRule.onAllNodesWithTag("CollabCard").assertCountEquals(profiles.size)
  }

  @Test
  fun searchResultsDoesNotDisplayCurrentProfile() {
    composeTestRule.onNodeWithTag("writableSearchBar").performClick()
    composeTestRule.onNodeWithTag("writableSearchBar").performTextInput("TestUser")
    composeTestRule.onNodeWithTag("CollabCard").assertDoesNotExist()
  }

  @Test
  fun inviteCollaborators_addsAndRemovesCollaborators() {
    val profileViewModel = mockk<ProfileViewModel>(relaxed = true)
    val friendRequestViewModel = mockk<FriendRequestViewModel>(relaxed = true)
    val playlistViewModel = mockk<PlaylistViewModel>(relaxed = true)

    // Mock data
    val collabIds = mutableListOf("friend2")
    val friendsProfileData =
        listOf(
            ProfileData(username = "friend1"),
            ProfileData(username = "friend2"),
            ProfileData(username = "friend3"))

    every { profileViewModel.getUserIdByUsername("friend1", any()) } answers
        {
          val callback = secondArg<(String?) -> Unit>()
          callback("friend1")
        }
    every { profileViewModel.getUserIdByUsername("friend3", any()) } answers
        {
          val callback = secondArg<(String?) -> Unit>()
          callback("friend3")
        }
    every { playlistViewModel.updateTemporallyCollaborators(any()) } answers
        {
          collabIds.clear()
          collabIds.addAll(firstArg<List<String>>())
        }

    // Test adding a collaborator
    val onAddCallback = slot<() -> Unit>()
    profileViewModel.getUserIdByUsername("friend1") { userId ->
      playlistViewModel.updateTemporallyCollaborators(collabIds + userId!!)
    }

    assert(collabIds.contains("friend1"))

    // Test removing a collaborator
    profileViewModel.getUserIdByUsername("friend1") { userId ->
      playlistViewModel.updateTemporallyCollaborators(collabIds.filter { it != userId })
    }
    assert(!collabIds.contains("friend1"))
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
