package com.spiderbiggen.manga.presentation.usecases

import com.spiderbiggen.manga.domain.model.AppError

class FormatAppError {
    operator fun invoke(error: AppError): String = when (error) {
        is AppError.Remote.Http -> "HTTP ${error.code}: ${error.httpMessage ?: "Unknown error"}"

        is AppError.Remote.Io -> "IO: ${error.cause.message ?: "Unknown error"}"

        is AppError.Remote.NoConnection -> "Couldn't connect to server"

        is AppError.Remote.BadRequest -> "Bad request"

        is AppError.Remote.NotFound -> "Resource not found"

        is AppError.Remote.Conflict -> "Conflict"

        is AppError.Auth.Unauthorized -> "Not authorized"

        is AppError.Auth.Forbidden -> "Access forbidden"

        is AppError.Auth.Invalid -> "Invalid data"

        is AppError.Multi -> {
            val firstError = error.errors.firstOrNull()?.let { invoke(it) }
            "${error.errors.size} error(s), first is ${firstError ?: "unknown"}"
        }

        is AppError.Unknown -> "Internal error: ${error.cause.message ?: "Unknown error"}"
    }
}
