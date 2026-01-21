package com.spiderbiggen.manga.presentation.ui.manga.navigation

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.spiderbiggen.manga.presentation.navigation.popUpToInclusive
import com.spiderbiggen.manga.presentation.ui.manga.chapter.list.navigation.MangaChapterListRoute
import com.spiderbiggen.manga.presentation.ui.manga.chapter.list.navigation.mangaChapterListDestination
import com.spiderbiggen.manga.presentation.ui.manga.chapter.list.navigation.navigateToMangaList
import com.spiderbiggen.manga.presentation.ui.manga.chapter.reader.navigation.MangaChapterReaderRoute
import com.spiderbiggen.manga.presentation.ui.manga.chapter.reader.navigation.mangaChapterReaderDestination
import com.spiderbiggen.manga.presentation.ui.manga.chapter.reader.navigation.navigateToMangaReader
import com.spiderbiggen.manga.presentation.ui.manga.list.navigation.mangaListDestination
import com.spiderbiggen.manga.presentation.ui.profile.navigation.navigateToProfile
import com.spiderbiggen.manga.presentation.ui.profile.state.ProfileState

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun rememberMangaSceneStrategy(): ListDetailSceneStrategy<NavKey> {
    val adaptiveInfo = currentWindowAdaptiveInfo(true)
    val defaultDirective = calculatePaneScaffoldDirective(adaptiveInfo)
    return rememberListDetailSceneStrategy(
        directive = defaultDirective,
        backNavigationBehavior = BackNavigationBehavior.PopUntilContentChange,
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
fun EntryProviderScope<NavKey>.manga(
    backStack: NavBackStack<NavKey>,
    profileState: State<ProfileState>,
    snackbarHostState: SnackbarHostState,
    floatAnimationSpec: FiniteAnimationSpec<Float>,
) {
    mangaListDestination(
        snackbarHostState = snackbarHostState,
        profileState = profileState,
        onProfileClick = { backStack.navigateToProfile(profileState.value) },
        onMangaClick = backStack::navigateToMangaList,
        metadata = ListDetailSceneStrategy.listPane(),
    )
    mangaChapterListDestination(
        snackbarHostState = snackbarHostState,
        onBackClick = { backStack.popUpToInclusive<MangaChapterListRoute>() },
        onChapterClick = backStack::navigateToMangaReader,
        metadata = ListDetailSceneStrategy.listPane(),
    )
    mangaChapterReaderDestination(
        snackbarHostState = snackbarHostState,
        floatAnimationSpec = floatAnimationSpec,
        onBackClick = { backStack.popUpToInclusive<MangaChapterReaderRoute>() },
        onChapterClick = backStack::navigateToMangaReader,
        metadata = ListDetailSceneStrategy.detailPane(),
    )
}
