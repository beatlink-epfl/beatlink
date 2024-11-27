package com.epfl.beatlink.ui.library

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.ui.components.AddButton
import com.epfl.beatlink.ui.components.ProfilePicture
import com.epfl.beatlink.ui.components.library.PlaylistCard
import com.epfl.beatlink.ui.components.topAppBarModifier
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen.INVITE_COLLABORATORS
import com.epfl.beatlink.ui.theme.LightGray
import com.epfl.beatlink.ui.theme.PrimaryGradientBrush
import com.epfl.beatlink.ui.theme.PrimaryGray


@Composable
fun InviteCollaboratorsOverlay(navigationActions: NavigationActions,
                                onDismissRequest: () -> Unit,
                               onAddCollaborator: (String) -> Unit) {
    val profilePicture = remember { mutableStateOf<Bitmap?>(null) }
    val list = listOf("Alice", "Morgane")
    // Overlay background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f)) // Semi-transparent background
            .pointerInput(Unit) {
                detectTapGestures(onTap = { onDismissRequest() }) // Close overlay when tapping outside
            },
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier =
            Modifier
                .size(384.dp)
                .padding(bottom = 11.dp)
                .border(
                width = 2.dp,
                brush = PrimaryGradientBrush,
                shape = RoundedCornerShape(10.dp)
            ).pointerInput(Unit) {
                    detectTapGestures(onTap = { /* Do nothing, block propagation */ })
                },
            contentAlignment = Alignment.Center)
        {
            Column(
                verticalArrangement = Arrangement.Top,
                modifier = Modifier.fillMaxSize()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(10.dp))) {

                Spacer(modifier = Modifier.height(15.dp))
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.background)
                    .topAppBarModifier()

                    ) {
                    Row(modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .background(color = LightGray, shape = RoundedCornerShape(5.dp))
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
                        Text(text = "Search friends to collaborate",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primaryContainer)
                    }

                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(11.dp)) {
                    items(list.size) { i ->
                        CollaboratorCard(list[i], "hello", profilePicture)
                    }
                }
            }
        }
    }
}


@Composable
fun CollaboratorCard(name: String?, username: String, profilePicture: MutableState<Bitmap?>) {

    Card(
        modifier = Modifier.fillMaxWidth().height(67.dp)
            .border(width = 2.dp, brush = PrimaryGradientBrush, shape = RoundedCornerShape(5.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically) {
            ProfilePicture(profilePicture, 55.dp)
            Column(modifier = Modifier.padding(start = 14.dp)) {
                Text(
                    text = name ?: "",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "@$username".uppercase(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primaryContainer
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            AddButton { }
        }
    }

}