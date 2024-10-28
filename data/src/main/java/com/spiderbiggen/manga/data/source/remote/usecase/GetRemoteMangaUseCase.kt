package com.spiderbiggen.manga.data.source.remote.usecase

import com.spiderbiggen.manga.data.source.remote.MangaService
import javax.inject.Inject
import javax.inject.Provider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class GetRemoteMangaUseCase @Inject constructor(private val getService: Provider<MangaService>) {
    suspend operator fun invoke(skipCache: Boolean = false) = runCatching {
        val response = withContext(Dispatchers.IO) {
            val service = getService.get()
            when {
                skipCache -> service.getAllMangasUncached()
                else -> service.getAllMangas()
            }
        }

        if (!response.isSuccessful) {
            throw HttpException(response)
        }
        response.body()!!
    }
}
