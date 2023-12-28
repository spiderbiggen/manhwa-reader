package com.spiderbiggen.manhwa.presentation.ui.main

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
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
                composable("overview") {
                    val viewModel: ManhwaViewModel = hiltViewModel()
                    ManhwaOverview(
                        navigateToManhwa = { navController.navigate("manhwa/${it}") },
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
                        navigateToChapter = { navController.navigate("manhwa/$manhwaId/chapter/$it") },
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
                    val viewModel: ImagesViewModel = hiltViewModel()
                    val manhwaId = checkNotNull(backStackEntry.arguments?.getString("manhwaId"))
                    ImagesOverview(
                        onBackClick = {
                            navController.popBackStack(route = "manhwa/$manhwaId", false)
                        },
                        toChapterClicked = { navController.navigate("manhwa/$manhwaId/chapter/$it") },
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}
