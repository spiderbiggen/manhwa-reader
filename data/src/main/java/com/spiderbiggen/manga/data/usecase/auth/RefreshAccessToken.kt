package com.spiderbiggen.manga.data.usecase.auth

import com.spiderbiggen.manga.data.source.local.repository.AuthenticationRepository
import com.spiderbiggen.manga.data.source.remote.AuthService
import com.spiderbiggen.manga.data.source.remote.model.auth.RefreshTokenBody
import com.spiderbiggen.manga.data.usecase.either
import com.spiderbiggen.manga.domain.model.AppError
import arrow.core.Either
import arrow.core.left
import arrow.core.right

class RefreshAccessToken(
    private val authService: AuthService,
    private val authenticationRepository: AuthenticationRepository,
) {
    suspend operator fun invoke(): Either<AppError, String> {
        val refreshToken = authenticationRepository.getRefreshToken()
            ?: return AppError.Auth.Unauthorized.left()

        return runCatching {
            val response = authService.refresh(RefreshTokenBody(refreshToken.token))
            authenticationRepository.saveTokens(response.accessToken, response.refreshToken)
            response.accessToken.token
        }.either()
    }
}
