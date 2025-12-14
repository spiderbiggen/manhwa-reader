package com.spiderbiggen.manga.data.usecase.auth

import com.spiderbiggen.manga.data.source.local.repository.AuthenticationRepository
import com.spiderbiggen.manga.data.source.remote.AuthService
import com.spiderbiggen.manga.data.source.remote.model.auth.RefreshTokenBody
import com.spiderbiggen.manga.data.usecase.either
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import javax.inject.Inject
import javax.inject.Provider

class RefreshAccessToken @Inject constructor(
    private val authService: Provider<AuthService>,
    private val authenticationRepository: AuthenticationRepository,
) {
    suspend operator fun invoke(): Either<String, AppError> {
        val refreshToken = authenticationRepository.getRefreshToken()
            ?: return Either.Right(AppError.Auth.Unauthorized)

        return runCatching {
            val response = authService.get().refresh(RefreshTokenBody(refreshToken.token))
            authenticationRepository.saveTokens(response.accessToken, response.refreshToken)
            response.accessToken.token
        }.either()
    }
}
