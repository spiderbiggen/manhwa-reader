package com.spiderbiggen.manga.data.source.remote.usecase

import com.spiderbiggen.manga.data.source.remote.MangaService
import javax.inject.Inject
import javax.inject.Provider
import kotlin.time.Instant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class GetRemoteMangaUseCase @Inject constructor(private val getService: Provider<MangaService>) {
    suspend operator fun invoke(since: Instant? = null, skipCache: Boolean = false) = runCatching {
        val response = withContext(Dispatchers.IO) {
            val service = getService.get()
            when {
                skipCache -> service.getAllMangasUncached(since)
                else -> service.getAllMangas(since)
            }
        }

        if (!response.isSuccessful) {
            throw HttpException(response)
        }
        response.body()!!
    }
}
