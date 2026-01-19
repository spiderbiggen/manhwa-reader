package com.spiderbiggen.manga.presentation.di

import com.spiderbiggen.manga.presentation.ui.manga.chapter.list.MapChapterRowData
import com.spiderbiggen.manga.presentation.ui.manga.list.MapMangaListViewData
import com.spiderbiggen.manga.presentation.ui.manga.list.SplitMangasIntoSections
import com.spiderbiggen.manga.presentation.usecases.FormatAppError
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val uiModule = module {
    factoryOf(::MapMangaListViewData)
    factoryOf(::SplitMangasIntoSections)
    factoryOf(::MapChapterRowData)
    factoryOf(::FormatAppError)
}
