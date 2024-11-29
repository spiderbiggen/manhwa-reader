package com.spiderbiggen.manga.presentation.ui.manga.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.NewReleases
import androidx.compose.ui.graphics.vector.ImageVector

sealed class MangaBottomNavigationItem(val title: String, val route: MangaRoutes, val icon: ImageVector) {
    data object Discover : MangaBottomNavigationItem(
        title = "Discover",
        route = HostedMangaRoutes.Explore,
        icon = Icons.Outlined.NewReleases,
    )

    data object Favorites : MangaBottomNavigationItem(
        title = "Favorites",
        route = HostedMangaRoutes.Favorites,
        icon = Icons.Outlined.Favorite,
    )
}
