package com.spiderbiggen.manga.presentation.ui.main

import android.os.Bundle
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.unit.IntOffset
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController.OnDestinationChangedListener
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.spiderbiggen.manga.presentation.ui.chapter.ChapterOverview
import com.spiderbiggen.manga.presentation.ui.chapter.ChapterViewModel
import com.spiderbiggen.manga.presentation.ui.chapter.model.ChapterRoute
import com.spiderbiggen.manga.presentation.ui.images.ImagesOverview
import com.spiderbiggen.manga.presentation.ui.images.ImagesViewModel
import com.spiderbiggen.manga.presentation.ui.images.model.ImagesRoute
import com.spiderbiggen.manga.presentation.ui.manga.MangaOverview
import com.spiderbiggen.manga.presentation.ui.manga.MangaViewModel
import com.spiderbiggen.manga.presentation.ui.manga.model.MangaRoute

@Composable
fun MainContent() {
    val navController = rememberNavController()

    NavigationTrackingSideEffect(navController)
    NavHost(
        navController = navController,
        startDestination = MangaRoute,
        enterTransition = { slideIn(initialOffset = { IntOffset(it.width, 0) }) },
        exitTransition = { slideOut(targetOffset = { IntOffset(-it.width, 0) }) },
        popEnterTransition = { slideIn(initialOffset = { IntOffset(-it.width, 0) }) },
        popExitTransition = { slideOut(targetOffset = { IntOffset(it.width, 0) }) },
    ) {
        composable<MangaRoute> { backStackEntry ->
            val viewModel: MangaViewModel = hiltViewModel()
            MangaOverview(
                viewModel = viewModel,
                navigateToManga = { mangaId ->
                    backStackEntry.ifResumed {
                        navController.navigate(ChapterRoute(mangaId.inner))
                    }
                },
            )
        }
        composable<ChapterRoute> { backStackEntry ->
            val mangaId = backStackEntry.toRoute<ChapterRoute>().mangaId
            ChapterOverview(
                viewModel = hiltViewModel<ChapterViewModel>(),
                onBackClick = { navController.popBackStack() },
                navigateToChapter = { chapterId ->
                    backStackEntry.ifResumed {
                        navController.navigate(ImagesRoute(mangaId, chapterId.inner)) {
                            restoreState = true
                        }
                    }
                },
            )
        }
        composable<ImagesRoute>(
            enterTransition = { fadeIn(animationSpec = tween(700)) },
            exitTransition = { fadeOut(animationSpec = tween(700)) },
        ) { backStackEntry ->
            val mangaId = backStackEntry.toRoute<ImagesRoute>().mangaId
            ImagesOverview(
                viewModel = hiltViewModel<ImagesViewModel>(),
                onBackClick = {
                    backStackEntry.ifResumed {
                        navController.popBackStack<ChapterRoute>(inclusive = false)
                    }
                },
                toChapterClicked = { chapterId ->
                    backStackEntry.ifResumed {
                        navController.navigate(ImagesRoute(mangaId, chapterId.inner))
                    }
                },
            )
        }
    }
}

/**
 * If the lifecycle is not resumed it means this NavBackStackEntry already processed a nav event.
 *
 * This is used to de-duplicate navigation events.
 */
private fun NavBackStackEntry.lifecycleIsResumed() = this.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)

private inline fun NavBackStackEntry.ifResumed(block: () -> Unit) {
    // FIXME: predictive back currently breaks a lot of things due to lifetime issues.
    // This is currently broken when canceling predictive back
    // possibly related to https://issuetracker.google.com/issues/343124455
    if (lifecycleIsResumed()) {
        block()
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
