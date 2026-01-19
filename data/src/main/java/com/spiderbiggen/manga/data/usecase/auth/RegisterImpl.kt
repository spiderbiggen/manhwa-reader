package com.spiderbiggen.manga.data.usecase.auth

import com.spiderbiggen.manga.data.source.local.repository.AuthenticationRepository
import com.spiderbiggen.manga.data.source.remote.AuthService
import com.spiderbiggen.manga.data.source.remote.model.auth.RegisterBody
import com.spiderbiggen.manga.data.source.remote.usecase.FetchCurrentUser
import com.spiderbiggen.manga.data.source.remote.usecase.ResetBearerToken
import com.spiderbiggen.manga.data.usecase.either
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.andThenLeft
import com.spiderbiggen.manga.domain.usecase.auth.Register

class RegisterImpl(
    private val authService: AuthService,
    private val authenticationRepository: AuthenticationRepository,
    private val fetchCurrentUser: FetchCurrentUser,
    private val resetBearerToken: ResetBearerToken,
) : Register {
    override suspend fun invoke(username: String, email: String?, password: String) =
        updateSession(username, email, password)
            .andThenLeft { fetchCurrentUser() }

    private suspend fun updateSession(username: String, email: String?, password: String): Either<Unit, AppError> =
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
