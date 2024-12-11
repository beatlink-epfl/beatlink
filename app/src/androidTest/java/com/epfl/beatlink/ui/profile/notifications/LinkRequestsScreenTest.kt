package com.epfl.beatlink.ui.profile.notifications

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ApplicationProvider
import com.epfl.beatlink.model.profile.ProfileData
import com.epfl.beatlink.repository.profile.FriendRequestRepositoryFirestore
import com.epfl.beatlink.repository.profile.ProfileRepositoryFirestore
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Route
import com.epfl.beatlink.ui.profile.FakeFriendRequestViewModel
import com.epfl.beatlink.viewmodel.profile.FriendRequestViewModel
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class LinkRequestsScreenTest {
  @get:Rule val composeTestRule = createComposeRule()

  private val testDispatcher = StandardTestDispatcher()

  private val profileData =
      ProfileData(
          username = "",
          name = null,
          bio = null,
          links = 0,
          profilePicture = null,
          favoriteMusicGenres = listOf("Pop", "Rock", "Jazz", "Classic"))

  private lateinit var navigationActions: NavigationActions

  private lateinit var profileRepositoryFirestore: ProfileRepositoryFirestore
  private lateinit var profileViewModel: ProfileViewModel

  private lateinit var friendRequestRepositoryFirestore: FriendRequestRepositoryFirestore
  private lateinit var friendRequestViewModel: FriendRequestViewModel

  @OptIn(ExperimentalCoroutinesApi::class)
  @Before
  fun setUp() {
    composeTestRule.mainClock.autoAdvance = false // Control clock manually during the test
    MockitoAnnotations.openMocks(this)
    Dispatchers.setMain(testDispatcher)

    profileRepositoryFirestore = mock(ProfileRepositoryFirestore::class.java)
    profileViewModel =
        ProfileViewModel(repository = profileRepositoryFirestore, initialProfile = profileData)

    friendRequestRepositoryFirestore = mock(FriendRequestRepositoryFirestore::class.java)
    friendRequestViewModel = FriendRequestViewModel(friendRequestRepositoryFirestore)

    navigationActions = mock(NavigationActions::class.java)
    `when`(navigationActions.currentRoute()).thenReturn(Route.PROFILE)

    // Initialize Firebase if necessary
    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @After
  fun tearDown() {
    Dispatchers.resetMain() // Reset the Main dispatcher after tests
  }

  @Test
  fun everythingIsDisplayed() {
    composeTestRule.setContent {
      LinkRequestsScreen(navigationActions, profileViewModel, friendRequestViewModel)
    }
    composeTestRule.onNodeWithTag("linkRequestsScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("linkRequestsScreenTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("tabRow").assertIsDisplayed()
  }

  @Test
  fun tabRowIsCorrectlyDisplayed() {
    val fakeFriendRequestViewModel = FakeFriendRequestViewModel()
    fakeFriendRequestViewModel.setOwnRequests(emptyList())
    fakeFriendRequestViewModel.setFriendRequests(emptyList())

    composeTestRule.setContent {
      LinkRequestsScreen(navigationActions, profileViewModel, fakeFriendRequestViewModel)
    }

    composeTestRule.onNodeWithText("SENT (0)").assertExists()
    composeTestRule.onNodeWithText("RECEIVED (0)").assertExists()

    composeTestRule.onNodeWithTag("emptyRequestsPrompt").assertExists()
    composeTestRule.onNodeWithText("No requests sent.").assertExists()
  }

  @Test
  fun sentFriendRequestsAreCorrectlyDisplayed() {
    val profileA = ProfileData(bio = "", links = 0, name = "A", username = "AAA")
    val profileB = ProfileData(bio = "", links = 1, name = "B", username = "BBB")

    val mockSentRequests = listOf("idA", "idB")
    val fakeFriendRequestViewModel = FakeFriendRequestViewModel()
    fakeFriendRequestViewModel.setOwnRequests(mockSentRequests)
    val mockProfiles = listOf(profileA, profileB)

    composeTestRule.setContent {
      LinkRequestsScreen(navigationActions, profileViewModel, fakeFriendRequestViewModel)
    }

    composeTestRule.onNodeWithText("SENT (2)").assertExists()
  }
}
