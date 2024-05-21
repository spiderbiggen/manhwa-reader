package com.spiderbiggen.manhwa.presentation.ui.main

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.IntOffset
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.spiderbiggen.manhwa.presentation.theme.MangaReaderTheme
import com.spiderbiggen.manhwa.presentation.theme.Purple80
import com.spiderbiggen.manhwa.presentation.ui.chapter.ChapterOverview
import com.spiderbiggen.manhwa.presentation.ui.chapter.ChapterViewModel
import com.spiderbiggen.manhwa.presentation.ui.chapter.model.ChapterRoute
import com.spiderbiggen.manhwa.presentation.ui.images.ImagesOverview
import com.spiderbiggen.manhwa.presentation.ui.images.ImagesViewModel
import com.spiderbiggen.manhwa.presentation.ui.images.model.ImagesRoute
import com.spiderbiggen.manhwa.presentation.ui.manga.MangaOverview
import com.spiderbiggen.manhwa.presentation.ui.manga.MangaViewModel
import com.spiderbiggen.manhwa.presentation.ui.manga.model.MangaRoute

@Composable
fun MainContent() {
    val navController = rememberNavController()

    var seedColor by remember { mutableStateOf(Purple80) }
    MangaReaderTheme(seedColor = seedColor) {
        NavHost(
            navController = navController,
            startDestination = MangaRoute,
            enterTransition = { slideIn(initialOffset = { IntOffset(it.width, 0) }) },
            exitTransition = { slideOut(targetOffset = { IntOffset(-it.width, 0) }) },
            popEnterTransition = { slideIn(initialOffset = { IntOffset(-it.width, 0) }) },
            popExitTransition = { slideOut(targetOffset = { IntOffset(it.width, 0) }) },
        ) {
            composable<MangaRoute> { backStackEntry ->
                val viewModel: MangaViewModel = hiltViewModel()
                LaunchedEffect(null) { seedColor = Purple80 }
                MangaOverview(
                    viewModel = viewModel,
                    navigateToManga = { mangaId ->
                        backStackEntry.ifResumed {
                            navController.navigate(ChapterRoute(mangaId))
                        }
                    },
                )
            }
            composable<ChapterRoute> { backStackEntry ->
                val mangaId = backStackEntry.toRoute<ChapterRoute>().mangaId
                ChapterOverview(
                    viewModel = hiltViewModel<ChapterViewModel>(),
                    onColorChanged = { seedColor = it },
                    onBackClick = { navController.popBackStack() },
                    navigateToChapter = { chapterId ->
                        backStackEntry.ifResumed {
                            navController.navigate(ImagesRoute(mangaId, chapterId)) {
                                restoreState = true
                            }
                        }
                    },
                )
            }
            composable<ImagesRoute>(
                enterTransition = { fadeIn(animationSpec = tween(700)) },
                exitTransition = { fadeOut(animationSpec = tween(700)) },
            ) { backStackEntry ->
                val mangaId = backStackEntry.toRoute<ImagesRoute>().mangaId
                ImagesOverview(
                    viewModel = hiltViewModel<ImagesViewModel>(),
                    onBackClick = {
                        backStackEntry.ifResumed {
                            navController.popBackStack<ChapterRoute>(inclusive = false)
                        }
                    },
                    toChapterClicked = { chapterId ->
                        backStackEntry.ifResumed {
                            navController.navigate(ImagesRoute(mangaId, chapterId))
                        }
                    },
                )
            }
        }
    }
}

/**
 * If the lifecycle is not resumed it means this NavBackStackEntry already processed a nav event.
 *
 * This is used to de-duplicate navigation events.
 */
private fun NavBackStackEntry.lifecycleIsResumed() = this.lifecycle.currentState == Lifecycle.State.RESUMED

private inline fun NavBackStackEntry.ifResumed(block: () -> Unit) {
    if (lifecycleIsResumed()) {
        block()
    }
}
