package com.spiderbiggen.manga.presentation.ui.manga.host

import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.dropUnlessStarted
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import coil3.ImageLoader
import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.domain.model.id.MangaId
import com.spiderbiggen.manga.presentation.components.MangaNavigationBar
import com.spiderbiggen.manga.presentation.ui.chapter.overview.ChapterOverview
import com.spiderbiggen.manga.presentation.ui.chapter.overview.ChapterViewModel
import com.spiderbiggen.manga.presentation.ui.manga.MangaOverview
import com.spiderbiggen.manga.presentation.ui.manga.explore.ExploreViewModel
import com.spiderbiggen.manga.presentation.ui.manga.favorites.MangaFavoritesViewModel
import com.spiderbiggen.manga.presentation.ui.manga.model.MangaRoutes

@Composable
fun MangaHost(coverImageLoader: ImageLoader, navigateToReader: (MangaId, ChapterId) -> Unit = { _, _ -> }) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { MangaNavigationBar(navController) },
        contentWindowInsets = WindowInsets.navigationBars,
    ) { contentPadding ->
        // TODO determine default route at stare
        MangaNavHost(
            coverImageLoader = coverImageLoader,
            navController = navController,
            modifier = Modifier.padding(contentPadding),
            navigateToReader = navigateToReader,
        )
    }
}

@Composable
private fun MangaNavHost(
    coverImageLoader: ImageLoader,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    navigateToReader: (MangaId, ChapterId) -> Unit = { _, _ -> },
) {
    NavHost(
        navController,
        route = MangaRoutes.Host::class,
        startDestination = MangaRoutes.Favorites,
        modifier = modifier,
    ) {
        composable<MangaRoutes.Explore> {
            MangaOverview(
                viewModel = hiltViewModel<ExploreViewModel>(),
                imageLoader = coverImageLoader,
                navigateToManga = { mangaId ->
                    navController.navigate(MangaRoutes.Chapters(mangaId))
                },
            )
        }
        composable<MangaRoutes.Favorites> {
            MangaOverview(
                viewModel = hiltViewModel<MangaFavoritesViewModel>(),
                imageLoader = coverImageLoader,
                navigateToManga = { mangaId ->
                    navController.navigate(MangaRoutes.Chapters(mangaId))
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
                onBackClick = dropUnlessStarted { navController.popBackStack() },
                navigateToChapter = { chapterId ->
                    navigateToReader(mangaId, chapterId)
                },
            )
        }
    }
}
