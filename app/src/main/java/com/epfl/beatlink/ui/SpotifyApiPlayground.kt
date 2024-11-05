package com.epfl.beatlink.ui

import com.epfl.beatlink.model.spotify.SpotifyApiRepository
import android.app.Application
import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.epfl.beatlink.model.spotify.SpotifyApiViewModel
import com.epfl.beatlink.ui.authentication.SpotifyAuth
import com.epfl.beatlink.ui.authentication.SpotifyAuthViewModel
import okhttp3.OkHttpClient

@Composable
fun Playground(application: Application, authViewModel: SpotifyAuthViewModel) {
	val client = OkHttpClient()

	val sharedPreferences = application.getSharedPreferences("spotify_auth", Context.MODE_PRIVATE)
	val apiRepository = SpotifyApiRepository(client, sharedPreferences)
	val apiViewModel = SpotifyApiViewModel(application, apiRepository)


	SpotifyAuth(authViewModel)

	Box {
		Column {
			OutlinedButton(
				onClick = {
					apiViewModel.fetchCurrentUserProfile { result ->
						result.onSuccess { json ->
							val displayName = json.getString("display_name")
							val email = json.getString("email")
							val followers = json.getJSONObject("followers").getInt("total")
							val product = json.getString("product")
							val uri = json.getString("uri")
							val image = json.getJSONArray("images").getJSONObject(0).getString("url")

							// Display the user profile information
							// (this is just a simple example, in a real app you would use a Composable)
							println("User Profile:")
							println("Display Name: $displayName")
							println("Email: $email")
							println("Followers: $followers")
							println("Product: $product")
							println("URI: $uri")
							println("Image: $image")
						}
						result.onFailure { error ->
							println("Failed to fetch user profile: ${error.message}")
						}
					}
				}
			) {
				Text("Fetch User Profile")
			}

			OutlinedButton(
				onClick = {
					apiViewModel.pausePlayback { result ->
						result.onSuccess {
							println("Playback paused successfully")
						}
						result.onFailure { error ->
							println("Failed to pause playback: ${error.message}")
						}
					}
				}
			) {
				Text("Pause Playback")
			}
		}
	}
}