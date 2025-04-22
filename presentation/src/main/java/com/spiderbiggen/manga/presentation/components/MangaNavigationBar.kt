package com.spiderbiggen.manga.presentation.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.lifecycle.compose.dropUnlessStarted
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.spiderbiggen.manga.presentation.ui.manga.model.MangaBottomNavigationItem

private val navigationItem = mutableListOf(
    MangaBottomNavigationItem.Discover,
    MangaBottomNavigationItem.Favorites,
)

@Composable
fun MangaNavigationBar(navController: NavController, modifier: Modifier = Modifier) {
    NavigationBar(modifier) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        navigationItem.forEach { item ->
            val isSelected = currentDestination?.hasRoute(item.route::class) == true
            NavigationBarItem(
                selected = isSelected,
                onClick = dropUnlessStarted {
                    if (isSelected) return@dropUnlessStarted
                    navController.navigate(item.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(rememberVectorPainter(item.icon), null) },
                label = { Text(text = item.title) },
            )
        }
    }
}
