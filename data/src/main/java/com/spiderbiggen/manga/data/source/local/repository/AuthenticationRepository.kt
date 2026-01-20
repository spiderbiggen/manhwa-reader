package com.spiderbiggen.manga.data.source.local.repository

import android.content.Context
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStore
import arrow.core.Either
import arrow.core.raise.either
import com.spiderbiggen.manga.data.source.local.preferences.AuthenticationPreferences
import com.spiderbiggen.manga.data.source.local.preferences.AuthenticationPreferencesSerializer
import com.spiderbiggen.manga.data.source.remote.model.auth.TokenEntity
import com.spiderbiggen.manga.data.source.remote.model.user.UserEntity
import com.spiderbiggen.manga.data.usecase.appError
import com.spiderbiggen.manga.domain.model.AppError
import kotlin.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

private val Context.authdataStore by dataStore(
    fileName = "auth-preferences",
    serializer = AuthenticationPreferencesSerializer,
    corruptionHandler = ReplaceFileCorruptionHandler(
        produceNewData = { AuthenticationPreferencesSerializer.defaultValue },
    ),
)

class AuthenticationRepository(context: Context) {

    private val dataStore = context.authdataStore

    suspend fun clear(): Either<AppError, Unit> = either {
        appError { dataStore.updateData { AuthenticationPreferences.Unauthenticated } }
    }

    suspend fun getAuthenticatedState(): Either<AppError, AuthenticationPreferences.Authenticated?> = either {
        appError { dataStore.data.firstOrNull() as? AuthenticationPreferences.Authenticated }
    }

    suspend fun getAccessToken(): Either<AppError, TokenEntity?> = either {
        getAuthenticatedState().bind()?.accessToken
    }

    suspend fun getRefreshToken(): Either<AppError, TokenEntity?> = either {
        getAuthenticatedState().bind()?.refreshToken
    }

    suspend fun getUser(): Either<AppError, UserEntity?> = either {
        getAuthenticatedState().bind()?.user
    }

    suspend fun getLastSynchronizationTime(): Either<AppError, Instant?> = either {
        getAuthenticatedState().bind()?.lastSynchronizationTime
    }

    fun getUserFlow(): Flow<UserEntity?> = dataStore.data.map {
        (it as? AuthenticationPreferences.Authenticated)?.user
    }

    fun getLastSynchronizationTimeFlow(): Flow<Instant?> = dataStore.data.map {
        (it as? AuthenticationPreferences.Authenticated)?.lastSynchronizationTime
    }

    suspend fun saveTokens(accessToken: TokenEntity, refreshToken: TokenEntity): Either<AppError, Unit> = either {
        appError {
            dataStore.updateData {
                when (it) {
                    is AuthenticationPreferences.Authenticated ->
                        it.copy(accessToken = accessToken, refreshToken = refreshToken)

                    else -> AuthenticationPreferences.Authenticated(accessToken, refreshToken)
                }
            }
        }
    }

    suspend fun saveUser(user: UserEntity): Either<AppError, UserEntity?> = either {
        val result = appError {
            dataStore.updateData { data ->
                (data as? AuthenticationPreferences.Authenticated)
                    ?.copy(user = user)
                    ?: data
            }
        }
        (result as? AuthenticationPreferences.Authenticated)?.user
    }

    suspend fun saveLastSynchronizationTime(time: Instant): Either<AppError, Unit> = either {
        appError {
            dataStore.updateData { data ->
                (data as? AuthenticationPreferences.Authenticated)
                    ?.copy(lastSynchronizationTime = time)
                    ?: data
            }
        }
    }
}
