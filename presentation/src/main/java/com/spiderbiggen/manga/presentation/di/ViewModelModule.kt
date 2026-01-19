package com.spiderbiggen.manga.presentation.di

import com.spiderbiggen.manga.presentation.ui.manga.chapter.list.MangaChapterListViewModel
import com.spiderbiggen.manga.presentation.ui.manga.chapter.reader.MangaChapterReaderViewModel
import com.spiderbiggen.manga.presentation.ui.manga.list.MangaListViewModel
import com.spiderbiggen.manga.presentation.ui.profile.login.LoginViewModel
import com.spiderbiggen.manga.presentation.ui.profile.overview.ProfileOverviewViewModel
import com.spiderbiggen.manga.presentation.ui.profile.registration.RegistrationViewModel
import com.spiderbiggen.manga.presentation.ui.profile.state.ProfileViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::MangaListViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::ProfileOverviewViewModel)
    viewModelOf(::RegistrationViewModel)
    viewModelOf(::ProfileViewModel)

    viewModel { params ->
        MangaChapterListViewModel(
            navKey = params.get(),
            getOverviewChapters = get(),
            getManga = get(),
            toggleFavorite = get(),
            updateChaptersFromRemote = get(),
            mapChapterRowData = get(),
            formatAppError = get(),
        )
    }

    viewModel { params ->
        MangaChapterReaderViewModel(
            navKey = params.get(),
            getChapter = get(),
            getSurroundingChapters = get(),
            getChapterImages = get(),
            isFavorite = get(),
            toggleFavorite = get(),
            setRead = get(),
            setReadUpToChapter = get(),
        )
    }
}
