package com.spiderbiggen.manga.presentation.usecases

import com.spiderbiggen.manga.domain.model.AppError
import javax.inject.Inject

class FormatAppError @Inject constructor() {
    operator fun invoke(error: AppError): String = when (error) {
        is AppError.Remote.Http -> "HTTP ${error.code}: ${error.message}"
        is AppError.Remote.Io -> "IO: ${error.exception.message}"
        is AppError.Remote.NoConnection -> "Couldn't connect to server"
        is AppError.Remote.NotFound -> "Resource not found"
        is AppError.Unknown -> "Internal error"
    }
}
