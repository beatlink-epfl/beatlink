package com.epfl.beatlink.ui.components.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.model.profile.MusicGenre.Companion.MAX_SELECTABLE_GENRES
import com.epfl.beatlink.ui.theme.PrimaryGradientBrush

@Composable
fun SelectFavoriteMusicGenres(onGenreSelectionVisibilityChanged: (Boolean) -> Unit) {
  Box(
      modifier =
          Modifier.clickable { onGenreSelectionVisibilityChanged(true) }
              .padding(16.dp)
              .border(
                  width = 1.dp,
                  color = MaterialTheme.colorScheme.primary,
                  shape = RoundedCornerShape(10.dp))
              .height(52.dp)
              .padding(16.dp),
      contentAlignment = Alignment.Center) {
        Text(
            text = "Select your favorite music genres",
            modifier = Modifier.testTag("selectFavoriteGenresText"),
            style = MaterialTheme.typography.bodyLarge)
      }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicGenreSelectionDialog(
    musicGenres: List<String>,
    selectedGenres: MutableList<String>,
    onDismissRequest: () -> Unit,
    onGenresSelected: (MutableList<String>) -> Unit
) {
  val genreCheckedStates = remember { mutableStateMapOf<String, Boolean>() }

  // Initialize genre states
  musicGenres.forEach { genre ->
    genreCheckedStates.putIfAbsent(genre, selectedGenres.contains(genre))
  }

  // Track number of selected genres
  val selectedCount = genreCheckedStates.filter { it.value }.size

  // Dialog for selecting user's favorite music genres
  androidx.compose.ui.window.Dialog(onDismissRequest = onDismissRequest) {
    Surface(
        modifier = Modifier.fillMaxHeight(0.8F),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp) {
          Scaffold(
              topBar = { TopAppBar(title = { MusicGenreSelectionDialogTitle() }) },
              bottomBar = {
                Column(
                    modifier =
                        Modifier.background(MaterialTheme.colorScheme.surface)
                            .padding(bottom = 16.dp)) {
                      // Show message when the set limit of music genres are selected
                      if (selectedCount >= MAX_SELECTABLE_GENRES) {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            contentAlignment = Alignment.Center) {
                              Text(
                                  text = "You can select up to $MAX_SELECTABLE_GENRES genres only",
                                  textAlign = TextAlign.Center,
                                  color = MaterialTheme.colorScheme.error,
                                  style = MaterialTheme.typography.bodyMedium)
                            }
                      }
                      MusicGenreSelectionDialogButtons(
                          onDismissRequest = onDismissRequest,
                          onGenresSelected = {
                            onGenresSelected(
                                genreCheckedStates.filter { it.value }.keys.toMutableList())
                          },
                          modifier = Modifier.testTag("dialogButtonRow"))
                    }
              },
              content = { padding ->
                Box(
                    modifier =
                        Modifier.fillMaxWidth()
                            .background(color = MaterialTheme.colorScheme.surface)
                            .padding(padding)
                            .verticalScroll(rememberScrollState())
                            .testTag("musicGenreSelectionDialog")) {
                      Column(modifier = Modifier.fillMaxWidth().padding(bottom = 72.dp)) {

                        // Loop through each genre and create a checkbox with a label
                        musicGenres.forEach { genre ->
                          val isChecked = genreCheckedStates[genre] ?: false
                          val isDisabled = selectedCount >= MAX_SELECTABLE_GENRES && !isChecked

                          MusicGenreCheckbox(
                              musicGenre = genre,
                              checkedState = isChecked,
                              isDisabled = isDisabled,
                              onCheckedChange = { newCheckedState ->
                                if (newCheckedState) {
                                  if (selectedCount < MAX_SELECTABLE_GENRES) {
                                    genreCheckedStates[genre] = true
                                  }
                                } else {
                                  genreCheckedStates[genre] = false
                                }
                              })
                        }
                      }
                    }
              })
        }
  }
}

@Composable
fun MusicGenreSelectionDialogTitle() {
  Column {
    Text(
        text = "MUSIC GENRES",
        style = MaterialTheme.typography.headlineLarge,
        modifier =
            Modifier.graphicsLayer(alpha = 0.99f)
                .drawWithCache {
                  onDrawWithContent {
                    drawContent()
                    drawRect(PrimaryGradientBrush, blendMode = BlendMode.SrcAtop)
                  }
                }
                .testTag("dialogTitle"))
  }
}

// Music Genre checkbox --> Checkbox + Label
@Composable
fun MusicGenreCheckbox(
    musicGenre: String,
    checkedState: Boolean,
    isDisabled: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
  Row(
      Modifier.fillMaxWidth()
          .height(56.dp)
          .toggleable(value = checkedState, onValueChange = onCheckedChange, role = Role.Checkbox)
          .padding(horizontal = 16.dp),
      verticalAlignment = Alignment.CenterVertically) {

        // Checkbox
        Checkbox(
            checked = checkedState,
            onCheckedChange = { if (!isDisabled) onCheckedChange(!checkedState) },
            colors =
                CheckboxDefaults.colors(
                    uncheckedColor = MaterialTheme.colorScheme.primary,
                    checkmarkColor = MaterialTheme.colorScheme.surface,
                    checkedColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.testTag("genreCheckbox_${musicGenre.replace(" ", "_")}"))

        // Music genre text
        Text(
            text = musicGenre,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyMedium,
            modifier =
                Modifier.padding(start = 16.dp)
                    .testTag("genreCheckboxLabel_${musicGenre.replace(" ", "_")}")
                    .alpha(if (isDisabled) 0.5f else 1f))
      }
}

@Composable
fun MusicGenreSelectionDialogButtons(
    onDismissRequest: () -> Unit,
    onGenresSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
  Row(modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(top = 16.dp)) {
    // Spacer to push the buttons to the right
    Spacer(modifier = Modifier.weight(1f))

    // Cancel Button
    Text(
        text = "CANCEL",
        modifier = Modifier.clickable { onDismissRequest() }.testTag("cancelButton"),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.primary)

    // Ok Button
    Text(
        text = "OK",
        modifier =
            Modifier.clickable { onGenresSelected() }
                .padding(start = 32.dp, end = 8.dp)
                .testTag("okButton"),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.primary)
  }
}
