package com.spiderbiggen.manhwa.domain.model

import java.io.IOException

sealed interface AppError {
    sealed interface Remote : AppError {

        data object NotFound : Remote
        data object NoConnection : Remote


        /**
         * Fall back http error
         */
        class Http(val code: Int, val message: String?) : Remote

        /**
         * Fall back http error
         */
        class Io(val exception: IOException) : Remote
    }

    sealed interface Local : AppError {
        data object NoCache : Local
        data object Unknown : Local
    }

    class Unknown(val exception: Exception) : AppError
}