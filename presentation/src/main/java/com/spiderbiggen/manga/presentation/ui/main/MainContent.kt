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
import com.spiderbiggen.manga.presentation.ui.chapter.read.ReadChapterScreen
import com.spiderbiggen.manga.presentation.ui.manga.host.MangaHost
import com.spiderbiggen.manga.presentation.ui.manga.model.MangaRoutes

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
                        navController.navigate(MangaRoutes.Chapters.Read(mangaId, chapterId))
                    },
                )
            }
            composable<MangaRoutes.Chapters.Read>(
                enterTransition = { fadeIn(animationSpec = tween(700)) },
                exitTransition = { fadeOut(animationSpec = tween(700)) },
            ) { backStackEntry ->
                val mangaId = backStackEntry.toRoute<MangaRoutes.Chapters.Read>().mangaId
                ReadChapterScreen(
                    imageLoader = chapterImageLoader,
                    onBackClick = dropUnlessStarted {
                        navController.popBackStack<MangaRoutes.Host>(inclusive = false)
                    },
                    toChapterClicked = { chapterId ->
                        navController.navigate(MangaRoutes.Chapters.Read(mangaId, chapterId))
                    },
                )
            }
        }
    }
}


