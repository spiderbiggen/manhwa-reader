package com.spiderbiggen.manga

import android.app.Application
import androidx.compose.runtime.Composer
import androidx.compose.runtime.tooling.ComposeStackTraceMode
import com.google.android.material.color.DynamicColors
import com.spiderbiggen.manga.data.di.dataModule
import com.spiderbiggen.manga.presentation.di.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MangaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)

        Composer.setDiagnosticStackTraceMode(ComposeStackTraceMode.Auto)

        startKoin {
            androidLogger()
            androidContext(this@MangaApp)
            modules(dataModule, presentationModule)
        }
    }
}
