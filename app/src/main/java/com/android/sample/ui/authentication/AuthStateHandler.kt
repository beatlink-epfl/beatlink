package com.android.sample.ui.authentication

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.android.sample.model.authentication.AuthState
import com.android.sample.model.authentication.AuthViewModel
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen

@Composable
fun AuthStateHandler(
    authState: AuthState,
    context: Context,
    navigationActions: NavigationActions,
    authViewModel: AuthViewModel,
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
        Toast.makeText(context, (authState as AuthState.Error).message, Toast.LENGTH_SHORT).show()
        authViewModel.resetState()
      }
      is AuthState.Idle -> {
        // No action needed
      }
    }
  }
}
