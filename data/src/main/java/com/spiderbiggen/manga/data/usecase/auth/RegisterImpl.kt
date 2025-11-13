package com.spiderbiggen.manga.data.usecase.auth

import com.spiderbiggen.manga.data.source.local.repository.AuthenticationRepository
import com.spiderbiggen.manga.data.source.remote.AuthService
import com.spiderbiggen.manga.data.source.remote.model.auth.RegisterBody
import com.spiderbiggen.manga.data.source.remote.usecase.GetCurrentUser
import com.spiderbiggen.manga.data.usecase.either
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.model.andThenLeft
import com.spiderbiggen.manga.domain.model.auth.User
import com.spiderbiggen.manga.domain.model.mapLeft
import com.spiderbiggen.manga.domain.usecase.auth.Register
import javax.inject.Inject
import javax.inject.Provider
import retrofit2.HttpException

class RegisterImpl @Inject constructor(
    private val authService: Provider<AuthService>,
    private val authenticationRepository: Provider<AuthenticationRepository>,
    private val getCurrentUser: GetCurrentUser,
) : Register {
    override suspend fun invoke(username: String, email: String?, password: String) =
        updateSession(username, email, password)
            .andThenLeft { getCurrentUser() }
            .mapLeft { User(it.username, it.email, it.avatar) }

    private suspend fun updateSession(username: String, email: String?, password: String): Either<Unit, AppError> =
        runCatching {
            val body = RegisterBody(
                username = username,
                email = email,
                password = password,
            )
            val response = authService.get().register(body)
            if (response.isSuccessful) {
                val session = response.body()!!
                authenticationRepository.get().saveTokens(session.accessToken, session.refreshToken)
            }
            throw HttpException(response)
        }.either()
}
