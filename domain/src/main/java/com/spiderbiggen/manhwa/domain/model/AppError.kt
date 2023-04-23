package com.spiderbiggen.manhwa.domain.model

import java.io.IOException

sealed interface AppError {
    sealed interface Remote : AppError {

        object NotFound : Remote
        object NoConnection : Remote


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
        object NoCache : Local
        object Unknown : Local
    }

    class Unknown(val exception: Exception) : AppError
}