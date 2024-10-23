package com.spiderbiggen.manga.presentation.ui.manga.host

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import coil3.ImageLoader
import com.spiderbiggen.manga.presentation.components.MangaNavigationBar
import com.spiderbiggen.manga.presentation.ui.chapter.overview.ChapterOverview
import com.spiderbiggen.manga.presentation.ui.chapter.overview.ChapterViewModel
import com.spiderbiggen.manga.presentation.ui.chapter.read.ReadChapterScreen
import com.spiderbiggen.manga.presentation.ui.manga.MangaOverview
import com.spiderbiggen.manga.presentation.ui.manga.explore.ExploreViewModel
import com.spiderbiggen.manga.presentation.ui.manga.favorites.MangaFavoritesViewModel
import com.spiderbiggen.manga.presentation.ui.manga.model.MangaRoutes

fun NavGraphBuilder.mangaNavigation(
    hostNavController: NavHostController,
    coverImageLoader: ImageLoader,
    chapterImageLoader: ImageLoader,
) {
    composable<MangaRoutes.Host> {
        val otherNavController = rememberNavController()
        Scaffold(
            bottomBar = { MangaNavigationBar(otherNavController) },
            contentWindowInsets = WindowInsets.navigationBars,
        ) { contentPadding ->
            // TODO determine default route at stare
            NavHost(
                otherNavController,
                route = MangaRoutes.Host::class,
                startDestination = MangaRoutes.Favorites,
                modifier = Modifier.padding(contentPadding)
            ) {
                composable<MangaRoutes.Explore> {
                    MangaOverview(
                        viewModel = hiltViewModel<ExploreViewModel>(),
                        imageLoader = coverImageLoader,
                        navigateToManga = { mangaId ->
                            otherNavController.navigate(MangaRoutes.Chapters(mangaId))
                        },
                    )
                }
                composable<MangaRoutes.Favorites> {
                    MangaOverview(
                        viewModel = hiltViewModel<MangaFavoritesViewModel>(),
                        imageLoader = coverImageLoader,
                        navigateToManga = { mangaId ->
                            otherNavController.navigate(MangaRoutes.Chapters(mangaId))
                        },
                    )
                }

                composable<MangaRoutes.Chapters>(
                    enterTransition = { slideIn(initialOffset = { IntOffset(it.width, 0) }) },
                    exitTransition = { slideOut(targetOffset = { IntOffset(-it.width, 0) }) },
                    popEnterTransition = { slideIn(initialOffset = { IntOffset(-it.width, 0) }) },
                    popExitTransition = { slideOut(targetOffset = { IntOffset(it.width, 0) }) },
                ) { backStackEntry ->
                    val mangaId = backStackEntry.toRoute<MangaRoutes.Chapters>().mangaId
                    ChapterOverview(
                        viewModel = hiltViewModel<ChapterViewModel>(),
                        onBackClick = dropUnlessResumed { otherNavController.popBackStack() },
                        navigateToChapter = { chapterId ->
                            otherNavController.navigate(
                                MangaRoutes.Chapters.Read(
                                    mangaId,
                                    chapterId
                                )
                            ) {
                                restoreState = true
                            }
                        },
                    )
                }
            }
        }
    }

    composable<MangaRoutes.Chapters.Read>(
        enterTransition = { fadeIn(animationSpec = tween(700)) },
        exitTransition = { fadeOut(animationSpec = tween(700)) },
    ) { backStackEntry ->
        val mangaId = backStackEntry.toRoute<MangaRoutes.Chapters.Read>().mangaId
        ReadChapterScreen(
            imageLoader = chapterImageLoader,
            onBackClick = dropUnlessResumed {
                hostNavController.popBackStack<MangaRoutes.Chapters>(inclusive = false)
            },
            toChapterClicked = { chapterId ->
                hostNavController.navigate(MangaRoutes.Chapters.Read(mangaId, chapterId))
            },
        )
    }
}
