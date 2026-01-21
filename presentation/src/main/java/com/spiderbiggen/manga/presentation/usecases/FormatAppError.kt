package com.spiderbiggen.manga.presentation.usecases

import com.spiderbiggen.manga.domain.model.AppError

private const val UNKNOWN_ERROR_MESSAGE = "Unknown error"

class FormatAppError {
    operator fun invoke(error: AppError): String = when (error) {
        is AppError.Multi -> formatMultiError(error)
        is AppError.Remote -> formatRemoteError(error)
        is AppError.Database.Io -> "Database error: ${error.cause.message ?: UNKNOWN_ERROR_MESSAGE}"
        is AppError.Unknown -> "Internal error: ${error.cause.message ?: UNKNOWN_ERROR_MESSAGE}"
    }

    private fun formatMultiError(error: AppError.Multi): String {
        val firstError = invoke(error.errors.first())
        return "${error.errors.size} error(s), first is $firstError"
    }

    private fun formatRemoteError(error: AppError.Remote): String = when (error) {
        is AppError.Remote.Http -> "HTTP ${error.code}: ${error.httpMessage ?: UNKNOWN_ERROR_MESSAGE}"

        is AppError.Remote.Io -> "IO: ${error.cause.message ?: UNKNOWN_ERROR_MESSAGE}"

        is AppError.Remote.NoConnection -> "Couldn't connect to server"

        is AppError.Remote.BadRequest -> "Bad request"

        is AppError.Remote.NotFound -> "Resource not found"

        is AppError.Remote.Conflict -> "Conflict"

        // Auth
        is AppError.Auth.Unauthorized -> "Not authorized"

        is AppError.Auth.Forbidden -> "Access forbidden"

        // TODO format list of validation errors
        is AppError.Auth.Invalid -> "Invalid data"
    }
}
