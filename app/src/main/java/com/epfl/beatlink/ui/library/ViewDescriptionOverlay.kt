package com.epfl.beatlink.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.ui.components.CloseButton
import com.epfl.beatlink.ui.components.ReusableOverlay
import com.epfl.beatlink.ui.theme.PrimaryGradientBrush

@Composable
fun ViewDescriptionOverlay(onDismissRequest: () -> Unit, description: String) {

  ReusableOverlay(
      onDismissRequest = onDismissRequest, modifier = Modifier.heightIn(min = 0.dp, max = 300.dp)) {
        Column(
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier =
                Modifier.background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(10.dp))) {
              Row(
                  modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Description",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.testTag("descriptionTitle"))

                    CloseButton { onDismissRequest() }
                  }
              Box(
                  modifier =
                      Modifier.width(356.dp)
                          .heightIn(min = 0.dp, max = 250.dp)
                          .padding(bottom = 11.dp)
                          .border(
                              width = 2.dp,
                              brush = PrimaryGradientBrush,
                              shape = RoundedCornerShape(10.dp))
                          .verticalScroll(rememberScrollState())) {
                    Text(
                        text = description,
                        modifier = Modifier.padding(16.dp).testTag("description"),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyLarge)
                  }
            }
      }
}
