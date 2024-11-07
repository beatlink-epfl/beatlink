package com.epfl.beatlink.ui.authentication

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.epfl.beatlink.model.authentication.AuthState
import com.epfl.beatlink.model.authentication.FirebaseAuthViewModel
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen

@Composable
fun AuthStateHandler(
    authState: AuthState,
    context: Context,
    navigationActions: NavigationActions,
    authViewModel: FirebaseAuthViewModel,
    successMessage: String
) {
  LaunchedEffect(authState) {
    when (authState) {
      is AuthState.Success -> {
        Toast.makeText(context, successMessage, Toast.LENGTH_SHORT).show()
        authViewModel.resetState()
        navigationActions.navigateTo(Screen.HOME)
      }
      is AuthState.Error -> {
        Toast.makeText(context, (authState).message, Toast.LENGTH_SHORT).show()
        authViewModel.resetState()
      }
      is AuthState.Idle -> {
        // No action needed
      }
    }
  }
}
