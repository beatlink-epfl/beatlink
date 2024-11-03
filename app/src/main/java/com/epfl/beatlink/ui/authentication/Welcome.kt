package com.epfl.beatlink.ui.authentication

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.R
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.ui.theme.PrimaryGradientBrush
import com.epfl.beatlink.ui.theme.PrimaryRed

@Composable
fun WelcomeScreen(navigationActions: NavigationActions) {
  Column(
      modifier =
          Modifier.fillMaxSize()
              .background(MaterialTheme.colorScheme.background)
              .verticalScroll(rememberScrollState())
              .testTag("welcomeScreen"),
      verticalArrangement = Arrangement.Top,
      horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(80.dp))

        // App logo
        Image(
            painter = painterResource(id = R.drawable.logo_beatlink),
            contentDescription = "beatlink logo",
            modifier = Modifier.size(180.dp).testTag("appLogo"))

        // App name
        Text(
            modifier = Modifier.testTag("appName"),
            text =
                buildAnnotatedString {
                  withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                    append("Beat")
                  }
                  withStyle(style = SpanStyle(color = PrimaryRed)) { append("Link") }
                },
            style = MaterialTheme.typography.displayMedium)

        Text(
            modifier = Modifier.testTag("appText"),
            text = "Link Up Through Music",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.displaySmall)

        Spacer(modifier = Modifier.height(100.dp))

        // Sign Up Button
        SignUpButton(navigationActions)

        Spacer(modifier = Modifier.height(16.dp))

        // Login Button
        LoginButton(navigationActions)
      }
}

@Composable
fun SignUpButton(navigationActions: NavigationActions) {
  Box(
      modifier =
          Modifier.border(
                  width = 2.dp, brush = PrimaryGradientBrush, shape = RoundedCornerShape(30.dp))
              .width(320.dp)
              .height(48.dp)
              .testTag("signUpButton"),
      contentAlignment = Alignment.Center) {
        Button(
            onClick = { navigationActions.navigateTo(Screen.REGISTER) },
            modifier = Modifier.fillMaxSize(),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(30.dp),
            elevation = null // Optional: Remove button shadow
            ) {
              Text(text = "Sign up", style = MaterialTheme.typography.labelLarge)
            }
      }
}

@Composable
fun LoginButton(navigationActions: NavigationActions) {
  Box(
      modifier =
          Modifier.width(320.dp)
              .height(48.dp)
              .background(brush = PrimaryGradientBrush, shape = RoundedCornerShape(30.dp))
              .testTag("loginButton"),
      contentAlignment = Alignment.Center) {
        // Transparent Button to allow gradient background to show
        Button(
            onClick = { navigationActions.navigateTo(Screen.LOGIN) },
            modifier = Modifier.fillMaxSize(),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent, contentColor = Color.White),
            shape = RoundedCornerShape(30.dp),
            elevation = null // Optional: Remove button shadow if desired
            ) {
              Text(text = "Login", style = MaterialTheme.typography.labelLarge)
            }
      }
}
