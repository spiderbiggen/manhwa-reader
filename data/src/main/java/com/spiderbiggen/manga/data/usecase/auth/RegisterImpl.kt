package com.spiderbiggen.manga.data.usecase.auth

import com.spiderbiggen.manga.data.source.local.repository.AuthenticationRepository
import com.spiderbiggen.manga.data.source.remote.AuthService
import com.spiderbiggen.manga.data.source.remote.model.auth.RegisterBody
import com.spiderbiggen.manga.data.source.remote.usecase.FetchCurrentUser
import com.spiderbiggen.manga.data.source.remote.usecase.ResetBearerToken
import com.spiderbiggen.manga.data.usecase.either
import com.spiderbiggen.manga.domain.model.AppError
import arrow.core.Either
import arrow.core.flatMap
import com.spiderbiggen.manga.domain.model.auth.User
import com.spiderbiggen.manga.domain.usecase.auth.Register

class RegisterImpl(
    private val authService: AuthService,
    private val authenticationRepository: AuthenticationRepository,
    private val fetchCurrentUser: FetchCurrentUser,
    private val resetBearerToken: ResetBearerToken,
) : Register {
    override suspend fun invoke(username: String, email: String?, password: String): Either<AppError, User> =
        updateSession(username, email, password)
            .flatMap { fetchCurrentUser() }

    private suspend fun updateSession(username: String, email: String?, password: String): Either<AppError, Unit> =
        runCatching {
            val body = RegisterBody(
                username = username,
                email = email?.takeUnless { it.isEmpty() },
                password = password,
            )
            val session = authService.register(body)
            authenticationRepository.saveTokens(session.accessToken, session.refreshToken)
            resetBearerToken()
        }.either()
}
