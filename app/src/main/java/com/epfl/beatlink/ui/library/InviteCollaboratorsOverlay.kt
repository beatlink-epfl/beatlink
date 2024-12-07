package com.epfl.beatlink.ui.library

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.ui.components.ReusableOverlay
import com.epfl.beatlink.ui.components.library.CollaboratorCard
import com.epfl.beatlink.ui.components.topAppBarModifier
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen.INVITE_COLLABORATORS

@Composable
fun InviteCollaboratorsOverlay(navigationActions: NavigationActions, onDismissRequest: () -> Unit) {
  val profilePicture = remember { mutableStateOf<Bitmap?>(null) }
  val list: List<String> = listOf()

  ReusableOverlay(
      onDismissRequest = onDismissRequest,
      modifier = Modifier.height(384.dp),
  ) {
    Column(
        verticalArrangement = Arrangement.Top,
        modifier =
            Modifier.fillMaxSize()
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(10.dp))) {
          Spacer(modifier = Modifier.height(15.dp))
          Row(
              modifier =
                  Modifier.fillMaxWidth()
                      .background(color = MaterialTheme.colorScheme.surfaceVariant)
                      .topAppBarModifier()) {
                Row(
                    modifier =
                        Modifier.testTag("searchBar")
                            .fillMaxSize()
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceContainer,
                                shape = RoundedCornerShape(5.dp))
                            .clickable { navigationActions.navigateTo(INVITE_COLLABORATORS) },
                    verticalAlignment = Alignment.CenterVertically) {
                      Icon(
                          imageVector = Icons.Default.Search,
                          contentDescription = "Search Icon",
                          tint = MaterialTheme.colorScheme.primaryContainer,
                          modifier =
                              Modifier.testTag("writableSearchBarIcon")
                                  .size(30.dp)
                                  .padding(start = 5.dp))
                      Text(
                          text = "Search friends to collaborate",
                          style = MaterialTheme.typography.bodyMedium,
                          color = MaterialTheme.colorScheme.primaryContainer)
                    }
              }
          LazyColumn(
              modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 10.dp),
              verticalArrangement = Arrangement.spacedBy(11.dp)) {
                items(list.size) { i ->
                  CollaboratorCard(
                      list[i], "hello", profilePicture, false, onAdd = {}, onRemove = {})
                }
              }
        }
  }
}
