package com.spiderbiggen.manga.data.source.local.repository

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import com.spiderbiggen.manga.data.source.local.preferences.CachedRelease
import com.spiderbiggen.manga.data.source.local.preferences.UpdatePreferences
import com.spiderbiggen.manga.data.source.local.preferences.UpdatePreferencesSerializer
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import kotlin.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Real DataStore files cannot be replaced atomically on Windows JVMs, so the repository logic is
 * tested against an in-memory store and the serializer against plain byte streams.
 */
private class InMemoryDataStore<T>(initial: T) : DataStore<T> {
    private val state = MutableStateFlow(initial)
    private val mutex = Mutex()

    override val data: Flow<T> = state

    override suspend fun updateData(transform: suspend (t: T) -> T): T = mutex.withLock {
        transform(state.value).also { state.value = it }
    }
}

class UpdateRepositoryTest {
    private val release =
        CachedRelease("v1.2.3", "https://example.com", "body", isPreRelease = false)
    private val checkedAt = Instant.parse("2026-11-24T12:00:00Z")

    private fun repository() = UpdateRepository(InMemoryDataStore(UpdatePreferences()))

    @Test
    fun `given a fresh store when read then nothing was checked`() = runBlocking {
        val repository = repository()

        assertNull(repository.getLastCheckedAt().getOrNull())
        assertEquals(UpdatePreferences(), repository.observe().first())
    }

    @Test
    fun `given a check when saved then time and releases are persisted`() = runBlocking {
        val repository = repository()

        assertTrue(repository.saveCheck(listOf(release), checkedAt).isRight())

        assertEquals(checkedAt, repository.getLastCheckedAt().getOrNull())
        assertEquals(listOf(release), repository.observe().first().releases)
    }

    @Test
    fun `given a dismissed notice when a new check is saved then it is shown again`() =
        runBlocking {
            val repository = repository()
            repository.saveCheck(listOf(release), checkedAt)

            assertTrue(repository.dismiss().isRight())
            assertTrue(repository.observe().first().dismissed)

            repository.saveCheck(listOf(release), checkedAt)
            assertFalse(repository.observe().first().dismissed)
        }

    @Test
    fun `given preferences when written and read then they round trip`() = runBlocking {
        val preferences = UpdatePreferences(checkedAt, listOf(release), dismissed = true)
        val output = ByteArrayOutputStream()

        UpdatePreferencesSerializer.writeTo(preferences, output)
        val read = UpdatePreferencesSerializer.readFrom(ByteArrayInputStream(output.toByteArray()))

        assertEquals(preferences, read)
    }

    @Test
    fun `given unknown keys when read then they are ignored`() = runBlocking {
        val json = """{"dismissed":true,"somethingNew":1}"""

        val read = UpdatePreferencesSerializer.readFrom(ByteArrayInputStream(json.toByteArray()))

        assertTrue(read.dismissed)
    }

    @Test(expected = CorruptionException::class)
    fun `given garbage bytes when read then corruption is reported`() {
        runBlocking {
            UpdatePreferencesSerializer.readFrom(ByteArrayInputStream("not json".toByteArray()))
        }
    }
}
