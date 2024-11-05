package com.android.sample.ui

import SpotifyApiRepository
import android.app.Application
import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.android.sample.model.spotify.SpotifyApiViewModel
import com.android.sample.model.spotify.SpotifyAuthRepository
import com.android.sample.ui.authentication.SpotifyAuth
import com.android.sample.ui.authentication.SpotifyAuthViewModel
import okhttp3.OkHttpClient
import org.json.JSONObject

@Composable
fun Playground(application: Application) {
	val client = OkHttpClient()
	val authRepository = SpotifyAuthRepository(client)
	val authViewModel = SpotifyAuthViewModel(application, authRepository)

	val sharedPreferences = application.getSharedPreferences("spotify_auth", Context.MODE_PRIVATE)
	val apiRepository = SpotifyApiRepository(client, sharedPreferences)
	val apiViewModel = SpotifyApiViewModel(application, apiRepository)


	SpotifyAuth(authViewModel)

	Box {
		Column {
			OutlinedButton(
				onClick = {
					apiViewModel.fetchUserProfile {}
				}
			) {
				Text("Fetch User Profile")
			}
		}
	}
}