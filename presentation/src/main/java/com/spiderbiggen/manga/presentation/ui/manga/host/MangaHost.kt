package com.spiderbiggen.manga.presentation.ui.manga.host

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.ui.unit.IntOffset
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import coil3.ImageLoader
import com.spiderbiggen.manga.presentation.ui.chapter.overview.ChapterOverview
import com.spiderbiggen.manga.presentation.ui.chapter.overview.ChapterViewModel
import com.spiderbiggen.manga.presentation.ui.chapter.read.ReadChapterScreen
import com.spiderbiggen.manga.presentation.ui.manga.MangaOverview
import com.spiderbiggen.manga.presentation.ui.manga.explore.ExploreViewModel
import com.spiderbiggen.manga.presentation.ui.manga.favorites.MangaFavoritesViewModel
import com.spiderbiggen.manga.presentation.ui.manga.model.MangaRoutes

@OptIn(ExperimentalSharedTransitionApi::class)
fun NavGraphBuilder.mangaNavigation(
    navController: NavController,
    coverImageLoader: ImageLoader,
    chapterImageLoader: ImageLoader,
) {
    // TODO determine default route at stare
    navigation<MangaRoutes.Host>(
        startDestination = MangaRoutes.Favorites,
    ) {
        composable<MangaRoutes.Explore>(deepLinks = listOf()) {
            MangaOverview(
                viewModel = hiltViewModel<ExploreViewModel>(),
                navController = navController,
                imageLoader = coverImageLoader,
                navigateToManga = { mangaId ->
                    navController.navigate(MangaRoutes.Chapters(mangaId))
                },
                animatedVisibilityScope = this@composable,
            )
        }

        composable<MangaRoutes.Favorites> {
            MangaOverview(
                viewModel = hiltViewModel<MangaFavoritesViewModel>(),
                navController = navController,
                imageLoader = coverImageLoader,
                navigateToManga = { mangaId ->
                    navController.navigate(MangaRoutes.Chapters(mangaId))
                },
                animatedVisibilityScope = this@composable,
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
                navController = navController,
                onBackClick = dropUnlessResumed { navController.popBackStack() },
                navigateToChapter = { chapterId ->
                    navController.navigate(MangaRoutes.Chapters.Read(mangaId, chapterId)) {
                        restoreState = true
                    }
                },
                animatedVisibilityScope = this@composable,
            )
        }
        composable<MangaRoutes.Chapters.Read>(
            enterTransition = { fadeIn(animationSpec = tween(700)) },
            exitTransition = { fadeOut(animationSpec = tween(700)) },
        ) { backStackEntry ->
            val mangaId = backStackEntry.toRoute<MangaRoutes.Chapters.Read>().mangaId
            ReadChapterScreen(
                imageLoader = chapterImageLoader,
                onBackClick = dropUnlessResumed {
                    navController.popBackStack<MangaRoutes.Chapters>(inclusive = false)
                },
                toChapterClicked = { chapterId ->
                    navController.navigate(MangaRoutes.Chapters.Read(mangaId, chapterId))
                },
            )
        }
    }
}
