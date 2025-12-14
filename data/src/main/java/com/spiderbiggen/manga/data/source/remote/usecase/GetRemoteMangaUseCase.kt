package com.spiderbiggen.manga.data.source.remote.usecase

import com.spiderbiggen.manga.data.source.remote.MangaService
import com.spiderbiggen.manga.data.usecase.either
import javax.inject.Inject
import javax.inject.Provider
import kotlin.time.Instant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetRemoteMangaUseCase @Inject constructor(private val getService: Provider<MangaService>) {
    suspend operator fun invoke(since: Instant? = null, skipCache: Boolean = false) = runCatching {
        withContext(Dispatchers.IO) {
            getService.get().getAllMangas(since, skipCache = false)
        }
    }
}
