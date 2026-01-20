package com.spiderbiggen.manga.data.usecase

import android.database.sqlite.SQLiteException
import arrow.core.Either
import arrow.core.left
import arrow.core.raise.Raise
import arrow.core.raise.catch
import arrow.core.right
import com.spiderbiggen.manga.domain.model.AppError
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
    is RedirectResponseException -> handleResponseException(this.response.status, this)

    is ClientRequestException -> handleResponseException(this.response.status, this)

    is ServerResponseException -> handleResponseException(this.response.status, this)

    // Database
    is SQLiteException -> AppError.Database.Io(this)

    // Connections
    is SocketTimeoutException -> AppError.Remote.NoConnection(this)

    is UnknownHostException -> AppError.Remote.NoConnection(this)

    is ConnectionShutdownException -> AppError.Remote.NoConnection(this)

    is IOException -> AppError.Remote.Io(this)

    is Exception -> AppError.Unknown(this)

    // Anything else
    else -> throw this
}

private fun handleResponseException(status: HttpStatusCode, cause: Throwable): AppError {
    val message = cause.message
    return when (status) {
        HttpStatusCode.BadRequest -> AppError.Remote.BadRequest(cause)
        HttpStatusCode.Unauthorized -> AppError.Auth.Unauthorized(cause)
        HttpStatusCode.Forbidden -> AppError.Auth.Forbidden(cause)
        HttpStatusCode.NotFound -> AppError.Remote.NotFound(cause)
        HttpStatusCode.Conflict -> AppError.Remote.Conflict(cause)
        else -> AppError.Remote.Http(status.value, message, cause)
    }
}

fun <R> Result<R>.either(): Either<AppError, R> = fold(
    { it.right() },
    { it.toAppError().left() },
)

inline fun <A> Raise<AppError>.appError(block: () -> A): A = catch(block) { raise(it.toAppError()) }
