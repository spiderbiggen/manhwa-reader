package com.spiderbiggen.manga.presentation.ui.main

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.dropUnlessStarted
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import coil3.ImageLoader
import com.spiderbiggen.manga.presentation.components.TrackNavigationSideEffect
import com.spiderbiggen.manga.presentation.theme.MangaReaderTheme
import com.spiderbiggen.manga.presentation.theme.Purple80
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

    val dominantColor = rememberSaveable(stateSaver = ColorSaver) { mutableStateOf(Purple80) }
    TrackNavigationSideEffect(navController)

    MangaReaderTheme(dominantColor.value) {
        val animationSpec = MaterialTheme.motionScheme.slowSpatialSpec<Float>()
        NavHost(
            navController = navController,
            startDestination = MangaRoutes.Overview,
        ) {
            composable<MangaRoutes.Overview> {
                MangaReaderTheme(Purple80) {
                    MangaOverview(
                        showSnackbar = { snackbarHostState.showSnackbar(it) },
                        imageLoader = coverImageLoader,
                        navigateToManga = { mangaId ->
                            navController.navigate(MangaRoutes.Chapters(mangaId))
                        },
                    )
                }
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
                    onBackgroundColorChanged = { dominantColor.value = it },
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
    }
}

val ColorSaver = Saver<Color, Long>(
    save = { it.value.toLong() },
    restore = { Color(it.toULong()) },
)
