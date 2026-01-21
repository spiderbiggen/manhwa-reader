package com.spiderbiggen.manga.data.usecase.auth

import arrow.core.Either
import arrow.core.raise.either
import com.spiderbiggen.manga.data.source.local.repository.AuthenticationRepository
import com.spiderbiggen.manga.data.source.remote.AuthService
import com.spiderbiggen.manga.data.source.remote.model.auth.RefreshTokenBody
import com.spiderbiggen.manga.data.usecase.appError
import com.spiderbiggen.manga.domain.model.AppError

class RefreshAccessToken(
    private val authService: AuthService,
    private val authenticationRepository: AuthenticationRepository,
) {
    suspend operator fun invoke(): Either<AppError, String> = either {
        val refreshToken = authenticationRepository.getRefreshToken().bind()
            ?: raise(AppError.Auth.Unauthorized())

        val response = appError {
            authService.refresh(RefreshTokenBody(refreshToken.token))
        }
        authenticationRepository.saveTokens(response.accessToken, response.refreshToken).bind()
        response.accessToken.token
    }
}
