package com.spiderbiggen.manga.di

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import com.spiderbiggen.manga.data.di.dataModule
import com.spiderbiggen.manga.data.usecase.update.UpdateConfig
import com.spiderbiggen.manga.domain.model.update.AppVersion
import com.spiderbiggen.manga.presentation.di.presentationModule
import com.spiderbiggen.manga.presentation.ui.manga.chapter.list.MangaChapterListViewModel
import com.spiderbiggen.manga.presentation.ui.manga.chapter.list.navigation.MangaChapterListRoute
import com.spiderbiggen.manga.presentation.ui.manga.chapter.reader.MangaChapterReaderViewModel
import com.spiderbiggen.manga.presentation.ui.manga.chapter.reader.navigation.MangaChapterReaderRoute
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import org.junit.Test
import org.koin.dsl.module
import org.koin.test.verify.definition
import org.koin.test.verify.injectedParameters
import org.koin.test.verify.verify

class KoinGraphTest {
    @Test
    fun `given all modules when verified then every constructor dependency is provided`() {
        module { includes(appModule, dataModule, presentationModule) }
            .verify(
                // Provided by the framework at runtime rather than by a module definition.
                extraTypes = listOf(Context::class, SavedStateHandle::class),
                // Built from literals in the app module instead of being injected.
                injections =
                    injectedParameters(
                        definition<UpdateConfig>(AppVersion::class, Boolean::class),
                        // The navigation key is passed as a parameter when the screen is created.
                        definition<MangaChapterListViewModel>(MangaChapterListRoute::class),
                        definition<MangaChapterReaderViewModel>(MangaChapterReaderRoute::class),
                        // Created by HttpClientFactory, not by its constructor.
                        definition<HttpClient>(
                            HttpClientEngine::class,
                            HttpClientConfig::class,
                            Boolean::class,
                        ),
                    ),
            )
    }
}
