package com.spiderbiggen.manhwa.presentation.ui.main

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
import com.spiderbiggen.manhwa.presentation.ui.manhwa.overview.ManhwaOverview
import com.spiderbiggen.manhwa.presentation.ui.manhwa.overview.ManhwaViewModel

@Composable
fun MainContent() {
    val navController = rememberNavController()
    ManhwaReaderTheme {
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
            ) {
                val viewModel: ChapterViewModel = hiltViewModel()
                ChapterOverview(
                    navigateToChapter = { navController.navigate("chapter/${it}") },
                    viewModel = viewModel
                )
            }
            composable(
                route = "chapter/{chapterId}",
                arguments = listOf(
                    navArgument("chapterId") { type = NavType.StringType }
                )
            ) {
                val viewModel: ImagesViewModel = hiltViewModel()
                ImagesOverview(
                    viewModel = viewModel
                )
            }
        }
    }
}
