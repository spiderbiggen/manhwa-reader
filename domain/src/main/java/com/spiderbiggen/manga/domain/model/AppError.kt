package com.spiderbiggen.manga.domain.model

import java.io.IOException

sealed interface AppError {

    data class Multi(val errors: Collection<AppError>) : AppError

    sealed interface Remote : AppError {

        data object BadRequest : Remote

        data object NotFound : Remote
        data object NoConnection : Remote
        data object Conflict : Remote

        /**
         * Fall back http error
         */
        data class Http(val code: Int, val message: String?) : Remote

        /**
         * Fall back http error
         */
        data class Io(val exception: IOException) : Remote
    }

    sealed interface Auth : Remote {
        data object Unauthorized : Auth
        data object Forbidden : Auth

        data class Invalid(
            val username: List<ValidationError> = emptyList(),
            val password: List<ValidationError> = emptyList(),
        ) : Auth

        sealed interface ValidationError {
            data class Length(val min: Int, val max: Int?) : ValidationError
            data object Uppercase : ValidationError
            data object Lowercase : ValidationError
            data object Numeric : ValidationError
            data object Special : ValidationError
        }
    }

    data class Unknown(val exception: Exception) : AppError
}
