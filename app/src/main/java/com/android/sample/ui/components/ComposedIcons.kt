package com.android.sample.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.android.sample.R

@Composable
fun CombinedPartyIcon() {
  Box(contentAlignment = Alignment.Center, modifier = Modifier.testTag("partyIcon")) {
    Icon(
        painter = painterResource(id = R.drawable.ellipse_icon),
        contentDescription = "Icon Part 1",
        tint = Color.Unspecified,
        modifier = Modifier.size(28.dp).testTag("ellipsePart"))
    Icon(
        painter = painterResource(id = R.drawable.group_fill_icon),
        contentDescription = "Icon Part 2",
        tint = Color.Unspecified,
        modifier = Modifier.size(18.dp).testTag("groupPart"))
  }
}
