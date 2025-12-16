package com.spiderbiggen.manga.data.source.remote.usecase

import com.spiderbiggen.manga.data.source.remote.MangaService
import javax.inject.Inject
import javax.inject.Provider
import kotlin.time.Instant

class GetRemoteMangaUseCase @Inject constructor(private val getService: Provider<MangaService>) {
    suspend operator fun invoke(since: Instant? = null, skipCache: Boolean = false) = runCatching {
        getService.get().getAllMangas(since, skipCache = false)
    }
}
