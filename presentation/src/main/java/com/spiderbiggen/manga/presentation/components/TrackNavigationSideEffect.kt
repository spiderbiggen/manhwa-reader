package com.spiderbiggen.manga.presentation.components

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.navigation.NavController.OnDestinationChangedListener
import androidx.navigation.NavHostController
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

/**
 * Stores information about navigation events
 */
@Composable
fun TrackNavigationSideEffect(navController: NavHostController) {
    DisposableEffect(navController) {
        val listener = OnDestinationChangedListener { _, destination, arguments ->
            val bundle = (arguments?.deepCopy() ?: Bundle()).apply {
                val extraKeys = keySet() - destination.arguments.keys
                extraKeys.forEach { key -> remove(key) }
                val route = destination.route?.removePrefix("com.spiderbiggen.manga.presentation.")
                putString(FirebaseAnalytics.Param.SCREEN_NAME, route?.takeLast(100))
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
