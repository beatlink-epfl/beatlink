package com.android.sample.ui.authentication // Adjust the package according to your structure

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.android.sample.ui.library.CornerIcons
import com.android.sample.ui.theme.PrimaryRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthTopAppBar(navigationAction: () -> Unit) {
  TopAppBar(
      title = { BeatLinkTopLogo() },
      navigationIcon = {
        CornerIcons(
            onClick = navigationAction,
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Go back",
            modifier = Modifier.testTag("goBackButton"),
            iconSize = 30.dp)
      })
}

@Composable
fun BeatLinkTopLogo() {
  Box(
      modifier = Modifier.fillMaxWidth().padding(end = 36.dp),
      contentAlignment = Alignment.Center) {
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
