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
import com.spiderbiggen.manhwa.presentation.theme.ManhwaReaderTheme
import com.spiderbiggen.manhwa.presentation.ui.chapter.images.ImagesOverview
import com.spiderbiggen.manhwa.presentation.ui.chapter.images.ImagesViewModel
import com.spiderbiggen.manhwa.presentation.ui.chapter.overview.ChapterOverview
import com.spiderbiggen.manhwa.presentation.ui.chapter.overview.ChapterViewModel
import com.spiderbiggen.manhwa.presentation.ui.manhwa.ManhwaOverview
import com.spiderbiggen.manhwa.presentation.ui.manhwa.ManhwaViewModel

@Composable
fun MainContent() {
    val navController = rememberNavController()
    ManhwaReaderTheme {
        Surface {
            NavHost(navController = navController, startDestination = "overview") {
                composable("overview") { backStackEntry ->
                    val viewModel: ManhwaViewModel = hiltViewModel()
                    ManhwaOverview(
                        navigateToManhwa = {
                            if (backStackEntry.lifecycleIsResumed()) {
                                navController.navigate("manhwa/${it}") {
                                    restoreState = true
                                }
                            }
                        },
                        viewModel = viewModel
                    )
                }
                composable(
                    route = "manhwa/{manhwaId}",
                    arguments = listOf(
                        navArgument("manhwaId") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val viewModel: ChapterViewModel = hiltViewModel()
                    val manhwaId = checkNotNull(backStackEntry.arguments?.getString("manhwaId"))
                    ChapterOverview(
                        onBackClick = { navController.popBackStack() },
                        navigateToChapter = {
                            if (backStackEntry.lifecycleIsResumed()) {
                                navController.navigate("manhwa/$manhwaId/chapter/$it") {
                                    restoreState = true
                                }
                            }
                        },
                        viewModel = viewModel
                    )
                }
                composable(
                    route = "manhwa/{manhwaId}/chapter/{chapterId}",
                    arguments = listOf(
                        navArgument("manhwaId") { type = NavType.StringType },
                        navArgument("chapterId") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val manhwaId = checkNotNull(backStackEntry.arguments?.getString("manhwaId"))
                    val viewModel: ImagesViewModel = hiltViewModel()
                    ImagesOverview(
                        onBackClick = {
                            if (backStackEntry.lifecycleIsResumed()) {
                                navController.navigate("manhwa/$manhwaId") {
                                    restoreState = true
                                    popUpTo("manhwa/$manhwaId") {
                                        inclusive = true
                                        saveState = true
                                    }
                                }
                            }
                        },
                        toChapterClicked = {
                            if (backStackEntry.lifecycleIsResumed()) {
                                navController.navigate("manhwa/$manhwaId/chapter/$it") {
                                    popUpTo("manhwa/$manhwaId") {
                                        inclusive = true
                                        saveState = true
                                    }
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

