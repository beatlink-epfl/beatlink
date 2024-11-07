package com.epfl.beatlink.ui.profile

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.epfl.beatlink.R
import com.epfl.beatlink.ui.authentication.CustomInputField
import com.epfl.beatlink.ui.components.CornerIcons
import com.epfl.beatlink.ui.navigation.BottomNavigationMenu
import com.epfl.beatlink.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.theme.LightGray
import com.epfl.beatlink.ui.theme.PrimaryGradientBrush
import com.epfl.beatlink.ui.theme.lightThemeBackground

@OptIn(ExperimentalMaterial3Api::class)
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
      topBar = {
        TopAppBar(
            title = {
              Text(
                  text = "Edit profile",
                  textAlign = TextAlign.Center,
                  color = MaterialTheme.colorScheme.primary,
                  style = MaterialTheme.typography.headlineLarge,
                  fontSize = 20.sp,
                  fontWeight = FontWeight.Bold,
                  modifier =
                      Modifier.fillMaxWidth().offset(x = (-28).dp).testTag("editProfileTitle"))
            },
            navigationIcon = {
              CornerIcons(
                  onClick = { navigationActions.goBack() },
                  icon = Icons.AutoMirrored.Filled.ArrowBack,
                  contentDescription = "GoBack",
                  modifier = Modifier.testTag("editProfileBackButton"),
              )
            })
      },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = navigationActions.currentRoute())
      },
      content = { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).verticalScroll(scrollState).testTag("editProfileContent"),
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
              Box(
                  modifier =
                      Modifier.border(1.dp, PrimaryGradientBrush, RoundedCornerShape(30.dp))
                          .width(320.dp)
                          .height(48.dp)) {
                    Button(
                        onClick = { /* Handle button click */},
                        modifier = Modifier.fillMaxWidth().testTag("saveProfileButton"),
                        colors =
                            ButtonDefaults.buttonColors(containerColor = lightThemeBackground)) {
                          Text(
                              text = "Save",
                              fontWeight = FontWeight.Bold,
                              fontSize = 16.sp,
                              color = MaterialTheme.colorScheme.primary)
                        }
                  }
            }
      })
}

@Composable
fun CircleWithIcon(icon: ImageVector, backgroundColor: Color) {
  Box(
      modifier =
          Modifier.size(32.dp)
              .background(color = backgroundColor, shape = CircleShape)
              .clickable { /* Handle click */}) {
        Icon(
            imageVector = icon,
            contentDescription = "Edit",
            tint = LightGray,
            modifier = Modifier.padding(6.dp))
      }
}
