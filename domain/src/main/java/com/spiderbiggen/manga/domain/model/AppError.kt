package com.spiderbiggen.manga.domain.model

import arrow.core.NonEmptyList

sealed class AppError(override val message: String? = null, override val cause: Throwable? = null) :
    Throwable(message, cause) {

    data class Multi(val errors: NonEmptyList<AppError>) :
        AppError(
            message = errors.joinToString("\n") { it.message ?: it.toString() },
        )

    sealed class Remote(message: String? = null, cause: Throwable? = null) : AppError(message, cause) {

        data class BadRequest(override val cause: Throwable? = null) : Remote("Bad Request", cause)

        data class NotFound(override val cause: Throwable? = null) : Remote("Resource Not Found", cause)
        data class NoConnection(override val cause: Throwable? = null) : Remote("No Connection", cause)
        data class Conflict(override val cause: Throwable? = null) : Remote("Conflict", cause)

        /**
         * Fall back http error
         */
        data class Http(val code: Int, val httpMessage: String?, override val cause: Throwable? = null) :
            Remote("HTTP $code: $httpMessage", cause)

        /**
         * Fall back io error
         */
        data class Io(override val cause: Throwable) : Remote(cause.message, cause)
    }

    sealed class Database(message: String? = null, cause: Throwable? = null) : AppError(message, cause) {
        data class Io(override val cause: Throwable) : Database(cause.message, cause)
    }

    sealed class Auth(message: String? = null, cause: Throwable? = null) : Remote(message, cause) {
        data class Unauthorized(override val cause: Throwable? = null) : Auth("Unauthorized", cause)
        data class Forbidden(override val cause: Throwable? = null) : Auth("Forbidden", cause)

        data class Invalid(
            val username: List<ValidationError> = emptyList(),
            val password: List<ValidationError> = emptyList(),
            override val cause: Throwable? = null,
        ) : Auth("Invalid Credentials", cause)

        sealed interface ValidationError {
            data class Length(val min: Int, val max: Int?) : ValidationError
            data object Uppercase : ValidationError
            data object Lowercase : ValidationError
            data object Numeric : ValidationError
            data object Special : ValidationError
        }
    }

    data class Unknown(override val cause: Throwable) : AppError(cause.message, cause)
}
