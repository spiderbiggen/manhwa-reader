package com.spiderbiggen.manga.presentation.ui.manga.chapter.reader.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.material3.SnackbarHostState
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.spiderbiggen.manga.domain.model.id.ChapterId
import com.spiderbiggen.manga.domain.model.id.MangaId
import com.spiderbiggen.manga.presentation.ui.manga.chapter.reader.MangaChapterReaderScreen
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Serializable
data class MangaChapterReaderRoute(val mangaId: MangaId, val chapterId: ChapterId) : NavKey

fun NavBackStack<NavKey>.navigateToMangaReader(mangaId: MangaId, chapterId: ChapterId) {
    add(MangaChapterReaderRoute(mangaId, chapterId))
}

fun EntryProviderScope<NavKey>.mangaChapterReaderDestination(
    snackbarHostState: SnackbarHostState,
    floatAnimationSpec: FiniteAnimationSpec<Float>,
    onBackClick: () -> Unit,
    onChapterClick: (MangaId, ChapterId) -> Unit,
    metadata: Map<String, Any> = emptyMap(),
) {
    entry<MangaChapterReaderRoute>(
        metadata = NavDisplay.transitionSpec {
            fadeIn(floatAnimationSpec) togetherWith ExitTransition.KeepUntilTransitionsFinished
        } + NavDisplay.popTransitionSpec {
            EnterTransition.None togetherWith fadeOut(floatAnimationSpec)
        } + NavDisplay.predictivePopTransitionSpec {
            EnterTransition.None togetherWith fadeOut(floatAnimationSpec)
        } + metadata,
    ) { key ->
        MangaChapterReaderScreen(
            viewModel = koinViewModel(parameters = { parametersOf(key) }),
            snackbarHostState = snackbarHostState,
            onBackClick = onBackClick,
            onChapterClick = { onChapterClick(key.mangaId, it) },
        )
    }
}
