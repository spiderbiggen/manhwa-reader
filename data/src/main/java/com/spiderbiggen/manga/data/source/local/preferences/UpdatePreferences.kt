package com.spiderbiggen.manga.data.source.local.preferences

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import java.io.InputStream
import java.io.OutputStream
import kotlin.time.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/** State of the update check, kept in plain text because nothing in here is sensitive. */
@Serializable
data class UpdatePreferences(
    val lastCheckedAt: Instant? = null,
    val releases: List<CachedRelease> = emptyList(),
    val noticeDismissed: Boolean = false,
)

@Serializable
data class CachedRelease(
    val tagName: String,
    val url: String,
    val body: String,
    val isPreRelease: Boolean,
)

object UpdatePreferencesSerializer : Serializer<UpdatePreferences> {
    private val json = Json { ignoreUnknownKeys = true }

    override val defaultValue: UpdatePreferences = UpdatePreferences()

    override suspend fun readFrom(input: InputStream): UpdatePreferences =
        try {
            json.decodeFromString(input.readBytes().decodeToString())
        } catch (e: IllegalArgumentException) {
            // SerializationException is an IllegalArgumentException; I/O errors must not be
            // swallowed.
            throw CorruptionException("Failed to read UpdatePreferences", e)
        }

    override suspend fun writeTo(t: UpdatePreferences, output: OutputStream) {
        output.write(json.encodeToString(t).toByteArray())
    }
}
