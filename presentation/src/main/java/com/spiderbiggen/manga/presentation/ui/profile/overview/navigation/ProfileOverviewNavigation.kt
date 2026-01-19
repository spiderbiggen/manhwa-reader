package com.spiderbiggen.manga.presentation.ui.profile.overview.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.spiderbiggen.manga.presentation.ui.profile.overview.ProfileOverview
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
data object ProfileOverviewRoute : NavKey

fun NavBackStack<NavKey>.navigateToProfileOverview() {
    add(ProfileOverviewRoute)
}

fun EntryProviderScope<NavKey>.profileOverviewDestination(
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onLogout: () -> Unit,
) {
    entry<ProfileOverviewRoute> {
        ProfileOverview(
            viewModel = koinViewModel(),
            snackbarHostState = snackbarHostState,
            onBackClick = onBackClick,
            onLogout = onLogout,
        )
    }
}
