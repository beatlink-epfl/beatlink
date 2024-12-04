package com.epfl.beatlink.ui.components.search

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.model.profile.ProfileData
import com.epfl.beatlink.ui.components.PrincipalButton
import com.epfl.beatlink.ui.components.ProfilePicture
import com.epfl.beatlink.ui.theme.TypographySongs
import com.epfl.beatlink.viewmodel.profile.ProfileViewModel

@Composable
fun PeopleItem(people: ProfileData?, profileViewModel: ProfileViewModel) {
  val profilePicture = remember { mutableStateOf<Bitmap?>(null) }
  if (people != null) {
    profileViewModel.getUserIdByUsername(people.username) { uid ->
      if (uid == null) {
        return@getUserIdByUsername
      } else {
        profileViewModel.loadProfilePicture(uid) { profilePicture.value = it }
      }
    }
  } else {
    Log.d("PeopleItem", "profile data null")
  }
  Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.testTag("peopleItem")) {
    Box(modifier = Modifier.padding(16.dp).size(60.dp).clip(CircleShape).testTag("peopleImage")) {
      ProfilePicture(profilePicture)
    }
    Spacer(modifier = Modifier.size(10.dp))
    if (people != null) {
      Text(
          text = people.username,
          style = TypographySongs.titleLarge,
          modifier = Modifier.testTag("peopleUsername"))
    } else {
      Log.d("PeopleItem", "profile data null")
    }
    Spacer(modifier = Modifier.weight(1f))
    PrincipalButton(buttonText = "Link", buttonTag = "peopleLink", width = 88.dp, height = 35.dp) {}
  }
}
