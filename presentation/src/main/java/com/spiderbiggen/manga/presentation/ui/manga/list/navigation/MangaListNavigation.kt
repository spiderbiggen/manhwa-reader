package com.spiderbiggen.manga.presentation.ui.manga.list.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.State
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.spiderbiggen.manga.domain.model.id.MangaId
import com.spiderbiggen.manga.presentation.ui.manga.list.MangaListScreen
import com.spiderbiggen.manga.presentation.ui.profile.state.ProfileState
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
data object MangaListRoute : NavKey

fun EntryProviderScope<NavKey>.mangaListDestination(
    snackbarHostState: SnackbarHostState,
    profileState: State<ProfileState>,
    onProfileClick: () -> Unit,
    onMangaClick: (MangaId) -> Unit,
    metadata: Map<String, Any> = emptyMap(),
) {
    entry<MangaListRoute>(metadata = metadata) {
        MangaListScreen(
            viewModel = koinViewModel(),
            snackbarHostState = snackbarHostState,
            profileState = profileState.value,
            onProfileClick = onProfileClick,
            onMangaClick = onMangaClick,
        )
    }
}
