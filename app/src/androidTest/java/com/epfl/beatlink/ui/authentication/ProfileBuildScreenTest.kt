package com.epfl.beatlink.ui.authentication

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.epfl.beatlink.model.profile.ProfileData
import com.epfl.beatlink.repository.profile.ProfileRepositoryFirestore
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileBuildScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions
  private lateinit var profileViewModel: ProfileViewModel
  private lateinit var profileRepository: ProfileRepositoryFirestore

  @Before
  fun setUp() {
    navigationActions = mockk(relaxed = true)
    profileRepository = mockk(relaxed = true)
    profileViewModel = ProfileViewModel(profileRepository)

    // Set the content for the composable
    composeTestRule.setContent { ProfileBuildScreen(navigationActions, profileViewModel) }
  }

  @Test
  fun displayAllMainComponents() {
    composeTestRule.onNodeWithTag("profileBuildScreen").assertIsDisplayed()

    composeTestRule.onNodeWithTag("appName").assertIsDisplayed()

    composeTestRule.onNodeWithTag("profileBuildTitle").performScrollTo().assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("profileBuildTitle")
        .performScrollTo()
        .assertTextContains("Account created !\nNow build up your profile")

    composeTestRule.onNodeWithTag("addProfilePicture").performScrollTo().assertIsDisplayed()

    composeTestRule.onNodeWithTag("inputName").performScrollTo().assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("inputName", useUnmergedTree = true)
        .performScrollTo()
        .onChildren()
        .filterToOne(hasText("Name", substring = true))
        .assertIsDisplayed()

    composeTestRule.onNodeWithTag("inputDescription").performScrollTo().assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("inputDescription", useUnmergedTree = true)
        .performScrollTo()
        .onChildren()
        .filterToOne(hasText("Description", substring = true))
        .assertIsDisplayed()

    composeTestRule
        .onNodeWithTag("selectFavoriteGenresText", useUnmergedTree = true)
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("selectFavoriteGenresText", useUnmergedTree = true)
        .performScrollTo()
        .assertTextEquals("Select Your Favorite Music Genres")

    composeTestRule.onNodeWithTag("saveButton").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("saveButton").performScrollTo().assertTextEquals("Save")
  }

  @Test
  fun toggleMusicGenreSelectionDialog() {
    // Open the genre selection dialog
    composeTestRule
        .onNodeWithTag("selectFavoriteGenresText", useUnmergedTree = true)
        .performScrollTo()
        .performClick()
    composeTestRule.onNodeWithTag("musicGenreSelectionDialog").assertIsDisplayed()

    // Close the dialog by clicking "CANCEL"
    composeTestRule.onNodeWithTag("cancelButton").performScrollTo().performClick()
    composeTestRule.onNodeWithTag("musicGenreSelectionDialog").assertDoesNotExist()
  }

  @Test
  fun selectMusicGenreAndSave() {
    // Open the music genre selection dialog
    composeTestRule
        .onNodeWithTag("selectFavoriteGenresText", useUnmergedTree = true)
        .performScrollTo()
        .performClick()

    composeTestRule.onNodeWithTag("musicGenreSelectionDialog").assertIsDisplayed()

    composeTestRule.onNodeWithTag("dialogTitle").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("dialogTitle").performScrollTo().assertTextEquals("MUSIC GENRES")

    composeTestRule.onNodeWithTag("dialogTitleDivider").performScrollTo().assertIsDisplayed()

    // Select Pop genre using `useUnmergedTree = true`
    composeTestRule
        .onNodeWithTag("genreCheckbox_Pop", useUnmergedTree = true)
        .performScrollTo()
        .performClick()

    // Confirm selection by clicking "OK"
    composeTestRule.onNodeWithTag("okButton").performScrollTo().performClick()

    // Verify the selection text reflects the chosen genre
    composeTestRule
        .onNodeWithTag("YOUR FAVORITE MUSIC GENRESTitle")
        .performScrollTo()
        .assertTextContains("YOUR FAVORITE MUSIC GENRES")

    composeTestRule
        .onNodeWithTag("PopMusicCard", useUnmergedTree = true)
        .performScrollTo()
        .assertIsDisplayed()
  }

  @Test
  fun saveButtonUpdatesProfileAndNavigatesToHomeScreen() {
    composeTestRule.onNodeWithTag("saveButton").performScrollTo().performClick()
    coVerify { profileRepository.updateProfile(any(), any<ProfileData>()) }
    verify { navigationActions.navigateToAndClearAllBackStack(Screen.HOME) }
  }
}
