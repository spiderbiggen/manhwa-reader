package com.spiderbiggen.manga.data.source.local.repository

import android.content.Context
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStore
import com.spiderbiggen.manga.data.source.local.preferences.AuthenticationPreferences
import com.spiderbiggen.manga.data.source.local.preferences.AuthenticationPreferencesSerializer
import com.spiderbiggen.manga.data.source.remote.model.auth.TokenEntity
import com.spiderbiggen.manga.data.source.remote.model.user.UserEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
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

@Singleton
class AuthenticationRepository @Inject constructor(@ApplicationContext context: Context) {

    private val dataStore = context.authdataStore

    suspend fun clear() {
        dataStore.updateData { AuthenticationPreferences.Unauthenticated }
    }

    suspend fun getAuthenticatedState(): AuthenticationPreferences.Authenticated? =
        dataStore.data.firstOrNull() as? AuthenticationPreferences.Authenticated

    suspend fun getAccessToken(): TokenEntity? = getAuthenticatedState()?.accessToken

    suspend fun getRefreshToken(): TokenEntity? = getAuthenticatedState()?.refreshToken

    suspend fun getUser(): UserEntity? = getAuthenticatedState()?.user

    suspend fun getLastSynchronizationTime(): Instant? = getAuthenticatedState()?.lastSynchronizationTime

    fun getUserFlow(): Flow<UserEntity?> = dataStore.data.map {
        (it as? AuthenticationPreferences.Authenticated)?.user
    }

    fun getLastSynchronizationTimeFlow(): Flow<Instant?> = dataStore.data.map {
        (it as? AuthenticationPreferences.Authenticated)?.lastSynchronizationTime
    }

    suspend fun saveTokens(accessToken: TokenEntity, refreshToken: TokenEntity) {
        dataStore.updateData {
            when (it) {
                is AuthenticationPreferences.Authenticated ->
                    it.copy(accessToken = accessToken, refreshToken = refreshToken)

                else -> AuthenticationPreferences.Authenticated(accessToken, refreshToken)
            }
        }
    }

    suspend fun saveUser(user: UserEntity): UserEntity? {
        val result = dataStore.updateData { data ->
            (data as? AuthenticationPreferences.Authenticated)
                ?.copy(user = user)
                ?: data
        }
        return (result as? AuthenticationPreferences.Authenticated)?.user
    }

    suspend fun saveLastSynchronizationTime(time: Instant) {
        dataStore.updateData { data ->
            (data as? AuthenticationPreferences.Authenticated)
                ?.copy(lastSynchronizationTime = time)
                ?: data
        }
    }
}
