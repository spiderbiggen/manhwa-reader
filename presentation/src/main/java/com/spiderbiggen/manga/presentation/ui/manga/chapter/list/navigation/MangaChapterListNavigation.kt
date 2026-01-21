package com.spiderbiggen.manga.presentation.ui.manga.chapter.list.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.domain.model.id.MangaId
import com.spiderbiggen.manga.presentation.ui.manga.chapter.list.ChapterListScreen
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Serializable
data class MangaChapterListRoute(val id: MangaId) : NavKey

fun NavBackStack<NavKey>.navigateToMangaList(id: MangaId) {
    add(MangaChapterListRoute(id))
}

fun EntryProviderScope<NavKey>.mangaChapterListDestination(
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onChapterClick: (MangaId, ChapterId) -> Unit,
    metadata: Map<String, Any> = emptyMap(),
) {
    entry<MangaChapterListRoute>(metadata = metadata) { key ->
        ChapterListScreen(
            viewModel = koinViewModel(parameters = { parametersOf(key) }),
            snackbarHostState = snackbarHostState,
            onBackClick = onBackClick,
            onChapterClick = { onChapterClick(key.id, it) },
        )
    }
}
