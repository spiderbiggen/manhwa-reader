package com.spiderbiggen.manga.presentation.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.spiderbiggen.manga.presentation.ui.main.LocalSharedTransitionScope
import com.spiderbiggen.manga.presentation.ui.manga.model.MangaBottomNavigationItem

private val navigationItem = mutableListOf(
    MangaBottomNavigationItem.Discover,
    MangaBottomNavigationItem.Favorites,
)

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MangaNavigationBar(
    navController: NavController,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    with(LocalSharedTransitionScope.current!!) {
        NavigationBar(
            modifier
                .sharedElement(
                    rememberSharedContentState(key = ""),
                    animatedVisibilityScope = animatedVisibilityScope,
                )
                .skipToLookaheadSize(),
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            navigationItem.forEach { item ->
                NavigationBarItem(
                    selected = currentDestination?.hasRoute(item.route::class) == true,
                    onClick = dropUnlessResumed {
                        navController.navigate(item.route) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            // on the back stack as users select items
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when
                            // reselecting the same item
//                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    },
                    icon = {
                        Icon(
                            when (item.icon) {
                                is AppDrawable.Resource -> painterResource(item.icon.resId)
                                is AppDrawable.Vector -> rememberVectorPainter(item.icon.image)
                            },
                            item.title,
                        )
                    },
                    label = { Text(text = item.title) },
                )
            }
        }
    }
}
