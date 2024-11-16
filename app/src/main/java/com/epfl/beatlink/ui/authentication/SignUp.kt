package com.epfl.beatlink.ui.authentication

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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.epfl.beatlink.ui.components.CustomInputField
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.ui.spotify.SpotifyAuth
import com.epfl.beatlink.ui.theme.PrimaryGradientBrush
import com.epfl.beatlink.viewmodel.auth.FirebaseAuthViewModel
import com.epfl.beatlink.viewmodel.spotify.auth.SpotifyAuthViewModel

@Composable
fun SignUpScreen(
    navigationActions: NavigationActions,
    spotifyAuthViewModel: SpotifyAuthViewModel,
    firebaseAuthViewModel: FirebaseAuthViewModel =
        viewModel(factory = FirebaseAuthViewModel.Factory)
) {
  var email by remember { mutableStateOf("") }
  var username by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var confirmPassword by remember { mutableStateOf("") }

  val context = LocalContext.current
  val authState by firebaseAuthViewModel.authState.collectAsState()

  // Handle authentication state
  AuthStateHandler(
      authState = authState,
      context = context,
      onSuccess = { navigationActions.navigateTo(Screen.PROFILE_BUILD) },
      authViewModel = firebaseAuthViewModel,
      successMessage = "Sign up successful" // Success message for sign up
      )

  Scaffold(
      modifier = Modifier.testTag("signUpScreen"),
      topBar = {
        AuthTopAppBar(navigationAction = { navigationActions.navigateTo(Screen.WELCOME) })
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
                  text =
                      buildAnnotatedString {
                        append("Create an account ")
                        withStyle(
                            style =
                                SpanStyle(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontStyle = FontStyle.Italic)) {
                              append("now")
                            }
                        append("\nto join our community")
                      },
                  style = MaterialTheme.typography.displayLarge,
                  color = MaterialTheme.colorScheme.primary,
                  modifier =
                      Modifier.fillMaxWidth().padding(bottom = 15.dp).testTag("greetingText"))

              // Email input field
              CustomInputField(
                  value = email,
                  onValueChange = { email = it },
                  label = "My Email Address",
                  placeholder = "Enter email address",
                  keyboardType = KeyboardType.Email,
                  modifier = Modifier.testTag("inputEmail"))

              // Username input field
              CustomInputField(
                  value = username,
                  onValueChange = { username = it },
                  label = "My Username",
                  placeholder = "Enter username",
                  supportingText = "No special characters, no spaces",
                  modifier = Modifier.testTag("inputUsername"))

              // Password input field
              CustomInputField(
                  value = password,
                  onValueChange = { password = it },
                  label = "My Password",
                  placeholder = "Enter password",
                  keyboardType = KeyboardType.Password,
                  visualTransformation = PasswordVisualTransformation(),
                  supportingText = "6-18 characters",
                  modifier = Modifier.testTag("inputPassword"))

              // Confirm Password input field
              CustomInputField(
                  value = confirmPassword,
                  onValueChange = { confirmPassword = it },
                  label = "Confirm Password",
                  placeholder = "Enter password",
                  keyboardType = KeyboardType.Password,
                  visualTransformation = PasswordVisualTransformation(),
                  supportingText = "6-18 characters",
                  modifier = Modifier.testTag("inputConfirmPassword"))

              // Link Spotify button
              SpotifyAuth(spotifyAuthViewModel)

              Spacer(modifier = Modifier.height(16.dp))

              // Create new account button
              CreateNewAccountButton(
                  authViewModel = firebaseAuthViewModel,
                  email = email,
                  password = password,
                  confirmPassword = confirmPassword,
                  username = username)

              NavigationTextRow(
                  mainText = "Already have an account ?",
                  clickableText = "Login",
                  onClick = { navigationActions.navigateTo(Screen.LOGIN) },
                  mainTextTag = "accountText",
                  clickableTextTag = "loginText")
            }
      }
}

@Composable
fun CreateNewAccountButton(
    authViewModel: FirebaseAuthViewModel,
    email: String,
    password: String,
    confirmPassword: String,
    username: String
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
                email.isBlank() ||
                    username.isBlank() ||
                    password.isBlank() ||
                    confirmPassword.isBlank() -> {
                  Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
                password != confirmPassword -> {
                  Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
                else -> {
                  authViewModel.signUp(email, password, username)
                }
              }
            },
            modifier = Modifier.fillMaxSize().testTag("createAccountButton"),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(30.dp),
            elevation = null) {
              Text(
                  modifier = Modifier.testTag("createAccountText"),
                  text = "Create New Account",
                  style = MaterialTheme.typography.labelLarge)
            }
      }
}
