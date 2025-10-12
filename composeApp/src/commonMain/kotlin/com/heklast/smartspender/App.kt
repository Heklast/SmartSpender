package org.smartspender.project

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.smartspender.project.feature.home.BeginScreen

import org.smartspender.project.features.Intro.IntroScreen
import org.smartspender.project.navigation.AppState
import org.smartspender.project.navigation.Route

import smartspender.composeapp.generated.resources.Res
import smartspender.composeapp.generated.resources.compose_multiplatform

@Composable
@Preview
fun App() {
    val appState = remember { AppState() }
    val route by appState.route.collectAsState()

    MaterialTheme {
        Surface {
    when (route) {
        Route.Intro -> IntroScreen(onTimeout = { appState.navigate(Route.Begin) })
        Route.Begin -> BeginScreen()
    }        }
    }
}
