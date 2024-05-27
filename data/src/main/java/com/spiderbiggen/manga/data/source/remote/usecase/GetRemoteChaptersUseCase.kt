package com.spiderbiggen.manga.data.source.remote.usecase

import com.spiderbiggen.manga.data.source.remote.MangaService
import javax.inject.Inject
import javax.inject.Provider
import retrofit2.HttpException

class GetRemoteChaptersUseCase @Inject constructor(private val getService: Provider<MangaService>) {
    suspend operator fun invoke(id: String, skipCache: Boolean = false) = runCatching {
        val response = if (skipCache) {
            getService.get().getMangaChaptersSkipCache(id)
        } else {
            getService.get().getMangaChapters(id)
        }

        if (!response.isSuccessful) {
            throw HttpException(response)
        }
        response.body()!!
    }
}
