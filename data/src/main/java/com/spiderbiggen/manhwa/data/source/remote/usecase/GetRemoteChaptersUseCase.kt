package com.spiderbiggen.manhwa.data.source.remote.usecase

import com.spiderbiggen.manhwa.data.source.remote.ManhwaService
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Provider

class GetRemoteChaptersUseCase @Inject constructor(private val getService: Provider<ManhwaService>) {
    suspend operator fun invoke(id: String) = runCatching {
        val response = getService.get().getManhwaChapters(id)
        if (!response.isSuccessful) {
            throw HttpException(response)
        }
        response.body()!!
    }
}