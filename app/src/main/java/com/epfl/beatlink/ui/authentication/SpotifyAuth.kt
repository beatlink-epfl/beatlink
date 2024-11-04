package com.epfl.beatlink.ui.authentication

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.R

@Composable
fun SpotifyAuth(spotifyViewModel: SpotifyAuthViewModel) {
  val authState by spotifyViewModel.authState
  val context = LocalContext.current

  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    OutlinedCard(
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier.fillMaxWidth().padding(16.dp).testTag("SpotifyAuthCard")) {
          Row(
              modifier = Modifier.padding(4.dp, end = 4.dp),
          ) {
            Spacer(Modifier.width(8.dp))
            Image(
                painter = painterResource(id = R.drawable.spotify),
                contentDescription = "Spotify Logo",
                modifier =
                    Modifier.size(32.dp).align(Alignment.CenterVertically).testTag("SpotifyLogo"))

            Text(
                text =
                    when (authState) {
                      is AuthState.Authenticated -> "Spotify account Linked "
                      AuthState.Idle -> "Link your Spotify account"
                    },
                color = MaterialTheme.colorScheme.primary,
                modifier =
                    Modifier.align(Alignment.CenterVertically)
                        .padding(8.dp)
                        .testTag("AuthStateText"))

            Spacer(Modifier.width(48.dp))

            OutlinedButton(
                onClick = {
                  when (authState) {
                    is AuthState.Authenticated -> {
                      spotifyViewModel.clearAuthData(context)
                    }
                    AuthState.Idle -> {
                      spotifyViewModel.requestUserAuthorization(context)
                    }
                  }
                },
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                colors =
                    ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.testTag("AuthActionButton")) {
                  Text(
                      text =
                          when (authState) {
                            is AuthState.Authenticated -> "Unlink"
                            AuthState.Idle -> "Link"
                          })
                }
          }
        }
  }
}
