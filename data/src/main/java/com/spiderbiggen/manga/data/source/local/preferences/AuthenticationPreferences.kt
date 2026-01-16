package com.spiderbiggen.manga.data.source.local.preferences

import android.util.Log
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.spiderbiggen.manga.data.crypto.Crypto
import com.spiderbiggen.manga.data.source.remote.model.auth.TokenEntity
import com.spiderbiggen.manga.data.source.remote.model.user.UserEntity
import java.io.InputStream
import java.io.OutputStream
import kotlin.io.encoding.Base64
import kotlin.time.Instant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
sealed interface AuthenticationPreferences {
    @Serializable
    data object Unauthenticated : AuthenticationPreferences

    @Serializable
    data class Authenticated(
        val accessToken: TokenEntity,
        val refreshToken: TokenEntity,
        val user: UserEntity? = null,
        val lastSynchronizationTime: Instant? = null,
    ) : AuthenticationPreferences
}

object AuthenticationPreferencesSerializer : Serializer<AuthenticationPreferences> {
    override val defaultValue: AuthenticationPreferences = AuthenticationPreferences.Unauthenticated

    override suspend fun readFrom(input: InputStream): AuthenticationPreferences = try {
        val encryptedBytes = input.use { it.readBytes() }
        val encryptedBytesDecoded = Base64.decode(encryptedBytes)
        val decryptedBytes = Crypto.decrypt(encryptedBytesDecoded)
        val decodedJsonString = decryptedBytes.decodeToString()
        Json.decodeFromString<AuthenticationPreferences>(decodedJsonString)
    } catch (e: Exception) {
        Log.e("AuthenticationPreferencesSerializer", "Failed to read AuthenticationPreferences", e)
        throw CorruptionException("Failed to read AuthenticationPreferences", e)
    }

    override suspend fun writeTo(t: AuthenticationPreferences, output: OutputStream) {
        try {
            val json = Json.encodeToString(t)
            val bytes = json.toByteArray()
            val encryptedBytes = Crypto.encrypt(bytes)
            val encryptedBytesBase64 = Base64.encodeToByteArray(encryptedBytes)
            withContext(Dispatchers.IO) {
                output.use {
                    it.write(encryptedBytesBase64)
                }
            }
        } catch (e: Exception) {
            Log.e("AuthenticationPreferencesSerializer", "Failed to write AuthenticationPreferences", e)
            throw e
        }
    }
}
