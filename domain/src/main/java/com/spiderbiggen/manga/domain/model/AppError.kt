package com.spiderbiggen.manga.domain.model

import java.io.IOException

sealed interface AppError {
    sealed interface Remote : AppError {

        data object NotFound : Remote
        data object NoConnection : Remote

        /**
         * Fall back http error
         */
        data class Http(val code: Int, val message: String?) : Remote

        /**
         * Fall back http error
         */
        data class Io(val exception: IOException) : Remote
    }

    data class Unknown(val exception: Exception) : AppError
}
