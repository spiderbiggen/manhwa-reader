package com.spiderbiggen.manga.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.navigation.NavController.OnDestinationChangedListener
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.ktx.Firebase
import com.spiderbiggen.manga.presentation.ui.manga.model.MangaRoutes

/**
 * Stores information about navigation events
 */
@Composable
fun TrackNavigationSideEffect(navController: NavHostController) {
    DisposableEffect(navController) {
        val listener = OnDestinationChangedListener { _, destination, arguments ->
            val route = destination.route?.sanitize() ?: return@OnDestinationChangedListener
            if (destination.hasRoute<MangaRoutes.Host>()) return@OnDestinationChangedListener

            Firebase.analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
                arguments?.let { bundle.putAll(it) }
                param(FirebaseAnalytics.Param.SCREEN_NAME, route)
            }
        }

        navController.addOnDestinationChangedListener(listener)

        onDispose {
            navController.removeOnDestinationChangedListener(listener)
        }
    }
}

private fun String.sanitize() = removePrefix("com.spiderbiggen.manga.presentation.").takeLast(100)
