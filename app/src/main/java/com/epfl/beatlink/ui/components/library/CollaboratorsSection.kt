package com.epfl.beatlink.ui.components.library

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.ui.components.AddButton
import com.epfl.beatlink.ui.components.CheckButton
import com.epfl.beatlink.ui.components.CloseButton
import com.epfl.beatlink.ui.components.ProfilePicture
import com.epfl.beatlink.ui.navigation.AppIcons.collabAdd
import com.epfl.beatlink.ui.theme.PrimaryGradientBrush
import com.epfl.beatlink.ui.theme.PrimaryGray
import com.epfl.beatlink.ui.theme.primaryGray

@Composable
fun CollaboratorsSection(
    collabUsernames: List<String>,
    onClick: () -> Unit,
    onRemove: (String) -> Unit
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
        CollabButton { onClick() }
      }
  CollabList(collaborators = collabUsernames, onRemove)
  Spacer(modifier = Modifier.height(10.dp))
}

@Composable
fun CollabButton(onClick: () -> Unit) {
  Row(
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
      modifier =
          Modifier.border(
                  width = 1.dp,
                  brush = PrimaryGradientBrush,
                  shape = RoundedCornerShape(size = 20.dp))
              .width(185.dp)
              .height(28.dp)
              .clickable { onClick() }
              .semantics { contentDescription = "Invite Collaborators" }
              .background(
                  color = MaterialTheme.colorScheme.surfaceVariant,
                  shape = RoundedCornerShape(size = 20.dp))
              .padding(start = 16.dp, end = 16.dp)
              .testTag("collabButton")) {
        Text(
            text = "Invite Collaborators",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.CenterVertically))
        Icon(
            imageVector = collabAdd,
            contentDescription = "Collab Add",
            tint = Color.Unspecified,
            modifier = Modifier.align(Alignment.CenterVertically))
      }
}

@Composable
fun CollabList(collaborators: List<String>, onRemove: (String) -> Unit) {
  Box(
      modifier =
          Modifier.border(
                  width = 1.dp,
                  color = MaterialTheme.colorScheme.primary,
                  shape = RoundedCornerShape(size = 5.dp))
              .width(320.dp)
              .height(120.dp)
              .background(
                  color = MaterialTheme.colorScheme.surfaceVariant,
                  shape = RoundedCornerShape(size = 5.dp))
              .testTag("collabBox")) {
        if (collaborators.isEmpty()) {
          Text(
              text = "NO COLLABORATORS",
              color = PrimaryGray,
              style = MaterialTheme.typography.bodyLarge,
              modifier = Modifier.align(Alignment.Center).testTag("emptyCollab"))
        } else {
          LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(collaborators.size) { i ->
              MiniCollabCard(collaborators[i]) { onRemove(collaborators[i]) }
            }
          }
        }
      }
}

/** Collaborator card in the Collaborator List with cross button */
@Composable
fun MiniCollabCard(username: String, onRemove: () -> Unit) {
  Card(
      modifier = Modifier.fillMaxWidth().testTag("collabCard"),
      shape = RoundedCornerShape(size = 5.dp),
  ) {
    Row(
        modifier =
            Modifier.fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween) {
          Text(
              text = "@${username}",
              color = MaterialTheme.colorScheme.primaryGray,
              style = MaterialTheme.typography.bodyLarge)
          CloseButton { onRemove() }
        }
  }
}

/** Collaborator card in the collaborator search with add/check button */
@Composable
fun CollaboratorCard(
    name: String?,
    username: String,
    profilePicture: MutableState<Bitmap?>,
    isCollaborator: Boolean,
    onAdd: () -> Unit,
    onRemove: () -> Unit
) {

  Card(
      modifier =
          Modifier.fillMaxWidth()
              .height(67.dp)
              .border(width = 2.dp, brush = PrimaryGradientBrush, shape = RoundedCornerShape(5.dp)),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically) {
              ProfilePicture(profilePicture, 55.dp)
              Column(modifier = Modifier.padding(start = 14.dp)) {
                Text(
                    text = name ?: "",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary)
                Text(
                    text = "@$username".uppercase(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primaryContainer)
              }
              Spacer(modifier = Modifier.weight(1f))
              if (isCollaborator) {
                CheckButton { onRemove() }
              } else {
                AddButton { onAdd() }
              }
            }
      }
}
