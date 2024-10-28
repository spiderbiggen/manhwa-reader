package com.spiderbiggen.manga.data.source.remote.usecase

import com.spiderbiggen.manga.data.source.remote.MangaService
import com.spiderbiggen.manga.domain.model.id.MangaId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Provider

class GetRemoteChaptersUseCase @Inject constructor(private val mangaServiceProvider: Provider<MangaService>) {
    suspend operator fun invoke(id: MangaId, skipCache: Boolean = false) = runCatching {
        val response = withContext(Dispatchers.IO) {
            val service = mangaServiceProvider.get()
            when {
                skipCache -> service.getMangaChaptersSkipCache(id)
                else -> service.getMangaChapters(id)
            }
        }

        if (!response.isSuccessful) {
            throw HttpException(response)
        }
        response.body()!!
    }
}
