package com.android.sample.ui.authentication

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.sample.model.spotify.AuthState
import com.android.sample.model.spotify.SpotifyViewModel

@Composable
fun SpotifyLogin(
    spotifyViewModel: SpotifyViewModel
) {
    val authState by spotifyViewModel.authState
    val context = LocalContext.current

    if (authState is AuthState.Error) {
        Toast.makeText(context, "Error: ${(authState as AuthState.Error).error}", Toast.LENGTH_LONG).show()
        spotifyViewModel.resetAuthState()
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        OutlinedCard(
            shape = RoundedCornerShape(4.dp)
        ) {
            Row(
                modifier = Modifier.padding(4.dp),
            ) {
                Spacer(Modifier.width(8.dp))
                Image(
                    painter = painterResource(id = R.drawable.spotify),
                    contentDescription = "Spotify Logo",
                    modifier = Modifier.size(32.dp).align(Alignment.CenterVertically)
                )
                Text(
                    text = "Link your Spotify account",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.CenterVertically).padding(8.dp)
                )
                Spacer(Modifier.width(48.dp))
                when (authState) {
                    is AuthState.Idle, is AuthState.Error -> OutlinedButton(
                        onClick = {
                            spotifyViewModel.requestUserAuthorization(context)
                        },
                        shape = RoundedCornerShape(4.dp),
                    ) {
                        Text("Link")
                    }
                    is AuthState.Success -> OutlinedButton(
                        enabled = false,
                        onClick = {},
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            disabledContentColor = Color(0xFF00D960)
                        ),
                        border = BorderStroke(1.dp, Color(0xFF00D960))
                    ) {
                        Text("Linked")
                    }
                }
            }
        }
    }
}
