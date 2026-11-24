package com.spiderbiggen.manga

import android.app.Application
import androidx.compose.runtime.Composer
import androidx.compose.runtime.tooling.ComposeStackTraceMode
import com.spiderbiggen.manga.data.di.dataModule
import com.spiderbiggen.manga.data.usecase.update.UpdateConfig
import com.spiderbiggen.manga.presentation.di.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module

class MangaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Composer.setDiagnosticStackTraceMode(ComposeStackTraceMode.Auto)

        startKoin {
            androidLogger()
            androidContext(this@MangaApp)
            modules(
                dataModule,
                presentationModule,
                module {
                    single {
                        UpdateConfig(
                            currentVersion = BuildConfig.VERSION_NAME,
                            enabled = !BuildConfig.DEBUG,
                        )
                    }
                },
            )
        }
    }
}
