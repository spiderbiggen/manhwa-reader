package com.spiderbiggen.manga.di

import com.spiderbiggen.manga.BuildConfig
import com.spiderbiggen.manga.data.usecase.update.UpdateConfig
import com.spiderbiggen.manga.domain.model.update.AppVersion
import org.koin.dsl.module

/** Bindings that depend on the app module's own [BuildConfig]. */
val appModule = module {
    single {
        UpdateConfig(
            currentVersion = AppVersion.parse(BuildConfig.VERSION_NAME),
            // Debug builds use a different application ID and are never replaced by a release APK.
            enabled = !BuildConfig.DEBUG,
        )
    }
}
