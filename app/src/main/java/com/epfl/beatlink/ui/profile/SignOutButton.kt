package com.epfl.beatlink.ui.profile

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.viewmodel.auth.FirebaseAuthViewModel

@Composable
fun SignOutButton(
    navigationActions: NavigationActions,
    firebaseAuthViewModel: FirebaseAuthViewModel =
        viewModel(factory = FirebaseAuthViewModel.Factory)
) {
  Box(
      modifier =
          Modifier.width(320.dp)
              .height(48.dp)
              .border(
                  width = 2.dp,
                  color = MaterialTheme.colorScheme.secondary,
                  shape = RoundedCornerShape(size = 30.dp)),
      contentAlignment = Alignment.Center) {
        Button(
            onClick = {
              firebaseAuthViewModel.signOut()
              navigationActions.navigateTo(Screen.WELCOME)
            },
            modifier = Modifier.width(320.dp).height(48.dp).testTag("signOutButton"),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.secondary),
            shape = RoundedCornerShape(size = 30.dp)) {
              Text(
                  modifier = Modifier.testTag("signOutText"),
                  text = "Sign out",
                  style = MaterialTheme.typography.labelLarge,
                  color = MaterialTheme.colorScheme.secondary,
                  textAlign = TextAlign.Center)
            }
      }
}
