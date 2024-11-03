package com.epfl.beatlink.ui.authentication

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.epfl.beatlink.R
import com.epfl.beatlink.model.authentication.FirebaseAuthViewModel
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.ui.theme.PrimaryGradientBrush

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navigationActions: NavigationActions,
    firebaseAuthViewModel: FirebaseAuthViewModel =
        viewModel(factory = FirebaseAuthViewModel.Factory)
) {
  var email by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }

  val context = LocalContext.current
  val authState by firebaseAuthViewModel.authState.collectAsState()

  // Handle authentication state
  AuthStateHandler(
      authState = authState,
      context = context,
      navigationActions = navigationActions,
      authViewModel = firebaseAuthViewModel,
      successMessage = "Login successful")

  Scaffold(
      modifier = Modifier.testTag("loginScreen"),
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
                  text = "Hello again,\nGood to see you back !",
                  color = MaterialTheme.colorScheme.primary,
                  style = MaterialTheme.typography.displayLarge,
                  modifier = Modifier.padding(bottom = 80.dp).fillMaxWidth().testTag("loginTitle"))

              // Email input field
              CustomInputField(
                  value = email,
                  onValueChange = { email = it },
                  label = "Email",
                  placeholder = "Enter email address",
                  keyboardType = KeyboardType.Email,
                  modifier = Modifier.testTag("inputEmail"))

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

              // Spacer between password field and login button
              Spacer(modifier = Modifier.height(16.dp))

              // Login button
              LoginFirebaseButton(
                  authViewModel = firebaseAuthViewModel,
                  email = email,
                  password = password,
                  context = context)

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

@Composable
fun LoginFirebaseButton(
    authViewModel: FirebaseAuthViewModel,
    email: String,
    password: String,
    context: Context
) {

  Box(
      modifier =
          Modifier.width(320.dp)
              .height(48.dp)
              .background(brush = PrimaryGradientBrush, shape = RoundedCornerShape(30.dp))
              .testTag("loginButton"),
      contentAlignment = Alignment.Center) {
        Button(
            onClick = {
              if (email.isBlank() || password.isBlank()) {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
              } else {
                authViewModel.login(email, password)
              }
            },
            modifier = Modifier.fillMaxSize(),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent, contentColor = Color.White),
            shape = RoundedCornerShape(30.dp),
            elevation = null // Optional: Remove button shadow if desired
            ) {
              Text(
                  text = "Login",
                  style =
                      TextStyle(
                          fontSize = 14.sp,
                          lineHeight = 20.sp,
                          fontFamily = FontFamily(Font(R.font.roboto)),
                          fontWeight = FontWeight(500),
                          letterSpacing = 0.14.sp))
            }
      }
}
