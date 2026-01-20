package com.spiderbiggen.manga.data.usecase.auth

import android.util.Log
import arrow.core.Either
import arrow.core.raise.either
import com.spiderbiggen.manga.data.source.local.repository.AuthenticationRepository
import com.spiderbiggen.manga.data.source.remote.AuthService
import com.spiderbiggen.manga.data.source.remote.model.auth.RefreshTokenBody
import com.spiderbiggen.manga.data.usecase.appError
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.usecase.auth.Logout

class LogoutImpl(
    private val authService: AuthService,
    private val authenticationRepository: AuthenticationRepository,
) : Logout {
    override suspend fun invoke(): Either<AppError, Unit> = either {
        val token = authenticationRepository.getRefreshToken().bind() ?: return@either

        val body = RefreshTokenBody(token.token)

        try {
            authService.logout(body)
        } catch (e: Exception) {
            Log.w("LogoutImpl", "failed to logout", e)
        }

        authenticationRepository.clear().bind()
    }
}
