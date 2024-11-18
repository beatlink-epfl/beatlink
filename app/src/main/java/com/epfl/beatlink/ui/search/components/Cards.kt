package com.epfl.beatlink.ui.search.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.epfl.beatlink.R
import com.epfl.beatlink.model.spotify.objects.SpotifyTrack
import com.epfl.beatlink.model.profile.ProfileData
import com.epfl.beatlink.ui.theme.PrimaryGradientBrush
import com.epfl.beatlink.ui.theme.PrimaryPurple
import com.epfl.beatlink.ui.theme.lightThemeBackground

@Composable
fun ProfileCard(profile: ProfileData) {
  Card(modifier = Modifier.testTag("profileCardItem").width(65.dp)) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            Modifier.testTag("profileCardColumn")
                .fillMaxSize()
                .background(color = lightThemeBackground)) {
          Image(
              painter = painterResource(id = R.drawable.default_profile_picture),
              contentDescription = null,
              modifier = Modifier.testTag("profileCardImage").fillMaxWidth().height(65.dp))

          Text(
              text = profile.name ?: "",
              style =
                  TextStyle(
                      fontSize = 10.sp,
                      lineHeight = 20.sp,
                      fontFamily = FontFamily(Font(R.font.roboto)),
                      fontWeight = FontWeight(700),
                      color = PrimaryPurple,
                      textAlign = TextAlign.Center,
                      letterSpacing = 0.1.sp,
                  ),
              modifier = Modifier.testTag("profileCardName").background(color = Color.White))

          Text(
              text = profile.username,
              style =
                  TextStyle(
                      fontSize = 8.sp,
                      lineHeight = 20.sp,
                      fontFamily = FontFamily(Font(R.font.roboto)),
                      fontWeight = FontWeight(400),
                      color = PrimaryPurple,
                      textAlign = TextAlign.Center,
                      letterSpacing = 0.08.sp,
                  ),
              modifier = Modifier.testTag("profileCardUsername"))
        }
  }
}

@Composable
fun PartyCard(title: String, username: String, description: String) {
  Card(
      modifier =
          Modifier.testTag("partyCardItem")
              .width(200.dp)
              .height(84.dp)
              .border(1.dp, PrimaryGradientBrush, RoundedCornerShape(10.dp)),
      shape = RoundedCornerShape(8.dp),
  ) {
    Column(
        modifier =
            Modifier.testTag("partyCardColumn")
                .fillMaxSize()
                .background(color = lightThemeBackground)
                .padding(12.dp)) {
          // First Row: Icon + Title and Username
          Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.testTag("partyCardRow").fillMaxWidth()) {
                // Icon on the left
                CombinedPartyIcon()

                // Title and Username
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier =
                        Modifier.testTag("partyCardTextColumn")
                            .height(28.dp) // Match icon's height
                            .padding(horizontal = 6.dp)) {
                      Text(
                          text = title,
                          fontSize = 14.sp,
                          fontWeight = FontWeight.Bold,
                          maxLines = 1,
                          modifier = Modifier.testTag("partyCardTitle"))
                      Text(
                          text = username,
                          fontSize = 10.sp,
                          color = Color.Gray,
                          maxLines = 1,
                          modifier = Modifier.testTag("partyCardUsername"))
                    }
              }

          // Second Row: Description
          Text(
              text = description,
              fontSize = 10.sp,
              color = Color.DarkGray,
              maxLines = 2,
              modifier =
                  Modifier.testTag("partyCardDescription").fillMaxWidth().padding(top = 5.dp))
        }
  }
}

@Composable
fun SongCard(song: SpotifyTrack) {
  Card(
      modifier = Modifier.testTag(song.name + "songCardItem").width(65.dp).height(85.dp),
      shape = RoundedCornerShape(1.dp)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier =
                Modifier.testTag(song.name + "songCardColumn")
                    .fillMaxSize()
                    .background(color = lightThemeBackground)) {
              Card(
                  modifier = Modifier.testTag(song.name + "songCardContainer").size(65.dp),
                  shape = RoundedCornerShape(5.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.cover_test1),
                        contentDescription = null, // Provide a description for accessibility
                        modifier =
                            Modifier.testTag(song.name + "songCardImage")
                                .fillMaxSize() // Make image fill the card
                        )
                  }

              Box(
                  modifier =
                      Modifier.testTag(song.name + "songCardTextBox")
                          .width(53.dp)
                          .height(20.dp)
                          .background(color = lightThemeBackground)) {
                    Column(
                        modifier = Modifier.testTag(song.name + "songCardTextColumn").fillMaxSize(),
                        verticalArrangement = Arrangement.Center, // Center vertically
                        horizontalAlignment = Alignment.CenterHorizontally // Center horizontally
                        ) {
                          Text(
                              text = song.name,
                              style =
                                  TextStyle(
                                      fontSize = 10.sp,
                                      lineHeight = 20.sp,
                                      fontFamily = FontFamily(Font(R.font.roboto)),
                                      fontWeight = FontWeight(400),
                                      color = PrimaryPurple,
                                      textAlign = TextAlign.Center,
                                      letterSpacing = 0.1.sp,
                                  ),
                              modifier = Modifier.testTag(song.name + "songCardText"))
                        }
                  }
            }
      }
}
