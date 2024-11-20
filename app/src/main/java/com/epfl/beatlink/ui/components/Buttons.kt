package com.epfl.beatlink.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.ui.theme.PrimaryGradientBrush
import com.epfl.beatlink.ui.theme.primaryWhite

/** Principal Button filled with Gradient color */
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

/** Button for viewing the description of a playlist */
@Composable
fun ViewDescriptionButton(onClick: () -> Unit) {
  Row(
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
      modifier =
          Modifier.border(
                  width = 1.dp,
                  color = MaterialTheme.colorScheme.primary,
                  shape = RoundedCornerShape(size = 20.dp))
              .width(140.dp)
              .height(24.dp)
              .clickable { onClick() }
              .semantics { contentDescription = "View Description" }
              .background(
                  color = MaterialTheme.colorScheme.surfaceVariant,
                  shape = RoundedCornerShape(size = 20.dp))
              .padding(start = 16.dp, end = 16.dp)
              .testTag("viewDescriptionButton")) {
        Text(
            text = "View Description",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.CenterVertically))
      }
}

@Composable
fun VoteButton(gradient: Brush, color: Color, painter: Painter) {
  var nbVote by remember { mutableIntStateOf(0) }
  var isVoted by remember { mutableStateOf(false) }

  Box(
      modifier =
          Modifier.border(width = 2.dp, brush = gradient, shape = RoundedCornerShape(30.dp))
              .width(78.dp)
              .height(30.dp)
              .clip(RoundedCornerShape(30.dp))
              .background(
                  brush =
                      if (isVoted) gradient
                      else Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent)))
              .clickable { // Handle click directly
                if (isVoted) {
                  nbVote-- // Decrease vote count
                  isVoted = false
                } else {
                  nbVote++ // Increase vote count
                  isVoted = true
                }
              },
  ) {
    Row(
        modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.Start, // Space them out horizontally
        verticalAlignment = Alignment.CenterVertically) {
          Icon(painter = painter, contentDescription = "fire", modifier = Modifier.size(24.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
              text = nbVote.toString(),
              color = if (isVoted) MaterialTheme.colorScheme.primaryWhite else color,
              modifier = Modifier.testTag("nbVote"))
        }
  }
}
