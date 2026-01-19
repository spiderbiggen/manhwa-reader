package com.spiderbiggen.manga.presentation.di

import org.koin.dsl.module

val presentationModule = module {
    includes(uiModule, viewModelModule)
}
