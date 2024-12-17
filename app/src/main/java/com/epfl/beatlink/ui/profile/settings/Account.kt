package com.epfl.beatlink.ui.profile.settings

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.ui.components.PrincipalButton
import com.epfl.beatlink.ui.components.ScreenTopAppBar
import com.epfl.beatlink.ui.components.TextInBox
import com.epfl.beatlink.ui.navigation.BottomNavigationMenu
import com.epfl.beatlink.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.ui.spotify.SpotifyAuth
import com.epfl.beatlink.viewmodel.auth.FirebaseAuthViewModel
import com.epfl.beatlink.viewmodel.library.PlaylistViewModel
import com.epfl.beatlink.viewmodel.map.user.MapUsersViewModel
import com.epfl.beatlink.viewmodel.profile.FriendRequestViewModel
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel
import com.epfl.beatlink.viewmodel.spotify.auth.SpotifyAuthViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.joinAll

@Composable
fun AccountScreen(
    navigationActions: NavigationActions,
    spotifyAuthViewModel: SpotifyAuthViewModel,
    profileViewModel: ProfileViewModel,
    firebaseAuthViewModel: FirebaseAuthViewModel,
    mapUsersViewModel: MapUsersViewModel,
    playlistViewModel: PlaylistViewModel,
    friendRequestViewModel: FriendRequestViewModel
) {
  val context = LocalContext.current
  LaunchedEffect(Unit) { profileViewModel.fetchProfile() }
  val profileData by profileViewModel.profile.collectAsState()
  val username by remember { mutableStateOf(profileData?.username ?: "") }
  val email by remember { mutableStateOf(profileData?.email ?: "") }
  val scrollState = rememberScrollState()
  var showDialog by remember { mutableStateOf(false) }
  var password by remember { mutableStateOf("") }
    val allFriends by friendRequestViewModel.allFriends.observeAsState(emptyList())

  Scaffold(
      topBar = { ScreenTopAppBar("Account", "accountScreenTitle", navigationActions) },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = navigationActions.currentRoute())
      },
      content = { paddingValue ->
        Column(
            modifier =
            Modifier
                .fillMaxSize()
                .padding(paddingValue)
                .verticalScroll(scrollState)
                .testTag("accountScreenContent"),
            horizontalAlignment = Alignment.CenterHorizontally) {
              Spacer(modifier = Modifier.height(53.dp))
              TextInBox("E-mail: $email")
              Spacer(modifier = Modifier.height(35.dp))
              TextInBox(
                  "Username: $username",
                  Modifier.clickable { navigationActions.navigateTo(Screen.CHANGE_USERNAME) },
                  Icons.AutoMirrored.Filled.ArrowForward)
              Spacer(modifier = Modifier.height(35.dp))
              TextInBox(
                  "Change password",
                  Modifier.clickable { navigationActions.navigateTo(Screen.CHANGE_PASSWORD) },
                  Icons.AutoMirrored.Filled.ArrowForward)
              Spacer(modifier = Modifier.height(45.dp))
              SpotifyAuth(spotifyAuthViewModel)
              Spacer(modifier = Modifier.height(165.dp))
              PrincipalButton(
                  "Delete account",
                  "deleteAccountButton",
                  isRed = true,
                  onClick = { showDialog = true })
            }
      })

  if (showDialog) {
    AlertDialog(
        onDismissRequest = { showDialog = false }, // Close the dialog when dismissed
        title = { Text(text = "Delete Account", style = MaterialTheme.typography.titleLarge) },
        text = {
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Are you sure you want to delete your account? This action is irreversible.",
                style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Enter Password") },
                placeholder = { Text("Your current password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("passwordField"))
          }
        },
        confirmButton = {
          TextButton(
              modifier = Modifier.testTag("confirmButton"),
              onClick = {
                deleteAccount(
                    password = password,
                    allFriends = allFriends,
                    profileViewModel = profileViewModel,
                    mapUsersViewModel = mapUsersViewModel,
                    playlistViewModel = playlistViewModel,
                    firebaseAuthViewModel = firebaseAuthViewModel,
                    friendRequestViewModel = friendRequestViewModel,
                    navigationActions = navigationActions,
                    onDeletionFailed = { errorMessage ->
                      Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                      showDialog = false
                      password = ""
                    },
                    onDeletionSuccess = {
                      Toast.makeText(context, "Account deleted successfully", Toast.LENGTH_SHORT)
                          .show()
                      showDialog = false
                      password = ""
                    })
              }) {
                Text("Confirm")
              }
        },
        dismissButton = {
          TextButton(
              modifier = Modifier.testTag("cancelButton"), onClick = { showDialog = false }) {
                Text("Cancel")
              }
        })
  }
}

/**
 * Handles account deletion by deleting the user's profile, map user data, and Firebase account.
 *
 * @param password The user's password used for authentication before account deletion.
 * @param profileViewModel The ViewModel responsible for managing the user's profile.
 * @param mapUsersViewModel The ViewModel responsible for managing map-related user data.
 * @param firebaseAuthViewModel The ViewModel responsible for Firebase authentication operations.
 * @param navigationActions Used to navigate to different screens after the account deletion.
 * @param onDeletionFailed A callback executed when the account deletion fails.
 * @param onDeletionSuccess A callback executed when the account deletion succeeds.
 */
@OptIn(DelicateCoroutinesApi::class)
private fun deleteAccount(
    password: String,
    allFriends: List<String>,
    profileViewModel: ProfileViewModel,
    mapUsersViewModel: MapUsersViewModel,
    playlistViewModel: PlaylistViewModel,
    firebaseAuthViewModel: FirebaseAuthViewModel,
    friendRequestViewModel: FriendRequestViewModel,
    navigationActions: NavigationActions,
    onDeletionFailed: (String) -> Unit,
    onDeletionSuccess: () -> Unit
) {
  if (password.isEmpty()) {
    onDeletionFailed("Please enter your password")
    return
  }

  kotlinx.coroutines.GlobalScope.launch {
    try {
      // Step 1: Verify the password
      val verificationResult = firebaseAuthViewModel.verifyPassword(password)
      if (!verificationResult.isSuccess) {
        withContext(Dispatchers.Main) { onDeletionFailed("Incorrect password") }
        return@launch
      }

      // Step 2: Delete Firestore data

        val jobs = allFriends.map{ friend ->
            launch { friendRequestViewModel.removeFriend(friend) }
        }

        jobs.joinAll()


      val deleteProfileResult = profileViewModel.deleteProfile()
      val deleteMapUserResult = mapUsersViewModel.deleteMapUser()
      val deletePlaylistsResult = playlistViewModel.deleteOwnedPlaylists()

      if (!deleteProfileResult || !deleteMapUserResult || !deletePlaylistsResult) {
        withContext(Dispatchers.Main) { onDeletionFailed("Failed to delete all associated data.") }
        return@launch
      }

      // Step 3: Delete Firebase Authentication account
      firebaseAuthViewModel.deleteAccount(
          currentPassword = password,
          onSuccess = {
            navigationActions.navigateToAndClearAllBackStack(Screen.WELCOME)
            onDeletionSuccess()
          },
          onFailure = { error -> onDeletionFailed("Account deletion failed: ${error.message}") })
    } catch (e: Exception) {
      withContext(Dispatchers.Main) { onDeletionFailed("An error occurred: ${e.message}") }
    }
  }
}
