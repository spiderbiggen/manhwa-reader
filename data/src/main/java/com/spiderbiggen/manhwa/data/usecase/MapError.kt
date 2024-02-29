package com.spiderbiggen.manhwa.data.usecase

import com.spiderbiggen.manhwa.domain.model.AppError
import com.spiderbiggen.manhwa.domain.model.Either
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.coroutines.cancellation.CancellationException
import okhttp3.internal.http2.ConnectionShutdownException
import retrofit2.HttpException

fun Throwable.toAppError(): AppError = when (this) {
    is CancellationException -> throw this

    is HttpException -> {
        when (val code = this.code()) {
            404 -> AppError.Remote.NotFound
            else -> AppError.Remote.Http(code, this.message)
        }
    }

    is SocketTimeoutException -> AppError.Remote.NoConnection

    is UnknownHostException -> AppError.Remote.NoConnection

    is ConnectionShutdownException -> AppError.Remote.NoConnection

    is Exception -> AppError.Unknown(this)

    else -> throw this
}

fun <L> Result<L>.either(): Either<L, AppError> {
    return fold(
        { Either.Left(it) },
        { Either.Right(it.toAppError()) },
    )
}
