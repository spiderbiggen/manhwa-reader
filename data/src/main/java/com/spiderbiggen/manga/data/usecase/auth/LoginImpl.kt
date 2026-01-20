package com.spiderbiggen.manga.data.usecase.auth

import com.spiderbiggen.manga.data.source.local.repository.AuthenticationRepository
import com.spiderbiggen.manga.data.source.remote.AuthService
import com.spiderbiggen.manga.data.source.remote.model.auth.LoginBody
import com.spiderbiggen.manga.data.source.remote.usecase.FetchCurrentUser
import com.spiderbiggen.manga.data.source.remote.usecase.ResetBearerToken
import com.spiderbiggen.manga.data.usecase.either
import com.spiderbiggen.manga.domain.model.AppError
import arrow.core.Either
import arrow.core.flatMap
import com.spiderbiggen.manga.domain.model.auth.User
import com.spiderbiggen.manga.domain.usecase.auth.Login
import com.spiderbiggen.manga.domain.usecase.user.SynchronizeWithRemote

class LoginImpl(
    private val authService: AuthService,
    private val authenticationRepository: AuthenticationRepository,
    private val fetchCurrentUser: FetchCurrentUser,
    private val synchronizeWithRemote: SynchronizeWithRemote,
    private val resetBearerToken: ResetBearerToken,
) : Login {
    override suspend fun invoke(usernameOrEmail: String, password: String): Either<AppError, User> =
        updateSession(usernameOrEmail, password)
            .flatMap { fetchCurrentUser() }
            .onRight { synchronizeWithRemote(true) }

    private suspend fun updateSession(usernameOrEmail: String, password: String): Either<AppError, Unit> = runCatching {
        val body = LoginBody(usernameOrEmail, password)
        val session = authService.login(body)
        authenticationRepository.saveTokens(session.accessToken, session.refreshToken)
        resetBearerToken()
    }.either()
}
