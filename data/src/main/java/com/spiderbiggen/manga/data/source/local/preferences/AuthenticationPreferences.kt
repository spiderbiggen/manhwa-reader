package com.spiderbiggen.manga.data.source.local.preferences

import androidx.datastore.core.Serializer
import com.spiderbiggen.manga.data.crypto.Crypto
import com.spiderbiggen.manga.data.source.remote.model.UserEntity
import com.spiderbiggen.manga.data.source.remote.model.auth.TokenEntity
import java.io.InputStream
import java.io.OutputStream
import kotlin.io.encoding.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class AuthenticationPreferences(
    val accessToken: TokenEntity,
    val refreshToken: TokenEntity,
    val user: UserEntity? = null,
)

object AuthenticationPreferencesSerializer: Serializer<AuthenticationPreferences?> {
    override val defaultValue: AuthenticationPreferences?
        get() = null

    override suspend fun readFrom(input: InputStream): AuthenticationPreferences? {
        val encryptedBytes = withContext(Dispatchers.IO) {
            input.use { it.readBytes() }
        }
        val encryptedBytesDecoded = Base64.decode(encryptedBytes)
        val decryptedBytes = Crypto.decrypt(encryptedBytesDecoded)
        val decodedJsonString = decryptedBytes.decodeToString()
        return Json.decodeFromString(decodedJsonString)
    }

    override suspend fun writeTo(t: AuthenticationPreferences?, output: OutputStream) {
        val json = Json.encodeToString(t)
        val bytes = json.toByteArray()
        val encryptedBytes = Crypto.encrypt(bytes)
        val encryptedBytesBase64 = Base64.encodeToByteArray(encryptedBytes)
        withContext(Dispatchers.IO) {
            output.use {
                it.write(encryptedBytesBase64)
            }
        }
    }
}

