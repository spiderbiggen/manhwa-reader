package com.spiderbiggen.manga.data.usecase.auth

import arrow.core.Either
import arrow.core.raise.either
import com.spiderbiggen.manga.data.source.local.repository.AuthenticationRepository
import com.spiderbiggen.manga.data.source.remote.AuthService
import com.spiderbiggen.manga.data.source.remote.model.auth.RegisterBody
import com.spiderbiggen.manga.data.source.remote.usecase.FetchCurrentUser
import com.spiderbiggen.manga.data.source.remote.usecase.ResetBearerToken
import com.spiderbiggen.manga.data.usecase.appError
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.auth.User
import com.spiderbiggen.manga.domain.usecase.auth.Register

class RegisterImpl(
    private val authService: AuthService,
    private val authenticationRepository: AuthenticationRepository,
    private val fetchCurrentUser: FetchCurrentUser,
    private val resetBearerToken: ResetBearerToken,
) : Register {
    override suspend fun invoke(username: String, email: String?, password: String): Either<AppError, User> = either {
        updateSession(username, email, password).bind()
        fetchCurrentUser().bind()
    }

    private suspend fun updateSession(username: String, email: String?, password: String): Either<AppError, Unit> =
        either {
            val session = appError {
                val body = RegisterBody(
                    username = username,
                    email = email?.takeUnless { it.isEmpty() },
                    password = password,
                )
                authService.register(body)
            }
            authenticationRepository.saveTokens(session.accessToken, session.refreshToken).bind()
            resetBearerToken()
        }
}
