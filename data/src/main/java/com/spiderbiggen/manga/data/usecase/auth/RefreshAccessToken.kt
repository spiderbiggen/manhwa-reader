package com.spiderbiggen.manga.data.usecase.auth

import com.spiderbiggen.manga.data.source.local.repository.AuthenticationRepository
import com.spiderbiggen.manga.data.source.remote.AuthService
import com.spiderbiggen.manga.data.source.remote.model.auth.RefreshTokenBody
import com.spiderbiggen.manga.data.usecase.either
import com.spiderbiggen.manga.domain.model.AppError
import com.spiderbiggen.manga.domain.model.Either
import javax.inject.Inject
import javax.inject.Provider
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response

class RefreshAccessToken @Inject constructor(
    private val authService: Provider<AuthService>,
    private val authenticationRepository: AuthenticationRepository,
) {
    suspend operator fun invoke(): Either<String, AppError> = runCatching {
        val refreshToken = authenticationRepository.getRefreshToken()
            ?: throw HttpException(Response.error<Unit>(401, ResponseBody.EMPTY))
        val response = authService.get().refresh(RefreshTokenBody(refreshToken.token))
        if (!response.isSuccessful) {
            throw HttpException(response)
        }
        val body = response.body()!!
        authenticationRepository.saveTokens(body.accessToken, body.refreshToken)
        body.accessToken.token
    }.either()
}
