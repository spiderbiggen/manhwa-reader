package com.spiderbiggen.manga.data.source.remote.usecase

import com.spiderbiggen.manga.data.source.remote.MangaService
import kotlin.time.Instant

class GetRemoteMangaUseCase(private val mangaService: MangaService) {
    suspend operator fun invoke(since: Instant? = null, skipCache: Boolean = false) = runCatching {
        mangaService.getAllMangas(since, skipCache = false)
    }
}
