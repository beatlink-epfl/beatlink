package com.epfl.beatlink.ui.profile

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.epfl.beatlink.ui.authentication.AuthStateHandler
import com.epfl.beatlink.ui.components.CustomInputField
import com.epfl.beatlink.ui.components.ScreenTopAppBar
import com.epfl.beatlink.ui.navigation.BottomNavigationMenu
import com.epfl.beatlink.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.ui.theme.PrimaryGradientBrush
import com.epfl.beatlink.viewmodel.auth.FirebaseAuthViewModel

@Composable
fun ChangePassword(
    navigationActions: NavigationActions,
    firebaseAuthViewModel: FirebaseAuthViewModel =
        viewModel(factory = FirebaseAuthViewModel.Factory)
) {
  val context = LocalContext.current
  val authState by firebaseAuthViewModel.authState.collectAsState()

  var currentPassword by remember { mutableStateOf("") }
  var newPassword by remember { mutableStateOf("") }
  var confirmNewPassword by remember { mutableStateOf("") }

  // Handle authentication state
  AuthStateHandler(
      authState = authState,
      context = context,
      onSuccess = { navigationActions.navigateTo(Screen.PROFILE) },
      authViewModel = firebaseAuthViewModel,
      successMessage = "Change password successful")

  Scaffold(
      modifier = Modifier.testTag("changePasswordScreen"),
      topBar = { ScreenTopAppBar("Change password", "changePasswordTitle", navigationActions) },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = navigationActions.currentRoute())
      }) { paddingValues ->
        Column(
            modifier =
                Modifier.padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                  text = "Please enter your existing\npassword and your new password.",
                  style = MaterialTheme.typography.displayLarge,
                  color = MaterialTheme.colorScheme.primary,
                  modifier =
                      Modifier.fillMaxWidth().padding(bottom = 15.dp).testTag("instructionText"))

              // Current Password input field
              CustomInputField(
                  value = currentPassword,
                  onValueChange = { currentPassword = it },
                  label = "Current Password",
                  placeholder = "Enter password",
                  keyboardType = KeyboardType.Password,
                  visualTransformation = PasswordVisualTransformation(),
                  modifier = Modifier.testTag("inputCurrentPassword"))

              // New Password input field
              CustomInputField(
                  value = newPassword,
                  onValueChange = { newPassword = it },
                  label = "New Password",
                  placeholder = "Enter password",
                  keyboardType = KeyboardType.Password,
                  visualTransformation = PasswordVisualTransformation(),
                  supportingText = "6-18 characters",
                  modifier = Modifier.testTag("inputNewPassword"))

              // Confirm New Password input field
              CustomInputField(
                  value = confirmNewPassword,
                  onValueChange = { confirmNewPassword = it },
                  label = "Confirm New Password",
                  placeholder = "Enter password",
                  keyboardType = KeyboardType.Password,
                  visualTransformation = PasswordVisualTransformation(),
                  supportingText = "6-18 characters",
                  modifier = Modifier.testTag("inputConfirmNewPassword"))

              Spacer(modifier = Modifier.height(16.dp))

              ChangePasswordButton(
                  authViewModel = firebaseAuthViewModel,
                  currentPassword = currentPassword,
                  newPassword = newPassword,
                  confirmNewPassword = confirmNewPassword)
            }
      }
}

@Composable
fun ChangePasswordButton(
    authViewModel: FirebaseAuthViewModel,
    currentPassword: String,
    newPassword: String,
    confirmNewPassword: String
) {
  val context = LocalContext.current
  Box(
      modifier =
          Modifier.border(
                  width = 2.dp, brush = PrimaryGradientBrush, shape = RoundedCornerShape(30.dp))
              .width(320.dp)
              .height(48.dp),
      contentAlignment = Alignment.Center) {
        Button(
            onClick = {
              when {
                currentPassword.isBlank() ||
                    newPassword.isBlank() ||
                    confirmNewPassword.isBlank() -> {
                  Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
                newPassword != confirmNewPassword -> {
                  Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
                else -> {
                  authViewModel.verifyAndChangePassword(
                      currentPassword = currentPassword, newPassword = newPassword)
                }
              }
            },
            modifier = Modifier.fillMaxSize().testTag("changePasswordButton"),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(30.dp),
            elevation = null) {
              Text(
                  modifier = Modifier.testTag("changePasswordText"),
                  text = "Save",
                  style = MaterialTheme.typography.labelLarge)
            }
      }
}
