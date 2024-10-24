package com.android.sample.ui.authentication

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.theme.IconsGradientBrush
import com.android.sample.ui.theme.PrimaryGradientBrush
import com.android.sample.ui.theme.PrimaryRed
import com.android.sample.ui.theme.SecondaryPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navigationActions: NavigationActions) {
  Scaffold(
      modifier = Modifier.testTag("signUpScreen"),
      topBar = {
        TopAppBar(
            title = {
              Box(
                  modifier = Modifier.fillMaxWidth().padding(end = 36.dp),
                  contentAlignment = Alignment.Center) {
                    Text(
                        modifier = Modifier.testTag("appName"),
                        text =
                            buildAnnotatedString {
                              withStyle(
                                  style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                                    append("Beat")
                                  }
                              withStyle(style = SpanStyle(color = PrimaryRed)) { append("Link") }
                            },
                        style = MaterialTheme.typography.headlineLarge)
                  }
            },
            navigationIcon = {
              IconButton(
                  onClick = { navigationActions.goBack() },
                  modifier = Modifier.testTag("backButton")) {
                    Icon(
                        modifier =
                            Modifier.size(30.dp).graphicsLayer(alpha = 0.99f).drawWithCache {
                              onDrawWithContent {
                                drawContent()
                                drawRect(IconsGradientBrush, blendMode = BlendMode.SrcAtop)
                              }
                            },
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go back")
                  }
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
              var email by remember { mutableStateOf("") }
              OutlinedTextField(
                  value = email,
                  onValueChange = { email = it },
                  label = { Text("My Email Address", color = MaterialTheme.colorScheme.primary) },
                  placeholder = { Text("Enter email address", color = SecondaryPurple) },
                  modifier = Modifier.width(320.dp).testTag("inputEmail"),
                  singleLine = true,
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))

              // Username input field
              var username by remember { mutableStateOf("") }
              OutlinedTextField(
                  value = username,
                  onValueChange = { username = it },
                  label = { Text("My Username", color = MaterialTheme.colorScheme.primary) },
                  placeholder = { Text("Enter username", color = SecondaryPurple) },
                  supportingText = {
                    Text(
                        text = "No special characters, no spaces",
                        color = MaterialTheme.colorScheme.primary)
                  },
                  modifier = Modifier.width(320.dp).testTag("inputUsername"),
                  singleLine = true)

              // Password input field
              var password by remember { mutableStateOf("") }
              OutlinedTextField(
                  value = password,
                  onValueChange = { password = it },
                  label = { Text("My Password", color = MaterialTheme.colorScheme.primary) },
                  placeholder = { Text("Enter password", color = SecondaryPurple) },
                  supportingText = {
                    Text(text = "6-18 characters", color = MaterialTheme.colorScheme.primary)
                  },
                  modifier = Modifier.width(320.dp).testTag("inputPassword"),
                  singleLine = true,
                  visualTransformation = PasswordVisualTransformation(),
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password))

              // Confirm Password input field
              var confirmPassword by remember { mutableStateOf("") }
              OutlinedTextField(
                  value = confirmPassword,
                  onValueChange = { confirmPassword = it },
                  label = { Text("Confirm Password", color = MaterialTheme.colorScheme.primary) },
                  placeholder = { Text("Enter password", color = SecondaryPurple) },
                  supportingText = {
                    Text(text = "6-18 characters", color = MaterialTheme.colorScheme.primary)
                  },
                  modifier = Modifier.width(320.dp).testTag("inputConfirmPassword"),
                  singleLine = true,
                  visualTransformation = PasswordVisualTransformation(),
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password))

              // Link Spotify button
              LinkSpotifyButton()

              Spacer(modifier = Modifier.height(16.dp))

              // Create new account button
              CreateNewAccountButton()

              // Text for sign up option
              Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.testTag("loginText"),
                    text = "Already have an account ?",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium)

                Spacer(modifier = Modifier.width(4.dp))

                // Sign up text with gradient color
                Text(
                    text = "Login",
                    modifier =
                        Modifier.testTag("loginClickableText")
                            .clickable(onClick = { navigationActions.navigateTo(Screen.LOGIN) }),
                    style = MaterialTheme.typography.labelMedium)
              }
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
fun CreateNewAccountButton() {
  Box(
      modifier =
          Modifier.border(
                  width = 2.dp, brush = PrimaryGradientBrush, shape = RoundedCornerShape(30.dp))
              .width(320.dp)
              .height(48.dp),
      contentAlignment = Alignment.Center) {
        Button(
            onClick = { /* TODO: Handle sign up click */},
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
