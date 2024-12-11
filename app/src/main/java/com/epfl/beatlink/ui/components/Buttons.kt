package com.epfl.beatlink.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.model.library.PlaylistTrack
import com.epfl.beatlink.ui.theme.PositiveGradientBrush
import com.epfl.beatlink.ui.theme.PrimaryGradientBrush
import com.epfl.beatlink.ui.theme.primaryRed
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
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically,
      modifier =
          Modifier.border(
                  width = 1.dp,
                  color = MaterialTheme.colorScheme.primary,
                  shape = RoundedCornerShape(size = 20.dp))
              .width(150.dp)
              .height(30.dp)
              .clickable { onClick() }
              .semantics { contentDescription = "View Description" }
              .background(
                  color = MaterialTheme.colorScheme.surfaceVariant,
                  shape = RoundedCornerShape(size = 20.dp))
              .padding(start = 16.dp, end = 16.dp)
              .testTag("viewDescriptionButton")) {
        Text(
            text = "View Description",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier =
                Modifier.align(Alignment.CenterVertically)
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp))
      }
}

@Composable
fun VoteButton(
    painter: Painter,
    playlistTrack: PlaylistTrack,
    userId: String, // Pass the current user's ID
    onVoteChanged: (String, Boolean) -> Unit // Pass track ID and vote status
) {
  // Check if the current user has already voted
  val isVoted = playlistTrack.likedBy.contains(userId) // No need for local state

  Box(
      modifier =
          Modifier.border(
                  width = 2.dp, brush = PositiveGradientBrush, shape = RoundedCornerShape(30.dp))
              .width(78.dp)
              .height(30.dp)
              .clip(RoundedCornerShape(30.dp))
              .background(
                  brush =
                      if (isVoted) PositiveGradientBrush
                      else Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent)))
              .clickable {
                // Notify the ViewModel about the vote change
                onVoteChanged(playlistTrack.track.trackId, !isVoted)
              }
              .testTag("voteButton")) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically) {
              Icon(
                  painter = painter,
                  contentDescription = "fire",
                  tint =
                      if (isVoted) MaterialTheme.colorScheme.primaryWhite
                      else MaterialTheme.colorScheme.primaryRed,
                  modifier = Modifier.size(24.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                  text = playlistTrack.likes.toString(),
                  color =
                      if (isVoted) MaterialTheme.colorScheme.primaryWhite
                      else MaterialTheme.colorScheme.primaryRed,
                  modifier = Modifier.testTag("nbVote"))
            }
      }
}

@Composable
fun LinkButton(
    buttonText: String,
    onClickLink: () -> Unit = {},
    onClickRequested: () -> Unit = {},
    onClickAccept: () -> Unit = {},
    onClickLinked: () -> Unit = {}
) {
  when (buttonText) {
    "Link" -> {
      // "Link" Button
      Box(
          modifier =
              Modifier.border(
                      width = 2.dp, brush = PrimaryGradientBrush, shape = RoundedCornerShape(30.dp))
                  .background(brush = PrimaryGradientBrush, shape = RoundedCornerShape(30.dp))
                  .width(100.dp)
                  .height(41.dp),
          contentAlignment = Alignment.Center) {
            Button(
                onClick = onClickLink,
                modifier = Modifier.fillMaxSize().testTag("linkedButton"),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent, contentColor = Color.White),
                shape = RoundedCornerShape(30.dp),
                elevation = null) {
                  Text(text = buttonText, style = MaterialTheme.typography.labelLarge)
                }
          }
    }
    "Requested" -> {
      // "Requested" Button
      Box(
          modifier =
              Modifier.border(
                      width = 2.dp, brush = PrimaryGradientBrush, shape = RoundedCornerShape(30.dp))
                  .background(brush = PrimaryGradientBrush, shape = RoundedCornerShape(30.dp))
                  .width(120.dp)
                  .height(41.dp),
          contentAlignment = Alignment.Center) {
            Button(
                onClick = onClickRequested,
                modifier = Modifier.fillMaxSize().testTag("linkedButton"),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            Color.White.copy(alpha = 0.75f), // Transparent background with alpha
                    ),
                shape = RoundedCornerShape(30.dp),
                elevation = null) {
                  GradientText(text = buttonText, style = MaterialTheme.typography.labelLarge)
                }
          }
    }
    "Accept" -> {
      // "Accept" Button
      Box(
          modifier =
              Modifier.border(
                      width = 2.dp, brush = PrimaryGradientBrush, shape = RoundedCornerShape(30.dp))
                  .background(brush = PrimaryGradientBrush, shape = RoundedCornerShape(30.dp))
                  .width(120.dp)
                  .height(41.dp),
          contentAlignment = Alignment.Center) {
            Button(
                onClick = onClickAccept,
                modifier = Modifier.fillMaxSize().testTag("linkedButton"),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            Color.White.copy(alpha = 0.75f), // Transparent background with alpha
                    ),
                shape = RoundedCornerShape(30.dp),
                elevation = null) {
                  GradientText(text = buttonText, style = MaterialTheme.typography.labelLarge)
                }
          }
    }
    "Linked" -> {
      // "Linked" Button with border
      Box(
          modifier =
              Modifier.border(
                      width = 2.dp, brush = PrimaryGradientBrush, shape = RoundedCornerShape(30.dp))
                  .width(100.dp)
                  .height(41.dp),
          contentAlignment = Alignment.Center) {
            Button(
                onClick = onClickLinked,
                modifier = Modifier.fillMaxSize().testTag("linkedButton"),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(30.dp),
                elevation = null) {
                  GradientText(text = buttonText, style = MaterialTheme.typography.labelLarge)
                }
          }
    }
  }
}

