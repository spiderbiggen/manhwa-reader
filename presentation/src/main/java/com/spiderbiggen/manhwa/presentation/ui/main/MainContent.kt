package com.spiderbiggen.manhwa.presentation.ui.main

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.IntOffset
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.spiderbiggen.manhwa.presentation.theme.MangaReaderTheme
import com.spiderbiggen.manhwa.presentation.theme.Purple80
import com.spiderbiggen.manhwa.presentation.ui.chapter.images.ImagesOverview
import com.spiderbiggen.manhwa.presentation.ui.chapter.images.ImagesViewModel
import com.spiderbiggen.manhwa.presentation.ui.chapter.overview.ChapterOverview
import com.spiderbiggen.manhwa.presentation.ui.chapter.overview.ChapterViewModel
import com.spiderbiggen.manhwa.presentation.ui.manga.MangaOverview
import com.spiderbiggen.manhwa.presentation.ui.manga.MangaViewModel

@Composable
fun MainContent() {
    val navController = rememberNavController()
    val mainViewModel = hiltViewModel<MainViewModel>()
    val refreshing = mainViewModel.updatingState.collectAsState(initial = false)

    var seedColor by remember { mutableStateOf(Purple80) }
    MangaReaderTheme(seedColor = seedColor) {
        NavHost(
            navController = navController,
            startDestination = "overview",
            enterTransition = { slideIn(initialOffset = { IntOffset(it.width, 0) }) },
            exitTransition = { slideOut(targetOffset = { IntOffset(-it.width, 0) }) },
            popEnterTransition = { slideIn(initialOffset = { IntOffset(-it.width, 0) }) },
            popExitTransition = { slideOut(targetOffset = { IntOffset(it.width, 0) }) },
        ) {
            composable("overview") { backStackEntry ->
                val viewModel: MangaViewModel = hiltViewModel()
                LaunchedEffect(null) { seedColor = Purple80 }
                MangaOverview(
                    viewModel = viewModel,
                    refreshing = refreshing,
                    onRefreshClicked = mainViewModel::onClickRefresh,
                    navigateToManga = {
                        if (backStackEntry.lifecycleIsResumed()) {
                            navController.navigate("manga/$it")
                        }
                    },
                )

            }
            composable(
                route = "manga/{mangaId}",
                arguments = listOf(
                    navArgument("mangaId") { type = NavType.StringType },
                )
            ) { backStackEntry ->
                val viewModel: ChapterViewModel = hiltViewModel()
                val mangaId = checkNotNull(backStackEntry.arguments?.getString("mangaId"))
                ChapterOverview(
                    onColorChanged = { seedColor = it },
                    onBackClick = { navController.popBackStack() },
                    navigateToChapter = {
                        if (backStackEntry.lifecycleIsResumed()) {
                            navController.navigate("manga/$mangaId/chapter/$it") {
                                restoreState = true
                            }
                        }
                    },
                    viewModel = viewModel,
                    refreshing = refreshing,
                )
            }
            composable(
                route = "manga/{mangaId}/chapter/{chapterId}",
                arguments = listOf(
                    navArgument("mangaId") { type = NavType.StringType },
                    navArgument("chapterId") { type = NavType.StringType },
                ),
                enterTransition = { fadeIn(animationSpec = tween(700)) },
                exitTransition = { fadeOut(animationSpec = tween(700)) },
            ) { backStackEntry ->
                val mangaId = checkNotNull(backStackEntry.arguments?.getString("mangaId"))
                val viewModel: ImagesViewModel = hiltViewModel()
                ImagesOverview(
                    viewModel = viewModel,
                    onBackClick = {
                        if (backStackEntry.lifecycleIsResumed()) {
                            navController.popBackStack(route = "manga/$mangaId", inclusive = false)
                        }
                    },
                    toChapterClicked = { id ->
                        if (backStackEntry.lifecycleIsResumed()) {
                            navController.navigate("manga/$mangaId/chapter/$id")
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
private fun NavBackStackEntry.lifecycleIsResumed() =
    this.lifecycle.currentState == Lifecycle.State.RESUMED

