package com.android.sample.ui.authentication // Adjust the package according to your structure

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.ui.theme.PrimaryGradientBrush
import com.android.sample.ui.theme.PrimaryPurple
import com.android.sample.ui.theme.PrimaryRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthTopAppBar(navigationAction: () -> Unit) {
  TopAppBar(
      title = {
        Box(
            modifier = Modifier.fillMaxWidth().padding(end = 36.dp),
            contentAlignment = Alignment.Center) {
              Text(
                  modifier = Modifier.testTag("appName"),
                  text =
                      buildAnnotatedString {
                        append("Beat")
                        withStyle(style = androidx.compose.ui.text.SpanStyle(color = PrimaryRed)) {
                          append("Link")
                        }
                      },
                  style =
                      TextStyle(
                          fontSize = 20.sp,
                          fontFamily = FontFamily(Font(R.font.roboto)),
                          fontWeight = FontWeight(700),
                          color = PrimaryPurple,
                          letterSpacing = 0.2.sp,
                          textAlign = TextAlign.Center))
            }
      },
      navigationIcon = {
        IconButton(onClick = navigationAction, modifier = Modifier.testTag("goBackButton")) {
          Icon(
              modifier =
                  Modifier.size(30.dp).graphicsLayer(alpha = 0.99f).drawWithCache {
                    onDrawWithContent {
                      drawContent()
                      drawRect(PrimaryGradientBrush, blendMode = BlendMode.SrcAtop)
                    }
                  },
              imageVector = Icons.Filled.ArrowBack,
              contentDescription = "Go back")
        }
      })
}
