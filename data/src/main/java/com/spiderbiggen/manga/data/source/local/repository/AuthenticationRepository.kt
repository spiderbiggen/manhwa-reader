package com.spiderbiggen.manga.data.source.local.repository

import android.content.Context
import androidx.datastore.dataStore
import com.spiderbiggen.manga.data.source.local.preferences.AuthenticationPreferences
import com.spiderbiggen.manga.data.source.local.preferences.AuthenticationPreferencesSerializer
import com.spiderbiggen.manga.data.source.remote.model.UserEntity
import com.spiderbiggen.manga.data.source.remote.model.auth.TokenEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Provider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map


private val Context.authdataStore by dataStore(
    fileName = "auth-preferences",
    serializer = AuthenticationPreferencesSerializer,
)

class AuthenticationRepository @Inject constructor(
    @param:ApplicationContext private val context: Provider<Context>,
) {


    private val dataStore
        get() = context.get().authdataStore

    suspend fun clear() {
        dataStore.updateData { null }
    }

    suspend fun getAuthTokens(): AuthenticationPreferences? = dataStore.data.first()

    suspend fun getAccessToken(): TokenEntity? = getAuthTokens()?.accessToken

    suspend fun getRefreshToken(): TokenEntity? = getAuthTokens()?.refreshToken

    suspend fun getUser(): UserEntity? = getAuthTokens()?.user

    fun getUserFlow(): Flow<UserEntity?> = dataStore.data.map { it?.user }

    suspend fun saveTokens(accessToken: TokenEntity, refreshToken: TokenEntity) {
        dataStore.updateData {
            AuthenticationPreferences(accessToken, refreshToken)
        }
    }

    suspend fun saveUser(user: UserEntity): UserEntity? =
        dataStore.updateData { it?.copy(user = user) }
            ?.user
}
