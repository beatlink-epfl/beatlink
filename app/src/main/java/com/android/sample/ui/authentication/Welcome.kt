package com.android.sample.ui.authentication

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.navigation.TopLevelDestinations
import com.android.sample.ui.theme.PrimaryGradientBrush
import com.android.sample.ui.theme.PrimaryPurple
import com.android.sample.ui.theme.PrimaryRed

@Composable
fun WelcomeScreen(navigationActions: NavigationActions) {
  Column(
      modifier = Modifier.fillMaxSize().padding(16.dp).testTag("welcomeScreen"),
      verticalArrangement = Arrangement.Top,
      horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(80.dp))

        // App logo
        Image(
            painter = painterResource(id = R.drawable.beatlink_logo),
            contentDescription = null,
            modifier = Modifier.size(180.dp).testTag("appLogo"))

        // App name
        Text(
            modifier = Modifier.testTag("appName"),
            text =
                buildAnnotatedString {
                  append("Beat")
                  withStyle(style = androidx.compose.ui.text.SpanStyle(color = PrimaryRed)) {
                    append("Link")
                  }
                },
            style =
                TextStyle(
                    fontSize = 30.sp,
                    lineHeight = 20.sp,
                    fontFamily = FontFamily(Font(R.font.roboto)),
                    fontWeight = FontWeight(700),
                    color = PrimaryPurple,
                    letterSpacing = 0.3.sp,
                ))

        Text(
            modifier = Modifier.testTag("appText"),
            text = "Link Up Through Music",
            style =
                TextStyle(
                    fontSize = 18.sp,
                    lineHeight = 20.sp,
                    fontFamily = FontFamily(Font(R.font.roboto)),
                    fontWeight = FontWeight(500),
                    color = PrimaryPurple,
                    letterSpacing = 0.18.sp,
                ))

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
                    containerColor = Color.White, contentColor = PrimaryPurple),
            shape = RoundedCornerShape(30.dp),
            elevation = null // Optional: Remove button shadow
            ) {
              Text(
                  text = "Sign up",
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

@Composable
fun LoginButton(navigationActions: NavigationActions) {
  Box(
      modifier =
          Modifier.width(320.dp)
              .height(48.dp)
              .background(brush = PrimaryGradientBrush, shape = RoundedCornerShape(30.dp)),
      contentAlignment = Alignment.Center) {
        // Transparent Button to allow gradient background to show
        Button(
            onClick = { navigationActions.navigateTo(TopLevelDestinations.HOME) },
            modifier = Modifier.fillMaxSize().testTag("loginButton"),
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
