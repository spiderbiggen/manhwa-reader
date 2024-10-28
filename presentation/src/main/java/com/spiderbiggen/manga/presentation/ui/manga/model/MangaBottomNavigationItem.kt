package com.spiderbiggen.manga.presentation.ui.manga.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.NewReleases
import com.spiderbiggen.manga.presentation.components.AppDrawable

sealed class MangaBottomNavigationItem(val title: String, val route: MangaRoutes, val icon: AppDrawable) {
    data object Discover : MangaBottomNavigationItem(
        title = "Discover",
        route = MangaRoutes.Explore,
        icon = AppDrawable.Vector(Icons.Outlined.NewReleases),
    )

    data object Favorites : MangaBottomNavigationItem(
        title = "Favorites",
        route = MangaRoutes.Favorites,
        icon = AppDrawable.Vector(Icons.Outlined.Favorite),
    )
}
