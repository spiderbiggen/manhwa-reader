package com.spiderbiggen.manhwa.data.source.remote.usecase

import com.spiderbiggen.manhwa.data.source.remote.MangaService
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Provider

class GetRemoteChaptersUseCase @Inject constructor(private val getService: Provider<MangaService>) {
    suspend operator fun invoke(id: String, skipCache: Boolean = false) = runCatching {
        val response = if (skipCache) {
            getService.get().getMangaChaptersUncached(id)
        } else {
            getService.get().getMangaChapters(id)
        }

        if (!response.isSuccessful) {
            throw HttpException(response)
        }
        response.body()!!
    }
}