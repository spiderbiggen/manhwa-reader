@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.spiderbiggen.manga.presentation.ui.main

import android.os.Bundle
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavController.OnDestinationChangedListener
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import coil3.ImageLoader
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.spiderbiggen.manga.presentation.theme.MangaReaderTheme
import com.spiderbiggen.manga.presentation.ui.manga.host.mangaNavigation
import com.spiderbiggen.manga.presentation.ui.manga.model.MangaRoutes

val LocalNavAnimatedVisibilityScope = compositionLocalOf<AnimatedVisibilityScope?> { null }
val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }

@Composable
fun MainContent(coverImageLoader: ImageLoader, chapterImageLoader: ImageLoader) {
    val navController = rememberNavController()

    MangaReaderTheme {
        NavigationTrackingSideEffect(navController)
        SharedTransitionLayout {
            CompositionLocalProvider(
                LocalSharedTransitionScope provides this,
            ) {
                NavHost(
                    navController = navController,
                    startDestination = MangaRoutes.Host,
                ) {
                    mangaNavigation(navController, coverImageLoader, chapterImageLoader)
                }
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
                putString(FirebaseAnalytics.Event.SCREEN_VIEW, destination.route?.takeLast(100))
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
