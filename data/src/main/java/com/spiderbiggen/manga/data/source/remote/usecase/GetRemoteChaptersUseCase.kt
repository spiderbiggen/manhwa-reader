package com.spiderbiggen.manga.data.source.remote.usecase

import com.spiderbiggen.manga.data.source.remote.MangaService
import com.spiderbiggen.manga.domain.model.id.MangaId
import kotlin.time.Instant

class GetRemoteChaptersUseCase(private val mangaService: MangaService) {
    suspend operator fun invoke(id: MangaId, since: Instant? = null, skipCache: Boolean = false) = runCatching {
        mangaService.getMangaChapters(id, since, skipCache)
    }
}
