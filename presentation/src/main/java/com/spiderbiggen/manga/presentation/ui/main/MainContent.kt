package com.spiderbiggen.manga.presentation.ui.main

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.spiderbiggen.manga.presentation.theme.MangaReaderTheme
import com.spiderbiggen.manga.presentation.ui.manga.list.navigation.MangaListRoute
import com.spiderbiggen.manga.presentation.ui.manga.navigation.manga
import com.spiderbiggen.manga.presentation.ui.profile.navigation.profile
import com.spiderbiggen.manga.presentation.ui.profile.state.ProfileState
import com.spiderbiggen.manga.presentation.ui.profile.state.ProfileViewModel
import org.koin.androidx.compose.koinViewModel

val LocalAppVersion = staticCompositionLocalOf { "" }

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MainContent() {
    val snackbarHostState = remember { SnackbarHostState() }

    val profileViewModel: ProfileViewModel = koinViewModel()
    val profileState = profileViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(profileViewModel, snackbarHostState) {
        profileViewModel.snackbarFlow.collect {
            snackbarHostState.showSnackbar(it)
        }
    }

    // TrackNavigationSideEffect(navController)
    MangaReaderTheme {
        MangaNavHost(
            snackbarHostState = snackbarHostState,
            profileState = profileState,
        )
        StatusBarProtection()
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun MangaNavHost(snackbarHostState: SnackbarHostState, profileState: State<ProfileState>) {
    val backStack = rememberNavBackStack(MangaListRoute)
    val animationSpec = MaterialTheme.motionScheme.slowSpatialSpec<IntOffset>()
    val floatAnimationSpec = MaterialTheme.motionScheme.slowSpatialSpec<Float>()
    NavDisplay(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        entryDecorators = listOf(
            // Add the default decorators for managing scenes and saving state
            rememberSaveableStateHolderNavEntryDecorator(),
            // Then add the view model store decorator
            rememberViewModelStoreNavEntryDecorator(),
        ),
        backStack = backStack,
        transitionSpec = {
            slideInHorizontally(animationSpec) { it } togetherWith slideOutHorizontally(animationSpec) { -it / 2 }
        },
        popTransitionSpec = {
            slideInHorizontally(animationSpec) { -it / 2 } togetherWith slideOutHorizontally(animationSpec) { it }
        },
        predictivePopTransitionSpec = {
            slideInHorizontally(animationSpec) { -it / 2 } togetherWith slideOutHorizontally(animationSpec) { it }
        },
        entryProvider = entryProvider {
            manga(
                backStack = backStack,
                profileState = profileState,
                snackbarHostState = snackbarHostState,
                floatAnimationSpec = floatAnimationSpec,
            )

            profile(
                backStack = backStack,
                snackbarHostState = snackbarHostState,
            )
        },
    )
}

@Composable
private fun StatusBarProtection(
    color: Color = MaterialTheme.colorScheme.background,
    heightProvider: () -> Float = calculateGradientHeight(),
) {
    Canvas(Modifier.fillMaxSize()) {
        val calculatedHeight = heightProvider()
        val gradient = Brush.verticalGradient(
            colors = listOf(
                color.copy(alpha = 1f),
                Color.Transparent,
            ),
            startY = 0f,
            endY = calculatedHeight,
        )
        drawRect(
            brush = gradient,
            size = Size(size.width, calculatedHeight),
        )
    }
}

@Composable
fun calculateGradientHeight(): () -> Float {
    val statusBars = WindowInsets.statusBars
    val density = LocalDensity.current
    return { statusBars.getTop(density).toFloat() }
}
