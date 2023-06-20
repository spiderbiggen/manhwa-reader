package com.spiderbiggen.manhwa.data.source.remote.usecase

import com.spiderbiggen.manhwa.data.source.remote.ManhwaService
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Provider

class GetRemoteManhwaUseCase @Inject constructor(private val getService: Provider<ManhwaService>) {
    suspend operator fun invoke() = runCatching {
        val response = getService.get().getAllManhwas()
        if (!response.isSuccessful) {
            throw HttpException(response)
        }
        response.body()!!
    }
}