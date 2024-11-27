package com.epfl.beatlink.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.epfl.beatlink.R
import com.epfl.beatlink.ui.components.CornerIcons
import com.epfl.beatlink.ui.components.PageTitle
import com.epfl.beatlink.ui.components.ScreenTopAppBar
import com.epfl.beatlink.ui.components.topAppBarModifier
import com.epfl.beatlink.ui.navigation.NavigationActions
import com.epfl.beatlink.ui.navigation.Screen
import com.epfl.beatlink.ui.search.components.FullSearchBar
import com.epfl.beatlink.ui.theme.LightGray
import com.epfl.beatlink.ui.theme.PrimaryGray


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InviteCollaboratorsScreen(navigationActions: NavigationActions) {
    Scaffold(
        topBar = {
            FullSearchBar(navigationActions)
        },
        bottomBar = {},
        content = { p ->

        })


}