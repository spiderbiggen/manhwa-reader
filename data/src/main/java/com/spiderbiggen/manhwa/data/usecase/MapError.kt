package com.spiderbiggen.manhwa.data.usecase

import com.spiderbiggen.manhwa.domain.model.AppError
import okhttp3.internal.http2.ConnectionShutdownException
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class MapError @Inject constructor() {
    operator fun invoke(throwable: Throwable): AppError = when (throwable) {
        is HttpException -> {
            when (val code = throwable.code()) {
                404 -> AppError.Remote.NotFound
                else -> AppError.Remote.Http(code, throwable.message)
            }
        }

        is SocketTimeoutException -> AppError.Remote.NoConnection
        is UnknownHostException -> AppError.Remote.NoConnection
        is ConnectionShutdownException -> AppError.Remote.NoConnection

        is Exception -> AppError.Unknown(throwable)
        else -> throw throwable
    }
}