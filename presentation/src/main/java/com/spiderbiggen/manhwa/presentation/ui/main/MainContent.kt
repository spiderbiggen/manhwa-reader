package com.spiderbiggen.manhwa.presentation.ui.main

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.spiderbiggen.manhwa.presentation.theme.MangaReaderTheme
import com.spiderbiggen.manhwa.presentation.ui.chapter.images.ImagesOverview
import com.spiderbiggen.manhwa.presentation.ui.chapter.images.ImagesViewModel
import com.spiderbiggen.manhwa.presentation.ui.chapter.overview.ChapterOverview
import com.spiderbiggen.manhwa.presentation.ui.chapter.overview.ChapterViewModel
import com.spiderbiggen.manhwa.presentation.ui.manga.MangaOverview
import com.spiderbiggen.manhwa.presentation.ui.manga.MangaViewModel

@Composable
fun MainContent() {
    val navController = rememberNavController()
    MangaReaderTheme {
        Surface {
            NavHost(
                navController = navController,
                startDestination = "overview",
            ) {
                composable("overview") { backStackEntry ->
                    val viewModel: MangaViewModel = hiltViewModel()
                    MangaOverview(
                        navigateToManga = {
                            if (backStackEntry.lifecycleIsResumed()) {
                                navController.navigate("manga/$it") {
                                    restoreState = true
                                }
                            }
                        },
                        viewModel = viewModel
                    )
                }
                composable(
                    route = "manga/{mangaId}",
                    arguments = listOf(
                        navArgument("mangaId") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val viewModel: ChapterViewModel = hiltViewModel()
                    val mangaId = checkNotNull(backStackEntry.arguments?.getString("mangaId"))
                    ChapterOverview(
                        onBackClick = { navController.popBackStack() },
                        navigateToChapter = {
                            if (backStackEntry.lifecycleIsResumed()) {
                                navController.navigate("manga/$mangaId/chapter/$it") {
                                    restoreState = true
                                }
                            }
                        },
                        viewModel = viewModel
                    )
                }
                composable(
                    route = "manga/{mangaId}/chapter/{chapterId}",
                    arguments = listOf(
                        navArgument("mangaId") { type = NavType.StringType },
                        navArgument("chapterId") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val mangaId = checkNotNull(backStackEntry.arguments?.getString("mangaId"))
                    val viewModel: ImagesViewModel = hiltViewModel()
                    ImagesOverview(
                        onBackClick = {
                            if (backStackEntry.lifecycleIsResumed()) {
                                navController.navigate("manga/$mangaId") {
                                    popUpTo("manga/$mangaId") {
                                        inclusive = true
                                        saveState = true
                                    }
                                    restoreState = true
                                }
                            }
                        },
                        toChapterClicked = {
                            if (backStackEntry.lifecycleIsResumed()) {
                                navController.navigate("manga/$mangaId/chapter/$it") {
                                    restoreState = true
                                }
                            }
                        },
                        viewModel = viewModel
                    )
                }
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

