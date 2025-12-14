package com.spiderbiggen.manga.data.source.remote.usecase

import com.spiderbiggen.manga.data.source.remote.MangaService
import com.spiderbiggen.manga.data.usecase.either
import com.spiderbiggen.manga.domain.model.id.MangaId
import javax.inject.Inject
import javax.inject.Provider
import kotlin.time.Instant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetRemoteChaptersUseCase @Inject constructor(private val mangaServiceProvider: Provider<MangaService>) {
    suspend operator fun invoke(id: MangaId, since: Instant? = null, skipCache: Boolean = false) = runCatching {
        mangaServiceProvider.get().getMangaChapters(id, since, skipCache)
    }
}
