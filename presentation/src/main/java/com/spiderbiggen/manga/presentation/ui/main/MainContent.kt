package com.spiderbiggen.manga.presentation.ui.main

import android.os.Bundle
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation.NavController.OnDestinationChangedListener
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import coil3.ImageLoader
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.spiderbiggen.manga.presentation.theme.MangaReaderTheme
import com.spiderbiggen.manga.presentation.ui.chapter.read.ReadChapterScreen
import com.spiderbiggen.manga.presentation.ui.manga.host.MangaHost
import com.spiderbiggen.manga.presentation.ui.manga.model.MangaRoutes

@Composable
fun MainContent(coverImageLoader: ImageLoader, chapterImageLoader: ImageLoader) {
    val navController = rememberNavController()

    MangaReaderTheme {
        NavigationTrackingSideEffect(navController)
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
}

/**
 * Stores information about navigation events
 */
@Composable
private fun NavigationTrackingSideEffect(navController: NavHostController) {
    DisposableEffect(navController) {
        val listener = OnDestinationChangedListener { _, destination, arguments ->
            val bundle = (arguments?.deepCopy() ?: Bundle()).apply {
                val extraKeys = keySet() - destination.arguments.keys
                extraKeys.forEach { key -> remove(key) }
                putString(FirebaseAnalytics.Param.SCREEN_NAME, destination.route?.takeLast(100))
            }

            Firebase.analytics.logEvent(
                FirebaseAnalytics.Event.SCREEN_VIEW,
                bundle,
            )
        }

        navController.addOnDestinationChangedListener(listener)

        onDispose {
            navController.removeOnDestinationChangedListener(listener)
        }
    }
}
