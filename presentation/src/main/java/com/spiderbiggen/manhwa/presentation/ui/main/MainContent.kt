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
import com.spiderbiggen.manhwa.presentation.ui.manhwa.favorites.ManhwaFavoritesOverview
import com.spiderbiggen.manhwa.presentation.ui.manhwa.favorites.ManhwaFavoritesViewModel
import com.spiderbiggen.manhwa.presentation.ui.manhwa.overview.ManhwaOverview
import com.spiderbiggen.manhwa.presentation.ui.manhwa.overview.ManhwaViewModel

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
                        navigateToFavorites = { navController.navigate("favorites") },
                        viewModel = viewModel
                    )
                }
                composable("favorites") {
                    val viewModel: ManhwaFavoritesViewModel = hiltViewModel()
                    ManhwaFavoritesOverview(
                        navigateToManhwa = { navController.navigate("manhwa/${it}") },
                        navigateToOverview = { navController.navigate("overview") },
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
                        onBackClick = { navController.navigate("overview") { popUpTo("overview") } },
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
                            navController.navigate("manhwa/$manhwaId") {
                                popUpTo("manhwa/$manhwaId")
                            }
                        },
                        toChapterClicked = { navController.navigate("manhwa/$manhwaId/chapter/$it") },
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}
