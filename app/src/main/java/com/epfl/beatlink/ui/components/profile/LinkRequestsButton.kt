package com.epfl.beatlink.ui.components.profile

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.R
import com.epfl.beatlink.ui.theme.secondaryGray
import com.google.android.material.circularreveal.CircularRevealGridLayout
import com.google.maps.android.compose.Circle


@Composable
fun LinkRequestsButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp)
            .border(width = 1.dp,
                color = MaterialTheme.colorScheme.secondaryGray,
                shape = RoundedCornerShape(size = 10.dp))
            .clickable(onClick = onClick)
        .padding(horizontal = 16.dp)
            .testTag("linkRequestsButton"),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(55.dp)
                .clip(CircleShape)
                .border(width = 1.dp,
                    color = MaterialTheme.colorScheme.secondaryGray,
                    shape = CircleShape)
                .padding(8.dp)
                .align(Alignment.CenterVertically),
            contentAlignment = Alignment.Center
        ) {
            Icon(painter = painterResource(R.drawable.link_requests),
                contentDescription = "friend requests",
                modifier = Modifier.size(30.dp),
                tint = Color.Unspecified)
        }

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
            Text(
                text = "Link Requests",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "See Sent and Received Friend Requests",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )

        }
    }
}