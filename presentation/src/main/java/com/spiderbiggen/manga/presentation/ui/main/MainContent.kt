package com.spiderbiggen.manga.presentation.ui.main

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.dropUnlessStarted
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import coil3.ImageLoader
import com.spiderbiggen.manga.presentation.components.TrackNavigationSideEffect
import com.spiderbiggen.manga.presentation.theme.MangaReaderTheme
import com.spiderbiggen.manga.presentation.ui.manga.chapter.overview.ChapterOverview
import com.spiderbiggen.manga.presentation.ui.manga.chapter.overview.ChapterViewModel
import com.spiderbiggen.manga.presentation.ui.manga.model.MangaRoutes
import com.spiderbiggen.manga.presentation.ui.manga.overview.MangaOverview
import com.spiderbiggen.manga.presentation.ui.manga.reader.ReadChapterScreen

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MainContent(coverImageLoader: ImageLoader, chapterImageLoader: ImageLoader) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    TrackNavigationSideEffect(navController)
    MangaReaderTheme {
        val animationSpec = MaterialTheme.motionScheme.slowSpatialSpec<Float>()
        NavHost(
            navController = navController,
            startDestination = MangaRoutes.Overview,
        ) {
            composable<MangaRoutes.Overview> {
                MangaOverview(
                    showSnackbar = { snackbarHostState.showSnackbar(it) },
                    imageLoader = coverImageLoader,
                    navigateToManga = { mangaId ->
                        navController.navigate(MangaRoutes.Chapters(mangaId))
                    },
                )
            }
            composable<MangaRoutes.Chapters> { backStackEntry ->
                ChapterOverview(
                    viewModel = hiltViewModel<ChapterViewModel>(),
                    showSnackbar = { snackbarHostState.showSnackbar(it) },
                    onBackClick = dropUnlessStarted { navController.popBackStack() },
                    navigateToChapter = { chapterId ->
                        val mangaId = backStackEntry.toRoute<MangaRoutes.Chapters>().mangaId
                        navController.navigate(MangaRoutes.Reader(mangaId, chapterId))
                    },
                )
            }
            composable<MangaRoutes.Reader>(
                enterTransition = { fadeIn(animationSpec = animationSpec) },
                exitTransition = { fadeOut(animationSpec = animationSpec) },
            ) { backStackEntry ->
                ReadChapterScreen(
                    imageLoader = chapterImageLoader,
                    snackbarHostState = snackbarHostState,
                    onBackClick = dropUnlessStarted {
                        navController.popBackStack<MangaRoutes.Chapters>(inclusive = false)
                    },
                    toChapterClicked = { chapterId ->
                        val mangaId = backStackEntry.toRoute<MangaRoutes.Reader>().mangaId
                        navController.navigate(MangaRoutes.Reader(mangaId, chapterId))
                    },
                )
            }
        }

        StatusBarProtection()
    }
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
                color.copy(alpha = .8f),
                Color.Transparent
            ),
            startY = 0f,
            endY = calculatedHeight
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
    return { statusBars.getTop(density).times(1.2f) }
}
