package com.spiderbiggen.manga.data.usecase.auth

import android.util.Log
import com.spiderbiggen.manga.data.source.local.repository.AuthenticationRepository
import com.spiderbiggen.manga.data.source.remote.AuthService
import com.spiderbiggen.manga.data.source.remote.model.auth.RefreshTokenBody
import com.spiderbiggen.manga.data.usecase.either
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import com.spiderbiggen.manga.domain.usecase.auth.Logout
import javax.inject.Inject
import javax.inject.Provider

class LogoutImpl @Inject constructor(
    private val authService: Provider<AuthService>,
    private val authenticationRepository: Provider<AuthenticationRepository>,
) : Logout {
    override suspend fun invoke(): Either<Unit, AppError> = runCatching {
        val token = authenticationRepository.get().getRefreshToken() ?: return@runCatching

        val body = RefreshTokenBody(token.token)

        try {
            authService.get().logout(body)
        } catch (e: Exception) {
            Log.w("LogoutImpl", "failed to logout", e)
        }

        authenticationRepository.get().clear()
    }.either()
}
