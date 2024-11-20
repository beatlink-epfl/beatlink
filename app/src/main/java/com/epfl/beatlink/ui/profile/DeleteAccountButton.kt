package com.epfl.beatlink.ui.profile

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.viewmodel.auth.FirebaseAuthViewModel
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel

@Composable
fun DeleteAccountButton(
    navigationActions: NavigationActions,
    firebaseAuthViewModel: FirebaseAuthViewModel =
        viewModel(factory = FirebaseAuthViewModel.Factory),
    profileViewModel: ProfileViewModel = viewModel(factory = ProfileViewModel.Factory)) {

    var showDialog by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }

    Box(
        modifier =
        Modifier.width(320.dp)
            .height(48.dp)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.secondary,
                shape = RoundedCornerShape(size = 30.dp)
            ),
        contentAlignment = Alignment.Center) {
        Button(
            onClick = {
                showDialog = true
            },
            modifier = Modifier.width(320.dp).height(48.dp).testTag("deleteAccountButton"),
            colors =
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.secondary),
            shape = RoundedCornerShape(size = 30.dp)) {
            Text(
                modifier = Modifier.testTag("deleteAccountText"),
                text = "Delete Account",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center)
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false }, // Close the dialog when dismissed
            title = {
                Text(
                    text = "Delete Account",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Are you sure you want to delete your account? This action is irreversible.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Enter Password") },
                        placeholder = { Text("Your current password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth().testTag("passwordField")
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Perform deletion
                        firebaseAuthViewModel.deleteAccount(
                            currentPassword = password
                        )
                        profileViewModel.deleteProfile()
                        navigationActions.navigateTo(Screen.LOGIN)
                        showDialog = false
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Preview
@Composable
fun DeleteAccountButtonPreview() {
    DeleteAccountButton(navigationActions = NavigationActions(rememberNavController()))
}