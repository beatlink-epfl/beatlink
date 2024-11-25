package com.epfl.beatlink.ui.authentication

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.epfl.beatlink.model.auth.AuthState
import com.epfl.beatlink.ui.components.BackArrowButton
import com.epfl.beatlink.ui.components.CornerIcons
import com.epfl.beatlink.ui.theme.PrimaryRed
import com.epfl.beatlink.viewmodel.auth.FirebaseAuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthTopAppBar(navigationAction: () -> Unit) {
  TopAppBar(
      title = { BeatLinkTopLogo(Modifier
          .padding(end = 46.dp)) },
      navigationIcon = {
          BackArrowButton { navigationAction() }
      })
}

@Composable
fun BeatLinkTopLogo(modifier: Modifier = Modifier) {
  Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
    Text(
        modifier = Modifier.testTag("appName"),
        text =
            buildAnnotatedString {
              withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                append("Beat")
              }
              withStyle(style = SpanStyle(color = PrimaryRed)) { append("Link") }
            },
        style = MaterialTheme.typography.headlineLarge)
  }
}

@Composable
fun NavigationTextRow(
    mainText: String,
    clickableText: String,
    onClick: () -> Unit,
    mainTextTag: String,
    clickableTextTag: String
) {
  Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
    Text(
        text = mainText,
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.testTag(mainTextTag))
    Spacer(modifier = Modifier.width(4.dp))
    Text(
        text = clickableText,
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier.testTag(clickableTextTag).clickable(onClick = onClick),
    )
  }
}

@Composable
fun AuthStateHandler(
    authState: AuthState,
    context: Context,
    onSuccess: () -> Unit, // Updated parameter name for better flexibility
    authViewModel: FirebaseAuthViewModel,
    successMessage: String
) {
  LaunchedEffect(authState) {
    when (authState) {
      is AuthState.Success -> {
        Toast.makeText(context, successMessage, Toast.LENGTH_SHORT).show()
        authViewModel.resetState()
        onSuccess()
      }
      is AuthState.Error -> {
        Toast.makeText(context, authState.message, Toast.LENGTH_SHORT).show()
        authViewModel.resetState()
      }
      is AuthState.Idle -> {
        // No action needed
      }
    }
  }
}
