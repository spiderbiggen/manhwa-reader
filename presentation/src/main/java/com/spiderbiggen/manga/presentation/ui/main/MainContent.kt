package com.spiderbiggen.manga.presentation.ui.main

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.dropUnlessStarted
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import coil3.ImageLoader
import com.spiderbiggen.manga.presentation.components.TrackNavigationSideEffect
import com.spiderbiggen.manga.presentation.theme.MangaReaderTheme
import com.spiderbiggen.manga.presentation.ui.manga.host.MangaHost
import com.spiderbiggen.manga.presentation.ui.manga.model.MangaRoutes
import com.spiderbiggen.manga.presentation.ui.manga.reader.ReadChapterScreen

@Composable
fun MainContent(coverImageLoader: ImageLoader, chapterImageLoader: ImageLoader) {
    val navController = rememberNavController()

    MangaReaderTheme {
        TrackNavigationSideEffect(navController)
        NavHost(
            navController = navController,
            startDestination = MangaRoutes.Host,
        ) {
            composable<MangaRoutes.Host> {
                MangaHost(
                    coverImageLoader = coverImageLoader,
                    navigateToReader = { mangaId, chapterId ->
                        navController.navigate(MangaRoutes.Reader(mangaId, chapterId))
                    },
                )
            }
            composable<MangaRoutes.Reader>(
                enterTransition = { fadeIn(animationSpec = tween(700)) },
                exitTransition = { fadeOut(animationSpec = tween(700)) },
            ) { backStackEntry ->
                ReadChapterScreen(
                    imageLoader = chapterImageLoader,
                    onBackClick = dropUnlessStarted {
                        navController.popBackStack<MangaRoutes.Host>(inclusive = false)
                    },
                    toChapterClicked = { chapterId ->
                        val mangaId = backStackEntry.toRoute<MangaRoutes.Reader>().mangaId
                        navController.navigate(MangaRoutes.Reader(mangaId, chapterId))
                    },
                )
            }
        }
    }
}
