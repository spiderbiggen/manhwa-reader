package com.spiderbiggen.manga.presentation.ui.manga.chapter.list.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.domain.model.id.MangaId
import com.spiderbiggen.manga.presentation.ui.manga.chapter.list.ChapterListScreen
import com.spiderbiggen.manga.presentation.ui.manga.chapter.list.MangaChapterListViewModel
import kotlinx.serialization.Serializable

@Serializable
data class MangaChapterListRoute(val id: MangaId) : NavKey

fun NavBackStack<NavKey>.navigateToMangaList(id: MangaId) {
    add(MangaChapterListRoute(id))
}

fun EntryProviderScope<NavKey>.mangaChapterListDestination(
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onChapterClick: (MangaId, ChapterId) -> Unit,
) {
    entry<MangaChapterListRoute> { key ->
        ChapterListScreen(
            viewModel = hiltViewModel<MangaChapterListViewModel, MangaChapterListViewModel.Factory>(
                creationCallback = { factory -> factory.create(key) },
            ),
            snackbarHostState = snackbarHostState,
            onBackClick = onBackClick,
            onChapterClick = { onChapterClick(key.id, it) },
        )
    }
}
