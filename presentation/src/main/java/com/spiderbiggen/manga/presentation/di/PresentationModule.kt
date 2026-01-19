package com.spiderbiggen.manga.presentation.di

import com.spiderbiggen.manga.presentation.ui.manga.chapter.list.MangaChapterListViewModel
import com.spiderbiggen.manga.presentation.ui.manga.chapter.list.MapChapterRowData
import com.spiderbiggen.manga.presentation.ui.manga.chapter.reader.MangaChapterReaderViewModel
import com.spiderbiggen.manga.presentation.ui.manga.list.MangaListViewModel
import com.spiderbiggen.manga.presentation.ui.manga.list.MapMangaListViewData
import com.spiderbiggen.manga.presentation.ui.manga.list.SplitMangasIntoSections
import com.spiderbiggen.manga.presentation.ui.profile.login.LoginViewModel
import com.spiderbiggen.manga.presentation.ui.profile.overview.ProfileOverviewViewModel
import com.spiderbiggen.manga.presentation.ui.profile.registration.RegistrationViewModel
import com.spiderbiggen.manga.presentation.ui.profile.state.ProfileViewModel
import com.spiderbiggen.manga.presentation.usecases.FormatAppError
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.koin.core.module.dsl.viewModel

val presentationModule = module {
    factoryOf(::MapMangaListViewData)
    factoryOf(::SplitMangasIntoSections)
    factoryOf(::MapChapterRowData)
    factoryOf(::FormatAppError)

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
            formatAppError = get()
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