@Composable
fun ProfileCardLinkButton(buttonText: String, onClick: () -> Unit) {
  val linkModifier =
      Modifier.border(width = 2.dp, brush = PrimaryGradientBrush, shape = RoundedCornerShape(30.dp))
          .background(brush = PrimaryGradientBrush, shape = RoundedCornerShape(30.dp))
          .width(100.dp)
          .height(40.dp)

  val acceptModifier =
      Modifier.border(width = 2.dp, brush = PrimaryGradientBrush, shape = RoundedCornerShape(30.dp))
          .background(brush = PrimaryGradientBrush, shape = RoundedCornerShape(30.dp))
          .width(120.dp)
          .height(40.dp)

  val linkedModifier =
      Modifier.border(width = 2.dp, brush = PrimaryGradientBrush, shape = RoundedCornerShape(30.dp))
          .width(100.dp)
          .height(40.dp)

  Box(
      modifier =
          when (buttonText) {
            "Linked" -> linkedModifier
            "Link" -> linkModifier
            else -> acceptModifier
          },
      contentAlignment = Alignment.Center) {
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxSize().testTag("linkedButton"),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor =
                        if (buttonText == "Requested" || buttonText == "Accept")
                            Color.White.copy(alpha = 0.75f)
                        else Color.Transparent,
                    contentColor =
                        if (buttonText == "Linked") MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.primaryWhite),
            shape = RoundedCornerShape(30.dp),
            elevation = null) {
              if (buttonText == "Link") {
                Text(
                    text = buttonText,
                    color = MaterialTheme.colorScheme.primaryWhite,
                    style = MaterialTheme.typography.bodyMedium)
              } else {
                GradientText(text = buttonText, style = MaterialTheme.typography.bodyMedium)
              }
            }
      }
}

@Composable
fun ProfileLinkButton(buttonText: String, onClick: () -> Unit) {
  val filledModifier =
      Modifier.border(width = 2.dp, brush = PrimaryGradientBrush, shape = RoundedCornerShape(30.dp))
          .background(brush = PrimaryGradientBrush, shape = RoundedCornerShape(30.dp))
          .width(233.dp)
          .height(34.dp)

  val outlinedModifier =
      Modifier.border(width = 2.dp, brush = PrimaryGradientBrush, shape = RoundedCornerShape(30.dp))
          .width(233.dp)
          .height(34.dp)

  Box(
      modifier = if (buttonText == "Linked") outlinedModifier else filledModifier,
      contentAlignment = Alignment.Center) {
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxSize().testTag("linkedButton"),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor =
                        if (buttonText == "Requested" || buttonText == "Accept")
                            Color.White.copy(alpha = 0.75f)
                        else Color.Transparent,
                    contentColor =
                        if (buttonText == "Linked") MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.primaryWhite),
            shape = RoundedCornerShape(30.dp),
            elevation = null) {
              if (buttonText == "Link") {
                Text(
                    text = buttonText,
                    color = MaterialTheme.colorScheme.primaryWhite,
                    style = MaterialTheme.typography.bodyMedium)
              } else {
                GradientText(text = buttonText, style = MaterialTheme.typography.bodyMedium)
              }
            }
      }
}

@Composable
fun EditProfileButton(onClick: () -> Unit) {
  Box(
      modifier =
          Modifier.border(
                  width = 2.dp, brush = PrimaryGradientBrush, shape = RoundedCornerShape(30.dp))
              .width(233.dp)
              .height(34.dp)
              .testTag("editProfileButton"),
      contentAlignment = Alignment.Center) {
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxSize(),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(30.dp),
            elevation = null) {
              Text(text = "Edit Profile", style = MaterialTheme.typography.bodyMedium)
            }
      }
}
