package com.spiderbiggen.manga.data.usecase.auth

import android.util.Log
import com.spiderbiggen.manga.data.source.local.repository.AuthenticationRepository
import com.spiderbiggen.manga.data.source.remote.AuthService
import com.spiderbiggen.manga.data.source.remote.model.auth.RefreshTokenBody
import com.spiderbiggen.manga.data.usecase.either
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.usecase.auth.Logout

class LogoutImpl(
    private val authService: AuthService,
    private val authenticationRepository: AuthenticationRepository,
) : Logout {
    override suspend fun invoke(): Either<Unit, AppError> = runCatching {
        val token = authenticationRepository.getRefreshToken() ?: return@runCatching

        val body = RefreshTokenBody(token.token)

        try {
            authService.logout(body)
        } catch (e: Exception) {
            Log.w("LogoutImpl", "failed to logout", e)
        }

        authenticationRepository.clear()
    }.either()
}
