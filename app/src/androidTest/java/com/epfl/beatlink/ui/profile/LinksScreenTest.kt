package com.epfl.beatlink.ui.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ApplicationProvider
import com.epfl.beatlink.model.profile.ProfileData
import com.epfl.beatlink.repository.profile.FriendRequestRepositoryFirestore
import com.epfl.beatlink.repository.profile.ProfileRepositoryFirestore
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.viewmodel.profile.FriendRequestViewModel
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel
import com.google.firebase.FirebaseApp
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class LinksScreenTest {
  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions

  private lateinit var profileRepositoryFirestore: ProfileRepositoryFirestore
  private lateinit var profileViewModel: ProfileViewModel

  private lateinit var friendRequestRepositoryFirestore: FriendRequestRepositoryFirestore
  private lateinit var friendRequestViewModel: FriendRequestViewModel

  private val profileData =
      ProfileData(
          username = "testUsername",
          links = 2,
          name = null,
          bio = null,
          profilePicture = null,
          favoriteMusicGenres = listOf("Pop", "Rock", "Jazz", "Classic"),
          topSongs = emptyList(),
          topArtists = emptyList())

  @Before
  fun setUp() {
    profileRepositoryFirestore = mock(ProfileRepositoryFirestore::class.java)
    profileViewModel =
        ProfileViewModel(repository = profileRepositoryFirestore, initialProfile = profileData)

    friendRequestRepositoryFirestore = mock(FriendRequestRepositoryFirestore::class.java)
    friendRequestViewModel = FriendRequestViewModel(friendRequestRepositoryFirestore)

    navigationActions = mock(NavigationActions::class.java)
    `when`(navigationActions.currentRoute()).thenReturn(Screen.LINKS)

    // Initialize Firebase if necessary
    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }
  }

  @Test
  fun elementsAreDisplayed() {
    val friendsList = listOf("user1", "user2")
    val fakeProfileViewModel = FakeProfileViewModel()
    val fakeFriendRequestViewModel = FakeFriendRequestViewModel()
    fakeProfileViewModel.setFakeProfiles(
        listOf(
            ProfileData(
                username = "user1", links = 1, name = "Test User 1", bio = "Bio for User 1"),
            ProfileData(
                username = "user2", links = 1, name = "Test User 2", bio = "Bio for User 2")))
    fakeFriendRequestViewModel.setAllFriends(friendsList)
    composeTestRule.setContent {
      LinksScreen(navigationActions, fakeProfileViewModel, fakeFriendRequestViewModel)
    }
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("LinksScreenTitle").assertIsDisplayed()

    // Check if all friends are displayed
    //   composeTestRule.onNodeWithText("user1", useUnmergedTree = true).assertIsDisplayed()
    //   composeTestRule.onNodeWithText("user2", useUnmergedTree = true).assertIsDisplayed()
    //   composeTestRule.onNodeWithTag("emptyLinksPrompt").assertDoesNotExist()
  }

  @Test
  fun emptyFriendsListDisplaysNoLinksMessage() {
    val fakeProfileViewModel = FakeProfileViewModel()
    val fakeFriendRequestViewModel = FakeFriendRequestViewModel()

    fakeFriendRequestViewModel.setAllFriends(emptyList())

    composeTestRule.setContent {
      LinksScreen(
          navigationActions = navigationActions,
          profileViewModel = fakeProfileViewModel,
          friendRequestViewModel = fakeFriendRequestViewModel)
    }
    composeTestRule.onNodeWithTag("emptyLinksPrompt").assertExists()
    composeTestRule.onNodeWithText("No Links :(").assertExists()
  }
}
