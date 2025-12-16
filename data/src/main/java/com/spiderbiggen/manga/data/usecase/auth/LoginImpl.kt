package com.spiderbiggen.manga.data.usecase.auth

import com.spiderbiggen.manga.data.source.local.repository.AuthenticationRepository
import com.spiderbiggen.manga.data.source.remote.AuthService
import com.spiderbiggen.manga.data.source.remote.model.auth.LoginBody
import com.spiderbiggen.manga.data.source.remote.usecase.FetchCurrentUser
import com.spiderbiggen.manga.data.source.remote.usecase.ResetBearerToken
import com.spiderbiggen.manga.data.usecase.either
import com.spiderbiggen.manga.data.usecase.user.MapUserEntity
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.andThenLeft
import com.spiderbiggen.manga.domain.model.mapLeft
import com.spiderbiggen.manga.domain.model.onLeft
import com.spiderbiggen.manga.domain.usecase.auth.Login
import com.spiderbiggen.manga.domain.usecase.user.SynchronizeWithRemote
import javax.inject.Inject
import javax.inject.Provider

class LoginImpl @Inject constructor(
    private val authService: Provider<AuthService>,
    private val authenticationRepository: Provider<AuthenticationRepository>,
    private val fetchCurrentUser: FetchCurrentUser,
    private val synchronizeWithRemote: SynchronizeWithRemote,
    private val resetBearerToken: ResetBearerToken,
    private val mapUserEntity: MapUserEntity,
) : Login {
    override suspend fun invoke(usernameOrEmail: String, password: String) = updateSession(usernameOrEmail, password)
        .andThenLeft { fetchCurrentUser() }
        .mapLeft { mapUserEntity(it) }
        .onLeft { synchronizeWithRemote(true) }

    private suspend fun updateSession(usernameOrEmail: String, password: String): Either<Unit, AppError> = runCatching {
        val body = LoginBody(usernameOrEmail, password)
        val session = authService.get().login(body)
        authenticationRepository.get().saveTokens(session.accessToken, session.refreshToken)
        resetBearerToken()
    }.either()
}
