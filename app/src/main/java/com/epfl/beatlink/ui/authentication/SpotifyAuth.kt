package com.epfl.beatlink.ui.authentication

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.R

@Composable
fun SpotifyAuth(spotifyViewModel: SpotifyAuthViewModel) {
  val authState by spotifyViewModel.authState
  val context = LocalContext.current

  Row(
      modifier =
          Modifier.border(1.dp, Color.Gray, RoundedCornerShape(5.dp)) // Border color and shape
              .width(320.dp)
              .height(48.dp)
              .testTag("linkSpotifyBox"),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween) {
        // Spotify Icon
        Box(modifier = Modifier.size(48.dp).padding(8.dp), contentAlignment = Alignment.Center) {
          Image(
              painter = painterResource(id = R.drawable.spotify),
              contentDescription = "Spotify Icon",
              modifier = Modifier.size(32.dp))
        }

        Text(
            modifier = Modifier,
            text =
                when (authState) {
                  is AuthState.Authenticated -> "Spotify account Linked "
                  AuthState.Idle -> "Link your Spotify account"
                },
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.width(8.dp))

        // Link button
        Box(
            modifier =
                Modifier.border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(5.dp))
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .clickable(
                        onClick = {
                          when (authState) {
                            is AuthState.Authenticated -> {
                              spotifyViewModel.clearAuthData(context)
                            }
                            AuthState.Idle -> {
                              spotifyViewModel.requestUserAuthorization(context)
                            }
                          }
                        })
                    .wrapContentSize(),
            contentAlignment = Alignment.Center) {
              Text(
                  text =
                      when (authState) {
                        is AuthState.Authenticated -> "Unlink"
                        AuthState.Idle -> "Link"
                      },
                  color = MaterialTheme.colorScheme.primary,
                  style = MaterialTheme.typography.labelSmall)
            }
        Spacer(modifier = Modifier.width(8.dp))
      }
}
