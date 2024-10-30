package com.android.sample.ui.authentication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.android.sample.ui.library.CornerIcons
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.theme.SecondaryPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navigationActions: NavigationActions) {
  Scaffold(
      modifier = Modifier.testTag("loginScreen"),
      topBar = {
        TopAppBar(
            title = { BeatLinkTopLogo() },
            navigationIcon = {
              CornerIcons(
                  onClick = { navigationActions.goBack() },
                  icon = Icons.AutoMirrored.Filled.ArrowBack,
                  contentDescription = "Go back",
                  modifier = Modifier.testTag("goBackButton"),
                  iconSize = 30.dp)
            })
      }) { paddingValues ->
        Column(
            modifier =
                Modifier.padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
              // Greeting text
              Text(
                  text = "Hello again,\nGood to see you back !",
                  color = MaterialTheme.colorScheme.primary,
                  style = MaterialTheme.typography.displayLarge,
                  modifier = Modifier.padding(bottom = 80.dp).fillMaxWidth().testTag("loginTitle"))

              // Email input field
              var email by remember { mutableStateOf("") }
              OutlinedTextField(
                  value = email,
                  onValueChange = { email = it },
                  label = { Text("Email", color = MaterialTheme.colorScheme.primary) },
                  placeholder = { Text("Enter email address", color = SecondaryPurple) },
                  modifier = Modifier.width(320.dp).testTag("inputEmail"),
                  singleLine = true,
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))

              // Password input field
              var password by remember { mutableStateOf("") }
              CustomInputField(
                  value = password,
                  onValueChange = { password = it },
                  label = "My Password",
                  placeholder = "Enter password",
                  keyboardType = KeyboardType.Password,
                  visualTransformation = PasswordVisualTransformation(),
                  supportingText = "6-18 characters",
                  modifier = Modifier.testTag("inputPassword"))

              // Spacer between password field and login button
              Spacer(modifier = Modifier.height(16.dp))

              // Login button
              LoginButton(navigationActions)

              // Sign up text
              NavigationTextRow(
                  mainText = "Don’t have an account yet ?",
                  clickableText = "Sign up",
                  onClick = { navigationActions.navigateTo(Screen.REGISTER) },
                  mainTextTag = "noAccountText",
                  clickableTextTag = "signUpText")
            }
      }
}
