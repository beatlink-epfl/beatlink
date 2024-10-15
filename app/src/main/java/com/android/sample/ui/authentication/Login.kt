package com.android.sample.ui.authentication

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.ui.theme.PrimaryGradientBrush
import com.android.sample.ui.theme.PrimaryPurple
import com.android.sample.ui.theme.PrimaryRed
import com.android.sample.ui.theme.SecondaryPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen() {
  Scaffold(
      topBar = {
        TopAppBar(
            title = {
              Box(
                  modifier = Modifier.fillMaxWidth().padding(end = 36.dp),
                  contentAlignment = Alignment.Center) {
                    Text(
                        text =
                            buildAnnotatedString {
                              append("Beat")
                              withStyle(
                                  style = androidx.compose.ui.text.SpanStyle(color = PrimaryRed)) {
                                    append("Link")
                                  }
                            },
                        style =
                            TextStyle(
                                fontSize = 20.sp,
                                fontFamily = FontFamily(Font(R.font.roboto)),
                                fontWeight = FontWeight(700),
                                color = PrimaryPurple,
                                letterSpacing = 0.2.sp,
                                textAlign = TextAlign.Center))
                  }
            },
            navigationIcon = {
              IconButton(onClick = { /* TODO : Handle back navigation */}) {
                Icon(
                    modifier =
                        Modifier.size(30.dp).graphicsLayer(alpha = 0.99f).drawWithCache {
                          onDrawWithContent {
                            drawContent()
                            drawRect(PrimaryGradientBrush, blendMode = BlendMode.SrcAtop)
                          }
                        },
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Go back",
                )
              }
            })
      }) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
              // Greeting text
              Text(
                  text = "Hello again,\nGood to see you back !",
                  style =
                      TextStyle(
                          fontSize = 32.sp,
                          lineHeight = 40.sp,
                          fontFamily = FontFamily(Font(R.font.roboto)),
                          fontWeight = FontWeight(500),
                          color = PrimaryPurple,
                          textAlign = TextAlign.Center,
                          letterSpacing = 0.32.sp),
                  modifier = Modifier.padding(bottom = 80.dp).fillMaxWidth())

              // Email input field
              var email by remember { mutableStateOf("") }
              OutlinedTextField(
                  value = email,
                  onValueChange = { email = it },
                  label = { Text("Email", color = PrimaryPurple) },
                  placeholder = { Text("Enter email address", color = SecondaryPurple) },
                  modifier = Modifier.width(320.dp),
                  singleLine = true,
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))

              // Password input field
              var password by remember { mutableStateOf("") }
              OutlinedTextField(
                  value = password,
                  onValueChange = { password = it },
                  label = { Text("Password", color = PrimaryPurple) },
                  placeholder = { Text("Enter password", color = SecondaryPurple) },
                  modifier = Modifier.width(320.dp),
                  singleLine = true,
                  visualTransformation = PasswordVisualTransformation(),
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password))

              // Spacer between password field and login button
              Spacer(modifier = Modifier.height(16.dp))

              // Login button
              LoginButton()

              // Text for sign up option
              Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Don’t have an account yet ?",
                    style =
                        TextStyle(
                            fontSize = 14.sp,
                            fontFamily = FontFamily(Font(R.font.roboto)),
                            fontWeight = FontWeight(500),
                            color = PrimaryPurple,
                            letterSpacing = 0.14.sp))

                Spacer(modifier = Modifier.width(4.dp))

                // Sign up text with gradient color
                Text(
                    text = "Sign up",
                    modifier = Modifier.clickable(onClick = { /* TODO: Handle sign up click */}),
                    style =
                        TextStyle(
                            fontSize = 14.sp,
                            fontFamily = FontFamily(Font(R.font.roboto)),
                            fontWeight = FontWeight(500),
                            letterSpacing = 0.14.sp,
                            brush = PrimaryGradientBrush,
                            textDecoration = TextDecoration.Underline))
              }
            }
      }
}
//TODO : remove after tests
@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
  LoginScreen()
}
