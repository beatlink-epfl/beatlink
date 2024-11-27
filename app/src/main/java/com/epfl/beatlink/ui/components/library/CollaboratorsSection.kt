package com.epfl.beatlink.ui.components.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.ui.components.CollabButton
import com.epfl.beatlink.ui.components.CollabList
import com.epfl.beatlink.ui.library.InviteCollaboratorsOverlay
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen.INVITE_COLLABORATORS

@Composable
fun CollaboratorsSection(navigationActions: NavigationActions,
                         playlistCollab: List<String>
) {
  Spacer(Modifier.height(5.dp))
  Row(
      verticalAlignment = Alignment.CenterVertically, // Center items vertically
      horizontalArrangement = Arrangement.SpaceBetween, // Space items apart
      modifier = Modifier.width(320.dp)) {
        Text(
            text = "Collaborators",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.testTag("collaboratorsTitle"))
        CollabButton {
        }
      }
  CollabList(playlistCollab)
  Spacer(modifier = Modifier.height(10.dp))

}
