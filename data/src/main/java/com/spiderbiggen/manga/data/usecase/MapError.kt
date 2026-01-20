package com.spiderbiggen.manga.data.usecase

import com.spiderbiggen.manga.domain.model.AppError
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.coroutines.cancellation.CancellationException
import okhttp3.internal.http2.ConnectionShutdownException

fun Throwable.toAppError(): AppError = when (this) {
    is CancellationException -> throw this

    // HTTP Responses
    is RedirectResponseException -> handleResponseException(this.response.status, this.message)

    is ClientRequestException -> handleResponseException(this.response.status, this.message)

    is ServerResponseException -> handleResponseException(this.response.status, this.message)

    // Connections
    is SocketTimeoutException -> AppError.Remote.NoConnection

    is UnknownHostException -> AppError.Remote.NoConnection

    is ConnectionShutdownException -> AppError.Remote.NoConnection

    is IOException -> AppError.Remote.Io(this)

    is Exception -> AppError.Unknown(this)

    // Anything else
    else -> throw this
}

private fun handleResponseException(status: HttpStatusCode, message: String): AppError = when (status) {
    HttpStatusCode.BadRequest -> AppError.Remote.BadRequest
    HttpStatusCode.Unauthorized -> AppError.Auth.Unauthorized
    HttpStatusCode.Forbidden -> AppError.Auth.Forbidden
    HttpStatusCode.NotFound -> AppError.Remote.NotFound
    HttpStatusCode.Conflict -> AppError.Remote.Conflict
    else -> AppError.Remote.Http(status.value, message)
}

fun <R> Result<R>.either(): Either<AppError, R> = fold(
    { it.right() },
    { it.toAppError().left() },
)
