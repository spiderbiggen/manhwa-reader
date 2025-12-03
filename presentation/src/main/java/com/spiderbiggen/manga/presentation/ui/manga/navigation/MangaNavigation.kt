package com.spiderbiggen.manga.presentation.ui.manga.navigation

import androidx.compose.material3.SnackbarHostState
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
import com.spiderbiggen.manga.presentation.ui.manga.list.navigation.mangaDestination
import com.spiderbiggen.manga.presentation.ui.profile.navigation.navigateToProfile
import com.spiderbiggen.manga.presentation.ui.profile.state.ProfileState

fun EntryProviderScope<NavKey>.manga(
    backStack: NavBackStack<NavKey>,
    profileState: State<ProfileState>,
    snackbarHostState: SnackbarHostState,
) {
    mangaDestination(
        snackbarHostState = snackbarHostState,
        profileState = profileState,
        onProfileClick = { backStack.navigateToProfile(profileState.value) },
        onMangaClick = backStack::navigateToMangaList,
    )
    mangaChapterListDestination(
        snackbarHostState = snackbarHostState,
        onBackClick = { backStack.popUpToInclusive<MangaChapterListRoute>() },
        onChapterClick = backStack::navigateToMangaReader,
    )
    mangaChapterReaderDestination(
        snackbarHostState = snackbarHostState,
        onBackClick = { backStack.popUpToInclusive<MangaChapterReaderRoute>() },
        onChapterClick = backStack::navigateToMangaReader,
    )
}
