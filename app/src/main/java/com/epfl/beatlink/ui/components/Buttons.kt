package com.epfl.beatlink.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.ui.navigation.collabAdd
import com.epfl.beatlink.ui.theme.PrimaryGradientBrush


/**
 * Principal Button filled with Gradient color
 */
@Composable
fun FilledButton(buttonText: String, buttonTag: String, onClick: () -> Unit) {
    Box(
        modifier =
        Modifier.width(320.dp)
            .height(48.dp)
            .background(brush = PrimaryGradientBrush, shape = RoundedCornerShape(30.dp)),
        contentAlignment = Alignment.Center) {
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxSize().testTag(buttonTag),
            colors =
            ButtonDefaults.buttonColors(
                containerColor = Color.Transparent, contentColor = Color.White),
            shape = RoundedCornerShape(30.dp),
            elevation = null) {
            Text(text = buttonText, style = MaterialTheme.typography.labelLarge)
        }
    }
}

/**
 * Button for viewing the description of a playlist
 */
@Composable
fun ViewDescriptionButton(onClick: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier =
        Modifier.border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(size = 20.dp)
        )
            .width(140.dp)
            .height(24.dp)
            .clickable { onClick() }
            .semantics { contentDescription = "View Description" }
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(size = 20.dp)
            )
            .padding(start = 16.dp, end = 16.dp)
            .testTag("collabButton")) {
        Text(
            text = "View Description",
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