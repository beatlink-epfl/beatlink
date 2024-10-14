package com.android.sample.launch

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.MainActivity
import com.android.sample.resources.C
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

  // Create a rule to launch MainActivity
  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

  @Test
  fun testMainActivityDisplaysContent() {
    // Check if the main screen container is displayed
    composeTestRule.onNodeWithTag(C.Tag.main_screen_container).assertIsDisplayed()
  }
}
