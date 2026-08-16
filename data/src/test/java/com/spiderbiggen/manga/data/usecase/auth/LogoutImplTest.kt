package com.spiderbiggen.manga.data.usecase.auth

import arrow.core.Either
import arrow.core.right
import com.spiderbiggen.manga.data.source.local.repository.AuthenticationStore
import com.spiderbiggen.manga.data.source.remote.AuthService
import com.spiderbiggen.manga.data.source.remote.model.auth.LoginBody
import com.spiderbiggen.manga.data.source.remote.model.auth.RefreshTokenBody
import com.spiderbiggen.manga.data.source.remote.model.auth.RegisterBody
import com.spiderbiggen.manga.data.source.remote.model.auth.SessionResponse
import com.spiderbiggen.manga.data.source.remote.model.auth.TokenEntity
import com.spiderbiggen.manga.data.source.remote.usecase.BearerTokenResetter
import com.spiderbiggen.manga.domain.model.AppError
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Instant
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LogoutImplTest {
    @Test
    fun `given logout succeeds when invoked then bearer token and authentication are cleared`() =
        runBlocking {
            val authenticationStore = FakeAuthenticationStore()
            val bearerTokenResetter = FakeBearerTokenResetter()
            val result = logout(authenticationStore, bearerTokenResetter).invoke()

            assertTrue(result.isRight())
            assertEquals(1, bearerTokenResetter.resetCount)
            assertEquals(1, authenticationStore.clearCount)
        }

    @Test
    fun `given logout fails when invoked then bearer token and authentication are cleared`() =
        runBlocking {
            val authenticationStore = FakeAuthenticationStore()
            val bearerTokenResetter = FakeBearerTokenResetter()
            val result =
                logout(
                        authenticationStore,
                        bearerTokenResetter,
                        IOException("network unavailable"),
                    )
                    .invoke()

            assertTrue(result.isRight())
            assertEquals(1, bearerTokenResetter.resetCount)
            assertEquals(1, authenticationStore.clearCount)
        }

    @Test
    fun `given logout is cancelled when invoked then cancellation is rethrown and bearer token is cleared`() =
        runBlocking {
            val authenticationStore = FakeAuthenticationStore()
            val bearerTokenResetter = FakeBearerTokenResetter()

            try {
                logout(
                        authenticationStore,
                        bearerTokenResetter,
                        CancellationException("cancelled"),
                    )
                    .invoke()
                throw AssertionError("Expected cancellation")
            } catch (_: CancellationException) {
                assertEquals(1, bearerTokenResetter.resetCount)
                assertEquals(0, authenticationStore.clearCount)
            }
        }

    private fun logout(
        authenticationStore: AuthenticationStore,
        bearerTokenResetter: BearerTokenResetter,
        logoutException: Throwable? = null,
    ) =
        LogoutImpl(
            authService = FakeAuthService(logoutException),
            authenticationRepository = authenticationStore,
            resetBearerToken = bearerTokenResetter,
        )
}

private class FakeAuthenticationStore : AuthenticationStore {
    var clearCount = 0

    override suspend fun clear(): Either<AppError, Unit> {
        clearCount++
        return Unit.right()
    }

    override suspend fun getRefreshToken(): Either<AppError, TokenEntity?> =
        TokenEntity("refresh-token", Instant.parse("2026-01-01T00:00:00Z")).right()
}

private class FakeBearerTokenResetter : BearerTokenResetter {
    var resetCount = 0

    override fun invoke() {
        resetCount++
    }
}

private class FakeAuthService(private val logoutException: Throwable?) : AuthService {
    override suspend fun register(body: RegisterBody): SessionResponse = error("not used")

    override suspend fun login(body: LoginBody): SessionResponse = error("not used")

    override suspend fun refresh(body: RefreshTokenBody): SessionResponse = error("not used")

    override suspend fun logout(body: RefreshTokenBody) {
        logoutException?.let { throw it }
    }
}
