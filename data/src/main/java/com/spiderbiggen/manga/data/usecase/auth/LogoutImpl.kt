package com.spiderbiggen.manga.data.usecase.auth

import android.util.Log
import arrow.core.Either
import arrow.core.raise.either
import com.spiderbiggen.manga.data.source.local.repository.AuthenticationStore
import com.spiderbiggen.manga.data.source.remote.AuthService
import com.spiderbiggen.manga.data.source.remote.model.auth.RefreshTokenBody
import com.spiderbiggen.manga.data.source.remote.usecase.BearerTokenResetter
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.usecase.auth.Logout
import kotlin.coroutines.cancellation.CancellationException

class LogoutImpl(
    private val authService: AuthService,
    private val authenticationRepository: AuthenticationStore,
    private val resetBearerToken: BearerTokenResetter,
) : Logout {
    override suspend fun invoke(): Either<AppError, Unit> = either {
        val token = authenticationRepository.getRefreshToken().bind() ?: return@either

        val body = RefreshTokenBody(token.token)

        try {
            authService.logout(body)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w("LogoutImpl", "failed to logout", e)
        } finally {
            resetBearerToken()
        }

        authenticationRepository.clear().bind()
    }
}
