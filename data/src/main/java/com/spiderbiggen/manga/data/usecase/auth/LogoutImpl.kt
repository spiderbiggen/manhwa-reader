package com.spiderbiggen.manga.data.usecase.auth

import android.util.Log
import com.spiderbiggen.manga.data.source.local.repository.AuthenticationRepository
import com.spiderbiggen.manga.data.source.remote.AuthService
import com.spiderbiggen.manga.data.source.remote.model.auth.RefreshTokenBody
import com.spiderbiggen.manga.domain.usecase.auth.Logout
import javax.inject.Inject
import javax.inject.Provider
import retrofit2.HttpException

class LogoutImpl @Inject constructor(
    private val authService: Provider<AuthService>,
    private val authenticationRepository: Provider<AuthenticationRepository>,
) : Logout {
    override suspend fun invoke() {
        val token = authenticationRepository.get().getRefreshToken() ?: return

        val body = RefreshTokenBody(token.token)

        val response = authService.get().logout(body)
        if (!response.isSuccessful) {
            Log.w("LogoutImpl", "failed to logout", HttpException(response))
        }

        authenticationRepository.get().clear()
    }
}
