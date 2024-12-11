package com.epfl.beatlink.ui.search

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.epfl.beatlink.model.profile.FriendRequestRepository
import com.epfl.beatlink.model.profile.ProfileData
import com.epfl.beatlink.model.profile.ProfileRepository
import com.epfl.beatlink.ui.components.search.PeopleItem
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.profile.FakeFriendRequestViewModel
import com.epfl.beatlink.viewmodel.profile.FriendRequestViewModel
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

class PeopleItemTest {
  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions
  private lateinit var mockFriendRequestRepository: FriendRequestRepository
  private lateinit var mockFriendRequestViewModel: FriendRequestViewModel
  private lateinit var mockProfileRepository: ProfileRepository
  private lateinit var mockProfileViewModel: ProfileViewModel

  private val fakeFriendRequestViewModel = FakeFriendRequestViewModel()

  private val userProfile = ProfileData(username = "user")
  private val displayedUser1 = ProfileData(username = "username1")
  private val displayedUser2 = ProfileData(username = "username2")
  private val displayedUser3 = ProfileData(username = "username3")

  @Before
  fun setUp() {
    mockProfileRepository = mock(ProfileRepository::class.java)
    mockProfileViewModel = mockk(relaxed = true)
    mockFriendRequestRepository = mock(FriendRequestRepository::class.java)
    mockFriendRequestViewModel = mockk(relaxed = true)
    navigationActions = mock(NavigationActions::class.java)
  }

  @Test
  fun displayAllComponents(): Unit = runTest {
    composeTestRule.setContent {
      PeopleItem(
          selectedProfileData = userProfile,
        navigationActions = navigationActions,
          profileViewModel = mockProfileViewModel,
          friendRequestViewModel = fakeFriendRequestViewModel)
    }
    composeTestRule.onNodeWithTag("peopleItem").assertExists()
    composeTestRule.onNodeWithTag("linkedButton").assertExists()
    composeTestRule.onNodeWithText("Link").assertExists()
    composeTestRule.onNodeWithText("Link").performClick()
  }

  @Test
  fun linkButtonDisplaysRequest(): Unit = runTest {
    val displayedUserId = "TestId1"
    val ownRequests = listOf("TestId1", "user2")
    val fakeProfileViewModel =
        object : ProfileViewModel(repository = mockProfileRepository) {
          override fun getUserIdByUsername(username: String, onResult: (String?) -> Unit) {
            if (username == "username1") {
              onResult(displayedUserId) // Simulate the user ID being resolved
            } else {
              onResult(null) // No user ID found
            }
          }
        }
    fakeFriendRequestViewModel.setOwnRequests(ownRequests)

    composeTestRule.setContent {
      PeopleItem(
          selectedProfileData = displayedUser1,
        navigationActions = navigationActions,
          profileViewModel = fakeProfileViewModel,
          friendRequestViewModel = fakeFriendRequestViewModel)
    }
    composeTestRule.onNodeWithText("Requested").assertExists()
    composeTestRule.onNodeWithTag("linkedButton").assertExists()
  }

  @Test
  fun linkButtonDisplaysAccept(): Unit = runTest {
    val displayedUserId = "TestId2"
    val friendRequests = listOf("TestId2", "wanna be friend 2")
    val fakeProfileViewModel =
        object : ProfileViewModel(repository = mockProfileRepository) {
          override fun getUserIdByUsername(username: String, onResult: (String?) -> Unit) {
            if (username == "username2") {
              onResult(displayedUserId) // Simulate the user ID being resolved
            } else {
              onResult(null) // No user ID found
            }
          }
        }
    fakeFriendRequestViewModel.setFriendRequests(friendRequests)

    composeTestRule.setContent {
      PeopleItem(
          selectedProfileData = displayedUser2,
        navigationActions = navigationActions,
          profileViewModel = fakeProfileViewModel,
          friendRequestViewModel = fakeFriendRequestViewModel)
    }
    composeTestRule.onNodeWithText("Accept").assertExists()
    composeTestRule.onNodeWithTag("linkedButton").assertExists()
  }

  @Test
  fun linkButtonDisplaysLinked(): Unit = runTest {
    val displayedUserId = "TestId3"
    val allFriends = listOf("TestId3", "friend2")
    val fakeProfileViewModel =
        object : ProfileViewModel(repository = mockProfileRepository) {
          override fun getUserIdByUsername(username: String, onResult: (String?) -> Unit) {
            if (username == "username3") {
              onResult(displayedUserId) // Simulate the user ID being resolved
            } else {
              onResult(null) // No user ID found
            }
          }
        }
    fakeFriendRequestViewModel.setAllFriends(allFriends)

    composeTestRule.setContent {
      PeopleItem(
          selectedProfileData = displayedUser3,
        navigationActions = navigationActions,
          profileViewModel = fakeProfileViewModel,
          friendRequestViewModel = fakeFriendRequestViewModel)
    }
    composeTestRule.onNodeWithText("Linked").assertExists()
    composeTestRule.onNodeWithTag("linkedButton").assertExists()
  }
}
