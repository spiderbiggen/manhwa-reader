package com.spiderbiggen.manga.presentation.ui.manga.host

import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import com.spiderbiggen.manga.presentation.components.TrackNavigationSideEffect
import com.spiderbiggen.manga.presentation.components.snackbar.SnackbarData
import com.spiderbiggen.manga.presentation.ui.manga.MangaOverview
import com.spiderbiggen.manga.presentation.ui.manga.chapter.overview.ChapterOverview
import com.spiderbiggen.manga.presentation.ui.manga.chapter.overview.ChapterViewModel
import com.spiderbiggen.manga.presentation.ui.manga.explore.ExploreViewModel
import com.spiderbiggen.manga.presentation.ui.manga.favorites.MangaFavoritesViewModel
import com.spiderbiggen.manga.presentation.ui.manga.model.HostedMangaRoutes
import com.spiderbiggen.manga.presentation.ui.manga.model.MangaRoutes

@Composable
fun MangaHost(
    coverImageLoader: ImageLoader,
    snackbarHostState: SnackbarHostState,
    navigateToReader: (MangaId, ChapterId) -> Unit,
) {
    val navController = rememberNavController()
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = { MangaNavigationBar(navController) },
        contentWindowInsets = WindowInsets.navigationBars,
    ) { contentPadding ->
        // TODO determine default route at start
        MangaNavHost(
            coverImageLoader = coverImageLoader,
            navController = navController,
            modifier = Modifier.padding(contentPadding),
            navigateToReader = navigateToReader,
            showSnackbar = { snackbarHostState.showSnackbar(it) },
        )
    }
}

@Composable
private fun MangaNavHost(
    coverImageLoader: ImageLoader,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    navigateToReader: (MangaId, ChapterId) -> Unit,
    showSnackbar: suspend (SnackbarData) -> Unit,
) {
    TrackNavigationSideEffect(navController)
    NavHost(
        navController,
        route = MangaRoutes.Host::class,
        startDestination = HostedMangaRoutes.Favorites,
        modifier = modifier,
    ) {
        composable<HostedMangaRoutes.Explore> {
            MangaOverview(
                viewModel = hiltViewModel<ExploreViewModel>(),
                showSnackbar = showSnackbar,
                imageLoader = coverImageLoader,
                navigateToManga = { mangaId ->
                    navController.navigate(HostedMangaRoutes.Chapters(mangaId))
                },
            )
        }
        composable<HostedMangaRoutes.Favorites> {
            MangaOverview(
                viewModel = hiltViewModel<MangaFavoritesViewModel>(),
                showSnackbar = showSnackbar,
                imageLoader = coverImageLoader,
                navigateToManga = { mangaId ->
                    navController.navigate(HostedMangaRoutes.Chapters(mangaId))
                },
            )
        }

        composable<HostedMangaRoutes.Chapters>(
            enterTransition = { slideIn(initialOffset = { IntOffset(it.width, 0) }) },
            exitTransition = { slideOut(targetOffset = { IntOffset(-it.width, 0) }) },
            popEnterTransition = { slideIn(initialOffset = { IntOffset(-it.width, 0) }) },
            popExitTransition = { slideOut(targetOffset = { IntOffset(it.width, 0) }) },
        ) { backStackEntry ->
            ChapterOverview(
                viewModel = hiltViewModel<ChapterViewModel>(),
                showSnackbar = showSnackbar,
                onBackClick = dropUnlessStarted { navController.popBackStack() },
                navigateToChapter = { chapterId ->
                    val mangaId = backStackEntry.toRoute<HostedMangaRoutes.Chapters>().mangaId
                    navigateToReader(mangaId, chapterId)
                },
            )
        }
    }
}
