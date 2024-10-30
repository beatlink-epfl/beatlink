package com.android.sample.ui.authentication

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.sample.R
import com.android.sample.model.authentication.FirebaseAuthViewModel
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.theme.PrimaryGradientBrush

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    navigationActions: NavigationActions,
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
      navigationActions = navigationActions,
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
              LinkSpotifyButton()

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
                  mainTextTag = "loginText",
                  clickableTextTag = "loginClickableText")
            }
      }
}

@Composable
fun LinkSpotifyButton() {
  Row(
      modifier =
          Modifier.border(1.dp, Color.Gray, RoundedCornerShape(5.dp)) // Border color and shape
              .width(320.dp)
              .height(48.dp)
              .testTag("linkSpotifyBox"),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween) {
        // Spotify Icon
        Box(modifier = Modifier.size(48.dp).padding(8.dp), contentAlignment = Alignment.Center) {
          Image(
              painter = painterResource(id = R.drawable.spotify),
              contentDescription = "Spotify Icon",
              modifier = Modifier.size(32.dp).testTag("spotifyIcon"))
        }

        Text(
            modifier = Modifier.testTag("linkSpotifyText"),
            text = "Link My Spotify Account",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.width(8.dp))

        // Link button
        Box(
            modifier =
                Modifier.border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(5.dp))
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .clickable(onClick = { /* TODO: Handle link action */})
                    .wrapContentSize()
                    .testTag("linkBox"),
            contentAlignment = Alignment.Center) {
              Text(
                  modifier = Modifier.testTag("linkText"),
                  text = "Link",
                  color = MaterialTheme.colorScheme.primary,
                  style = MaterialTheme.typography.labelSmall)
            }
        Spacer(modifier = Modifier.width(8.dp))
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

@Composable
fun CustomInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    singleLine: Boolean = true,
    supportingText: String? = null
) {
  Column {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = MaterialTheme.colorScheme.primary) },
        placeholder = { Text(placeholder, color = MaterialTheme.colorScheme.onSecondary) },
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation,
        textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.primary),
        modifier = modifier.width(320.dp),
        colors =
            OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.primary,
                unfocusedTextColor = MaterialTheme.colorScheme.primary,
                cursorColor = MaterialTheme.colorScheme.onPrimary,
                errorTextColor = MaterialTheme.colorScheme.error,
                focusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                focusedLabelColor = MaterialTheme.colorScheme.onPrimary))
    supportingText?.let { Text(text = it, color = MaterialTheme.colorScheme.primary) }
  }
}

@Composable
fun NavigationTextRow(
    mainText: String,
    clickableText: String,
    onClick: () -> Unit,
    mainTextTag: String,
    clickableTextTag: String
) {
  Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
    Text(
        text = mainText,
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.testTag(mainTextTag))
    Spacer(modifier = Modifier.width(4.dp))
    Text(
        text = clickableText,
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier.testTag(clickableTextTag).clickable(onClick = onClick),
    )
  }
}
