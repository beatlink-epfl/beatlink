package com.epfl.beatlink.ui.profile

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.R
import com.epfl.beatlink.ui.authentication.CustomInputField
import com.epfl.beatlink.ui.components.CircleWithIcon
import com.epfl.beatlink.ui.components.PrincipalButton
import com.epfl.beatlink.ui.components.ScreenTopAppBar
import com.epfl.beatlink.ui.navigation.BottomNavigationMenu
import com.epfl.beatlink.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.epfl.beatlink.ui.navigation.NavigationActions

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EditProfileScreen(navigationActions: NavigationActions) {
  var name by remember { mutableStateOf("This is the current name") }
  var description by remember {
    mutableStateOf(
        "This is the current description and it is very long because didier is such a cool guy that he has a lot of things to say about himself")
  }
  val scrollState = rememberScrollState()
  val maxDescriptionLength = 100
  val maxUsernameLength = 20
  Scaffold(
      modifier = Modifier.testTag("editProfileScreen"),
      topBar = { ScreenTopAppBar("Edit profile", "editProfileTitle", navigationActions) },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = navigationActions.currentRoute())
      },
      content = { paddingValues ->
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .testTag("editProfileContent"),
            horizontalAlignment = Alignment.CenterHorizontally) {
              HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
              Spacer(modifier = Modifier.height(50.dp))
              Box {
                ProfilePicture(R.drawable.default_profile_picture) // TODO change to current image
                Box(modifier = Modifier.align(Alignment.BottomEnd)) {
                  CircleWithIcon(Icons.Filled.Edit, MaterialTheme.colorScheme.primary)
                }
              }
              Spacer(modifier = Modifier.height(66.dp))
              CustomInputField(
                  value = name,
                  onValueChange = { newName ->
                    if (newName.length <= maxUsernameLength) {
                      name = newName
                    }
                  },
                  label = "Name",
                  placeholder = "Current name",
                  supportingText = "Max $maxUsernameLength characters",
                  trailingIcon = Icons.Filled.Clear,
                  modifier = Modifier.testTag("editProfileNameInput"))
              Spacer(modifier = Modifier.height(16.dp))
              Row {
                CustomInputField(
                    value = description,
                    onValueChange = { newDescription ->
                      if (newDescription.length <= maxDescriptionLength) {
                        description = newDescription
                      }
                    },
                    label = "Description",
                    placeholder = "Current description",
                    supportingText = "Max $maxDescriptionLength characters",
                    singleLine = false,
                    trailingIcon = Icons.Filled.Clear,
                    modifier = Modifier.testTag("editProfileDescriptionInput"))
              }
              Spacer(modifier = Modifier.height(120.dp))
              PrincipalButton("Save", "saveProfileButton") {}
            }
      })
}
