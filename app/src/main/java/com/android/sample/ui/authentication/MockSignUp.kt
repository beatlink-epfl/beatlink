package com.android.sample.ui.authentication

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.ui.theme.PrimaryGradientBrush
import com.android.sample.ui.theme.PrimaryPurple
import com.android.sample.ui.theme.SecondaryPurple
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SignUpScreen() {
  val context = LocalContext.current
  var email by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var confirmPassword by remember { mutableStateOf("") }

  val auth: FirebaseAuth = FirebaseAuth.getInstance()

  Scaffold(
      modifier = Modifier.testTag("signUpScreen"),
      topBar = { /* Your existing TopAppBar code */}) { paddingValues ->
        Column(
            modifier =
                Modifier.padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
              // Existing input fields (Email, Username, Password, Confirm Password)...

              OutlinedTextField(
                  value = email,
                  onValueChange = { email = it },
                  label = { Text("My Email Address", color = PrimaryPurple) },
                  placeholder = { Text("Enter email address", color = SecondaryPurple) },
                  modifier = Modifier.width(320.dp).testTag("inputEmail"),
                  singleLine = true,
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))

              OutlinedTextField(
                  value = password,
                  onValueChange = { password = it },
                  label = { Text("My Password", color = PrimaryPurple) },
                  placeholder = { Text("Enter password", color = SecondaryPurple) },
                  supportingText = { Text("6-18 characters") },
                  modifier = Modifier.width(320.dp).testTag("inputPassword"),
                  singleLine = true,
                  visualTransformation = PasswordVisualTransformation(),
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password))

              OutlinedTextField(
                  value = confirmPassword,
                  onValueChange = { confirmPassword = it },
                  label = { Text("Confirm Password", color = PrimaryPurple) },
                  placeholder = { Text("Enter password", color = SecondaryPurple) },
                  supportingText = { Text("6-18 characters") },
                  modifier = Modifier.width(320.dp).testTag("inputConfirmPassword"),
                  singleLine = true,
                  visualTransformation = PasswordVisualTransformation(),
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password))

              Spacer(modifier = Modifier.height(16.dp))

              // Create new account button with Firebase integration
              CreateNewAccountButton(email, password, confirmPassword, auth, context)
            }
      }
}

@Composable
fun CreateNewAccountButton(
    email: String,
    password: String,
    confirmPassword: String,
    auth: FirebaseAuth,
    context: android.content.Context
) {
  Box(
      modifier =
          Modifier.border(
                  width = 2.dp, brush = PrimaryGradientBrush, shape = RoundedCornerShape(30.dp))
              .width(320.dp)
              .height(48.dp),
      contentAlignment = Alignment.Center) {
        Button(
            onClick = {
              if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (password == confirmPassword) {
                  auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task
                    ->
                    if (task.isSuccessful) {
                      // Account creation success
                      Toast.makeText(context, "Account created successfully!", Toast.LENGTH_SHORT)
                          .show()
                    } else {
                      // Account creation failed
                      Toast.makeText(
                              context, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT)
                          .show()
                    }
                  }
                } else {
                  Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
              } else {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
              }
            },
            modifier = Modifier.fillMaxSize().testTag("createAccountButton"),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = Color.White, contentColor = PrimaryPurple),
            shape = RoundedCornerShape(30.dp),
            elevation = null) {
              Text(
                  modifier = Modifier.testTag("createAccountText"),
                  text = "Create New Account",
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

//create preview code here
@Preview
@Composable
fun PreviewSignUpScreen() {
  SignUpScreen()
}
